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
    static Logger LOG;

    private List<TransactionRouter> tr = new ArrayList<>();
    private TransactionRouterFactory factAtoB;
    private TransactionRouterFactory factBtoA;

    private int sleepTime = 0;
    private int transactionCount = 0;

    public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
        // TODO: Crete the initial set of messages

        TransactionMonkey tm = new TransactionMonkey(args);
    }

    public TransactionMonkey(String[] args)
    {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(getOptions(), args);

            // Configure logging
            // Should be done first to support logging in other methods
            if (line.hasOption("log-level"))
            {
                configureLogging(line.getOptionValue("log-level"));
            }
            else {
                configureLogging("info");
            }

            // Print help
            if (line.hasOption("help")) {
                printHelp();
                return;
            }

            // Configure sleep time
            if (line.hasOption("wait-time"))
            {
                String waitTime = line.getOptionValue("wait-time");

                try {
                    sleepTime = Integer.parseInt(waitTime);
                    LOG.info("Waiting time set to " + sleepTime + " ms");
                }
                catch (NumberFormatException e)
                {
                    LOG.error("--wait-time option doesn't contain valid integer", e);
                    System.exit(1);
                }
            }

            // Configure transaction count
            if (line.hasOption("transaction-count"))
            {
                String transCount = line.getOptionValue("transaction-count");

                try {
                    transactionCount = Integer.parseInt(transCount);
                    LOG.info("Transaction count set to " + transactionCount + " ms");
                }
                catch (NumberFormatException e)
                {
                    LOG.error("--transaction-count option doesn't contain valid integer", e);
                    System.exit(1);
                }
            }

            // Collect broker details
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

            // Feed messages
            if (line.hasOption("feed-messages"))
            {
                feedMessages();
            }

            // Configure factories
            factAtoB = new TransactionRouterFactory(aHost, aPort, aUsername, aPassword, aQueue, bHost, bPort, bUsername, bPassword, bQueue);
            factBtoA = new TransactionRouterFactory(bHost, bPort, bUsername, bPassword, bQueue, aHost, aPort, aUsername, aPassword, aQueue);

            // Create the routers
            try {
                // AMQP 1.0 routers
                if (line.hasOption("enable-amqp10-routing")) {
                    LOG.info("Creating AMQP 1.0 routers");

                    int gapTime = 0;
                    int waitTime = 0;

                    // Wait time
                    if (line.hasOption("amqp10-routing-wait-time"))
                    {
                        String waitTimeOption = line.getOptionValue("amqp10-routing-wait-time");

                        try {
                            waitTime = Integer.parseInt(waitTimeOption);
                            LOG.info("--amqp10-routing-wait-time set to " + waitTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp10-routing-wait-time option doesn't contain valid integer", e);
                        }
                    }

                    // Gap time
                    if (line.hasOption("amqp10-routing-transaction-gap"))
                    {
                        String gapTimeOption = line.getOptionValue("amqp10-routing-transaction-gap");

                        try {
                            gapTime = Integer.parseInt(gapTimeOption);
                            LOG.info("--amqp10-routing-transaction-gap set to " + gapTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp10-routing-transaction-gap option doesn't contain valid integer", e);
                        }
                    }

                    tr.add(factAtoB.createAmqp10Router(gapTime, waitTime));
                    tr.add(factBtoA.createAmqp10Router(gapTime, waitTime));
                }

                // AMQP 0-10 routers
                if (line.hasOption("enable-amqp010-routing")) {
                    LOG.info("Creating AMQP 0-10 routers");

                    int gapTime = 0;
                    int waitTime = 0;

                    // Wait time
                    if (line.hasOption("amqp010-routing-wait-time"))
                    {
                        String waitTimeOption = line.getOptionValue("amqp010-routing-wait-time");

                        try {
                            waitTime = Integer.parseInt(waitTimeOption);
                            LOG.info("--amqp010-routing-wait-time set to " + waitTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-routing-wait-time option doesn't contain valid integer", e);
                        }
                    }

                    // Gap time
                    if (line.hasOption("amqp010-routing-transaction-gap"))
                    {
                        String gapTimeOption = line.getOptionValue("amqp010-routing-transaction-gap");

                        try {
                            gapTime = Integer.parseInt(gapTimeOption);
                            LOG.info("--amqp010-routing-transaction-gap set to " + gapTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-routing-transaction-gap option doesn't contain valid integer", e);
                        }
                    }

                    tr.add(factAtoB.createAmqp010Router(gapTime, waitTime));
                    tr.add(factBtoA.createAmqp010Router(gapTime, waitTime));
                }

                // AMQP 1.0 rollbacks
                if (line.hasOption("enable-amqp10-rollback")) {
                    LOG.info("Creating AMQP 1.0 rollbacks");

                    int gapTime = 0;
                    int waitTime = 0;

                    // Wait time
                    if (line.hasOption("amqp10-rollback-wait-time"))
                    {
                        String waitTimeOption = line.getOptionValue("amqp10-rollback-wait-time");

                        try {
                            waitTime = Integer.parseInt(waitTimeOption);
                            LOG.info("--amqp10-rollback-wait-time set to " + waitTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp10-rollback-wait-time option doesn't contain valid integer", e);
                        }
                    }

                    // Gap time
                    if (line.hasOption("amqp10-rollback-transaction-gap"))
                    {
                        String gapTimeOption = line.getOptionValue("amqp10-rollback-transaction-gap");

                        try {
                            gapTime = Integer.parseInt(gapTimeOption);
                            LOG.info("--amqp10-rollback-transaction-gap set to " + gapTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp10-rollback-transaction-gap option doesn't contain valid integer", e);
                        }
                    }

                    tr.add(factAtoB.createAmqp10Rollback(gapTime, waitTime));
                    tr.add(factBtoA.createAmqp10Rollback(gapTime, waitTime));
                }

                // AMQP 0-10 rollbacks
                if (line.hasOption("enable-amqp010-rollback")) {
                    LOG.info("Creating AMQP 0-10 rollbacks");

                    int gapTime = 0;
                    int waitTime = 0;

                    // Wait time
                    if (line.hasOption("amqp010-rollback-wait-time"))
                    {
                        String waitTimeOption = line.getOptionValue("amqp010-rollback-wait-time");

                        try {
                            waitTime = Integer.parseInt(waitTimeOption);
                            LOG.info("--amqp010-rollback-wait-time set to " + waitTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-rollback-wait-time option doesn't contain valid integer", e);
                        }
                    }

                    // Gap time
                    if (line.hasOption("amqp010-rollback-transaction-gap"))
                    {
                        String gapTimeOption = line.getOptionValue("amqp010-rollback-transaction-gap");

                        try {
                            gapTime = Integer.parseInt(gapTimeOption);
                            LOG.info("--amqp010-rollback-transaction-gap set to " + gapTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-rollback-transaction-gap option doesn't contain valid integer", e);
                        }
                    }

                    tr.add(factAtoB.createAmqp010Rollback(gapTime, waitTime));
                    tr.add(factBtoA.createAmqp010Rollback(gapTime, waitTime));
                }

                // AMQP 0-10 XA routers
                if (line.hasOption("enable-xa-amqp010-routing")) {
                    LOG.info("Creating AMQP 0-10 XA routers");

                    int gapTime = 0;
                    int waitTime = 0;

                    // Wait time
                    if (line.hasOption("amqp010-xa-routing-wait-time"))
                    {
                        String waitTimeOption = line.getOptionValue("amqp010-xa-routing-wait-time");

                        try {
                            waitTime = Integer.parseInt(waitTimeOption);
                            LOG.info("--amqp010-xa-routing-wait-time set to " + waitTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-xa-routing-wait-time option doesn't contain valid integer", e);
                        }
                    }

                    // Gap time
                    if (line.hasOption("amqp010-xa-routing-transaction-gap"))
                    {
                        String gapTimeOption = line.getOptionValue("amqp010-xa-routing-transaction-gap");

                        try {
                            gapTime = Integer.parseInt(gapTimeOption);
                            LOG.info("--amqp010-xa-routing-transaction-gap set to " + gapTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-xa-routing-transaction-gap option doesn't contain valid integer", e);
                        }
                    }

                    tr.add(factAtoB.createXARouter(gapTime, waitTime));
                    tr.add(factBtoA.createXARouter(gapTime, waitTime));
                }

                // AMQP 0-10 XA rollbacks
                if (line.hasOption("enable-xa-amqp010-rollback")) {
                    LOG.info("Creating AMQP 0-10 rollbacks");

                    int gapTime = 0;
                    int waitTime = 0;

                    // Wait time
                    if (line.hasOption("amqp010-xa-rollback-wait-time"))
                    {
                        String waitTimeOption = line.getOptionValue("amqp010-xa-rollback-wait-time");

                        try {
                            waitTime = Integer.parseInt(waitTimeOption);
                            LOG.info("--amqp010-xa-rollback-wait-time set to " + waitTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-xa-rollback-wait-time option doesn't contain valid integer", e);
                        }
                    }

                    // Gap time
                    if (line.hasOption("amqp010-xa-rollback-transaction-gap"))
                    {
                        String gapTimeOption = line.getOptionValue("amqp010-xa-rollback-transaction-gap");

                        try {
                            gapTime = Integer.parseInt(gapTimeOption);
                            LOG.info("--amqp010-xa-rollback-transaction-gap set to " + gapTime + " ms");
                        }
                        catch (NumberFormatException e)
                        {
                            LOG.warn("--amqp010-xa-rollback-transaction-gap option doesn't contain valid integer", e);
                        }
                    }

                    tr.add(factAtoB.createXARollback(gapTime, waitTime));
                    tr.add(factBtoA.createXARollback(gapTime, waitTime));
                }
            }
            catch (JMSException | NamingException e)
            {
                LOG.error("Failed to create router", e);

                // Stop the routers
                stopAllRouters();

                System.exit(1);
            }


        }
        catch (ParseException e)
        {
            System.out.println("Error parsing arguments: " + e.getMessage());
            printHelp();
            System.exit(1);
        }

        // Start the routers
        startAllRouters();

        if (transactionCount > 0)
        {
            LOG.info("Waiting for " + transactionCount + " transactions before exit");
            int totalTransactions = 0;

            while (transactionCount > totalTransactions) {
                try {
                    Thread.sleep(1000);

                    totalTransactions = 0;

                    for (TransactionRouter router : tr) {
                        totalTransactions += router.getMessageCount();
                    }

                    LOG.trace("Routers processed already " + totalTransactions + " transactions");
                } catch (InterruptedException e) {
                    LOG.error("Sleep was interrupted", e);
                }
            }
        }
        else
        {
            if (sleepTime == 0)
            {
                // Default sleep time is 1 minute
                sleepTime = 60*1000;
            }

            try {
                LOG.info("Waiting for " + sleepTime/1000 + " seconds before exit");
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOG.error("Sleep was interrupted", e);
            }
        }



        // Stop the routers
        stopAllRouters();
    }

    private void startAllRouters()
    {
        LOG.info("Starting all routers");
        for (TransactionRouter router : tr) {
            LOG.info("Starting router", router);
            router.start();
        }
    }

    private void stopAllRouters()
    {
        LOG.info("Stopping all routers");
        for (TransactionRouter router : tr) {
            if (router.isAlive()) {
                LOG.info("Stopping router", router);
                router.finish();

                try {
                    router.join();
                } catch (InterruptedException e) {
                    LOG.error("Failed to join the router", e);
                }
            }
            else
            {
                LOG.warn("Router is not alive", router);
            }
        }
        LOG.info("All routers are stopped");
    }

    private void configureLogging(String logLevel)
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", logLevel);
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");
        LOG = LoggerFactory.getLogger(TransactionMonkey.class);
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
        opts.addOption(Option.builder().longOpt("second-broker-username").hasArg().argName("Username").desc("Username of the second broker").build());
        opts.addOption(Option.builder().longOpt("second-broker-password").hasArg().argName("Password").desc("Password of the second broker").build());
        opts.addOption(Option.builder().longOpt("second-broker-queue").hasArg().argName("Queue name").desc("Name of the queue which should be used on the second broker").required().build());

        opts.addOption(Option.builder().longOpt("enable-amqp10-routing").desc("Enable routing using AMQP 1.0 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp10-routing-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before commit (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp10-routing-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        opts.addOption(Option.builder().longOpt("enable-amqp10-rollback").desc("Enable rollbacks using AMQP 1.0 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp10-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp10-rollback-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        opts.addOption(Option.builder().longOpt("enable-amqp010-routing").desc("Enable routing using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-routing-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before commit (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp010-routing-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        opts.addOption(Option.builder().longOpt("enable-amqp010-rollback").desc("Enable rollbacks using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp010-rollback-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        opts.addOption(Option.builder().longOpt("enable-xa-amqp010-routing").desc("Enable XA routing using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-routing-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before commit (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-routing-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        opts.addOption(Option.builder().longOpt("enable-xa-amqp010-rollback").desc("Enable XA rollbacks using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-rollback-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        OptionGroup timeToRun = new OptionGroup();
        timeToRun.addOption(Option.builder().longOpt("wait-time").hasArg().argName("time (ms)").desc("How long should the routing proceed (default 1 minute)").build());
        timeToRun.addOption(Option.builder().longOpt("transaction-count").hasArg().argName("number of messages").desc("Number of transactions to process").build());
        opts.addOptionGroup(timeToRun);

        opts.addOption(Option.builder().longOpt("feed-messages").desc("Feed messages").build());

        opts.addOption(Option.builder().longOpt("log-level").hasArg().argName("Log level").desc("Enable routing using AMQP 1.0 protocol (default INFO)").build());

        opts.addOption(Option.builder().longOpt("help").desc("Show this help").build());

        return opts;
    }
}
