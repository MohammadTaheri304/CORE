package io.zino.core.transaction.repository.transaction;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.model.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRepository {


    private static final TransactionRepository instance = new TransactionRepository();

    private TransactionRepository() {
    }

    public static TransactionRepository getInstance() {
        return instance;
    }

    private String getBalanceQuery(long id){
        StringBuilder query = new StringBuilder()
                .append(DBUtil.SELECT)
                .append(Account.PROP_BALANCE)
                .append(DBUtil.FROM)
                .append(Account.SCHEMA_TABLE)
                .append(DBUtil.WHERE)
                .append(Account.PROP_ID)
                .append("=" + id + ";");

        return query.toString();
    }

    private String insertTransactionQuery(Transaction transaction){
        StringBuilder createQuery = new StringBuilder()
                .append(DBUtil.INSERT_INTO)
                .append(Transaction.SCHEMA_TABLE)
                .append("(")
                .append(Transaction.PROP_EXTUID)
                .append(", ")
                .append(Transaction.PROP_FROM_ACCOUNT_ID)
                .append(", ")
                .append(Transaction.PROP_TO_ACCOUNT_ID)
                .append(", ")
                .append(Transaction.PROP_AMOUNT)
                .append(", ")
                .append(Transaction.PROP_DESCRIPTION)
                .append(", ")
                .append(Transaction.PROP_VERIFY)
                .append(")")
                .append(DBUtil.VALUES)
                .append("('")
                .append(transaction.getExtuid())
                .append("', ")
                .append(transaction.getFromAccountId())
                .append(", ")
                .append(transaction.getToAccountId())
                .append(", ")
                .append(transaction.getAmount())
                .append(", '")
                .append(transaction.getDescription())
                .append("', ")
                .append(false)
                .append(");");

        return createQuery.toString();
    }

    private String updateAccountBalanceQuery(long id, BigDecimal newBalance){
        StringBuilder updateQuery = new StringBuilder()
                .append(DBUtil.UPDATE)
                .append(Account.SCHEMA_TABLE)
                .append(DBUtil.SET)
                .append(Account.PROP_BALANCE)
                .append("=" + newBalance)
                .append(DBUtil.WHERE)
                .append(Account.PROP_ID)
                .append("=" + id + ";");

        return updateQuery.toString();
    }

    private String updateTransactionVerifyQuery(long id){
        StringBuilder updateQuery = new StringBuilder()
                .append(DBUtil.UPDATE)
                .append(Transaction.SCHEMA_TABLE)
                .append(DBUtil.SET)
                .append(Transaction.PROP_VERIFY)
                .append("=")
                .append(true)
                .append(", ")
                .append(Transaction.PROP_VERIFY_DATE)
                .append("=")
                .append("NOW()")
                .append(DBUtil.WHERE)
                .append(Transaction.PROP_ID)
                .append("=")
                .append(id)
                .append(";");
        return updateQuery.toString();
    }

    private String selectTransactionQuery(String extUid){
        StringBuilder findScript = new StringBuilder()
                .append(DBUtil.SELECT)
                .append(" * ")
                .append(DBUtil.FROM)
                .append(Transaction.SCHEMA_TABLE)
                .append(DBUtil.WHERE)
                .append(Transaction.PROP_EXTUID)
                .append("='" + extUid + "';");

        return findScript.toString();
    }

    private String selectTransactionQuery(long id){
        StringBuilder findScript = new StringBuilder()
                .append(DBUtil.SELECT)
                .append(" * ")
                .append(DBUtil.FROM)
                .append(Transaction.SCHEMA_TABLE)
                .append(DBUtil.WHERE)
                .append(Transaction.PROP_ID)
                .append("=" + id + ";");

        return findScript.toString();
    }

    private String deleteTransactionQuery(long id){
        StringBuilder deleteQuery = new StringBuilder()
                .append(DBUtil.DELETE_FROM)
                .append(Transaction.SCHEMA_TABLE)
                .append(DBUtil.WHERE)
                .append(Transaction.PROP_ID)
                .append("=")
                .append(id)
                .append(";");
        return deleteQuery.toString();
    }

    public boolean create(Transaction transaction) {

        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("zero/negetive amount!!");
            }

            ResultSet fromBalanceResult = conn.createStatement().executeQuery(getBalanceQuery(transaction.getFromAccountId()));
            if (!fromBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal fromBalance = fromBalanceResult.getBigDecimal(Account.PROP_BALANCE);
            BigDecimal newFromBalance = fromBalance.add(transaction.getAmount().negate());

            if (newFromBalance.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("not enough money!");
            }

            String query = new StringBuilder()
                    .append(DBUtil.BEGIN + ";")
                    .append(insertTransactionQuery(transaction))
                    .append(updateAccountBalanceQuery(transaction.getFromAccountId(), newFromBalance))
                    .append(DBUtil.COMMIT + ";")
                    .toString();
            conn.setAutoCommit(false);
            conn.createStatement().execute(query);
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verify(long id) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            Transaction transaction = findById(id);

            ResultSet toBalanceResult = conn.createStatement().executeQuery(getBalanceQuery(transaction.getToAccountId()));
            if (!toBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            BigDecimal toBalance = toBalanceResult.getBigDecimal(Account.PROP_BALANCE);
            BigDecimal newToBalance = toBalance.add(transaction.getAmount());

            String query = new StringBuilder()
                    .append(DBUtil.BEGIN + ";")
                    .append(updateTransactionVerifyQuery(id))
                    .append(updateAccountBalanceQuery(transaction.getToAccountId(), newToBalance))
                    .append(DBUtil.COMMIT + ";")
                    .toString();
            conn.setAutoCommit(false);
            conn.createStatement().execute(query.toString());
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Transaction findByExtUid(String extUid) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {

            ResultSet selectResult = conn.createStatement().executeQuery(selectTransactionQuery(extUid));
            if (!selectResult.next()) {
                throw new Exception("transaction not found");
            }
            Transaction ac = new Transaction(
                    selectResult.getString(Transaction.PROP_EXTUID),
                    selectResult.getLong(Transaction.PROP_FROM_ACCOUNT_ID),
                    selectResult.getLong(Transaction.PROP_TO_ACCOUNT_ID),
                    selectResult.getBigDecimal(Transaction.PROP_AMOUNT),
                    selectResult.getString(Transaction.PROP_DESCRIPTION)
            );

            ac.setId(selectResult.getLong(Transaction.PROP_ID));
            ac.setVerify(selectResult.getBoolean(Transaction.PROP_VERIFY));
            ac.setVerifyDate(selectResult.getDate(Transaction.PROP_VERIFY_DATE));

            return ac;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Transaction findById(long id) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {

            ResultSet selectResult = conn.createStatement().executeQuery(selectTransactionQuery(id));
            if (!selectResult.next()) {
                throw new Exception("transaction not found");
            }
            Transaction ac = new Transaction(
                    selectResult.getString(Transaction.PROP_EXTUID),
                    selectResult.getLong(Transaction.PROP_FROM_ACCOUNT_ID),
                    selectResult.getLong(Transaction.PROP_TO_ACCOUNT_ID),
                    selectResult.getBigDecimal(Transaction.PROP_AMOUNT),
                    selectResult.getString(Transaction.PROP_DESCRIPTION)
            );

            ac.setId(selectResult.getLong(Transaction.PROP_ID));
            ac.setVerify(selectResult.getBoolean(Transaction.PROP_VERIFY));
            ac.setVerifyDate(selectResult.getDate(Transaction.PROP_VERIFY_DATE));

            return ac;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete(long id) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {

            conn.createStatement().execute(deleteTransactionQuery(id));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
