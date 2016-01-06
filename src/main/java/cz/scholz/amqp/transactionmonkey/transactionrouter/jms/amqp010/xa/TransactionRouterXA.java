package cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp010.xa;

import cz.scholz.amqp.transactionmonkey.transactionrouter.TransactionRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.Properties;

/**
 * Created by schojak on 31.12.15.
 */
public class TransactionRouterXA extends TransactionRouter {
    private int RECEIVE_TIMEOUT = 1000;

    final static Logger LOG = LoggerFactory.getLogger(TransactionRouterXA.class);

    protected Properties props;
    protected InitialContext ctx;
    protected XAConnection sourceConnection, targetConnection;
    protected XASession sourceXaSession, targetXaSession;
    protected Session sourceSession, targetSession;
    protected MessageConsumer sourceReceiver;
    protected MessageProducer targetSender;

    protected int messageCounter = 0;
    protected boolean finish = false;

    public TransactionRouterXA(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) throws NamingException, JMSException {
        LOG.info("Creating new AMQP 0-10 XA router");
        prepareProperties(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue);
        attachSource();
        attachTarget();
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

    protected Destination getSourceDestination() throws NamingException {
        return (Destination)ctx.lookup("sourceDest");
    }

    protected Destination getTargetDestination() throws NamingException {
        return (Destination)ctx.lookup("targetDest");
    }

    protected void attachSource() throws JMSException, NamingException {
        XAConnectionFactory fact = (XAQueueConnectionFactory)ctx.lookup("sourceBroker");
        sourceConnection = fact.createXAConnection();
        sourceXaSession = sourceConnection.createXASession();
        sourceSession = sourceXaSession.getSession();
        sourceReceiver = sourceSession.createConsumer(getSourceDestination());
        sourceConnection.start();
    }

    protected void attachTarget() throws JMSException, NamingException {
        XAConnectionFactory fact = (XAQueueConnectionFactory)ctx.lookup("targetBroker");
        targetConnection = fact.createXAConnection();
        targetXaSession = targetConnection.createXASession();
        targetSession = targetXaSession.getSession();
        targetSender = targetSession.createProducer(getTargetDestination());
        targetSender.setDeliveryMode(DeliveryMode.PERSISTENT);
        targetConnection.start();
    }

    protected void detachSource() throws JMSException {
        sourceReceiver.close();
        sourceSession.close();
        sourceXaSession.close();
        sourceConnection.close();
    }

    protected void detachTarget() throws JMSException {
        targetSender.close();
        targetSession.close();
        targetXaSession.close();
        targetConnection.close();
    }

    public void finish()
    {
        LOG.info("Setting finish to true");
        finish = true;
    }

    public void run()
    {
        LOG.info("Starting XA routing");

        XAResource xares1 = sourceXaSession.getXAResource();
        XAResource xares2 = targetXaSession.getXAResource();

        Message msg;
        Xid xid;

        while (!finish)
        {
            try {
                xid = XidFactory.generate();

                xares2.start(xid, XAResource.TMNOFLAGS);
                xares1.start(xid, XAResource.TMNOFLAGS);

                msg = sourceReceiver.receive(RECEIVE_TIMEOUT);

                if (msg != null)
                {
                    targetSender.send(msg);

                    messageCounter++;

                    if (messageCounter % 1000 == 0)
                    {
                        LOG.info(messageCounter + " messages XA routed");
                    }
                }
                else
                {
                    LOG.trace("Didn't received any message within " + RECEIVE_TIMEOUT/1000 + "s");
                }

                xares1.end(xid, XAResource.TMSUCCESS);
                xares2.end(xid, XAResource.TMSUCCESS);

                xares1.prepare(xid);
                xares2.prepare(xid);

                xares1.commit(xid, false);
                xares2.commit(xid, false);

            } catch (JMSException e) {
                LOG.info("JMS Exception occurred ", e);
            } catch (XAException e) {
                LOG.info("XA Exception occurred ", e);
            }
        }

        LOG.info(messageCounter + " messages XA routed");
        LOG.info("Finishing XA routing");

        try {
            detachSource();
        } catch (JMSException e) {
            LOG.info("Failed to detach source connection");
        }

        try {
            detachTarget();
        } catch (JMSException e) {
            LOG.info("Failed to detach target connection");
        }
    }
}
