package cz.scholz.amqp.transactionmonkey;

/**
 * Created by jakub on 03.03.16.
 */
public class TransactionMonkeyException extends Exception {
    public TransactionMonkeyException()
    {
        super();
    }

    public TransactionMonkeyException(String message)
    {
        super(message);
    }

    public TransactionMonkeyException(Throwable cause)
    {
        super(cause);
    }

    public TransactionMonkeyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TransactionMonkeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
