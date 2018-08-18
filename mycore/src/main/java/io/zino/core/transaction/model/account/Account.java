package io.zino.core.transaction.model.account;

import java.math.BigDecimal;
import java.util.Date;

public class Account {
    public final static String SCHEMA_TABLE = "core.accounts";
    public final static String TABLE = "accounts";

    public static final String PROP_ID = "id";
    public static final String PROP_EXTUID = "extuid";
    public static final String PROP_CREATION_DATE = "creationDate";
    public static final String PROP_ACTIVE = "active";
    public final static String PROP_BALANCE = "balance";

    private long id;
    private String extuid;
    private Date creationDate;
    private boolean active;
    private BigDecimal balance;

    public Account(String extuid, boolean active, BigDecimal balance) {
        this.extuid = extuid;
        this.active = active;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public String getExtuid() {
        return extuid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
