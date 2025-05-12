/* Name: Bryan Aneyro Hernandez
Course: CNT 4714 – Spring 2024
Assignment title: Project 1 – An Event-driven Enterprise Simulation
Date: Sunday January 28, 2024
*/

package com.example.enterpriseproject1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ShopTrip extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ShopTrip.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get CSS styles for GUI
        String stylesCSS = this.getClass().getResource("application.css").toExternalForm();
        scene.getStylesheets().add(stylesCSS);

        stage.setTitle("Welcome to ShopTrip!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}