package cz.scholz.amqp.transactionmonkey.transactionrouter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by schojak on 31.12.15.
 */
public abstract class TransactionRouter extends Thread {
    public abstract void finish();
    public abstract void run();
}
