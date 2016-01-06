package cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp10;

import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.TransactionRouterJms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by schojak on 31.12.15.
 */
public class TransactionRouterAmqp10 extends TransactionRouterJms {
    final static Logger LOG = LoggerFactory.getLogger(TransactionRouterAmqp10.class);

    public TransactionRouterAmqp10(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) throws NamingException, JMSException {
        super(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue);
        LOG.info("Creating new AMQP 1.0 router");
    }

    protected void prepareProperties(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) throws NamingException
    {
        String sourceBroker = String.format("amqp://%s:%s?jms.username=%s&jms.password=%s&jms.prefetchPolicy.all=10", sourceHost, sourcePort, sourceUsername, sourcePassword);
        LOG.info("Source broker URL prepared as " + sourceBroker);
        String targetBroker = String.format("amqp://%s:%s?jms.username=%s&jms.password=%s&jms.prefetchPolicy.all=10", targetHost, targetPort, targetUsername, targetPassword);
        LOG.info("Target broker URL prepared as " + targetBroker);

        props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.sourceBroker", sourceBroker);
        props.setProperty("connectionfactory.targetBroker", targetBroker);
        props.setProperty("queue.sourceQueue", sourceQueue);
        props.setProperty("queue.targetQueue", targetQueue);

        ctx = new InitialContext(props);
    }

    @Override
    protected Destination getSourceDestination() throws NamingException {
        return (Queue)ctx.lookup("sourceQueue");
    }

    @Override
    protected Destination getTargetDestination() throws NamingException {
        return (Queue)ctx.lookup("targetQueue");
    }
}
