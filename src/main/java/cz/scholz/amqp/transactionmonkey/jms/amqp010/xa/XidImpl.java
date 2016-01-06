package cz.scholz.amqp.transactionmonkey.jms.amqp010.xa;

import javax.transaction.xa.Xid;

/**
 * Created by schojak on 6.1.16.
 */
public class XidImpl implements Xid {
    private byte[] _branchQualifier;
    private int _formatID;
    private byte[] _globalTransactionID;

    public XidImpl(byte[] _branchQualifier, int _formatID, byte[] _globalTransactionID) {
        this._branchQualifier = _branchQualifier;
        this._formatID = _formatID;
        this._globalTransactionID = _globalTransactionID;
    }

    public byte[] getBranchQualifier() {
        return _branchQualifier;
    }

    public int getFormatId() {
        return _formatID;
    }

    public byte[] getGlobalTransactionId() {
        return _globalTransactionID;
    }
}
