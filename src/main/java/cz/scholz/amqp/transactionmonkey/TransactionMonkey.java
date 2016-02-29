package cz.scholz.amqp.transactionmonkey;

import cz.scholz.amqp.transactionmonkey.transactionrouter.TransactionRouter;
import cz.scholz.amqp.transactionmonkey.transactionrouter.TransactionRouterFactory;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by schojak on 31.12.15.
 */
public class TransactionMonkey {
    final static Logger LOG = LoggerFactory.getLogger(TransactionMonkey.class);

    private List<TransactionRouter> tr = new ArrayList<>();
    private TransactionRouterFactory factAtoB;
    private TransactionRouterFactory factBtoA;

    public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
        // TODO: Make rollback timeout/sleep configurable
        // TODO: Crete the initial set of messages

        TransactionMonkey tm = new TransactionMonkey(args);
    }

    public TransactionMonkey(String[] args)
    {


        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(getOptions(), args);

            // Print help
            if (line.hasOption("help")) {
                printHelp();
                return;
            }

            // Feed messages
            if (line.hasOption("feed-messages"))
            {
                feedMessages();
            }

            // Configure logging
            if (line.hasOption("log-level"))
            {
                configureLogging(line.getOptionValue("log-level"));
            }
            else {
                configureLogging("info");
            }

            // Configure factories
            String aHost = line.getOptionValue("first-broker-host");
            String aPort = line.getOptionValue("first-broker-port");
            String aUsername = line.getOptionValue("first-broker-username", null);
            String aPassword = line.getOptionValue("first-broker-password", null);
            String aQueue = line.getOptionValue("first-broker-queue");
            String bHost = line.getOptionValue("second-broker-host");
            String bPort = line.getOptionValue("second-broker-port");
            String bUsername = line.getOptionValue("second-broker-username", null);
            String bPassword = line.getOptionValue("second-broker-password", null);
            String bQueue = line.getOptionValue("second-broker-queue");

            factAtoB = new TransactionRouterFactory(aHost, aPort, aUsername, aPassword, aQueue, bHost, bPort, bUsername, bPassword, bQueue);
            factBtoA = new TransactionRouterFactory(bHost, bPort, bUsername, bPassword, bQueue, aHost, aPort, aUsername, aPassword, aQueue);

            // Create the routers
            try {
                // AMQP 1.0 routers
                if (line.hasOption("enable-amqp10-routing")) {
                    LOG.info("Creating AMQP 1.0 routers");
                    tr.add(factAtoB.createAmqp10Router());
                    tr.add(factBtoA.createAmqp10Router());
                }

                // AMQP 0-10 routers
                if (line.hasOption("enable-amqp010-routing")) {
                    LOG.info("Creating AMQP 0-10 routers");
                    tr.add(factAtoB.createAmqp010Router());
                    tr.add(factBtoA.createAmqp010Router());
                }

                // AMQP 1.0 rollbacks
                if (line.hasOption("enable-amqp10-rollback")) {
                    // TODO: add waiting times support
                    LOG.info("Creating AMQP 1.0 rollbacks");
                    tr.add(factAtoB.createAmqp10Rollback());
                    tr.add(factBtoA.createAmqp10Rollback());
                }

                // AMQP 0-10 rollbacks
                if (line.hasOption("enable-amqp010-rollback")) {
                    // TODO: add waiting times support
                    LOG.info("Creating AMQP 0-10 rollbacks");
                    tr.add(factAtoB.createAmqp010Rollback());
                    tr.add(factBtoA.createAmqp010Rollback());
                }

                // AMQP 0-10 XA routers
                if (line.hasOption("enable-xa-amqp010-routing")) {
                    LOG.info("Creating AMQP 0-10 XA routers");
                    tr.add(factAtoB.createXARouter());
                    tr.add(factBtoA.createXARouter());
                }

                // AMQP 0-10 XA rollbacks
                if (line.hasOption("enable-xa-amqp010-rollback")) {
                    // TODO: add waiting times support
                    LOG.info("Creating AMQP 0-10 rollbacks");
                    tr.add(factAtoB.createXARollback());
                    tr.add(factBtoA.createXARollback());
                }
            }
            catch (JMSException e)
            {
                LOG.error("Failed to create router", e);

                // Stop the routers
                stopAllRouters();

                return;
            }
            catch (NamingException e)
            {
                LOG.error("Failed to create router", e);

                // Stop the routers
                stopAllRouters();

                return;
            }
        }
        catch (ParseException e)
        {
            System.out.println("Error parsing arguments: " + e.getMessage());
            printHelp();
            return;
        }

        // Start the routers
        startAllRouters();

        try {
            Thread.sleep(12*60*60*1000);
        } catch (InterruptedException e) {
            LOG.error("Sleep was interrupted", e);
        }

        // Stop the routers
        stopAllRouters();
    }

    private void startAllRouters()
    {
        LOG.info("Starting all routers");
        for (TransactionRouter router : tr) {
            router.start();
        }
    }

    private void stopAllRouters()
    {
        LOG.info("Stopping all routers");
        for (TransactionRouter router : tr) {
            router.finish();

            try {
                router.join();
            } catch (InterruptedException e) {
                LOG.error("Failed to join the router", e);
            }
        }
    }

    private void configureLogging(String logLevel)
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", logLevel);
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");
    }

    private void feedMessages()
    {
        // TODO: Implement feedMessages()
    }

    private void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setWidth(100);
        formatter.printHelp( "java", getOptions());
    }

    private Options getOptions()
    {
        Options opts = new Options();

        opts.addOption(Option.builder().longOpt("first-broker-host").hasArg().argName("Hostname / IP address").desc("Hostname of the first broker").required().build());
        opts.addOption(Option.builder().longOpt("first-broker-port").hasArg().argName("Port").desc("Port number of the first broker").required().build());
        opts.addOption(Option.builder().longOpt("first-broker-username").hasArg().argName("Username").desc("Username of the first broker").build());
        opts.addOption(Option.builder().longOpt("first-broker-password").hasArg().argName("Password").desc("Password of the first broker").build());
        opts.addOption(Option.builder().longOpt("first-broker-queue").hasArg().argName("Queue name").desc("Name of the queue which should be used on the first broker").required().build());

        opts.addOption(Option.builder().longOpt("second-broker-host").hasArg().argName("Hostname / IP address").desc("Hostname of the second broker").required().build());
        opts.addOption(Option.builder().longOpt("second-broker-port").hasArg().argName("Port").desc("Port number of the second broker").required().build());
        opts.addOption(Option.builder().longOpt("second-broker-user").hasArg().argName("Username").desc("Username of the second broker").build());
        opts.addOption(Option.builder().longOpt("second-broker-password").hasArg().argName("Password").desc("Password of the second broker").build());
        opts.addOption(Option.builder().longOpt("second-broker-queue").hasArg().argName("Queue name").desc("Name of the queue which should be used on the second broker").required().build());

        opts.addOption(Option.builder().longOpt("enable-amqp10-routing").desc("Enable routing using AMQP 1.0 protocol").build());

        opts.addOption(Option.builder().longOpt("enable-amqp10-rollback").desc("Enable rollbacks using AMQP 1.0 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp10-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback").build());

        opts.addOption(Option.builder().longOpt("enable-amqp010-routing").desc("Enable routing using AMQP 0-10 protocol").build());

        opts.addOption(Option.builder().longOpt("enable-amqp010-rollback").desc("Enable rollbacks using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback").build());

        opts.addOption(Option.builder().longOpt("enable-xa-amqp010-routing").desc("Enable XA routing using AMQP 0-10 protocol").build());

        opts.addOption(Option.builder().longOpt("enable-xa-amqp010-rollback").desc("Enable XA rollbacks using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback").build());

        opts.addOption(Option.builder().longOpt("feed-messages").desc("Feed messages").build());

        opts.addOption(Option.builder().longOpt("log-level").hasArg().argName("Log level").desc("Enable routing using AMQP 1.0 protocol").build());

        opts.addOption(Option.builder().longOpt("help").desc("Show this help").build());

        return opts;
    }
}
