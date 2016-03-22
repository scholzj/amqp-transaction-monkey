package cz.scholz.amqp.transactionmonkey.tests;

import cz.scholz.amqp.transactionmonkey.configuration.Configuration;
import cz.scholz.amqp.transactionmonkey.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by jakub on 08.03.16.
 */
public class ConfigurationTest {
    public String[] getRequiredOptions()
    {
        List<String> options = new ArrayList<>();
        options.add("--first-broker-host");
        options.add("host1");
        options.add("--first-broker-port");
        options.add("11111");
        options.add("-first-broker-username");
        options.add("user1");
        options.add("--first-broker-password");
        options.add("password1");
        options.add("--first-broker-queue");
        options.add("queue1");
        options.add("--second-broker-host");
        options.add("host2");
        options.add("--second-broker-port");
        options.add("22222");
        options.add("--second-broker-username");
        options.add("user2");
        options.add("--second-broker-password");
        options.add("password2");
        options.add("--second-broker-queue");
        options.add("queue2");

        String[] args = new String[options.size()];
        options.toArray(args);
        return args;
    }

    @Test
    public void testRequiredOptions()
    {
        try {
            Configuration config = new Configuration(getRequiredOptions());
        }
        catch (ConfigurationException e) {
            Assert.fail();
        }
    }

    @Test
    public void testPorts()
    {
        Configuration config = new Configuration();

        try {
            config.setaPort("5672");
            Assert.assertEquals("5672", config.getaPort());
        }
        catch (ConfigurationException e) {
            Assert.fail("Valid port for first broker was not accepted - 5672");
        }

        try {
            config.setbPort("5672");
            Assert.assertEquals("5672", config.getbPort());
        }
        catch (ConfigurationException e) {
            Assert.fail("Valid port for second broker was not accepted - 5672");
        }

        try {
            config.setaPort("xxx");
            Assert.fail("Invalid port for first broker was not detected - string");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
            config.setaPort("66666");
            Assert.fail("Invalid port for first broker was not detected - 66666");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
            config.setaPort("0");
            Assert.fail("Invalid port for first broker was not detected - 66666");
        }
        catch (ConfigurationException e) {
            //pass
        }
    }

    @Test
    public void testLogLevel() throws ConfigurationException
    {
        Configuration config = new Configuration();
        String DEFAULT_LOG_LEVEL = config.getLogLevel();

        config.setLogLevel(null);
        Assert.assertEquals(DEFAULT_LOG_LEVEL, config.getLogLevel(), "Incorrect Log Level set for null");

        config.setLogLevel("error");
        Assert.assertEquals("error", config.getLogLevel(), "Incorrect Log Level set for error");

        config.setLogLevel("warning");
        Assert.assertEquals("warning", config.getLogLevel(), "Incorrect Log Level set for warning");

        config.setLogLevel("info");
        Assert.assertEquals("info", config.getLogLevel(), "Incorrect Log Level set for info");

        config.setLogLevel("debug");
        Assert.assertEquals("debug", config.getLogLevel(), "Incorrect Log Level set for debug");

        config.setLogLevel("trace");
        Assert.assertEquals("trace", config.getLogLevel(), "Incorrect Log Level set for trace");

        config.setLogLevel("TRACE");
        Assert.assertEquals("trace".toLowerCase(), config.getLogLevel().toLowerCase(), "Incorrect Log Level set for TRACE");

        config.setLogLevel("Trace");
        Assert.assertEquals("trace".toLowerCase(), config.getLogLevel().toLowerCase(), "Incorrect Log Level set for Trace");


        try {
            config.setLogLevel("xxx");
            Assert.fail("Invalid Log Level was accepted");
        }
        catch (ConfigurationException e) {
            //pass
        }
    }

    @Test
    public void testProcessInt() throws ConfigurationException
    {
        String[] defaultCli = getRequiredOptions();
        String[] cli = new String[defaultCli.length+2];
        System.arraycopy(defaultCli, 0, cli, 0, defaultCli.length);

        int key = defaultCli.length;
        int val = defaultCli.length+1;

        // Test ineteger
        cli[key] = "--transaction-count";
        cli[val] = "60000";
        new Configuration(cli);

        // Test string
        try {
            cli[key] = "--transaction-count";
            cli[val] = "xxx";
            new Configuration(cli);
            Assert.fail("xxx was accepted as integer");
        }
        catch (ConfigurationException e)
        {
            // pass
        }

        // Test negative value
        try {
            cli[key] = "--transaction-count";
            cli[val] = "-1";
            new Configuration(cli);
            Assert.fail("-1 was accepted as non-negative value");
        }
        catch (ConfigurationException e)
        {
            // pass
        }
    }

    @Test
    public void testAllOptions() throws ConfigurationException {
        List<String> cli = new ArrayList<>(Arrays.asList(getRequiredOptions()));
        cli.add("--enable-amqp10-routing");
        cli.add("--amqp10-routing-wait-time");
        cli.add("101");
        cli.add("--amqp10-routing-transaction-gap");
        cli.add("102");
        cli.add("--enable-amqp10-rollback");
        cli.add("--amqp10-rollback-wait-time");
        cli.add("103");
        cli.add("--amqp10-rollback-transaction-gap");
        cli.add("104");
        cli.add("--enable-amqp010-routing");
        cli.add("--amqp010-routing-wait-time");
        cli.add("105");
        cli.add("--amqp010-routing-transaction-gap");
        cli.add("106");
        cli.add("--enable-amqp010-rollback");
        cli.add("--amqp010-rollback-wait-time");
        cli.add("107");
        cli.add("--amqp010-rollback-transaction-gap");
        cli.add("108");
        cli.add("--enable-amqp010-xa-routing");
        cli.add("--amqp010-xa-routing-wait-time");
        cli.add("109");
        cli.add("--amqp010-xa-routing-transaction-gap");
        cli.add("110");
        cli.add("--enable-amqp010-xa-rollback");
        cli.add("--amqp010-xa-rollback-wait-time");
        cli.add("111");
        cli.add("--amqp010-xa-rollback-transaction-gap");
        cli.add("112");
        cli.add("--wait-time");
        cli.add("113");
        cli.add("--feed-messages");
        cli.add("--feed-messages-count");
        cli.add("115");
        cli.add("--feed-messages-size");
        cli.add("116");
        cli.add("--log-level");
        cli.add("trace");

        String[] args = new String[cli.size()];
        cli.toArray(args);

        Configuration config = new Configuration(args);

        Assert.assertEquals(config.isAmqp10Router(), true, "AMQP 1.0 router is not enabled");
        Assert.assertEquals(config.getAmqp10RouterWaitTime(), 101, "AMQP 1.0 router wait time does not match");
        Assert.assertEquals(config.getAmqp10RouterTransactionGap(), 102, "AMQP 1.0 router transaction gap does not match");

        Assert.assertEquals(config.isAmqp10Rollback(), true, "AMQP 1.0 rollback is not enabled");
        Assert.assertEquals(config.getAmqp10RollbackWaitTime(), 103, "AMQP 1.0 rollback wait time does not match");
        Assert.assertEquals(config.getAmqp10RollbackTransactionGap(), 104, "AMQP 1.0 rollback transaction gap does not match");

        Assert.assertEquals(config.isAmqp010Router(), true, "AMQP 0-10 router is not enabled");
        Assert.assertEquals(config.getAmqp010RouterWaitTime(), 105, "AMQP 0-10 router wait time does not match");
        Assert.assertEquals(config.getAmqp010RouterTransactionGap(), 106, "AMQP 0-10 router transaction gap does not match");

        Assert.assertEquals(config.isAmqp010Rollback(), true, "AMQP 0-10 rollback is not enabled");
        Assert.assertEquals(config.getAmqp010RollbackWaitTime(), 107, "AMQP 0-10 rollback wait time does not match");
        Assert.assertEquals(config.getAmqp010RollbackTransactionGap(), 108, "AMQP 0-10 rollback transaction gap does not match");

        Assert.assertEquals(config.isXaRouter(), true, "AMQP 0-10 XA router is not enabled");
        Assert.assertEquals(config.getXaRouterWaitTime(), 109, "AMQP 0-10 XA router wait time does not match");
        Assert.assertEquals(config.getXaRouterTransactionGap(), 110, "AMQP 0-10 XA router transaction gap does not match");

        Assert.assertEquals(config.isXaRollback(), true, "AMQP 0-10 XA rollback is not enabled");
        Assert.assertEquals(config.getXaRollbackWaitTime(), 111, "AMQP 0-10 XA rollback wait time does not match");
        Assert.assertEquals(config.getXaRollbackTransactionGap(), 112, "AMQP 0-10 XA rollback transaction gap does not match");

        Assert.assertEquals(config.getWaitTime(), 113, "Wait time does not match");

        Assert.assertEquals(config.isFeedMessages(), true, "Feeding of messages is not enabled");
        Assert.assertEquals(config.getFeedMessagesCount(), 115, "Feeding message count does not match");
        Assert.assertEquals(config.getFeedMessagesSize(), 116, "Feeding message size does not match");

        Assert.assertEquals(config.getLogLevel(), "trace", "Log level does not match");
    }
}
