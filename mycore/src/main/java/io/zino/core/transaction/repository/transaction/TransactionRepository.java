package io.zino.core.transaction.repository.transaction;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.model.transaction.Transaction;

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

    public boolean create(Transaction transaction) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder createQuery = new StringBuilder()
                    .append(DBUtil.INSERT_INTO)
                    .append(Transaction.SCHEMA_TABLE)
                    .append("(")
                    .append(Transaction.PROP_EXTUID)
                    .append(", ")
                    .append(Transaction.PROP_FROM_ACCOUNT_ID)
                    .append(", ")
                    .append(Transaction.PROP_TEMP_ACCOUNT_ID)
                    .append(", ")
                    .append(Transaction.PROP_TO_ACCOUNT_ID)
                    .append(", ")
                    .append(Transaction.PROP_AMOUNT)
                    .append(", ")
                    .append(Transaction.PROP_DESCRIPTION)
                    .append(")")
                    .append(DBUtil.VALUES)
                    .append("('")
                    .append(transaction.getExtuid())
                    .append("', ")
                    .append(transaction.getFromAccountId())
                    .append(", ")
                    .append(transaction.getTempAccountId())
                    .append(", ")
                    .append(transaction.getToAccountId())
                    .append(", ")
                    .append(transaction.getAmount())
                    .append(", '")
                    .append(transaction.getDescription())
                    .append("');");
            conn.createStatement().execute(createQuery.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verify(long id) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder updateQuery = new StringBuilder()
                    .append(DBUtil.UPDATE)
                    .append(Transaction.SCHEMA_TABLE)
                    .append(DBUtil.SET)
                    .append(Transaction.PROP_VERIFY)
                    .append("=")
                    .append(true)
                    .append(", ")
                    .append(Transaction.PROP_VERIFY)
                    .append("=")
                    .append("NOW()")
                    .append(DBUtil.WHERE)
                    .append(Transaction.PROP_ID)
                    .append("=")
                    .append(id)
                    .append(";");
            conn.createStatement().executeUpdate(updateQuery.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Transaction findByExtUid(String extUid) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder findScript = new StringBuilder()
                    .append(DBUtil.SELECT)
                    .append(" * ")
                    .append(DBUtil.FROM)
                    .append(Transaction.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(Transaction.PROP_EXTUID)
                    .append("=" + extUid + ";");
            ResultSet selectResult = conn.createStatement().executeQuery(findScript.toString());
            if (!selectResult.next()) {
                throw new Exception("transaction not found");
            }
            Transaction ac = new Transaction(
                    selectResult.getString(Transaction.PROP_EXTUID),
                    selectResult.getLong(Transaction.PROP_FROM_ACCOUNT_ID),
                    selectResult.getLong(Transaction.PROP_TEMP_ACCOUNT_ID),
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

    public Boolean inqueryByExtUid(String extUid) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder findScript = new StringBuilder()
                    .append(DBUtil.SELECT)
                    .append(Transaction.PROP_VERIFY)
                    .append(DBUtil.FROM)
                    .append(Transaction.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(Transaction.PROP_EXTUID)
                    .append("=" + extUid + ";");
            ResultSet selectResult = conn.createStatement().executeQuery(findScript.toString());
            if (!selectResult.next()) {
                throw new Exception("transaction not found");
            }

            return selectResult.getBoolean(Transaction.PROP_VERIFY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete(long id) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder updateQuery = new StringBuilder()
                    .append(DBUtil.DELETE_FROM)
                    .append(Transaction.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(Transaction.PROP_ID)
                    .append("=")
                    .append(id)
                    .append(";");
            conn.createStatement().executeUpdate(updateQuery.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
