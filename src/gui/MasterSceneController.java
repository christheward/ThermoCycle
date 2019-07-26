/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import thermocycle.Cycle;
import thermocycle.UnitsControl.UnitsSystem;

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
    protected ToolbarController toolbar;
    protected CanvasController canvas;
    protected ConsoleController console;
    protected InfoboxController infobox;
    protected GraphController graph;
    protected ChartController chart;
    
    // Properties
    private final ReadOnlyObjectWrapper<Cycle> model;
    protected final ReadOnlyObjectProperty<Cycle> modelReadOnly;
    protected final ObjectProperty<UnitsSystem> unitsSystem;
    protected final BooleanProperty buildMode;
    
    protected final BooleanProperty nodeVisibility;
    protected final BooleanProperty nameVisibility;
    protected final BooleanProperty toolboxLock;
    private final ReadOnlyObjectWrapper<Node> focus;
    private final BooleanProperty focusAbsent;
    
    /**
     * Constructor
     */
    public MasterSceneController() {
        
        // Create properties and bindings
        model = new ReadOnlyObjectWrapper();
        modelReadOnly = model.getReadOnlyProperty();
        unitsSystem = new SimpleObjectProperty();
        
        nodeVisibility = new SimpleBooleanProperty(true);
        nameVisibility = new SimpleBooleanProperty(true);
        toolboxLock = new SimpleBooleanProperty(false);
        focus = new ReadOnlyObjectWrapper();
        focusAbsent = new SimpleBooleanProperty(true);
        buildMode = new SimpleBooleanProperty(true);
        
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
        
        // Set up toolbar
        toolbar = new ToolbarController(this);
        vbox.getChildren().add(1, toolbar);
        toolbar.disableProperty().bind(modelReadOnly.isNull());
        
        // Set up canvas
        canvas = new CanvasController(this);
        canvas.disableProperty().bind(modelReadOnly.isNull());
        splitpane.getItems().add(canvas);
        
        // Set up console
        console = new ConsoleController(this);
        splitpane.getItems().add(console);
        
        // Set up split pane
        splitpane.setDividerPositions(0.75); // Needed because scenebuilder keeps overwriting this.
        
        // Setup infobox
        infobox = new InfoboxController(this);
        infobox.disableProperty().bind(modelReadOnly.isNull());
        hbox.getChildren().add(infobox);
        
        // Setup bindings
        focusAbsent.bind(focus.isNull());
        
    }
    
    /**
     * Gets the current focus
     * @return the node that is currently in focus
     */
    protected Node getFocus() {
        return focus.getValue();
    }
    
    /**
     * Sets the focus.
     * @param node The node that is the new focus.
     */
    protected void setFocus(Node node) {
        focus.setValue(node);
    }
    
    /**
     * A read only property wrapping the focus object for binding.
     * @return the read only object property wrapping the focus.
     */
    protected ReadOnlyObjectProperty<Node> focusProperty() {
        return focus.getReadOnlyProperty();
    }
    
    /**
     * Gets the current model
     * @return 
     */
    protected Cycle getModel() {
        return model.getValue();
    }
    
    /**
     * Sets the model variable
     * @param cycle The new model
     */
    protected void setModel(Cycle cycle) {
        model.setValue(cycle);
    }
    
    /**
     * A read only object property wrapping the model for binding.
     * @return the object property wrapping the model.
     */
    /**
    protected ReadOnlyObjectProperty<Cycle> modelProperty() {
        return model.getReadOnlyProperty();
    }
    */
    
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
     * Converts the optional double to a string for display
     * @param value The optional double to print.
     * @return Returns a string to print.
     */
    /**
    public static String displayOptionalDouble(OptionalDouble value) {
        if (value.isPresent()) {
            return String.valueOf(value.getAsDouble());
        }
        return "";
    }
    */
}
