package io.zino.core.transaction.model.account;

import java.math.BigDecimal;
import java.util.Date;

public class AccountBalance {
    public final static String SCHEMA_TABLE = "core.account_balances";
    public final static String TABLE = "account_balances";

    public static final String PROP_ID = "id";
    public static final String PROP_CREATION_DATE = "creationDate";
    public static final String PROP_ACCOUNT_ID = "accountId";
    public static final String PROP_LAST_BALANCE_ID = "lastBalanceId";
    public static final String PROP_TRANSACTION_EXTUID = "transactionExtUid";
    public final static String PROP_BALANCE = "balance";

    private long id;
    private Date creationDate;
    private long accountId;
    private Long lastBalanceId;
    private String transactionExtUid;
    private BigDecimal balance;

    public AccountBalance(long accountId, Long lastBalanceId, String transactionExtUid, BigDecimal balance) {
        this.accountId = accountId;
        this.lastBalanceId = lastBalanceId;
        this.transactionExtUid = transactionExtUid;
        this.balance = balance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getAccountId() {
        return accountId;
    }

    public Long getLastBalanceId() {
        return lastBalanceId;
    }

    public String getTransactionExtUid() {
        return transactionExtUid;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
