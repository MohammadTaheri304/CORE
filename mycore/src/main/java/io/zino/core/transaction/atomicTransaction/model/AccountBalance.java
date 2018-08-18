package io.zino.core.transaction.atomicTransaction.model;

public class AccountBalance {
    public final static String SCHEMA_TABLE = "core.account_balances";
    public final static String TABLE = "account_balances";
    public final static String PROP_ID = "id";
    public final static String PROP_ACCOUNT_ID = "accountId";
    public final static String PROP_BALANCE = "balance";

    private long id;

    private long accountId;
    private String balance;

    public AccountBalance(long accountId, String balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getBalance() {
        return balance;
    }
}
