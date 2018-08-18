package io.zino.core.transaction.model.transaction;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    public final static String SCHEMA_TABLE = "tnx.transactions";
    public final static String TABLE = "transactions";

    public static final String PROP_ID = "id";
    public static final String PROP_EXTUID = "extuid";
    public static final String PROP_FROM_ACCOUNT_ID = "fromAccountId";
    public static final String PROP_TEMP_ACCOUNT_ID = "tempAccountId";
    public static final String PROP_TO_ACCOUNT_ID = "toAccountId";
    public static final String PROP_AMOUNT = "amount";
    public static final String PROP_CREATION_DATE = "creationDate";
    public static final String PROP_VERIFY_DATE = "verifyDate";
    public static final String PROP_VERIFY = "verify";
    public static final String PROP_DESCRIPTION = "description";

    private long id;
    private String extuid;
    private long fromAccountId;
    private long tempAccountId;
    private long toAccountId;
    private BigDecimal amount;
    private Date creationDate;
    private Date verifyDate;
    private boolean verify;
    private String description;

    public Transaction(String extuid, long fromAccountId, long tempAccountId, long toAccountId, BigDecimal amount, String description) {
        this.extuid = extuid;
        this.fromAccountId = fromAccountId;
        this.tempAccountId = tempAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.verify = false;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public long getId() {
        return id;
    }

    public String getExtuid() {
        return extuid;
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public long getTempAccountId() {
        return tempAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public boolean isVerify() {
        return verify;
    }

    public String getDescription() {
        return description;
    }

}
