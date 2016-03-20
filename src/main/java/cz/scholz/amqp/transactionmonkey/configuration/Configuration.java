package cz.scholz.amqp.transactionmonkey.configuration;

import org.apache.commons.cli.*;

/**
 * Created by jakub on 06.03.16.
 */
public class Configuration {
    private String aHost;
    private String aPort;
    private String aUsername;
    private String aPassword;
    private String aQueue;

    private String bHost;
    private String bPort;
    private String bUsername;
    private String bPassword;
    private String bQueue;

    private boolean feedMessages = false;
    private int feedMessagesCount = 1000;
    private int feedMessagesSize = 1024;

    private String logLevel = "info";

    private int waitTime = 60*1000;
    private int transactionCount = 0;

    private boolean amqp10Router;
    private int amqp10RouterWaitTime = 0;
    private int amqp10RouterTransactionGap = 0;

    private boolean amqp10Rollback;
    private int amqp10RollbackWaitTime = 0;
    private int amqp10RollbackTransactionGap = 0;

    private boolean amqp010Router;
    private int amqp010RouterWaitTime = 0;
    private int amqp010RouterTransactionGap = 0;

    private boolean amqp010Rollback;
    private int amqp010RollbackWaitTime = 0;
    private int amqp010RollbackTransactionGap = 0;

    private boolean xaRouter;
    private int xaRouterWaitTime = 0;
    private int xaRouterTransactionGap = 0;

    private boolean xaRollback;
    private int xaRollbackWaitTime = 0;
    private int xaRollbackTransactionGap = 0;

    public Configuration() {
        // Create empty configuration
    }

    public Configuration(String[] args) throws ConfigurationException {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(getConfigurationOptions(), args);

            // Configure log level
            if (line.hasOption("log-level"))
            {
                setLogLevel(line.getOptionValue("log-level"));
            }

            // Configure sleep time
            if (line.hasOption("wait-time"))
            {
                setWaitTime(line.getOptionValue("wait-time"));
            }

            // Configure transaction count
            if (line.hasOption("transaction-count"))
            {
                String option = line.getOptionValue("transaction-count");

                try {
                    transactionCount = Integer.parseInt(option);
                }
                catch (NumberFormatException e)
                {
                    throw new ConfigurationException("--transaction-count option doesn't contain valid integer", e);
                }
            }

            // Collect broker details
            setaHost(line.getOptionValue("first-broker-host"));
            setaPort(line.getOptionValue("first-broker-port"));
            setaUsername(line.getOptionValue("first-broker-username", null));
            setaPassword(line.getOptionValue("first-broker-password", null));
            setaQueue(line.getOptionValue("first-broker-queue"));
            setbHost(line.getOptionValue("second-broker-host"));
            setbPort(line.getOptionValue("second-broker-port"));
            setbUsername(line.getOptionValue("second-broker-username", null));
            setbPassword(line.getOptionValue("second-broker-password", null));
            setbQueue(line.getOptionValue("second-broker-queue"));

            // Feed messages
            if (line.hasOption("feed-messages"))
            {
                feedMessages = true;

                if (line.hasOption("feed-messages-count"))
                {
                    String option = line.getOptionValue("feed-messages-count");

                    try {
                        feedMessagesCount = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--feed-messages-count option doesn't contain valid integer", e);
                    }
                }

                if (line.hasOption("feed-messages-size"))
                {
                    String option = line.getOptionValue("feed-messages-size");

                    try {
                        feedMessagesSize = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--feed-messages-size option doesn't contain valid integer", e);
                    }
                }
            }

            // Create the routers
            // AMQP 1.0 routers
            if (line.hasOption("enable-amqp10-routing")) {
                amqp10Router = true;

                // Wait time
                if (line.hasOption("amqp10-routing-wait-time"))
                {
                    String option = line.getOptionValue("amqp10-routing-wait-time");

                    try {
                        amqp10RouterWaitTime = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp10-routing-wait-time option doesn't contain valid integer", e);
                    }
                }

                // Gap time
                if (line.hasOption("amqp10-routing-transaction-gap"))
                {
                    String option = line.getOptionValue("amqp10-routing-transaction-gap");

                    try {
                        amqp10RouterTransactionGap = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp10-routing-transaction-gap option doesn't contain valid integer", e);
                    }
                }
            }

            // AMQP 1.0 rollback
            if (line.hasOption("enable-amqp10-rollback")) {
                amqp10Rollback = true;

                // Wait time
                if (line.hasOption("amqp10-rollback-wait-time"))
                {
                    String option = line.getOptionValue("amqp10-rollback-wait-time");

                    try {
                        amqp10RollbackWaitTime = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp10-rollback-wait-time option doesn't contain valid integer", e);
                    }
                }

                // Gap time
                if (line.hasOption("amqp10-rollback-transaction-gap"))
                {
                    String option = line.getOptionValue("amqp10-rollback-transaction-gap");

                    try {
                        amqp10RollbackTransactionGap = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp10-rollback-transaction-gap option doesn't contain valid integer", e);
                    }
                }
            }

            // AMQP 0-10 routers
            if (line.hasOption("enable-amqp010-routing")) {
                amqp010Router = true;

                // Wait time
                if (line.hasOption("amqp010-routing-wait-time"))
                {
                    String option = line.getOptionValue("amqp010-routing-wait-time");

                    try {
                        amqp010RouterWaitTime = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-routing-wait-time option doesn't contain valid integer", e);
                    }
                }

                // Gap time
                if (line.hasOption("amqp010-routing-transaction-gap"))
                {
                    String option = line.getOptionValue("amqp010-routing-transaction-gap");

                    try {
                        amqp010RouterTransactionGap = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-routing-transaction-gap option doesn't contain valid integer", e);
                    }
                }
            }

            // AMQP 0-10 rollback
            if (line.hasOption("enable-amqp010-rollback")) {
                amqp010Rollback = true;

                // Wait time
                if (line.hasOption("amqp010-rollback-wait-time"))
                {
                    String option = line.getOptionValue("amqp010-rollback-wait-time");

                    try {
                        amqp010RollbackWaitTime = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-rollback-wait-time option doesn't contain valid integer", e);
                    }
                }

                // Gap time
                if (line.hasOption("amqp010-rollback-transaction-gap"))
                {
                    String option = line.getOptionValue("amqp010-rollback-transaction-gap");

                    try {
                        amqp010RollbackTransactionGap = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-rollback-transaction-gap option doesn't contain valid integer", e);
                    }
                }
            }

            // AMQP 0-10 XA router
            if (line.hasOption("enable-amqp010-xa-routing")) {
                xaRouter = true;

                // Wait time
                if (line.hasOption("amqp010-xa-routing-wait-time"))
                {
                    String option = line.getOptionValue("amqp010-xa-routing-wait-time");

                    try {
                        xaRouterWaitTime = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-xa-routing-wait-time option doesn't contain valid integer", e);
                    }
                }

                // Gap time
                if (line.hasOption("amqp010-xa-routing-transaction-gap"))
                {
                    String option = line.getOptionValue("amqp010-xa-routing-transaction-gap");

                    try {
                        xaRouterTransactionGap = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-xa-routing-transaction-gap option doesn't contain valid integer", e);
                    }
                }
            }

            // AMQP 0-10 XA rollback
            if (line.hasOption("enable-amqp010-xa-rollback")) {
                xaRollback = true;

                // Wait time
                if (line.hasOption("amqp010-xa-rollback-wait-time"))
                {
                    String option = line.getOptionValue("amqp010-xa-rollback-wait-time");

                    try {
                        xaRollbackWaitTime = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-xa-rollback-wait-time option doesn't contain valid integer", e);
                    }
                }

                // Gap time
                if (line.hasOption("amqp010-xa-rollback-transaction-gap"))
                {
                    String option = line.getOptionValue("amqp010-xa-rollback-transaction-gap");

                    try {
                        xaRollbackTransactionGap = Integer.parseInt(option);
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ConfigurationException("--amqp010-xa-rollback-transaction-gap option doesn't contain valid integer", e);
                    }
                }
            }
        }
        catch (ParseException e)
        {
            System.out.println("Error parsing arguments: " + e.getMessage());
            printHelp();
            throw new ConfigurationException("Failed to parse command line arguments", e);
        }
    }

    public static Options getConfigurationOptions()
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

        opts.addOption(Option.builder().longOpt("enable-amqp010-xa-routing").desc("Enable XA routing using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-routing-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before commit (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-routing-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        opts.addOption(Option.builder().longOpt("enable-amqp010-xa-rollback").desc("Enable XA rollbacks using AMQP 0-10 protocol").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-rollback-wait-time").hasArg().argName("Time (ms)").desc("Set wait time before rollback (default 0ms)").build());
        opts.addOption(Option.builder().longOpt("amqp010-xa-rollback-transaction-gap").hasArg().argName("Time (ms)").desc("Set time gap before starting new transaction (default 0ms)").build());

        OptionGroup timeToRun = new OptionGroup();
        timeToRun.addOption(Option.builder().longOpt("wait-time").hasArg().argName("time (ms)").desc("How long should the routing proceed (default 1 minute)").build());
        timeToRun.addOption(Option.builder().longOpt("transaction-count").hasArg().argName("number of messages").desc("Number of transactions to process").build());
        opts.addOptionGroup(timeToRun);

        opts.addOption(Option.builder().longOpt("feed-messages").desc("Feed messages").build());
        opts.addOption(Option.builder().longOpt("feed-messages-count").hasArg().argName("number of messages").desc("Number of messages to feed into each broker (Default: 1000 msg)").build());
        opts.addOption(Option.builder().longOpt("feed-messages-size").hasArg().argName("message size (bytes)").desc("Message size (Default: 1024 bytes)").build());

        opts.addOption(Option.builder().longOpt("log-level").hasArg().argName("Log level").desc("Enable routing using AMQP 1.0 protocol (default INFO)").build());

        opts.addOption(Option.builder().longOpt("help").desc("Show this help").build());

        return opts;
    }

    public static void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setWidth(100);
        formatter.printHelp( "java", getConfigurationOptions());
    }

    public String getaHost() {
        return aHost;
    }

    public void setaHost(String aHost) {
        this.aHost = aHost;
    }

    public String getaPort() {
        return aPort;
    }

    public void setaPort(String aPort) throws ConfigurationException {
        try {
            validatePort(aPort);
        }
        catch (ConfigurationException e)
        {
            throw new ConfigurationException("--first-broker-port is not valid", e);
        }

        this.aPort = aPort;
    }

    private void validatePort(String port) throws ConfigurationException {
        try {
            int iPort = Integer.parseInt(port);

            if (iPort < 1 || iPort > 65535)
            {
                throw new ConfigurationException("Port doesn't contain valid port number between 1 and 65535 - port contains " + port);
            }
        }
        catch (NumberFormatException e)
        {
            throw new ConfigurationException("Port option doesn't contain valid integer", e);
        }
    }

    public String getaUsername() {
        return aUsername;
    }

    public void setaUsername(String aUsername) {
        this.aUsername = aUsername;
    }

    public String getaPassword() {
        return aPassword;
    }

    public void setaPassword(String aPassword) {
        this.aPassword = aPassword;
    }

    public String getaQueue() {
        return aQueue;
    }

    public void setaQueue(String aQueue) {
        this.aQueue = aQueue;
    }

    public String getbHost() {
        return bHost;
    }

    public void setbHost(String bHost) {
        this.bHost = bHost;
    }

    public String getbPort() {
        return bPort;
    }

    public void setbPort(String bPort) throws ConfigurationException {
        try {
            validatePort(bPort);
        }
        catch (ConfigurationException e)
        {
            throw new ConfigurationException("--second-broker-port is not valid", e);
        }

        this.bPort = bPort;
    }

    public String getbUsername() {
        return bUsername;
    }

    public void setbUsername(String bUsername) {
        this.bUsername = bUsername;
    }

    public String getbPassword() {
        return bPassword;
    }

    public void setbPassword(String bPassword) {
        this.bPassword = bPassword;
    }

    public String getbQueue() {
        return bQueue;
    }

    public void setbQueue(String bQueue) {
        this.bQueue = bQueue;
    }

    public boolean isFeedMessages() {
        return feedMessages;
    }

    public void setFeedMessages(boolean feedMessages) {
        this.feedMessages = feedMessages;
    }

    public int getFeedMessagesCount() {
        return feedMessagesCount;
    }

    public void setFeedMessagesCount(int feedMessagesCount) {
        this.feedMessagesCount = feedMessagesCount;
    }

    public int getFeedMessagesSize() {
        return feedMessagesSize;
    }

    public void setFeedMessagesSize(int feedMessagesSize) {
        this.feedMessagesSize = feedMessagesSize;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) throws ConfigurationException {
        try {
            this.waitTime = Integer.parseInt(waitTime);
        }
        catch (NumberFormatException e)
        {
            throw new ConfigurationException("--wait-time option doesn't contain valid integer", e);
        }
    }

    public void setWaitTime(int waitTime) throws ConfigurationException {
        this.waitTime = waitTime;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public boolean isAmqp10Router() {
        return amqp10Router;
    }

    public void setAmqp10Router(boolean amqp10Router) {
        this.amqp10Router = amqp10Router;
    }

    public int getAmqp10RouterWaitTime() {
        return amqp10RouterWaitTime;
    }

    public void setAmqp10RouterWaitTime(int amqp10RouterWaitTime) {
        this.amqp10RouterWaitTime = amqp10RouterWaitTime;
    }

    public int getAmqp10RouterTransactionGap() {
        return amqp10RouterTransactionGap;
    }

    public void setAmqp10RouterTransactionGap(int amqp10RouterTransactionGap) {
        this.amqp10RouterTransactionGap = amqp10RouterTransactionGap;
    }

    public boolean isAmqp10Rollback() {
        return amqp10Rollback;
    }

    public void setAmqp10Rollback(boolean amqp10Rollback) {
        this.amqp10Rollback = amqp10Rollback;
    }

    public int getAmqp10RollbackWaitTime() {
        return amqp10RollbackWaitTime;
    }

    public void setAmqp10RollbackWaitTime(int amqp10RollbackWaitTime) {
        this.amqp10RollbackWaitTime = amqp10RollbackWaitTime;
    }

    public int getAmqp10RollbackTransactionGap() {
        return amqp10RollbackTransactionGap;
    }

    public void setAmqp10RollbackTransactionGap(int amqp10RollbackTransactionGap) {
        this.amqp10RollbackTransactionGap = amqp10RollbackTransactionGap;
    }

    public boolean isAmqp010Router() {
        return amqp010Router;
    }

    public void setAmqp010Router(boolean amqp010Router) {
        this.amqp010Router = amqp010Router;
    }

    public int getAmqp010RouterWaitTime() {
        return amqp010RouterWaitTime;
    }

    public void setAmqp010RouterWaitTime(int amqp010RouterWaitTime) {
        this.amqp010RouterWaitTime = amqp010RouterWaitTime;
    }

    public int getAmqp010RouterTransactionGap() {
        return amqp010RouterTransactionGap;
    }

    public void setAmqp010RouterTransactionGap(int amqp010RouterTransactionGap) {
        this.amqp010RouterTransactionGap = amqp010RouterTransactionGap;
    }

    public boolean isAmqp010Rollback() {
        return amqp010Rollback;
    }

    public void setAmqp010Rollback(boolean amqp010Rollback) {
        this.amqp010Rollback = amqp010Rollback;
    }

    public int getAmqp010RollbackWaitTime() {
        return amqp010RollbackWaitTime;
    }

    public void setAmqp010RollbackWaitTime(int amqp010RollbackWaitTime) {
        this.amqp010RollbackWaitTime = amqp010RollbackWaitTime;
    }

    public int getAmqp010RollbackTransactionGap() {
        return amqp010RollbackTransactionGap;
    }

    public void setAmqp010RollbackTransactionGap(int amqp010RollbackTransactionGap) {
        this.amqp010RollbackTransactionGap = amqp010RollbackTransactionGap;
    }

    public boolean isXaRouter() {
        return xaRouter;
    }

    public void setXaRouter(boolean xaRouter) {
        this.xaRouter = xaRouter;
    }

    public int getXaRouterWaitTime() {
        return xaRouterWaitTime;
    }

    public void setXaRouterWaitTime(int xaRouterWaitTime) {
        this.xaRouterWaitTime = xaRouterWaitTime;
    }

    public int getXaRouterTransactionGap() {
        return xaRouterTransactionGap;
    }

    public void setXaRouterTransactionGap(int xaRouterTransactionGap) {
        this.xaRouterTransactionGap = xaRouterTransactionGap;
    }

    public boolean isXaRollback() {
        return xaRollback;
    }

    public void setXaRollback(boolean xaRollback) {
        this.xaRollback = xaRollback;
    }

    public int getXaRollbackWaitTime() {
        return xaRollbackWaitTime;
    }

    public void setXaRollbackWaitTime(int xaRollbackWaitTime) {
        this.xaRollbackWaitTime = xaRollbackWaitTime;
    }

    public int getXaRollbackTransactionGap() {
        return xaRollbackTransactionGap;
    }

    public void setXaRollbackTransactionGap(int xaRollbackTransactionGap) {
        this.xaRollbackTransactionGap = xaRollbackTransactionGap;
    }
}
