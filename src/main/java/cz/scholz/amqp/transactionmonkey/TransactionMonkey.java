package cz.scholz.amqp.transactionmonkey;

import cz.scholz.amqp.transactionmonkey.configuration.Configuration;
import cz.scholz.amqp.transactionmonkey.configuration.ConfigurationException;
import cz.scholz.amqp.transactionmonkey.feeder.MessageFeeder;
import cz.scholz.amqp.transactionmonkey.feeder.MessageFeederException;
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

    private Configuration config;

    public static void main(String[] args) {
        // TODO: Help

        Configuration config = null;

        try
        {
            config = new Configuration(args);
        }
        catch (ConfigurationException e)
        {
            System.exit(1);
        }

        TransactionMonkey tm = new TransactionMonkey(config);
    }

    public TransactionMonkey(Configuration newConfig)
    {
        config = newConfig;
        configureLogging(config.getLogLevel());
        createRouters();

        // Feeding messages
        if (config.isFeedMessages())
        {
            try {
                feedMessages();
            }
            catch (MessageFeederException e)
            {
                LOG.error("Failed to feed the messages", e);
                System.exit(1);
            }
        }

        // Start the routers
        startAllRouters();

        // Run the routers
        runRouters();

        // Stop the routers
        stopAllRouters();
    }


    private void createRouters() {
        // Configure factories
        TransactionRouterFactory factAtoB = new TransactionRouterFactory(config.getaHost(), config.getaPort(), config.getaUsername(), config.getaPassword(), config.getaQueue(), config.getbHost(), config.getbPort(), config.getbUsername(), config.getbPassword(), config.getbQueue());
        TransactionRouterFactory factBtoA = new TransactionRouterFactory(config.getbHost(), config.getbPort(), config.getbUsername(), config.getbPassword(), config.getbQueue(), config.getaHost(), config.getaPort(), config.getaUsername(), config.getaPassword(), config.getaQueue());

        // Create the routers
        try {
            // AMQP 1.0 routers
            if (config.isAmqp10Router()) {
                LOG.info("Creating AMQP 1.0 routers");
                tr.add(factAtoB.createAmqp10Router(config.getAmqp10RouterTransactionGap(), config.getAmqp10RouterWaitTime()));
                tr.add(factBtoA.createAmqp10Router(config.getAmqp10RouterTransactionGap(), config.getAmqp10RouterWaitTime()));
            }

            // AMQP 1.0 rollback
            if (config.isAmqp10Rollback()) {
                LOG.info("Creating AMQP 1.0 routers");
                tr.add(factAtoB.createAmqp10Rollback(config.getAmqp10RollbackTransactionGap(), config.getAmqp10RollbackWaitTime()));
                tr.add(factBtoA.createAmqp10Rollback(config.getAmqp10RollbackTransactionGap(), config.getAmqp10RollbackWaitTime()));
            }

            // AMQP 0-10 routers
            if (config.isAmqp10Router()) {
                LOG.info("Creating AMQP 0-10 routers");
                tr.add(factAtoB.createAmqp010Router(config.getAmqp010RouterTransactionGap(), config.getAmqp010RouterWaitTime()));
                tr.add(factBtoA.createAmqp010Router(config.getAmqp010RouterTransactionGap(), config.getAmqp010RouterWaitTime()));
            }

            // AMQP 0-10 rollback
            if (config.isAmqp10Rollback()) {
                LOG.info("Creating AMQP 0-10 routers");
                tr.add(factAtoB.createAmqp010Rollback(config.getAmqp010RollbackTransactionGap(), config.getAmqp010RollbackWaitTime()));
                tr.add(factBtoA.createAmqp010Rollback(config.getAmqp010RollbackTransactionGap(), config.getAmqp010RollbackWaitTime()));
            }

            // AMQP 0-10 XA routers
            if (config.isAmqp10Router()) {
                LOG.info("Creating AMQP 0-10 XA routers");
                tr.add(factAtoB.createXARouter(config.getXaRouterTransactionGap(), config.getXaRouterWaitTime()));
                tr.add(factBtoA.createXARouter(config.getXaRouterTransactionGap(), config.getXaRouterWaitTime()));
            }

            // AMQP 0-10 XA rollback
            if (config.isAmqp10Rollback()) {
                LOG.info("Creating AMQP 0-10 XA routers");
                tr.add(factAtoB.createXARollback(config.getXaRollbackTransactionGap(), config.getXaRollbackWaitTime()));
                tr.add(factBtoA.createXARollback(config.getXaRollbackTransactionGap(), config.getXaRollbackWaitTime()));
            }
        }
        catch (JMSException | NamingException e)
        {
            LOG.error("Failed to create router", e);

            // Stop the routers
            stopAllRouters();

            // TODO: Do not exit - throw exception
            System.exit(1);
        }
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

    private void runRouters()
    {
        if (config.getTransactionCount() > 0)
        {
            LOG.info("Waiting for " + config.getTransactionCount() + " transactions before exit");
            int totalTransactions = 0;

            while (config.getTransactionCount() > totalTransactions) {
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
            try {
                LOG.info("Waiting for " + config.getWaitTime()/1000 + " seconds before exit");
                Thread.sleep(config.getWaitTime());
            } catch (InterruptedException e) {
                LOG.error("Sleep was interrupted", e);
            }
        }
    }

    private void configureLogging(String logLevel)
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", logLevel);
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");
        LOG = LoggerFactory.getLogger(TransactionMonkey.class);
    }

    private void feedMessages() throws MessageFeederException
    {
        LOG.info("Feeding messages into first broker");
        MessageFeeder aFeeder = new MessageFeeder(config.getaHost(), config.getaPort(), config.getaUsername(), config.getaPassword(), config.getaQueue());
        aFeeder.feed(config.getFeedMessagesCount(), config.getFeedMessagesSize());

        LOG.info("Feeding messages into second broker");
        MessageFeeder bFeeder = new MessageFeeder(config.getbHost(), config.getbPort(), config.getbUsername(), config.getbPassword(), config.getbQueue());
        bFeeder.feed(config.getFeedMessagesCount(), config.getFeedMessagesSize());
    }
}
