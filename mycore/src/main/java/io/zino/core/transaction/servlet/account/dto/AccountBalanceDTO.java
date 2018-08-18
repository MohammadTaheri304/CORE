package io.zino.core.transaction.servlet.account.dto;

import java.math.BigDecimal;

public class AccountBalanceDTO {
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
