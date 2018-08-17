package io.zino.core.atomicTransaction.model;

public class AtomicTransaction {
    public final static String TABLE = "atomic_transactions";
    public final static String PROP_ID = "id";
    public final static String PROP_FROM = "fromAccountId";
    public final static String PROP_TO = "toAccountId";

    private long id;

    private long fromAccountId;
    private long toAccountId;
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
