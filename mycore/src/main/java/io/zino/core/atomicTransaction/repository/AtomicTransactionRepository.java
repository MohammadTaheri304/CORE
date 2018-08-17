package io.zino.core.atomicTransaction.repository;

import io.zino.base.db.DBConnectionFactory;
import io.zino.core.atomicTransaction.model.AccountBalance;
import io.zino.core.atomicTransaction.model.AtomicTransaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AtomicTransactionRepository {

    private static AtomicTransactionRepository instance = new AtomicTransactionRepository();

    private AtomicTransactionRepository() {
    }

    public static AtomicTransactionRepository getInstance() {
        return instance;
    }

    public boolean create(AtomicTransaction atomicTransaction) {

        long fromLockStamp = LockMgr.getInstance().getLock(atomicTransaction.getFromAccountId()).writeLock();
        long toLockStamp = LockMgr.getInstance().getLock(atomicTransaction.getToAccountId()).writeLock();

        try (Connection conn = DBConnectionFactory.getInstance().getConnection()){
            StringBuilder fromBalanceScript = new StringBuilder()
                    .append("SELECT balance FROM core.account_balances ")
                    .append(" WHERE accountId=" + atomicTransaction.getFromAccountId() + ";");
            ResultSet fromBalanceResult = conn.createStatement().executeQuery(fromBalanceScript.toString());
            if (!fromBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal fromBalance = new BigDecimal(fromBalanceResult.getString("balance"));

            StringBuilder toBalanceScript = new StringBuilder()
                    .append("SELECT balance FROM core.account_balances ")
                    .append(" WHERE accountId=" + atomicTransaction.getToAccountId() + ";");
            ResultSet toBalanceResult = conn.createStatement().executeQuery(toBalanceScript.toString());
            if (!toBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal toBalance = new BigDecimal(toBalanceResult.getString("balance"));

            BigDecimal transactionAmount = new BigDecimal(atomicTransaction.getAmount());
            BigDecimal newFromBalance = fromBalance.add(transactionAmount.negate());
            BigDecimal newToBalance = toBalance.add(transactionAmount);

            StringBuilder transactionScriptBegin = new StringBuilder().append("BEGIN;");
            StringBuilder transactionScriptInsertTrn = new StringBuilder().append("INSERT INTO core.atomic_transactions(fromAccountId, toAccountId, amount) VALUES (" + atomicTransaction.getFromAccountId() + ", " + atomicTransaction.getToAccountId() + ", '" + transactionAmount.toPlainString() + "');");
            StringBuilder transactionScriptUpdateFrom = new StringBuilder().append("UPDATE core.account_balances SET balance='" + newFromBalance.toPlainString() + "' WHERE accountId=" + atomicTransaction.getFromAccountId() + ";");
            StringBuilder transactionScriptUpdateTo = new StringBuilder().append("UPDATE core.account_balances SET balance='" + newToBalance.toPlainString() + "' WHERE accountId=" + atomicTransaction.getToAccountId() + ";");
            StringBuilder transactionScriptCommit = new StringBuilder().append("COMMIT;");


            String query = new StringBuilder()
                    .append(transactionScriptBegin)
                    .append(transactionScriptInsertTrn)
                    .append(transactionScriptUpdateFrom)
                    .append(transactionScriptUpdateTo)
                    .append(transactionScriptCommit)
                    .toString();
            System.out.println(query);
            conn.createStatement().execute(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            LockMgr.getInstance().getLock(atomicTransaction.getFromAccountId()).unlock(fromLockStamp);
            LockMgr.getInstance().getLock(atomicTransaction.getToAccountId()).unlock(toLockStamp);
        }
    }
}
