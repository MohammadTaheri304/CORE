package io.zino.core.transaction.repository.account;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.model.account.Account;
import io.zino.core.transaction.model.account.AccountBalance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepository {

    private static final AccountRepository instance = new AccountRepository();

    private AccountRepository() {
    }

    public static AccountRepository getInstance() {
        return instance;
    }

    public boolean create(Account account) {
        StringBuilder createAccountQuery = new StringBuilder()
                .append(DBUtil.INSERT_INTO)
                .append(Account.SCHEMA_TABLE)
                .append("(")
                .append(Account.PROP_EXTUID)
                .append(", ")
                .append(Account.PROP_ACTIVE)
                .append(")")
                .append(DBUtil.VALUES)
                .append("('")
                .append(account.getExtuid())
                .append("', ")
                .append(account.isActive())
                .append(");");

        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            conn.createStatement().execute(createAccountQuery.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeActive(long id, boolean newActive) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder updateQuery = new StringBuilder()
                    .append(DBUtil.UPDATE)
                    .append(Account.SCHEMA_TABLE)
                    .append(DBUtil.SET)
                    .append(Account.PROP_ACTIVE)
                    .append("=")
                    .append(newActive)
                    .append(DBUtil.WHERE)
                    .append(Account.PROP_ID)
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

    public Account findByExtUid(String extUid) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder findScript = new StringBuilder()
                    .append(DBUtil.SELECT)
                    .append(" * ")
                    .append(DBUtil.FROM)
                    .append(Account.SCHEMA_TABLE)
                    .append(DBUtil.WHERE)
                    .append(Account.PROP_EXTUID)
                    .append("='" + extUid + "';");
            ResultSet selectResult = conn.createStatement().executeQuery(findScript.toString());
            if (!selectResult.next()) {
                throw new Exception("account not found");
            }
            Account ac = new Account(
                    selectResult.getString(Account.PROP_EXTUID),
                    selectResult.getBoolean(Account.PROP_ACTIVE)
            );

            ac.setId(selectResult.getLong(Account.PROP_ID));
            ac.setCreationDate(selectResult.getDate(Account.PROP_CREATION_DATE));

            return ac;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountBalance getBalance(String extUid) {
        StringBuilder query = new StringBuilder()
                .append(DBUtil.SELECT)
                .append("*")
                .append(DBUtil.FROM)
                .append(AccountBalance.SCHEMA_TABLE)
                .append(DBUtil.INNER_JOIN)
                .append(Account.SCHEMA_TABLE)
                .append(DBUtil.ON)
                .append("(")
                .append(AccountBalance.TABLE + "." + AccountBalance.PROP_ACCOUNT_ID)
                .append("=")
                .append(Account.TABLE + "." + Account.PROP_ID)
                .append(")")
                .append(DBUtil.WHERE)
                .append(Account.PROP_EXTUID)
                .append("='" + extUid+"'")
                .append(DBUtil.ORDER_BY)
                .append(AccountBalance.TABLE+"."+AccountBalance.PROP_ID)
                .append(DBUtil.DESC)
                .append(DBUtil.LIMIT)
                .append(1);

        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            ResultSet balanceResult = conn.createStatement().executeQuery(query.toString());
            if (balanceResult.next()) {
                AccountBalance lastAccountBalance = new AccountBalance(
                        balanceResult.getLong(AccountBalance.PROP_ACCOUNT_ID),
                        balanceResult.getLong(AccountBalance.PROP_LAST_BALANCE_ID),
                        balanceResult.getString(AccountBalance.PROP_TRANSACTION_EXTUID),
                        balanceResult.getBigDecimal(AccountBalance.PROP_BALANCE)
                );
                lastAccountBalance.setId(balanceResult.getLong(AccountBalance.PROP_ID));
                lastAccountBalance.setCreationDate(balanceResult.getDate(AccountBalance.PROP_CREATION_DATE));
                return lastAccountBalance;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
