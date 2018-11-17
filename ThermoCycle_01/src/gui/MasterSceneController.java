/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.OptionalDouble;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    
    // Model elements
    private thermocycle.Cycle model;
    
    /**
     * Constructor
     */
    public MasterSceneController() {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MasterScene.fxml"));
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
        
        // Setup infobox
        infobox = new InfoboxBaseController(this);
        hbox.getChildren().add(infobox);
        
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
    protected void setModel(thermocycle.Cycle model) {
        this.model = model;
        canvas.setDisable(false);
        infobox.setDisable(false);
        infobox.showDetails(canvas);
        infobox.connectNewModel();
        
    }
    
    /**
     * Closes the model
     * @param model Closes the model
     */
    protected void closeModel() {
        this.model = null;
        canvas.setDisable(true);
        infobox.setDisable(true);
        infobox.showDetails(canvas);
        canvas.clearCanvas();
    }
    
    /**
     * Gets the current model
     * @return 
     */
    protected thermocycle.Cycle getModel() {
        return model;
    }
    
    /**
     * Determines if a model has been specified
     * @return True if the model is set, false if not.
     */
    protected boolean isModel() {
        return (model != null);
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
