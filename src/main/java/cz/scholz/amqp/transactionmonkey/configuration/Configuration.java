package cz.scholz.amqp.transactionmonkey.configuration;

import org.apache.commons.cli.*;

/**
 * Created by jakub on 06.03.16.
 */
public class Configuration {
    // Defaults
    private final int DEFAULT_WAIT_TIME = 60*1000;
    private final String DEFAULT_LOG_LEVEL = "info";
    private final int DEFAULT_TRANSACTION_COUNT = 0;
    private final int DEFAULT_FEED_MESSAGES_COUNT = 1000;
    private final int DEFAULT_FEED_MESSAGES_SIZE = 1024;

    // Values
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
    private int feedMessagesCount = DEFAULT_FEED_MESSAGES_COUNT;
    private int feedMessagesSize = DEFAULT_FEED_MESSAGES_SIZE;

    private String logLevel = DEFAULT_LOG_LEVEL;

    private int waitTime = DEFAULT_WAIT_TIME;
    private int transactionCount = DEFAULT_TRANSACTION_COUNT;

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

            // Log level
            setLogLevel(line.getOptionValue("log-level", null));

            // Wait time
            setWaitTime(processIntCliOption(line, "wait-time", DEFAULT_WAIT_TIME));

            // Transaction time
            setTransactionCount(processIntCliOption(line, "transaction-count", DEFAULT_TRANSACTION_COUNT));

            // Feed messages
            setFeedMessages(line.hasOption("feed-messages"));
            setFeedMessagesCount(processIntCliOption(line, "feed-messages-count", DEFAULT_FEED_MESSAGES_COUNT));
            setFeedMessagesSize(processIntCliOption(line, "feed-messages-size", DEFAULT_FEED_MESSAGES_SIZE));

            // AMQP 1.0 routers
            setAmqp10Router(line.hasOption("enable-amqp10-routing"));
            setAmqp10RouterTransactionGap(processIntCliOption(line, "amqp10-routing-transaction-gap", 0));
            setAmqp10RouterWaitTime(processIntCliOption(line, "amqp10-routing-wait-time", 0));

            // AMQP 1.0 rollback
            setAmqp10Rollback(line.hasOption("enable-amqp10-routing"));
            setAmqp10RollbackTransactionGap(processIntCliOption(line, "amqp10-rollback-transaction-gap", 0));
            setAmqp10RollbackWaitTime(processIntCliOption(line, "amqp10-rollback-wait-time", 0));

            // AMQP 0-10 routers
            setAmqp010Router(line.hasOption("enable-amqp010-routing"));
            setAmqp010RouterTransactionGap(processIntCliOption(line, "amqp010-routing-transaction-gap", 0));
            setAmqp010RouterWaitTime(processIntCliOption(line, "amqp010-routing-wait-time", 0));

            // AMQP 0-10 rollback
            setAmqp010Rollback(line.hasOption("enable-amqp010-routing"));
            setAmqp010RollbackTransactionGap(processIntCliOption(line, "amqp010-rollback-transaction-gap", 0));
            setAmqp010RollbackWaitTime(processIntCliOption(line, "amqp010-rollback-wait-time", 0));

            // AMQP 0-10 XA routers
            setXaRouter(line.hasOption("enable-amqp010-xa-routing"));
            setXaRouterTransactionGap(processIntCliOption(line, "amqp010-xa-routing-transaction-gap", 0));
            setXaRouterWaitTime(processIntCliOption(line, "amqp010-xa-routing-wait-time", 0));

            // AMQP 0-10 XA rollback
            setXaRollback(line.hasOption("enable-amqp010-xa-rollback"));
            setXaRollbackTransactionGap(processIntCliOption(line, "amqp010-xa-rollback-transaction-gap", 0));
            setXaRollbackWaitTime(processIntCliOption(line, "amqp010-xa-rollback-wait-time", 0));
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

    private int processIntCliOption(CommandLine line, String option, int defaultValue) throws ConfigurationException
    {
        String value = line.getOptionValue(option, null);

        if (value == null)
        {
            return defaultValue;
        }

        try {
            int intValue = Integer.parseInt(value);
            return intValue;
        }
        catch (NumberFormatException e)
        {
            throw new ConfigurationException("--" + option + " option doesn't contain valid integer", e);
        }
    }

    private void validateNonNegtive(String option, int value) throws ConfigurationException {
        if (value < 0)
        {
            throw new ConfigurationException("--" + option + " has to be non-negative integer!");
        }
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

    public void setFeedMessagesCount(int feedMessagesCount) throws ConfigurationException {
        validateNonNegtive("feed-messages-count", feedMessagesCount);
        this.feedMessagesCount = feedMessagesCount;
    }

    public void setFeedMessagesSize(int feedMessagesSize) throws ConfigurationException {
        validateNonNegtive("feed-messages-size", feedMessagesSize);
        this.feedMessagesSize = feedMessagesSize;
    }

    public int getFeedMessagesSize() {
        return feedMessagesSize;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) throws ConfigurationException {
        if (logLevel == null)
        {
            this.logLevel = DEFAULT_LOG_LEVEL;
        }
        else if ("error".equals(logLevel.toLowerCase()) || "warning".equals(logLevel.toLowerCase()) || "info".equals(logLevel.toLowerCase()) || "debug".equals(logLevel.toLowerCase()) || "trace".equals(logLevel.toLowerCase()))
        {
            this.logLevel = logLevel.toLowerCase();
        }
        else
        {
            throw new ConfigurationException("Invalid log level " + logLevel);
        }
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) throws ConfigurationException {
        validateNonNegtive("wait-time", waitTime);
        this.waitTime = waitTime;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) throws ConfigurationException {
        validateNonNegtive("transaction-count", transactionCount);
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

    public void setAmqp10RouterWaitTime(int amqp10RouterWaitTime) throws ConfigurationException {
        validateNonNegtive("amqp10-routing-wait-time", amqp10RouterWaitTime);
        this.amqp10RouterWaitTime = amqp10RouterWaitTime;
    }

    public int getAmqp10RouterTransactionGap() {
        return amqp10RouterTransactionGap;
    }

    public void setAmqp10RouterTransactionGap(int amqp10RouterTransactionGap) throws ConfigurationException {
        validateNonNegtive("amqp10-routing-transaction-gap", amqp10RouterTransactionGap);
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

    public void setAmqp10RollbackWaitTime(int amqp10RollbackWaitTime) throws ConfigurationException {
        validateNonNegtive("amqp10-rollback-wait-time", amqp10RollbackWaitTime);
        this.amqp10RollbackWaitTime = amqp10RollbackWaitTime;
    }

    public int getAmqp10RollbackTransactionGap() {
        return amqp10RollbackTransactionGap;
    }

    public void setAmqp10RollbackTransactionGap(int amqp10RollbackTransactionGap) throws ConfigurationException {
        validateNonNegtive("amqp10-rollback-transaction-gap", amqp10RollbackTransactionGap);
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

    public void setAmqp010RouterWaitTime(int amqp010RouterWaitTime) throws ConfigurationException {
        validateNonNegtive("amqp010-routing-wait-time", amqp010RouterWaitTime);
        this.amqp010RouterWaitTime = amqp010RouterWaitTime;
    }

    public int getAmqp010RouterTransactionGap() {
        return amqp010RouterTransactionGap;
    }

    public void setAmqp010RouterTransactionGap(int amqp010RouterTransactionGap) throws ConfigurationException {
        validateNonNegtive("amqp010-routing-transaction-gap", amqp010RouterTransactionGap);
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

    public void setAmqp010RollbackWaitTime(int amqp010RollbackWaitTime) throws ConfigurationException {
        validateNonNegtive("amqp010-rollback-wait-time", amqp010RollbackWaitTime);
        this.amqp010RollbackWaitTime = amqp010RollbackWaitTime;
    }

    public int getAmqp010RollbackTransactionGap() {
        return amqp010RollbackTransactionGap;
    }

    public void setAmqp010RollbackTransactionGap(int amqp010RollbackTransactionGap) throws ConfigurationException {
        validateNonNegtive("amqp010-rollback-transaction-gap", amqp010RollbackTransactionGap);
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

    public void setXaRouterWaitTime(int xaRouterWaitTime) throws ConfigurationException {
        validateNonNegtive("amqp010-xa-routing-wait-time", xaRouterWaitTime);
        this.xaRouterWaitTime = xaRouterWaitTime;
    }

    public int getXaRouterTransactionGap() {
        return xaRouterTransactionGap;
    }

    public void setXaRouterTransactionGap(int xaRouterTransactionGap) throws ConfigurationException {
        validateNonNegtive("amqp010-xa-routing-transaction-gap", xaRouterTransactionGap);
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

    public void setXaRollbackWaitTime(int xaRollbackWaitTime) throws ConfigurationException {
        validateNonNegtive("amqp010-xa-rollback-wait-time", xaRollbackWaitTime);
        this.xaRollbackWaitTime = xaRollbackWaitTime;
    }

    public int getXaRollbackTransactionGap() {
        return xaRollbackTransactionGap;
    }

    public void setXaRollbackTransactionGap(int xaRollbackTransactionGap) throws ConfigurationException {
        validateNonNegtive("amqp010-xa-rollback-transaction-gap", xaRollbackTransactionGap);
        this.xaRollbackTransactionGap = xaRollbackTransactionGap;
    }
}
