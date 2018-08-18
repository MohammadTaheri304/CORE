package io.zino.core.transaction.atomicTransaction.model;

import java.util.Date;

public class AtomicTransaction {
    public final static String SCHEMA_TABLE = "core.atomic_transactions";
    public final static String TABLE = "atomic_transactions";
    public final static String PROP_ID = "id";
    public static final String PROP_EXTUID = "extuid";
    public final static String PROP_FROM = "fromAccountId";
    public final static String PROP_TO = "toAccountId";
    public final static String PROP_AMOUNT = "amount";
    public final static String PROP_PUBLISH_DATE = "publishDate";
    public final static String PROP_DESCRIPTION = "description";

    private long id;

    private String extUid;
    private long fromAccountId;
    private long toAccountId;
    private Date publishDate;
    private String amount;
    private String description;

    public AtomicTransaction(String extUid, long fromAccountId, long toAccountId, String amount, String desc) {
        this.extUid = extUid;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = desc;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getExtUid() {
        return extUid;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "AtomicTransaction{" +
                "id=" + id +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount='" + amount + '\'' +
                '}';
    }
}
