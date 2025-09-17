package com.paradisecloud.fcm.terminal.fs.db;

import java.sql.*;

public class DatabaseUtil {

    /**
     * 创建数据库连接
     *
     * @return
     */
    public static Connection createConnection(String driverClass, String url, String username, String password) {
        Connection connection = null;
        try {
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 执行插入操作
     *
     * @param connection
     * @param insertSql
     * @throws SQLException
     */
    public static int executeInsert(Connection connection, String insertSql) {
        int result = 0;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeUpdate(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行查询操作
     *
     * @param connection
     * @param selectSql
     * @throws SQLException
     */
    public static ResultSet executeQuery(Connection connection, String selectSql) {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * 执行更新操作
     *
     * @param connection
     * @param updateSql
     * @throws SQLException
     */
    public static int executeUpdate(Connection connection, String updateSql) {
        int result = 0;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeUpdate(updateSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行删除操作
     *
     * @param connection
     * @param deleteSql
     * @throws SQLException
     */
    public static int executeDelete(Connection connection, String deleteSql) {
        int result = 0;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeUpdate(deleteSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
