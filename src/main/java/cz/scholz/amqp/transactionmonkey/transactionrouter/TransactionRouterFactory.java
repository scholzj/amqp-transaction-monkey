package cz.scholz.amqp.transactionmonkey.transactionrouter;

import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp010.TransactionRollbackAmqp010;
import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp010.TransactionRouterAmqp010;
import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp010.xa.TransactionRollbackXA;
import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp010.xa.TransactionRouterXA;
import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp10.TransactionRollbackAmqp10;
import cz.scholz.amqp.transactionmonkey.transactionrouter.jms.amqp10.TransactionRouterAmqp10;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Created by schojak on 6.1.16.
 */
public class TransactionRouterFactory {
    private String sourceHost;
    private String sourcePort;
    private String sourceUsername;
    private String sourcePassword;
    private String sourceQueue;
    private String targetHost;
    private String targetPort;
    private String targetUsername;
    private String targetPassword;
    private String targetQueue;

    public TransactionRouterFactory(String sourceHost, String sourcePort, String sourceUsername, String sourcePassword, String sourceQueue, String targetHost, String targetPort, String targetUsername, String targetPassword, String targetQueue) {
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.sourceUsername = sourceUsername;
        this.sourcePassword = sourcePassword;
        this.sourceQueue = sourceQueue;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.targetUsername = targetUsername;
        this.targetPassword = targetPassword;
        this.targetQueue = targetQueue;
    }

    public TransactionRouter createAmqp10Router(int gapTime, int waitTime) throws JMSException, NamingException {
        return new TransactionRouterAmqp10(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue, gapTime, waitTime);
    }

    public TransactionRouter createAmqp010Router(int gapTime, int waitTime) throws JMSException, NamingException {
        return new TransactionRouterAmqp010(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue, gapTime, waitTime);
    }

    public TransactionRouter createAmqp10Rollback(int gapTime, int waitTime) throws JMSException, NamingException {
        return new TransactionRollbackAmqp10(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue, gapTime, waitTime);
    }

    public TransactionRouter createAmqp010Rollback(int gapTime, int waitTime) throws JMSException, NamingException {
        return new TransactionRollbackAmqp010(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue, gapTime, waitTime);
    }

    public TransactionRouter createXARouter(int gapTime, int waitTime) throws JMSException, NamingException {
        return new TransactionRouterXA(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue, gapTime, waitTime);
    }

    public TransactionRouter createXARollback(int gapTime, int waitTime) throws JMSException, NamingException {
        return new TransactionRollbackXA(sourceHost, sourcePort, sourceUsername, sourcePassword, sourceQueue, targetHost, targetPort, targetUsername, targetPassword, targetQueue, gapTime, waitTime);
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    public void setSourcePassword(String sourcePassword) {
        this.sourcePassword = sourcePassword;
    }

    public void setSourceQueue(String sourceQueue) {
        this.sourceQueue = sourceQueue;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public void setTargetPassword(String targetPassword) {
        this.targetPassword = targetPassword;
    }

    public void setTargetQueue(String targetQueue) {
        this.targetQueue = targetQueue;
    }
}
