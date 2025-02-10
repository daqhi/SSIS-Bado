package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fxml files/RegistrationPage.fxml"));
        Parent root = loader.load();

        MyController controller = loader.getController();
        controller.setStage(primaryStage); 

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("My Student Information System");
        primaryStage.show();
    }  

    public static void main(String[] args) {
        launch(args);
    }
}

