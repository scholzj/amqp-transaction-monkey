package cz.scholz.amqp.transactionmonkey.tests;

import cz.scholz.amqp.transactionmonkey.configuration.Configuration;
import cz.scholz.amqp.transactionmonkey.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void testWaitTime() throws ConfigurationException
    {
        Configuration config = new Configuration();
        int DEFAULT_WAIT_TIME = config.getWaitTime();

        config.setWaitTime(null);
        Assert.assertEquals(DEFAULT_WAIT_TIME, config.getWaitTime(), "Incorrect wait time set for null");

        config.setWaitTime("60000");
        Assert.assertEquals(60000, config.getWaitTime(), "Incorrect wait time set for string 60000");

        config.setWaitTime(60000);
        Assert.assertEquals(60000, config.getWaitTime(), "Incorrect wait time set for int 60000");

        try {
            config.setWaitTime("xxx");
            Assert.fail("Invalid wait time xxx was accepted");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
            config.setWaitTime(-1);
            Assert.fail("Invalid wait time -1 was accepted");
        }
        catch (ConfigurationException e) {
            //pass
        }
    }

    @Test
    public void testTransactionCount() throws ConfigurationException
    {
        Configuration config = new Configuration();
        int DEFAULT_TRANSACTION_COUNT = config.getTransactionCount();

        config.setTransactionCount(null);
        Assert.assertEquals(DEFAULT_TRANSACTION_COUNT, config.getTransactionCount(), "Incorrect transaction count set for null");

        config.setTransactionCount("60000");
        Assert.assertEquals(60000, config.getTransactionCount(), "Incorrect transaction count set for string 60000");

        config.setTransactionCount(60000);
        Assert.assertEquals(60000, config.getTransactionCount(), "Incorrect transaction count set for int 60000");

        try {
            config.setTransactionCount("xxx");
            Assert.fail("Invalid transaction count xxx was accepted");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
            config.setTransactionCount(-1);
            Assert.fail("Invalid transaction count -1 was accepted");
        }
        catch (ConfigurationException e) {
            //pass
        }
    }
}
