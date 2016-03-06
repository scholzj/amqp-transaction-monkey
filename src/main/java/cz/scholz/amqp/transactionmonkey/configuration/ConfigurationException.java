package cz.scholz.amqp.transactionmonkey.configuration;

/**
 * Created by jakub on 03.03.16.
 */
public class ConfigurationException extends Exception {
    public ConfigurationException()
    {
        super();
    }

    public ConfigurationException(String message)
    {
        super(message);
    }

    public ConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
