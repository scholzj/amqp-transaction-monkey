package cz.scholz.amqp.transactionmonkey.feeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.Random;

/**
 * Created by jakub on 03.03.16.
 */
public class MessageFeeder {
    final static Logger LOG = LoggerFactory.getLogger(MessageFeeder.class);

    String host;
    String port;
    String username;
    String password;
    String queue;

    public MessageFeeder(String host, String port, String username, String password, String queue)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.queue = queue;

        LOG.info("New Message feeder created");
    }

    public void feed(int numberOfMessages, int messageSize) throws MessageFeederException
    {
        Connection connection;
        Session session;
        MessageProducer sender;

        try {
            Properties props = prepareProperties();
            Context ctx = new InitialContext(props);
            ConnectionFactory fact = (ConnectionFactory) ctx.lookup("broker");
            connection = fact.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            sender = session.createProducer((Queue) ctx.lookup("queue"));
            sender.setDeliveryMode(DeliveryMode.PERSISTENT);
            connection.start();
        }
        catch (JMSException | NamingException e)
        {
            LOG.error("Message feeder failed to connect to the broker", e);
            throw new MessageFeederException(e);
        }

        try {
            Random rand = new Random();
            BytesMessage msg;
            byte[] payload = new byte[messageSize];

            for (int i = 0; i < numberOfMessages; i++) {
                msg = session.createBytesMessage();
                rand.nextBytes(payload);
                msg.writeBytes(payload);
                sender.send(msg);
            }
        }
        catch (JMSException e)
        {
            LOG.error("Message feeder failed to feed messages", e);
            throw new MessageFeederException(e);
        }

        try {
            sender.close();
            session.close();
            connection.close();
        }
        catch (JMSException e)
        {
            LOG.error("Message feeder failed to close the connection", e);
            throw new MessageFeederException(e);
        }

    }

    protected Properties prepareProperties() throws NamingException
    {
        String broker = String.format("amqp://%s:%s?jms.username=%s&jms.password=%s&jms.prefetchPolicy.all=10", host, port, username, password);
        LOG.info("Source broker URL prepared as " + broker);

        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.broker", broker);
        props.setProperty("queue.queue", queue);

        return props;
    }
}
