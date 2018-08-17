package io.zino.core.atomicTransaction.model;

import java.util.Date;

public class AtomicTransaction {
    public final static String SCHEMA_TABLE = "core.atomic_transactions";
    public final static String TABLE = "atomic_transactions";
    public final static String PROP_ID = "id";
    public final static String PROP_FROM = "fromAccountId";
    public final static String PROP_TO = "toAccountId";
    public final static String PROP_AMOUNT = "amount";
    public final static String PROP_PUBLISH_DATE = "publishDate";

    private long id;

    private long fromAccountId;
    private long toAccountId;
    private Date publishDate;
    private String amount;

    public AtomicTransaction(long fromAccountId, long toAccountId, String amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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
