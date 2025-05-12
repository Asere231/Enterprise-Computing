/*
Name: Bryan Aneyro Hernandez
Course: CNT 4714 Spring 2024
Assignment title: Project 3 – A Specialized Accountant Application
Date: March 10, 2024
Class: HelloController
*/

package com.example.enterpriseproject3;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HelloController {
    @FXML
    private TextArea SQLCommandArea;
    @FXML
    private TableView<String> resultWindow;
    @FXML
    private ChoiceBox<String> dbPropertiesSelector;
    @FXML
    private ChoiceBox<String> userPropertiesSelector;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label connectionStatusLabel;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private MysqlDataSource dataSource;
    private Properties dataProperties;
    private Properties uProperties;
    private boolean connectedToDatabase = false;
    private static int queryCount = 0;
    private static int updateCount = 0;
    private static String currentUsername;

    public void initialize() {
        // Populate the ChoiceBoxes
        dbPropertiesSelector.getItems().addAll("project3.properties", "bikedb.properties", "operationslog.properties", "modeldb.properties");
        userPropertiesSelector.getItems().addAll("root.properties", "client1.properties", "client2.properties", "theaccountant.properties", "mysteryuser.properties", "userX.properties");
    }

    public void connectToDatabase() throws SQLException, IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String dbProperties = dbPropertiesSelector.getValue();
        String userProperties = userPropertiesSelector.getValue();

        if(username.isBlank()) {
            Alert usernameMissing = new Alert(Alert.AlertType.CONFIRMATION);
            usernameMissing.setTitle("Alert!!!!");
            usernameMissing.setHeaderText(null);
            usernameMissing.setContentText("Please enter USERNAME");
            usernameMissing.show();
            return;
        }

        if(password.isBlank()) {
            Alert passwordMissing = new Alert(Alert.AlertType.CONFIRMATION);
            passwordMissing.setTitle("Alert!!!!");
            passwordMissing.setHeaderText(null);
            passwordMissing.setContentText("Please enter PASSWORD");
            passwordMissing.show();
            return;
        }

        dataProperties = new Properties();
        uProperties = new Properties();
        FileInputStream filein2 =  null;
        FileInputStream filein = null;

        // Load new properties file for connection with selected database.
        try {
            filein = new FileInputStream(dbProperties);
            dataProperties.load(filein);
            filein2 = new FileInputStream(userProperties);
            uProperties.load(filein2);
            dataSource = new MysqlDataSource();

            currentUsername = uProperties.getProperty("MYSQL_DB_USERNAME");
            String p = uProperties.getProperty("MYSQL_DB_PASSWORD");
            String url = dataProperties.getProperty("MYSQL_DB_URL");

            dataSource.setURL(url);
            
            if(username.equals(currentUsername)) {
                dataSource.setUser(currentUsername);
            } else {
                connectionStatusLabel.setText("NO CONNECTION ESTABLISHED - User Credentials Don't Match Property File!");

                Alert usernameMismatch = new Alert(Alert.AlertType.CONFIRMATION);
                usernameMismatch.setTitle("Alert!!!!");
                usernameMismatch.setHeaderText(null);
                usernameMismatch.setContentText("Username is incorrect!!!");
                usernameMismatch.show();
                return;
            }

            if(password.equals(p)) {
                dataSource.setPassword(p);
            } else {
                connectionStatusLabel.setText("NO CONNECTION ESTABLISHED - User Credentials Don't Match Property File!");

                Alert passwordMismatch = new Alert(Alert.AlertType.CONFIRMATION);
                passwordMismatch.setTitle("Alert!!!!");
                passwordMismatch.setHeaderText(null);
                passwordMismatch.setContentText("Password is incorrect!!!");
                passwordMismatch.show();
                return;
            }

            connection = dataSource.getConnection();
            if (connection != null) {
                // Connection successful
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                connectedToDatabase = true;
                
                String connectionStatus = "CONNECTED TO: " + url;
                connectionStatusLabel.setText(connectionStatus);
                connectionStatusLabel.setTextFill(Color.YELLOW);

                System.out.println("Connection successful!");
            } else if(connectedToDatabase) {
                System.out.println("Connection exists already!");
            }
            else {
                System.out.println("Connection failed!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void executeSQLCommand() throws SQLException {
        String sqlCommand = SQLCommandArea.getText();
        String [] commandArray = sqlCommand.split("\\s+");
        String keyword = commandArray[0];
        String selectedUser = usernameTextField.getText();


        Properties databaseProperties = new Properties();
        Properties usersProperties = new Properties();
        FileInputStream filein2 =  null;
        FileInputStream filein = null;
        Statement newStatement = null;
        Connection newConnection = null;

        // Load new properties file for connection with operationslog database, connect and create table
        try {
            filein = new FileInputStream("operationslog.properties");
            databaseProperties.load(filein);
            filein2 = new FileInputStream("project3app.properties");
            usersProperties.load(filein2);
            MysqlDataSource newDataSource = new MysqlDataSource();

            newDataSource.setURL(databaseProperties.getProperty("MYSQL_DB_URL"));
            newDataSource.setUser(usersProperties.getProperty("MYSQL_DB_USERNAME"));
            newDataSource.setPassword(usersProperties.getProperty("MYSQL_DB_PASSWORD"));

            newConnection = newDataSource.getConnection();

            if(newConnection != null) {
                newStatement = newConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                System.out.println("project3app connected successfully!");

                ResultSet resultSet = newStatement.executeQuery("SELECT COUNT(*) AS count FROM operationscount");
                resultSet.next();
                int rowCount = resultSet.getInt("count");

                if (rowCount == 0) {
                    // Insert initial rows only if the table is empty
                    newStatement.executeUpdate("INSERT INTO operationscount(login_username, num_queries, num_updates) VALUES ('root@localhost', 0, 0)");
                    newStatement.executeUpdate("INSERT INTO operationscount(login_username, num_queries, num_updates) VALUES ('client1@localhost', 0, 0)");
                    newStatement.executeUpdate("INSERT INTO operationscount(login_username, num_queries, num_updates) VALUES ('client2@localhost', 0, 0)");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


        ResultSet resultSet = null;
        ResultSetMetaData metaData = null;

        // Execute queries and updates. Also, keep track of the query and update count to update operationslog database
        try {
            if(keyword.equals("select")) {
                resultSet = statement.executeQuery(sqlCommand);
                metaData = resultSet.getMetaData();

                if(!currentUsername.equals("theaccountant")) {
                    queryCount++;
                }

            } else {
                int updateNum = statement.executeUpdate(sqlCommand);

                if(!currentUsername.equals("theaccountant")) {
                    updateCount++;
                }

                if (updateNum > 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Update");
                    alert.setHeaderText(null);
                    alert.setContentText("Updates successfully done!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update");
                    alert.setHeaderText(null);
                    alert.setContentText("No rows were updated.");
                    alert.showAndWait();
                }
            }

            newStatement.executeUpdate("UPDATE operationscount SET num_queries = " + queryCount + ", num_updates = " + updateCount + " WHERE login_username = '" + currentUsername + "@localhost'");
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!!!");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.show();
            System.out.println(e.getMessage());
            return;
        }

        // Populate data as long as metaData is not null
        if (metaData != null) {
            int columnCount = metaData.getColumnCount();

            // Clear existing columns and items
            resultWindow.getColumns().clear();
            resultWindow.getItems().clear();

            // Create columns dynamically
            for (int i = 1; i <= columnCount; i++) {
                TableColumn<String, String> column = new TableColumn<>(metaData.getColumnName(i));
                final int columnIndex = i;
                column.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().split("\t")[columnIndex - 1]));
                resultWindow.getColumns().add(column);
            }

            // Populate data
            ObservableList<String> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                StringBuilder rowData = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.append(resultSet.getString(i)).append("\t");
                }
                data.add(rowData.toString());
            }

            resultWindow.setItems(data);
        } else {
            // Handle the case where metaData is null
            System.out.println("ResultSetMetaData is null. Query execution might have failed.");
        }
    }

    // Housekeeping
    public void disconnectFromDatabase() throws SQLException {
        connectionStatusLabel.setText("DISCONNECTED FROM DATABASE");
        connectionStatusLabel.setTextFill(Color.RED);
        statement.close();
        connection.close();

        usernameTextField.clear();
        passwordTextField.clear();
        SQLCommandArea.clear();
        resultWindow.getColumns().clear();
        resultWindow.getItems().clear();
    }

    // Housekeeping
    public void clearSQLCommand() {
        SQLCommandArea.clear();
    }

    // Housekeeping
    public void clearResultWindow() {
        resultWindow.getColumns().clear();
        resultWindow.getItems().clear();
    }
}