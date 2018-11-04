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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Chris
 */
public class InfoboxHeatController extends AnchorPane {

    // FXML Variables
    @FXML private TextField heatInput;
    @FXML private Button buttonClearHeat;
    
    private final CanvasController canvas;
    
    // Model variables
    protected thermocycle.HeatNode node;
    
    /**
     * Constructor
     * 
     */
    public InfoboxHeatController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxHeat.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
        // Build event handlers
        buildClickHandlers();
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
        
    public void showDetails(thermocycle.HeatNode node) {
        
        // Set component
        this.node = node;
        
        // Populate the values
        populate();
        
    }
    
    /**
     * Populates the heat flux values.
     */
    private void populate() {
        // Set heat flux
        heatInput.setText(CanvasController.displayOptionalDouble(canvas.model.getHeat(node)));
    }
    
    /**
     * Build click handlers for combo boxes and buttons
     */
    private void buildClickHandlers() {
        buttonClearHeat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.clearValue(node);
                populate();
                event.consume();
            }
        });
        
        heatInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setHeat(node, Double.valueOf(heatInput.getText()));
                populate();
                event.consume();
            }
        });
    }
    
}
