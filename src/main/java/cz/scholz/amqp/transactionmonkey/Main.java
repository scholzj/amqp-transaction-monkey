package cz.scholz.amqp.transactionmonkey;

import cz.scholz.amqp.transactionmonkey.configuration.Configuration;
import cz.scholz.amqp.transactionmonkey.configuration.ConfigurationException;

import java.util.Arrays;

/**
 * Created by jakub on 07.03.16.
 */
public class Main {
    public static void main(String[] args) {
        // Handle help
        if (Arrays.asList(args).contains("--help"))
        {
            Configuration.printHelp();
            System.exit(0);
        }

        Configuration config = null;

        try
        {
            config = new Configuration(args);
        }
        catch (ConfigurationException e)
        {
            System.exit(1);
        }

        try {
            TransactionMonkey tm = new TransactionMonkey(config);
        }
        catch (TransactionMonkeyException e)
        {
            System.exit(1);
        }
    }
}
