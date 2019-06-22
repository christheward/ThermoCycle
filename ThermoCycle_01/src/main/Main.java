/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.MasterSceneController;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import picocli.CommandLine;

/**
 *
 * @author Chris
 */
public class Main extends Application {
    
    /**
     * Class describing the command line arguments.
     */
    private static final CommandLineArguments commandLineArguments = new CommandLineArguments();
    
    /**
     * The main application stage.
     */
    private Stage stage;
    
    /**
     * The startup window stage.
     */
    private Stage startupStage;
    
    /**
     * Main program.
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        
        // Test arguments
        //String[] args_test = {"-h","-v","-c","test.cyc"};
        String[] args_test = {"-d"};
        
        // launch program
        Application.launch(Main.class, args_test);
    }
    
    
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        
        // Parse command line inputs
        CommandLine cmd = new CommandLine(commandLineArguments);
        cmd.parse(getParameters().getUnnamed().toArray(new String[0]));
        
        // If help is requeted
        if (cmd.isUsageHelpRequested()) {
            // Show command line help
            System.out.println(cmd.getUsageMessage());
            System.exit(0);
        }
        
        // specify verbose log4j2 configuration file location
        System.setProperty("log4j.configurationFile", "./src/resources/logging/log4j2.xml");
            
        // Set stage for main gui
        stage = primaryStage;
        
        if (cmd.parseArgs(getParameters().getUnnamed().toArray(new String[0])).matchedOptionValue("developer", false).booleanValue()) {
            System.out.println("Starting in developer mode.");
            launchGui();
        }
        else {
            // Launch startup window
            launchStartupGui();
        }
        
    }
    
    /**
     * Launches the startup window.
     */
    private void launchStartupGui() {
        
        // TODO: Set gui variables based on command line arguments?
        
        startupStage = new Stage();
        BorderPane startupRoot = new BorderPane();
        Scene startupScene = new Scene(startupRoot);
        startupStage.setScene(startupScene);
        startupStage.show();
        
        // Attach event listener to start button.
        StartupController startup = new StartupController(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startupStage.close();
                launchGui();
                event.consume();
            }
        });
        startupRoot.setCenter(startup);
        
    }
    
    /**
     * Launches the main application GUI.
     */
    private void launchGui() {
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        root.setCenter(new MasterSceneController());
    }
    
    @Override
    public void init() {
        // This is called before start()
        // Heavy lifting happens here.
        // THis is called prior to start.
        // Pre loader is shown during this stage.
    }
    
}
