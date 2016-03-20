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
    public void testInvalidPort()
    {
        try {
            List<String> options = new ArrayList<>();
            options.add("--first-broker-host");
            options.add("host1");
            options.add("--first-broker-port");
            options.add("xxx");
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

            Configuration config = new Configuration(args);
            Assert.fail("Invalid port for first broker was not detected");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
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
            options.add("xxx");
            options.add("--second-broker-username");
            options.add("user2");
            options.add("--second-broker-password");
            options.add("password2");
            options.add("--second-broker-queue");
            options.add("queue2");

            String[] args = new String[options.size()];
            options.toArray(args);

            Configuration config = new Configuration(args);
            Assert.fail("Invalid port for second broker was not detected");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
            List<String> options = new ArrayList<>();
            options.add("--first-broker-host");
            options.add("host1");
            options.add("--first-broker-port");
            options.add("0");
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

            Configuration config = new Configuration(args);
            Assert.fail("Invalid port for first broker was not detected");
        }
        catch (ConfigurationException e) {
            //pass
        }

        try {
            List<String> options = new ArrayList<>();
            options.add("--first-broker-host");
            options.add("host1");
            options.add("--first-broker-port");
            options.add("66666");
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

            Configuration config = new Configuration(args);
            Assert.fail("Invalid port for first broker was not detected");
        }
        catch (ConfigurationException e) {
            //pass
        }
    }
}
