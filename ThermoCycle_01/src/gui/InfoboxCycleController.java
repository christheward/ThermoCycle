/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import thermocycle.Properties;

/**
 *
 * @author Chris
 */
public class InfoboxCycleController extends AnchorPane {

    // FXML variables
    @FXML private TextField pressureInput;
    @FXML private TextField temperatureInput;
    @FXML private ListView listComponents;
    @FXML private ListView listFluids;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor
     * 
     */
    public InfoboxCycleController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxCycle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
        
        // Build event handlers
        buildEventHandlers();
        
    }
        
    /**
     * Refresh the infobox.
     */
    private void refresh() {
        pressureInput.setText(MasterSceneController.displayOptionalDouble(master.getModel().getAmbient(Properties.Property.PRESSURE)));
        temperatureInput.setText(MasterSceneController.displayOptionalDouble(master.getModel().getAmbient(Properties.Property.TEMPERATURE)));
    }
    
    /**
     * Builds the event handlers for the infobox
     */
    private void buildEventHandlers() {
        
        // Pressure text field handlers
        pressureInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setAmbient(Properties.Property.PRESSURE, Double.valueOf(pressureInput.getText()));
                refresh();
                event.consume();
            }
        });
        
        // Temperature text field handlers
        temperatureInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setAmbient(Properties.Property.TEMPERATURE, Double.valueOf(temperatureInput.getText()));
                refresh();
                event.consume();
            }
        });
        
    }
    
    /**
     * Connects the info panel to a new model
     */
    public void connectNewModel() {
        // Check if model exists
        if (master.isModel()) {
            // Connect to other lists
            listComponents.setItems(master.getModel().componentsReadOnly);
            listFluids.setItems(master.getModel().fluidsReadOnly);
            // Populate form
            refresh();
        }
        else {
            // Connnect to blank lists
            listComponents.setItems(FXCollections.observableList(new ArrayList<>()));
            listFluids.setItems(FXCollections.observableList(new ArrayList<>()));
        }
    }
    
}
