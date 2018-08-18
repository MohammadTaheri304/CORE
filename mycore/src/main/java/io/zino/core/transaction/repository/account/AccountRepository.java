package io.zino.core.transaction.repository.account;

import io.zino.base.db.DBConnectionFactory;
import io.zino.base.db.DBUtil;
import io.zino.core.transaction.model.account.Account;

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
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder createQuery = new StringBuilder()
                    .append(DBUtil.INSERT_INTO)
                    .append(Account.SCHEMA_TABLE)
                    .append("(")
                    .append(Account.PROP_EXTUID)
                    .append(", ")
                    .append(Account.PROP_PASSWORD)
                    .append(", ")
                    .append(Account.PROP_ACTIVE)
                    .append(")")
                    .append(DBUtil.VALUES)
                    .append("('")
                    .append(account.getExtuid())
                    .append("', '")
                    .append(account.getPassword())
                    .append("', ")
                    .append(account.isActive())
                    .append(");");
            conn.createStatement().execute(createQuery.toString());
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

    public boolean changePassword(long id, String newPassword) {
        try (Connection conn = DBConnectionFactory.getInstance().getConnection()) {
            StringBuilder updateQuery = new StringBuilder()
                    .append(DBUtil.UPDATE)
                    .append(Account.SCHEMA_TABLE)
                    .append(DBUtil.SET)
                    .append(Account.PROP_PASSWORD)
                    .append("=")
                    .append(newPassword)
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
                    .append("=" + extUid + ";");
            ResultSet selectResult = conn.createStatement().executeQuery(findScript.toString());
            if (!selectResult.next()) {
                throw new Exception("account not found");
            }
            Account ac = new Account(
                    selectResult.getString(Account.PROP_EXTUID),
                    selectResult.getString(Account.PROP_PASSWORD),
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
}
