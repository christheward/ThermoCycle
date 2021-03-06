/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import thermocycle.BoundaryCondition;

/**
 *
 * @author Chris
 */
public class ContextMenuController extends ContextMenu {
    
    // FXML variables
    @FXML private MenuItem solve;
    @FXML private CheckMenuItem nodeVisibility;
    @FXML private MenuItem report;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor
     * @param master The parent master
     */
    protected ContextMenuController (MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ContextMenu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
         
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Setup handlers
        buildMenuHandlers();
        
        // Setup bindings
        master.nodeVisibility.bindBidirectional(nodeVisibility.selectedProperty());
        solve.disableProperty().bind(master.modelReadOnly.isNull());
        report.disableProperty().bind(master.modelReadOnly.isNull());
    }
    
    /**
     * Builds input handers for dealing with key strokes.
     */
    private void buildMenuHandlers() {
        solve.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.getModel().solve(BoundaryCondition.getIdx());
                event.consume();
            }
            
        });
        report.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                event.consume();
            }
        });
    }
    
}
