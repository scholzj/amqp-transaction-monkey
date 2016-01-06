package cz.scholz.amqp.transactionmonkey;

import cz.scholz.amqp.transactionmonkey.transactionrouter.TransactionRouter;
import cz.scholz.amqp.transactionmonkey.transactionrouter.TransactionRouterFactory;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Created by schojak on 31.12.15.
 */
public class TransactionMonkey {
    public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
        // TODO: Make rollback timeout/sleep configurable
        // TODO: Crete the initial set of messages
        // TODO: Make log level configurable
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");

        String aHost = "eclbgc01.xeop.de";
        String aPort = "20707";
        String aUsername = "admin";
        String aPassword = "admin";
        String aQueue = "broadcast.user1.rtgQueue";

        String bHost = "cbgd03.xeop.de";
        String bPort = "21234";
        String bUsername = "admin";
        String bPassword = "admin";
        String bQueue = "broadcast.user1.rtgQueue";

        TransactionRouterFactory factAtoB = new TransactionRouterFactory(aHost, aPort, aUsername, aPassword, aQueue, bHost, bPort, bUsername, bPassword, bQueue);
        TransactionRouterFactory factBtoA = new TransactionRouterFactory(bHost, bPort, bUsername, bPassword, bQueue, aHost, aPort, aUsername, aPassword, aQueue);

        // Start AMQP 1.0 router A->B
        TransactionRouter amqp10RouterAtoB = factAtoB.createAmqp10Router();
        amqp10RouterAtoB.start();

        // Start AMQP 1.0 router B->A
        TransactionRouter amqp10RouterBtoA = factBtoA.createAmqp10Router();
        amqp10RouterBtoA.start();

        // Start AMQP 0-10 router A->B
        TransactionRouter amqp010RouterAtoB = factAtoB.createAmqp010Router();
        amqp010RouterAtoB.start();

        // Start AMQP 0-10 router B->A
        TransactionRouter amqp010RouterBtoA = factBtoA.createAmqp010Router();
        amqp010RouterBtoA.start();

        // Start AMQP 1.0 rollback A->B
        TransactionRouter amqp10RollbackAtoB = factAtoB.createAmqp10Rollback();
        amqp10RollbackAtoB.start();

        // Start AMQP 1.0 rollback B->A
        TransactionRouter amqp10RollbackBtoA = factBtoA.createAmqp10Rollback();
        amqp10RollbackBtoA.start();

        // Start AMQP 0-10 rollback A->B
        TransactionRouter amqp010RollbackAtoB = factAtoB.createAmqp010Rollback();
        amqp010RollbackAtoB.start();

        // Start AMQP 0-10 rollback B->A
        TransactionRouter amqp010RollbackBtoA = factBtoA.createAmqp010Rollback();
        amqp010RollbackBtoA.start();

        // Start XA router A->B
        TransactionRouter xaRouterAtoB = factAtoB.createXARouter();
        xaRouterAtoB.start();

        // Start XA router B->A
        TransactionRouter xaRouterBtoA = factBtoA.createXARouter();
        xaRouterBtoA.start();

        // Start XA rollback A->B
        TransactionRouter xaRollbackAtoB = factAtoB.createXARollback();
        xaRollbackAtoB.start();

        // Start XA rollback B->A
        TransactionRouter xaRollbackBtoA = factBtoA.createXARollback();
        xaRollbackBtoA.start();

        Thread.sleep(12*60*60*1000);

        // Finish AMQP 1.0 router A->B
        amqp10RouterAtoB.finish();
        amqp10RouterAtoB.join();

        // Finish AMQP 1.0 router B->A
        amqp10RouterBtoA.finish();
        amqp10RouterBtoA.join();

        // Finish AMQP 0-10 router A->B
        amqp010RouterAtoB.finish();
        amqp010RouterAtoB.join();

        // Finish AMQP 0-10 router B->A
        amqp010RouterBtoA.finish();
        amqp010RouterBtoA.join();

        // Finish AMQP 1.0 rollback A->B
        amqp10RollbackAtoB.finish();
        amqp10RollbackAtoB.join();

        // Finish AMQP 1.0 rollback B->A
        amqp10RollbackBtoA.finish();
        amqp10RollbackBtoA.join();

        // Finish AMQP 0-10 rollback A->B
        amqp010RollbackAtoB.finish();
        amqp010RollbackAtoB.join();

        // Finish AMQP 0-10 rollback B->A
        amqp010RollbackBtoA.finish();
        amqp010RollbackBtoA.join();

        // Finish XA router A->B
        xaRouterAtoB.finish();
        xaRouterAtoB.join();

        // Finish XA router B->A
        xaRouterBtoA.finish();
        xaRouterBtoA.join();

        // Finish XA rollback A->B
        xaRollbackAtoB.finish();
        xaRollbackAtoB.join();

        // Finish XA rollback B->A
        xaRollbackBtoA.finish();
        xaRollbackBtoA.join();

        return;
    }
}
