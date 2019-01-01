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

/**
 *
 * @author Chris
 */
public class ContextMenuController extends ContextMenu {
    
    // FXML variables
    @FXML private MenuItem contextSolve;
    @FXML private CheckMenuItem contextNodeVisibility;
    @FXML private MenuItem contextSummary;
    
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
        buildMenuHandlers();
    }
    
    /**
     * Builds input handers for dealing with key strokes.
     */
    private void buildMenuHandlers() {
        contextSolve.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.getModel().solve();
                event.consume();
            }
            
        });
        contextNodeVisibility.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.canvas.setNodeVisibility(contextNodeVisibility.isSelected());
                event.consume();
            }
            
        });
        contextSummary.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                event.consume();
            }
        });
    }
    
}
