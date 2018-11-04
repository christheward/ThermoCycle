/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
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

    // FXML Variables
    @FXML private TextField pressureInput;
    @FXML private TextField temperatureInput;
    @FXML private ListView listComponents;
    @FXML private ListView listFluids;
    private final CanvasController canvas;
    
    /**
     * Constructor
     * 
     */
    public InfoboxCycleController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxCycle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
        // Build event handlers
        buildEventHandlers();
        connectLists();
        populate();
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
    
    
    public void showDetails() {
    }
    
    /**
     * Populates the state table with properties and values.
     */
    private void populate() {
        pressureInput.setText(CanvasController.displayOptionalDouble(canvas.model.getAmbient(Properties.Property.PRESSURE)));
        temperatureInput.setText(CanvasController.displayOptionalDouble(canvas.model.getAmbient(Properties.Property.TEMPERATURE)));
    }
    
    private void buildEventHandlers() {
        pressureInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setAmbient(Properties.Property.PRESSURE, Double.valueOf(pressureInput.getText()));
                populate();
                event.consume();
            }
        });
        temperatureInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setAmbient(Properties.Property.TEMPERATURE, Double.valueOf(temperatureInput.getText()));
                populate();
                event.consume();
            }
        });
    }
    
    private void connectLists() {
        listComponents.setItems(canvas.model.componentsReadOnly);
        listFluids.setItems(canvas.model.fluidsReadOnly);
    }
    
}
