package io.zino.core.transaction.atomicTransaction.repository;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.atomicTransaction.model.AccountBalance;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountBalanceRepository {
    private static AccountBalanceRepository instance = new AccountBalanceRepository();

    private AccountBalanceRepository() {
    }

    public static AccountBalanceRepository getInstance() {
        return instance;
    }

    public boolean createAccountBalance(AccountBalance accountBalance) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder createQuery = new StringBuilder()
                    .append(DBUtil.INSERT_INTO)
                    .append(AccountBalance.SCHEMA_TABLE)
                    .append(" ( ")
                    .append(AccountBalance.PROP_ACCOUNT_ID)
                    .append(", ")
                    .append(AccountBalance.PROP_BALANCE)
                    .append(")")
                    .append(DBUtil.VALUES)
                    .append("(" + accountBalance.getAccountId() + ", " + accountBalance.getBalance() + ")");
            conn.createStatement().execute(createQuery.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public BigDecimal getAccountBalance(long accountId) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder balanceScript = new StringBuilder()
                    .append(DBUtil.SELECT)
                    .append(AccountBalance.PROP_BALANCE)
                    .append(DBUtil.FROM)
                    .append(AccountBalance.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(AccountBalance.PROP_ACCOUNT_ID)
                    .append("=" + accountId + ";");
            ResultSet balanceResult = conn.createStatement().executeQuery(balanceScript.toString());
            if (!balanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal balance = new BigDecimal(balanceResult.getString("balance"));
            return balance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
