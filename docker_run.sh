#!/bin/bash

DOCKER_NETWORK=java_tests_network_$$
AMQP_HOST1=amqp-host1
AMQP_HOST2=amqp-host2
AMQP_CONTAINER_NAME1=amqp-host1-$$
AMQP_CONTAINER_NAME2=amqp-host2-$$
JAVA_CONTAINER_NAME=java-tests-host-$$
JAVA_HOST=java-tests-host
TMP_DIR=
SUDO=
MRG_VERSIONS="3.2.0 3.0.0 0.34 0.36"
MVN_IMAGE_VERSIONS="3-jdk-7 3-jdk-8"
RESULTS_MSG="RESULTS:\n"

trap "stop_and_remove_left_containers && exit 1" SIGINT SIGTERM

function stop_and_remove_left_containers() {
    for container in $(${SUDO} docker ps -a --filter "status=running" --format "{{.Names}}") ; do
        if [ ${container} == ${AMQP_CONTAINER_NAME1} ] || ${container} == ${AMQP_CONTAINER_NAME2} ] || [ ${container} == ${JAVA_CONTAINER_NAME} ] ; then
            echo "Stopping container: ${container}"
            ${SUDO} docker stop ${container}
        fi
    done
    for container in $(${SUDO} docker ps -a --filter "status=exited" --format "{{.Names}}") ; do
        if [ ${container} == ${AMQP_CONTAINER_NAME1} ] || ${container} == ${AMQP_CONTAINER_NAME2} ] || [ ${container} == ${JAVA_CONTAINER_NAME} ] ; then
            echo "Removing container: ${container}"
            ${SUDO} docker rm ${container}
        fi
    done
    ${SUDO} docker network rm ${DOCKER_NETWORK}
}

function print_help() {
    local MY_NAME="$(basename $0 .sh)"
    echo "Usage: ${MY_NAME}.sh [OPTION]..."
    echo ""
    echo " optional"
    echo ""
    echo "  --mrg-version=VERSION    Use specific MRG version (e.g. 3.2.0)"
    echo "  --mvn-version=VERSION    Use specific Maven Docker image version (e.g. 3-jdk-8)"
    echo "  --use-sudo               Execute every docker command under sudo"
    echo "  --help, -h, -?           Print this help and exit"
}

function extract_parameter_value_from_string {
    echo "${1#*=}"
    return 0
}

function parse_cmdline_parameters() {
    for i in "$@" ; do
        case $i in
        --mrg-version=*)
            MRG_VERSIONS=$(extract_parameter_value_from_string $1);;
        --mvn-version=*)
            MVN_IMAGE_VERSIONS=$(extract_parameter_value_from_string $1);;
        --use-sudo)
            SUDO="sudo";;
        --help | -h | -?)
            print_help; exit 0;;
        "");;
        *)
            echo "Unknown parameter '$i'";
            exit 2;;
        esac
        shift
    done
}

function startup() {
    TMP_DIR=$(mktemp -d)
}

function create_network() {
    ${SUDO} docker network create --subnet=192.168.0.0/16 --driver bridge ${DOCKER_NETWORK}
}

# param: $1 - image version
function start_qpidd_container() {
    ${SUDO} docker run -d --net=${DOCKER_NETWORK} --name=${AMQP_CONTAINER_NAME1} --hostname=${AMQP_HOST1} scholzj/java-client-tests:$1
    RESULTS_MSG+="MRG: $1, "

    ${SUDO} docker run -d --net=${DOCKER_NETWORK} --name=${AMQP_CONTAINER_NAME2} --hostname=${AMQP_HOST2} scholzj/java-client-tests:$1
    RESULTS_MSG+="MRG: $1, "
}

# param: $1 - image version
function start_tests_container() {
    local AMQP_CONTAINER_NAME_IP_ADDRESS1=$(${SUDO} docker inspect --format "{{ .NetworkSettings.Networks.${DOCKER_NETWORK}.IPAddress }}" ${AMQP_CONTAINER_NAME1})
    local AMQP_CONTAINER_NAME_IP_ADDRESS2=$(${SUDO} docker inspect --format "{{ .NetworkSettings.Networks.${DOCKER_NETWORK}.IPAddress }}" ${AMQP_CONTAINER_NAME2})
    ${SUDO} docker run -d -it --net=${DOCKER_NETWORK} --add-host ${AMQP_HOST1}:${AMQP_CONTAINER_NAME_IP_ADDRESS1} --add-host ${AMQP_HOST2}:${AMQP_CONTAINER_NAME_IP_ADDRESS2} --name=${JAVA_CONTAINER_NAME} --hostname=${JAVA_HOST} maven:$1 bash
    RESULTS_MSG+="MAVEN IMAGE: $1, "
}

# create and copy maven's settings.xml file with proxy settings into the docker image
function prepare_maven_on_container() {
    cat > ${TMP_DIR}/settings.xml <<EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                  http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <proxies>
    <proxy>
      <active>true</active>
      <protocol>http</protocol>
      <host>webproxy.deutsche-boerse.de</host>
      <port>8080</port>
      <nonProxyHosts>cmqaart.deutsche-boerse.de</nonProxyHosts>
    </proxy>
  </proxies>
</settings>
EOF
    ${SUDO} docker cp ${TMP_DIR}/settings.xml ${JAVA_CONTAINER_NAME}:/root/.m2/
}

# get source code into the docker container
function prepare_sources_on_container() {
    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "cd && git clone https://github.com/scholzj/amqp-transaction-monkey.git amqp-transaction-monkey"
}

# param: $1 - maven pom xml name
function execute_tests() {
    local AMQP_CONTAINER_NAME_IP_ADDRESS1=$(${SUDO} docker inspect --format "{{ .NetworkSettings.Networks.${DOCKER_NETWORK}.IPAddress }}" ${AMQP_CONTAINER_NAME1})
    local AMQP_CONTAINER_NAME_IP_ADDRESS2=$(${SUDO} docker inspect --format "{{ .NetworkSettings.Networks.${DOCKER_NETWORK}.IPAddress }}" ${AMQP_CONTAINER_NAME2})

    ${SUDO} docker exec ${JAVA_CONTAINER_NAME} bash -c "cd && cd amqp-transaction-monkey && mvn package && java -jar target/transaction-monkey-0.1-SNAPSHOT.jar --first-broker-host ${AMQP_CONTAINER_NAME_IP_ADDRESS1} --first-broker-port 5672 --first-broker-username admin --first-broker-password admin --first-broker-queue broadcast.user1.rtgQueue --second-broker-host ${AMQP_CONTAINER_NAME_IP_ADDRESS2} --second-broker-port 5672 --second-broker-username admin --second-broker-password admin --second-broker-queue broadcast.user1.rtgQueue --enable-amqp10-routing --enable-amqp010-routing --enable-xa-amqp010-routing --enable-amqp10-rollback --enable-amqp010-rollback --enable-xa-amqp010-rollback --transaction-count=10000"
    # java -jar target/transaction-monkey-0.1-SNAPSHOT.jar --first-broker-host 192.168.99.100 --first-broker-port 32770 --first-broker-username admin --first-broker-password admin --first-broker-queue broadcast.user1.rtgQueue --second-broker-host 192.168.99.100 --second-broker-port 32768 --second-broker-username admin --second-broker-password admin --second-broker-queue broadcast.user1.rtgQueue --enable-amqp10-routing --enable-amqp010-routing --enable-xa-amqp010-routing --enable-amqp10-rollback --enable-amqp010-rollback --enable-xa-amqp010-rollback --transaction-count=10000
    local RETURN_CODE=$?
    RESULTS_MSG+="POM.XML: $1,"
    if [ ${RETURN_CODE} -eq 0 ] ; then
        RESULTS_MSG+=" RESULT: SUCCESS\n"
    else
        RESULTS_MSG+=" RESULT: FAILURE\n"
    fi
}

function cleanup() {
    rm -rf ${TMP_DIR}
    ${SUDO} docker stop ${AMQP_CONTAINER_NAME1}
    ${SUDO} docker rm ${AMQP_CONTAINER_NAME1}
    ${SUDO} docker stop ${AMQP_CONTAINER_NAME2}
    ${SUDO} docker rm ${AMQP_CONTAINER_NAME2}
    ${SUDO} docker stop ${JAVA_CONTAINER_NAME}
    ${SUDO} docker rm ${JAVA_CONTAINER_NAME}
    ${SUDO} docker network rm ${DOCKER_NETWORK}
}

# params: $1 - QPID container id, $2 - Maven container id, $3 - name of maven pom.xml on test container
function execute_single_run() {
    local QPIDD_CONTAINER_VERSION=$1
    local MAVEN_CONTAINER_VERSION=$2
    local TESTS_POM_XML_NAME=$3
    startup
    create_network && \
    start_qpidd_container ${QPIDD_CONTAINER_VERSION} && \
    start_tests_container ${MAVEN_CONTAINER_VERSION} && \
    prepare_maven_on_container && \
    prepare_sources_on_container && \
    execute_tests ${TESTS_POM_XML_NAME}
    cleanup
}

function execute_all_runs() {
    for mvn_version in ${MVN_IMAGE_VERSIONS} ; do
        for mrg_version in ${MRG_VERSIONS} ; do
            case ${mrg_version} in
              3.*)  execute_single_run ${mrg_version} ${mvn_version} mrg-${mrg_version}.xml ;;
              *) execute_single_run ${mrg_version} ${mvn_version} qpid-${mrg_version}.xml ;;
            esac
        done
    done
}

function print_results() {
    echo -e ${RESULTS_MSG}
}

parse_cmdline_parameters "$@"
execute_all_runs
print_results