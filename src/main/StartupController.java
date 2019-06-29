/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class StartupController extends AnchorPane {
    
    @FXML
    private Button start;
    
    @FXML
    private CheckBox batchMode;
    
    /**
     * Constructor
     */
    public StartupController(EventHandler<ActionEvent> eventHandler) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/Startup.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        start.addEventHandler(ActionEvent.ACTION, eventHandler);
        
    }
    
    /**
     * Gets the command line arguments from the startup window GUI
     * @return the command line arguments
     */
    public String[] getArguments() {
        List<String> arguments = new ArrayList();
        if (batchMode.isSelected()) {
            arguments.add("-b");
        }
        return arguments.toArray(new String[0]);
    }
    
}
