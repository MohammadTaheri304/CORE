package io.zino.core.transaction.repository.transaction;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.model.account.AccountBalance;
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

    private String getAccountBalanceQuery(long id) {
        StringBuilder query = new StringBuilder()
                .append(DBUtil.SELECT)
                .append("*")
                .append(DBUtil.FROM)
                .append(AccountBalance.SCHEMA_TABLE)
                .append(DBUtil.WHERE)
                .append(AccountBalance.PROP_ACCOUNT_ID)
                .append("=" + id)
                .append(DBUtil.ORDER_BY)
                .append(AccountBalance.PROP_ID)
                .append(DBUtil.DESC)
                .append(DBUtil.LIMIT)
                .append(1);

        return query.toString();
    }

    private String insertTransactionQuery(Transaction transaction) {
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

    private String updateAccountBalanceQuery(AccountBalance accountBalance) {
        StringBuilder updateQuery = new StringBuilder()
                .append(DBUtil.INSERT_INTO)
                .append(AccountBalance.SCHEMA_TABLE)
                .append("(")
                .append(AccountBalance.PROP_ACCOUNT_ID)
                .append(", ")
                .append(AccountBalance.PROP_LAST_BALANCE_ID)
                .append(", ")
                .append(AccountBalance.PROP_TRANSACTION_EXTUID)
                .append(", ")
                .append(AccountBalance.PROP_BALANCE)
                .append(")")
                .append(DBUtil.VALUES)
                .append("(")
                .append(accountBalance.getAccountId())
                .append(", ")
                .append(accountBalance.getLastBalanceId())
                .append(", '")
                .append(accountBalance.getTransactionExtUid())
                .append("', ")
                .append(accountBalance.getBalance())
                .append(");");

        return updateQuery.toString();
    }

    private String updateTransactionVerifyQuery(long id) {
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

    private String selectTransactionQuery(String extUid) {
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

    private String selectTransactionQuery(long id) {
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

    private String deleteTransactionQuery(long id) {
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

            ResultSet fromBalanceResult = conn.createStatement().executeQuery(getAccountBalanceQuery(transaction.getFromAccountId()));
            if (!fromBalanceResult.next()) {
                throw new Exception("account balance not found");
            }
            AccountBalance fromLastAccountBalance = new AccountBalance(
                    fromBalanceResult.getLong(AccountBalance.PROP_ACCOUNT_ID),
                    fromBalanceResult.getLong(AccountBalance.PROP_LAST_BALANCE_ID),
                    fromBalanceResult.getString(AccountBalance.PROP_TRANSACTION_EXTUID),
                    fromBalanceResult.getBigDecimal(AccountBalance.PROP_BALANCE)
            );
            fromLastAccountBalance.setId( fromBalanceResult.getLong(AccountBalance.PROP_ID));
            fromLastAccountBalance.setCreationDate( fromBalanceResult.getDate(AccountBalance.PROP_CREATION_DATE));

            BigDecimal newFromBalance = fromLastAccountBalance.getBalance().add(transaction.getAmount().negate());
            if (newFromBalance.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("not enough money!");
            }

            AccountBalance newLastAccountBalance = new AccountBalance(
                    transaction.getFromAccountId(),
                    fromLastAccountBalance.getId(),
                    transaction.getExtuid(),
                    newFromBalance
                    );


            String query = new StringBuilder()
                    .append(DBUtil.BEGIN + ";")
                    .append(insertTransactionQuery(transaction))
                    .append(updateAccountBalanceQuery(newLastAccountBalance))
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

            AccountBalance newLastAccountBalance = null;

            ResultSet toBalanceResult = conn.createStatement().executeQuery(getAccountBalanceQuery(transaction.getToAccountId()));
            if (toBalanceResult.next()) {
                AccountBalance toLastAccountBalance = new AccountBalance(
                        toBalanceResult.getLong(AccountBalance.PROP_ACCOUNT_ID),
                        toBalanceResult.getLong(AccountBalance.PROP_LAST_BALANCE_ID),
                        toBalanceResult.getString(AccountBalance.PROP_TRANSACTION_EXTUID),
                        toBalanceResult.getBigDecimal(AccountBalance.PROP_BALANCE)
                );
                toLastAccountBalance.setId( toBalanceResult.getLong(AccountBalance.PROP_ID));
                toLastAccountBalance.setCreationDate( toBalanceResult.getDate(AccountBalance.PROP_CREATION_DATE));

                BigDecimal newToBalance = toLastAccountBalance.getBalance().add(transaction.getAmount());
                newLastAccountBalance = new AccountBalance(
                        transaction.getToAccountId(),
                        toLastAccountBalance.getId(),
                        transaction.getExtuid(),
                        newToBalance
                );
            }else{
                BigDecimal newToBalance = transaction.getAmount();
                newLastAccountBalance = new AccountBalance(
                        transaction.getToAccountId(),
                        null,
                        transaction.getExtuid(),
                        newToBalance
                );
            }

            String query = new StringBuilder()
                    .append(DBUtil.BEGIN + ";")
                    .append(updateTransactionVerifyQuery(id))
                    .append(updateAccountBalanceQuery(newLastAccountBalance))
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
