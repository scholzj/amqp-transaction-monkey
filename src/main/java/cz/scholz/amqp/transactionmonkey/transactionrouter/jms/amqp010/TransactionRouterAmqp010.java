package cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp010;

import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.TransactionRouterJms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by schojak on 31.12.15.
 */
public class TransactionRouterAmqp010 extends TransactionRouterJms {
    final static Logger LOG = LoggerFactory.getLogger(TransactionRouterAmqp010.class);

    public TransactionRouterAmqp010(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) throws NamingException, JMSException {
        super(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue);
        LOG.info("Creating new AMQP 0-10 router");
    }

    protected void prepareProperties(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) throws NamingException
    {
        String sourceBroker = String.format("amqp://%s:%s@TransactionMonkey/?brokerlist='tcp://%s:%s'", sourceUsername, sourcePassword, sourceHost, sourcePort);
        LOG.info("Source broker URL prepared as " + sourceBroker);
        String targetBroker = String.format("amqp://%s:%s@TransactionMonkey/?brokerlist='tcp://%s:%s'", targetUsername, targetPassword, targetHost, targetPort);
        LOG.info("Target broker URL prepared as " + targetBroker);

        props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("connectionfactory.sourceBroker", sourceBroker);
        props.setProperty("connectionfactory.targetBroker", targetBroker);
        props.setProperty("destination.sourceDest", sourceQueue);
        props.setProperty("destination.targetDest", targetQueue);

        ctx = new InitialContext(props);
    }

    @Override
    protected Destination getSourceDestination() throws NamingException {
        return (Destination)ctx.lookup("sourceDest");
    }

    @Override
    protected Destination getTargetDestination() throws NamingException {
        return (Destination)ctx.lookup("targetDest");
    }
}
