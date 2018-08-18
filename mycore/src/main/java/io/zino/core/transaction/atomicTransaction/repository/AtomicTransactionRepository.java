package io.zino.core.transaction.atomicTransaction.repository;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.atomicTransaction.model.AccountBalance;
import io.zino.core.transaction.atomicTransaction.model.AtomicTransaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;

public class AtomicTransactionRepository {

    private static AtomicTransactionRepository instance = new AtomicTransactionRepository();

    private AtomicTransactionRepository() {
    }

    public static AtomicTransactionRepository getInstance() {
        return instance;
    }

    public boolean create(AtomicTransaction atomicTransaction) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder fromBalanceScript = new StringBuilder()
                    .append(DBUtil.SELECT)
                    .append(AccountBalance.PROP_BALANCE)
                    .append(DBUtil.FROM)
                    .append(AccountBalance.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(AccountBalance.PROP_ACCOUNT_ID)
                    .append("=" + atomicTransaction.getFromAccountId() + ";");
            ResultSet fromBalanceResult = conn.createStatement().executeQuery(fromBalanceScript.toString());
            if (!fromBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal fromBalance = new BigDecimal(fromBalanceResult.getString("balance"));

            StringBuilder toBalanceScript = new StringBuilder()
                    .append(DBUtil.SELECT)
                    .append(AccountBalance.PROP_BALANCE)
                    .append(DBUtil.FROM)
                    .append(AccountBalance.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(AccountBalance.PROP_ACCOUNT_ID)
                    .append("=" + atomicTransaction.getToAccountId() + ";");
            ResultSet toBalanceResult = conn.createStatement().executeQuery(toBalanceScript.toString());
            if (!toBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal toBalance = new BigDecimal(toBalanceResult.getString("balance"));

            BigDecimal transactionAmount = new BigDecimal(atomicTransaction.getAmount());
            BigDecimal newFromBalance = fromBalance.add(transactionAmount.negate());
            BigDecimal newToBalance = toBalance.add(transactionAmount);

            StringBuilder transactionScriptBegin = new StringBuilder().append(DBUtil.BEGIN + ";");
            StringBuilder transactionScriptInsertTrn = new StringBuilder()
                    .append(DBUtil.INSERT_INTO)
                    .append(AtomicTransaction.SCHEMA_TABLE)
                    .append("(")
                    .append(AtomicTransaction.PROP_FROM)
                    .append(", ")
                    .append(AtomicTransaction.PROP_TO)
                    .append(", ")
                    .append(AtomicTransaction.PROP_AMOUNT)
                    .append(", ")
                    .append(AtomicTransaction.PROP_DESCRIPTION)
                    .append(")")
                    .append(DBUtil.VALUES)
                    .append("(")
                    .append(atomicTransaction.getFromAccountId())
                    .append(", ")
                    .append(atomicTransaction.getToAccountId())
                    .append(", ")
                    .append("'" + transactionAmount.toPlainString() + "'")
                    .append(", ")
                    .append("'" + transactionAmount.toPlainString() + "');");
            StringBuilder transactionScriptUpdateFrom = new StringBuilder()
                    .append(DBUtil.UPDATE)
                    .append(AccountBalance.SCHEMA_TABLE)
                    .append(DBUtil.SET)
                    .append(AccountBalance.PROP_BALANCE)
                    .append("=" + newFromBalance.toPlainString())
                    .append(DBUtil.WHERE)
                    .append(AccountBalance.PROP_ACCOUNT_ID)
                    .append("=" + atomicTransaction.getFromAccountId() + ";");
            StringBuilder transactionScriptUpdateTo = new StringBuilder()
                    .append(DBUtil.UPDATE)
                    .append(AccountBalance.SCHEMA_TABLE)
                    .append(DBUtil.SET)
                    .append(AccountBalance.PROP_BALANCE)
                    .append("=" + newToBalance.toPlainString())
                    .append(DBUtil.WHERE)
                    .append(AccountBalance.PROP_ACCOUNT_ID)
                    .append("=" + atomicTransaction.getToAccountId() + ";");
            StringBuilder transactionScriptCommit = new StringBuilder().append(DBUtil.COMMIT + ";");


            String query = new StringBuilder()
                    .append(transactionScriptBegin)
                    .append(transactionScriptInsertTrn)
                    .append(transactionScriptUpdateFrom)
                    .append(transactionScriptUpdateTo)
                    .append(transactionScriptCommit)
                    .toString();
            //System.out.println(query);
            conn.createStatement().execute(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
