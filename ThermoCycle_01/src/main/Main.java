/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.Chart;
import gui.Graph;
import gui.CanvasController;
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
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        stage = primaryStage;
        Launcher();
    }
    
    private void Launcher() throws IOException, InterruptedException {
        
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/application.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        root.setCenter(new CanvasController());
        
    }
    
    private void ChartTest() throws IOException, InterruptedException {
        
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root,640,480);
            scene.getStylesheets().add(getClass().getResource("/gui/application.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        Chart chart = new Chart();
        chart.add("Cat 1", 5.0);
        chart.add("Cat 2", 3.0);
        chart.add("Cat 3", 7.0);
        root.setCenter(chart);
    }
    
    private void GraphTest() throws IOException, InterruptedException {
        
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root,640,480);
            scene.getStylesheets().add(getClass().getResource("/gui/application.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        Graph graph = new Graph();
        root.setCenter(graph);
    }
    
}
