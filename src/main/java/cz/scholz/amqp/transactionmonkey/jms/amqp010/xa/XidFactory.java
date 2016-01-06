package cz.scholz.amqp.transactionmonkey.jms.amqp010.xa;

import javax.transaction.xa.Xid;
import java.util.Random;
import java.util.UUID;

/**
 * Created by schojak on 6.1.16.
 */
public class XidFactory {
    private static Random rand = new Random();

    public static Xid generate()
    {
        byte[] txnId = new byte[16];
        rand.nextBytes(txnId);

        return new XidImpl(new byte[]{0x01}, 1, txnId);
        //return new XidImpl(new byte[]{0x01}, rand.nextInt(), new byte[]{0x02});
    }
}
