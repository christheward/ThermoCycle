/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.RootLayout;
import gui.Chart;
import gui.Graph;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import static thermocycle.ThermoCycle.Turbine_Test;

/**
 *
 * @author Chris
 */
public class Main extends Application {
    
    private Stage stage;
    private boolean loaded;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override public void start(Stage primaryStage) throws IOException, InterruptedException {
        stage = primaryStage;
        //DAD();
        //ChartTest();
        //GraphTest();
        try {
            Turbine_Test();
        } catch (ExecutionException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void DAD() throws IOException, InterruptedException {
        
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root,640,480);
            scene.getStylesheets().add(getClass().getResource("/gui/application.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        root.setCenter(new RootLayout());
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
