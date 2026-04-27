package ru.mentee.power;

import java.sql.*;
import java.util.Properties;

public class DatabaseConfig {
    private Connection connection;

    public void connect(String host, String port, String dbName,
                        String user, String password) throws SQLException {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        connection = DriverManager.getConnection(url, user, password);
    }

    public void createTables(){
        String sql = "CREATE TABLE IF NOT EXISTS calculations (\n" +
                "           id SERIAL PRIMARY KEY,\n" +
                "           expression VARCHAR(255),\n" +
                "           result DOUBLE PRECISION,\n" +
                "           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                "         )";
        try (Statement stmt = connection.createStatement()){
             stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCalculation(String expression, double result){
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO calculations (expression, result) VALUES (?, ?)");){
            preparedStatement.setString(1, expression);
            preparedStatement.setDouble(2, result);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getConnectionInfo() {
        try {
            Properties properties = connection.getClientInfo();
            return properties.toString();
        } catch (SQLException e) {
            return "Unable to get connection info";
        }
    }
}