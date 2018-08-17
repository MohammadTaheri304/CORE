package io.zino.base.db;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

public class DBConnectionFactory {
    private BasicDataSource connectionPool;
    private static DBConnectionFactory instance =
            new DBConnectionFactory(
                    "jdbc:postgresql://localhost:5432/CORE",
                    "core",
                    "core");


    private DBConnectionFactory(String url, String user, String password) {
        String dbUrl = url;
        connectionPool = new BasicDataSource();
        connectionPool.setUsername(user);
        connectionPool.setPassword(password);
        connectionPool.setDriverClassName("org.postgresql.Driver");
        connectionPool.setUrl(dbUrl);
        connectionPool.setInitialSize(1);
    }

    public static DBConnectionFactory getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
