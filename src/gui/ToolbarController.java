/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author Chris
 */
public class ToolbarController extends AnchorPane {
    
    // FXML variables
    @FXML private HBox toolbox;
    @FXML private Button btnRotateCCW;
    @FXML private Button btnRotateCW;
    @FXML private Button btnUndo;
    @FXML private Button btnRedo;
    @FXML private ToggleButton btnTglBuild;
    @FXML private ToggleButton btnTglAssess;
    
    // Properties
    private IntegerProperty iconSize;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor
     */
    public ToolbarController(MasterSceneController master) {
        
        // Set Master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Toolbar.fxml"));
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
        
        // Define icon size
        iconSize = new SimpleIntegerProperty(30);
        toolbox.getChildren().stream().filter(c -> c instanceof Button).map(i -> ((ImageView)(((Button)i).getGraphic()))).forEach(i -> {
            i.fitHeightProperty().bind(iconSize);
        });
        ((ImageView)btnTglBuild.getGraphic()).fitHeightProperty().bind(iconSize);
        ((ImageView)btnTglAssess.getGraphic()).fitHeightProperty().bind(iconSize);
        
        // Format toggle button
        btnTglBuild.getStyleClass().add("toggle-left");
        btnTglAssess.getStyleClass().add("toggle-right");
        
        // Set up toggle binding
        master.buildMode.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                btnTglBuild.setOnAction(null);
                btnTglAssess.setOnAction(null);
                btnTglBuild.selectedProperty().setValue(newValue);
                btnTglAssess.selectedProperty().setValue(!newValue);
                if (newValue) {
                    btnTglAssess.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            master.buildMode.setValue(false);
                        }
                    });
                }
                else {
                    btnTglBuild.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            master.buildMode.setValue(true);
                        }
                    });
                }

            }
        });
        master.buildMode.set(false);
        master.buildMode.set(true);
        
        // Setup handlers
        buildMenuHandlers();
        
    }
    
    /**
     * Build menu handlers
     */
    private void buildMenuHandlers() {
        btnRotateCCW.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.canvas.canvasClipboard.stream().forEach(c -> {
                    c.rotateCCW();
                });
            }
        });
        btnRotateCW.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.canvas.canvasClipboard.stream().forEach(c -> {
                    c.rotateCW();
                });
            }
        });
    }
    
}
