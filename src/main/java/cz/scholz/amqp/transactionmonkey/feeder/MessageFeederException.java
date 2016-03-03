package cz.scholz.amqp.transactionmonkey.feeder;

/**
 * Created by jakub on 03.03.16.
 */
public class MessageFeederException extends Exception {
    public MessageFeederException()
    {
        super();
    }

    public MessageFeederException(String message)
    {
        super(message);
    }

    public MessageFeederException(Throwable cause)
    {
        super(cause);
    }

    public MessageFeederException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MessageFeederException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
