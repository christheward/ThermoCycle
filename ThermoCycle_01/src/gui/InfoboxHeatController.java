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
    
    private final MasterSceneController master;
    
    // Model variables
    protected thermocycle.HeatNode node;
    
    /**
     * Constructor
     * 
     */
    public InfoboxHeatController(MasterSceneController master) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InfoboxHeat.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.master = master;
        
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
        refresh();
        
    }
    
    /**
     * Populates the heat flux values.
     */
    private void refresh() {
        // Set heat flux
        heatInput.setText(MasterSceneController.displayOptionalDouble(master.getModel().getHeatBoundaryCondition(node)));
    }
    
    /**
     * Build click handlers for combo boxes and buttons
     */
    private void buildClickHandlers() {
        buttonClearHeat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().removeBoundaryCondition(node);
                refresh();
                event.consume();
            }
        });
        
        heatInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setHeat(node, Double.valueOf(heatInput.getText()));
                refresh();
                event.consume();
            }
        });
    }
    
}
