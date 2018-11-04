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
public class InfoboxWorkController extends AnchorPane {

    // FXML Variables
    @FXML private TextField workInput;
    @FXML private Button buttonClearWork;
    
    private final CanvasController canvas;
    
    // Model variables
    protected thermocycle.WorkNode node;
    
    /**
     * Constructor
     * 
     */
    public InfoboxWorkController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxWork.fxml"));
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
    
    
    public void showDetails(thermocycle.WorkNode node) {
        
        // Set component
        this.node = node;
        
        // Populate the values
        populate();
        
    }
    
    /**
     * Populates the state table with properties and values.
     */
    private void populate() {
        // Set work
        workInput.setText(CanvasController.displayOptionalDouble(canvas.model.getWork(node)));
    }
    
    /**
     * Build click handlers for combo boxes and buttons
     */
    private void buildClickHandlers() {
        buttonClearWork.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.clearValue(node);
                populate();
                event.consume();
            }
        });
        
        workInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setWork(node, Double.valueOf(workInput.getText()));
                populate();
                event.consume();
            }
        });
    }
}
