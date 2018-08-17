package io.zino.core.atomicTransaction.repository;

import io.zino.base.db.DBConnectionFactory;
import io.zino.core.atomicTransaction.model.AccountBalance;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountBalanceRepository {
    private static AccountBalanceRepository instance = new AccountBalanceRepository();

    private AccountBalanceRepository(){}

    public static AccountBalanceRepository getInstance() {
        return instance;
    }

    public boolean createAccountBalance(AccountBalance accountBalance) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()){
            StringBuilder createQuery = new StringBuilder()
                    .append("INSERT INTO core.account_balances( accountId, balance)")
                    .append(" VALUES (" + accountBalance.getAccountId() + ", " + accountBalance.getBalance() + ")");
            boolean isSucc = conn.createStatement().execute(createQuery.toString());
            return isSucc;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public BigDecimal getAccountBalance(long accountId) {
        long lockStamp = LockMgr.getInstance().getLock(accountId).writeLock();
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()){
            StringBuilder balanceScript = new StringBuilder()
                    .append("SELECT balance FROM core.account_balances ")
                    .append(" WHERE accountId=" + accountId + ";");
            ResultSet balanceResult = conn.createStatement().executeQuery(balanceScript.toString());
            if(! balanceResult.next()){
                throw new Exception("account balance not found");
            }
            BigDecimal balance = new BigDecimal(balanceResult.getString("balance"));
            return balance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            LockMgr.getInstance().getLock(accountId).unlock(lockStamp);
        }
    }
}
