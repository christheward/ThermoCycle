/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.OptionalDouble;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thermocycle.Cycle;

/**
 *
 * @author Chris
 */
public class MasterSceneController extends VBox {
    
    // FXML variables
    @FXML private VBox vbox;
    @FXML private HBox hbox;
    @FXML private SplitPane splitpane;
    
    // GUI elements
    protected MenubarController menubar;
    protected CanvasController canvas;
    protected ConsoleController console;
    protected InfoboxBaseController infobox;
    protected GraphController graph;
    protected ChartController chart;
    
    // Properties
    protected BooleanProperty modelAbsent;
    protected BooleanProperty nodeVisibility;
    
    // Model elements
    private thermocycle.Cycle model;
    
    // Logger
    static private final Logger logger = LogManager.getLogger("GUILog");
    
    /**
     * Constructor
     */
    public MasterSceneController() {
        
        // Create properties
        modelAbsent = new SimpleBooleanProperty(true);
        nodeVisibility = new SimpleBooleanProperty(false);
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MasterScene.fxml"));
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
        
        // Set up menubar
        menubar = new MenubarController(this);
        vbox.getChildren().add(0, menubar);
        menubar.setVisible(true);
        
        // Set up toolbar
        // Not implemented yet
        
        // Set up canvas
        canvas = new CanvasController(this);
        canvas.setDisable(true);
        splitpane.getItems().add(canvas);
        
        // Set up console
        console = new ConsoleController(this);
        splitpane.getItems().add(console);
        
        // Set up split pane
        splitpane.setDividerPositions(0.75); // Needed because scenebuilder keeps overwriting this.
        
        // Setup infobox
        infobox = new InfoboxBaseController(this);
        hbox.getChildren().add(infobox);
        
        // Setup bindings
        canvas.disableProperty().bind(modelAbsent);
        infobox.disableProperty().bind(modelAbsent);
        
    }
    
    /**
     * Open a new window with a graph connected to a model
     */
    protected void openGraph() {
        graph = new GraphController(this);
        Stage stage = new Stage();
        stage.setTitle("Graph");
        stage.setScene(new Scene(graph, 800, 600));
        stage.show();
    }
    
    /**
     * Open a new window with a graph connected to a model
     */
    protected void openChart() {
        chart = new ChartController(this);
        Stage stage = new Stage();
        stage.setTitle("Chart");
        stage.setScene(new Scene(chart, 800, 600));
        stage.show();
    }
    
    /**
     * Sets the model variable
     * @param model The new model
     */
    protected void setModel(Cycle model) {
        this.model = model;
        modelAbsent.setValue(false);
        infobox.showDetails(canvas);
        infobox.connectNewModel();
    }
    
    /**
     * Closes the model
     * @param model Closes the model
     */
    protected void closeModel() {
        this.model = null;
        modelAbsent.setValue(true);
        infobox.showDetails(canvas);
        canvas.clearCanvas();
    }
    
    /**
     * Gets the current model
     * @return 
     */
    protected Cycle getModel() {
        return model;
    }
    
    /**
     * Converts the optional double to a string for display
     * @param value The optional double to print.
     * @return Returns a string to print.
     */
    public static String displayOptionalDouble(OptionalDouble value) {
        if (value.isPresent()) {
            return String.valueOf(value.getAsDouble());
        }
        return "";
    }
    
}
