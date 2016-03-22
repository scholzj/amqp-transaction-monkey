[![Build Status](https://travis-ci.org/scholzj/amqp-transaction-monkey.svg?branch=master)](https://travis-ci.org/scholzj/amqp-transaction-monkey)  [![Coverage Status](https://coveralls.io/repos/github/scholzj/amqp-transaction-monkey/badge.svg?branch=master)](https://coveralls.io/github/scholzj/amqp-transaction-monkey?branch=master)

# amqp-transaction-monkey

AMQP Transaction Monkey is utitility for testing of transaction handling in AMQP brokers. It is developed and tested against the Apache Qpid C++ broker (http://qpid.apache.org) 

##  Transaction routing

The monkey connects to two different AMQP brokers and routes messages between them. Messages are routed using:
- AMQP 1.0 local transactions
- AMQP 0-10 local transactions
- AMQP 0-10 XA transactions

The transaction routing using each methods can be enabled / disables individually. Each has its own configuration which defines:
- Whether the commit should be immediate or only after some wait time
- Whether routing of next transaction should start immediately or only after a wait time

That allows to configure the monkey with different scenarios.

## Transaction rollbacks

Additionally to routing messages, the monkey can also rollback the transactions. It read a messages from one broker and sends it to the second broker. But instead of committing the transactions it roles them back. The rollback processing supports the same configuration options as the transactions routers.

## Preparing messages

The monkey tool can feed some messages into the queues used for routing so that the routers have something to work on. However, since there is no standardized mechanism how to create queues on different brokers (yet), the queues used for routing will not be created and have to exist.

## Runtime

The amount of time for which the monkey will run can be defined in two ways:
- As a time for which messages should be routed. After this time elapses, the monkey will exit.
- As a number of messages which the routers (rollbacks) should process before the monke finishs. The number of transactions used is a sum of the transactions processed by all routers.

## Configuration

    --first-broker-host <Hostname / IP address>         Hostname of the first broker
    --first-broker-port <Port>                          Port number of the first broker
    --first-broker-username <Username>                  Username of the first broker
    --first-broker-password <Password>                  Password of the first broker
    --first-broker-queue <Queue name>                   Name of the queue which should be used on
                                                        the first broker
    --second-broker-host <Hostname / IP address>        Hostname of the second broker
    --second-broker-port <Port>                         Port number of the second broker
    --second-broker-username <Username>                 Username of the second broker
    --second-broker-password <Password>                 Password of the second broker
    --second-broker-queue <Queue name>                  Name of the queue which should be used on
                                                        the second broker
    --enable-amqp10-routing                             Enable routing using AMQP 1.0 protocol
    --amqp10-routing-wait-time <Time (ms)>              Set wait time before commit (default 0ms)
    --amqp10-routing-transaction-gap <Time (ms)>        Set time gap before starting new transaction
                                                        (default 0ms)
    --enable-amqp10-rollback                            Enable rollbacks using AMQP 1.0 protocol
    --amqp10-rollback-wait-time <Time (ms)>             Set wait time before rollback (default 0ms)
    --amqp10-rollback-transaction-gap <Time (ms)>       Set time gap before starting new transaction
                                                        (default 0ms)
    --enable-amqp010-routing                            Enable routing using AMQP 0-10 protocol
    --amqp010-routing-wait-time <Time (ms)>             Set wait time before commit (default 0ms)
    --amqp010-routing-transaction-gap <Time (ms)>       Set time gap before starting new transaction
                                                        (default 0ms)
    --enable-amqp010-rollback                           Enable rollbacks using AMQP 0-10 protocol
    --amqp010-rollback-wait-time <Time (ms)>            Set wait time before rollback (default 0ms)
    --amqp010-rollback-transaction-gap <Time (ms)>      Set time gap before starting new transaction
                                                        (default 0ms)
    --enable-xa-amqp010-routing                         Enable XA routing using AMQP 0-10 protocol
    --amqp010-xa-routing-wait-time <Time (ms)>          Set wait time before commit (default 0ms)
    --amqp010-xa-routing-transaction-gap <Time (ms)>    Set time gap before starting new transaction
                                                        (default 0ms)
    --enable-xa-amqp010-rollback                        Enable XA rollbacks using AMQP 0-10 protocol
    --amqp010-xa-rollback-wait-time <Time (ms)>         Set wait time before rollback (default 0ms)
    --amqp010-xa-rollback-transaction-gap <Time (ms)>   Set time gap before starting new transaction
                                                        (default 0ms)
    --transaction-count <number of messages>            Number of transactions to process
    --wait-time <time (ms)>                             How long should the routing proceed (default
                                                        1 minute)
    --feed-messages                                     Feed messages
    --feed-messages-count <number of messages>          Number of messages to feed into each broker
                                                        (Default: 1000 msg)
    --feed-messages-size <message size (bytes)>         Message size (Default: 1024 bytes)
    --log-level <Log level>                             Enable routing using AMQP 1.0 protocol
                                                        (default INFO)
    --help                                              Show this help

##  TODO

- Add support for random timeouts
- Add support for processing larger message blocks as part of one transactions
