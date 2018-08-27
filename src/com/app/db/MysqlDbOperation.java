package com.app.db;

import com.app.objects.Column;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by alicanb on 31.05.2018.
 */
public class MysqlDbOperation {
    private Connection connection = null;
    private String dbName;

    public MysqlDbOperation() {

    }

    public MysqlDbOperation(String db, String url, String userName, String password) {
        this.dbName = db;
        connection(db, url, userName, password);
    }

    public void connection(String db, String url, String userName, String password) {
        String driver = "com.mysql.jdbc.Driver";
        String dbUrl = "jdbc:mysql://" + url + "/" + db;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(dbUrl, userName, password);
        } catch (Exception e) {
            System.out.println("Connection Exception: " + e.getMessage());
        }
    }

    public ArrayList<String> getDbTableNames() {
        ArrayList<String> tableNames = new ArrayList<>();
        String selectDBTableNamesQuery = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema= " + "\"" + this.dbName + "\"";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(selectDBTableNamesQuery);
            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
            return tableNames;
        } catch (Exception e) {
            System.out.println("Get Db Table Names Exception: " + e.getMessage());
            return tableNames;
        }
    }

    public ArrayList<Column> getDbTableColumns(String selectedTableName) {
        ArrayList<Column> dbColumns = new ArrayList<>();
        String selectDBTableColumnsQuery = "SHOW COLUMNS FROM " + selectedTableName;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(selectDBTableColumnsQuery);
            while (resultSet.next()) {
                dbColumns.add(new Column(resultSet.getString(1), resultSet.getString(2)));
            }
            return dbColumns;
        } catch (Exception e) {
            System.out.println("Get Db Table Columns Exception: " + e.getMessage());
            return dbColumns;
        }
    }

    public ArrayList<String> getDbTablePrimary(String selectedTableName) {
        ArrayList<String> dbPrimaryKeys = new ArrayList<>();
        String selectDBTablePrimaryKeysQuery = "SHOW INDEX FROM " + selectedTableName + " WHERE Key_name = 'PRIMARY'";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(selectDBTablePrimaryKeysQuery);
            while (resultSet.next()) {
                dbPrimaryKeys.add(resultSet.getString(5));
            }
            return dbPrimaryKeys;
        } catch (Exception e) {
            System.out.println("Get Db Table Primary Keys Exception: " + e.getMessage());
            return dbPrimaryKeys;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Connection Close Exception: " + e.getMessage());
        }
    }
}
