package cz.scholz.amqp.transactionmonkey.transactionrouter.jms;

import cz.scholz.amqp.transactionmonkey.transactionrouter.TransactionRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by schojak on 31.12.15.
 */
abstract public class TransactionRouterJms extends TransactionRouter {
    private int RECEIVE_TIMEOUT = 1000;

    final static Logger LOG = LoggerFactory.getLogger(TransactionRouterJms.class);

    protected Properties props;
    protected InitialContext ctx;
    protected Connection sourceConnection, targetConnection;
    protected Session sourceSession, targetSession;
    protected MessageConsumer sourceReceiver;
    protected MessageProducer targetSender;

    protected int gapTime;
    protected int waitTime;

    protected int messageCounter = 0;
    protected boolean finish = false;

    public TransactionRouterJms(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue, int gapTime, int waitTime) throws NamingException, JMSException {
        prepareProperties(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue);

        attachSource();
        attachTarget();

        this.gapTime = gapTime;
        this.waitTime = waitTime;
    }

    protected abstract void prepareProperties(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) throws NamingException;

    protected abstract Destination getSourceDestination() throws NamingException;

    protected abstract Destination getTargetDestination() throws NamingException;

    protected void attachSource() throws JMSException, NamingException {
        ConnectionFactory fact = (ConnectionFactory)ctx.lookup("sourceBroker");
        sourceConnection = fact.createConnection();
        sourceSession = sourceConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        sourceReceiver = sourceSession.createConsumer(getSourceDestination());
        sourceConnection.start();
    }

    protected void attachTarget() throws JMSException, NamingException {
        ConnectionFactory fact = (ConnectionFactory)ctx.lookup("targetBroker");
        targetConnection = fact.createConnection();
        targetSession = targetConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        targetSender = targetSession.createProducer(getTargetDestination());
        targetSender.setDeliveryMode(DeliveryMode.PERSISTENT);
        targetConnection.start();
    }

    protected void detachSource() throws JMSException {
        sourceReceiver.close();
        sourceSession.close();
        sourceConnection.close();
    }

    protected void detachTarget() throws JMSException {
        targetSender.close();
        targetSession.close();
        targetConnection.close();
    }

    public void finish()
    {
        LOG.info("Setting finish to true");
        finish = true;
    }

    public void run()
    {
        LOG.info("Starting routing");

        Message msg;

        while (!finish)
        {
            try {
                msg = sourceReceiver.receive(RECEIVE_TIMEOUT);

                if (msg != null)
                {
                    targetSender.send(msg);

                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        LOG.error("Waiting before rollback has been interrupted!", e);
                    }

                    targetSession.commit();
                    sourceSession.commit();
                    messageCounter++;

                    if (messageCounter % 1000 == 0)
                    {
                        LOG.info(messageCounter + " messages routed");
                    }

                    try {
                        Thread.sleep(gapTime);
                    } catch (InterruptedException e) {
                        LOG.error("Waiting before next transactions has been interrupted!", e);
                    }
                }
                else
                {
                    LOG.trace("Didn't received any message within " + RECEIVE_TIMEOUT/1000 + "s");
                }
            } catch (JMSException e) {
                LOG.info("Exception occurred ", e);
            }
        }

        LOG.info(messageCounter + " messages routed");
        LOG.info("Finishing routing");

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
