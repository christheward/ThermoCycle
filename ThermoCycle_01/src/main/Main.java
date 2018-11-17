/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.ChartController;
import gui.MasterSceneController;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Chris
 */
public class Main extends Application {
    
    private Stage stage;
    //private Stage welcomeStage;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        stage = primaryStage;
        Launcher();
    }
    
    private void Launcher() throws IOException, InterruptedException {
        
        // Set up master stage
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/application.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        root.setCenter(new MasterSceneController());
        
        // Set up welome stage
        //welcomeStage = new Stage();
        //welcomeStage.setAlwaysOnTop(true);
        //welcomeStage.setTitle("Welcome to thermocycle.");
        //stage.setScene(new Scene(graph, 800, 600));
        //stage.show();
        
    }
    
}
