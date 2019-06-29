/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.jfoenix.controls.JFXDrawer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import thermocycle.Component;
import thermocycle.Cycle;
import utilities.SingletonCollector;

/**
 *
 * @author Chris
 */
public final class CanvasController extends AnchorPane {
    
    // FXML variables
    @FXML private AnchorPane canvas;
    @FXML private JFXDrawer draw;
    
    // GUI variables
    private final MasterSceneController master;
    private ToolboxController toolbox;
    protected final ComponentController dragIcon;
    protected final ConnectionController dragConnection;
    protected final SelectionTool lassoo;
    private final ContextMenuController contextMenu;
    
    // Copy and paste
    protected final ClipboardContent canvasClipboard;
    
    // Save and load variables
    private final Map<Component,Double[]> layout;
    
    // Event handlers
    protected EventHandler<DragEvent> componentDraggedOverCanvas;
    protected EventHandler<DragEvent> componentDroppedOnCanvas;
    protected EventHandler<DragEvent> componentDragComplete;
    protected EventHandler<DragEvent> connectionDragOverCanvas;
    protected EventHandler<DragEvent> lassooDragOverCanvas;
    protected EventHandler<DragEvent> lassooDragDroppedCanvas;
    protected ChangeListener numericField;
    
    /**
     * Constructor
     */
    public CanvasController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Create drag tools
        dragIcon = new ComponentController(master, false);
        dragConnection = new ConnectionController(master);
        lassoo = new SelectionTool();
        
        // Create context menu
        contextMenu = new ContextMenuController(master);
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Canvas.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Set up clipboard
        canvasClipboard = new ClipboardContent();
        
        // Set up layout
        layout = new HashMap();
        
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Add style
        canvas.getStyleClass().add("canvas");
        
        // Set up draw and add tooolbox to draw
        toolbox = new ToolboxController(master);
        draw.setSidePane(toolbox);
        draw.setOverLayVisible(false);
        draw.setBackground(null);
        draw.getStyleClass().add("canvas");
        
        // Set up draw control
        master.toolboxLock.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    draw.open();
                }
                else if (!draw.hoverProperty().getValue()) {
                    draw.close();
                }
            }
        });
        draw.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    draw.open();
                }
                else if (!master.toolboxLock.getValue()) {
                    draw.close();
                }
            }
        });
        
        // Setup bindings
        draw.disableProperty().bind(master.modelAbsent);
        
        // Setup drag handlers
        buildDragHandlers();
        
        // Setup click handlers
        buildClickHandlers();
        
        // Set up dragIcon
        dragIcon.setVisible(false);
        dragIcon.setOpacity(0.5);
        dragIcon.setMouseTransparent(true);
        canvas.getChildren().add(dragIcon);
        
        // Set up dragConnection
        dragConnection.setVisible(false);
        dragConnection.setOpacity(0.35);
        dragConnection.setMouseTransparent(true);
        canvas.getChildren().add(dragConnection);
        
        // Set up lasso
        lassoo.setVisible(false);
        lassoo.setOpacity(0.5);
        lassoo.setMouseTransparent(true);
        canvas.getChildren().add(lassoo);
        
        // If model changed then clear canvas.
        master.modelProperty().addListener(new ChangeListener<Cycle>() {
            @Override
            public void changed(ObservableValue<? extends Cycle> observable, Cycle oldValue, Cycle newValue) {
                CanvasController.this.clearCanvas();
            }
        });
        
    }
    
    /**
     * Build click handlers for the canvas
     */
    private void buildClickHandlers() {
        
        canvas.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(canvas, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    contextMenu.hide();
                    master.setFocus(CanvasController.this);
                }
                event.consume();
            }
        });
        
    }
    
    /**
     * Build drag handlers for the canvas
     */
    private void buildDragHandlers() {
        
        // Set up lasso selection handler
        canvas.setOnDragDetected(new EventHandler <MouseEvent> (){
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.SELECT,"");
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Not sure if these are needed
                startFullDrag();
                //setMouseTransparent(false);
                
                // Pin upper left corner of lassoo
                lassoo.DragFrom(event.getX(), event.getY());
                
                // Show lassoo
                lassoo.setVisible(true);
                
                // Consume event
                event.consume();
                
            }
        });
        
        canvas.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                // Accept all drag types
                event.acceptTransferModes(TransferMode.ANY);
                
                if (event.getDragboard().getContentTypes().contains(DragContainerController.CREATE_COMPONENT)) {
                    dragIcon.relocateToPointInScene(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                else if (event.getDragboard().getContentTypes().contains(DragContainerController.MOVE_COMPONENT)) {
                    thermocycle.Component modelComponent = (thermocycle.Component) event.getDragboard().getContent(DragContainerController.MOVE_COMPONENT);
                    ComponentController component = CanvasController.this.getComponents().filter(c -> (c.component.equals(modelComponent))).collect(SingletonCollector.singletonCollector());
                    component.relocateToPointInScene(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                else if (event.getDragboard().getContentTypes().contains(DragContainerController.CREATE_CONNECTION)) {
                    Optional<NodeController> node = nearestNode(event);
                    if (node.isPresent()) {
                        Point2D point = node.get().getLocationInScene();
                        dragConnection.dragTo(point.getX(), point.getY());
                    }
                    else {
                        dragConnection.dragTo(event.getSceneX(), event.getSceneY());
                    }
                }
                else if (event.getDragboard().getContentTypes().contains(DragContainerController.SELECT)) {
                    lassoo.DragTo(event.getSceneX(), event.getSceneY());
                }
                
                // Consume the event
                event.consume();
                
            }
        });
        
        canvas.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                event.acceptTransferModes(TransferMode.ANY);
                
                if (event.getDragboard().getContentTypes().contains(DragContainerController.CREATE_COMPONENT)) {
                    try {
                        
                        // Create new component
                        ComponentController component = new ComponentController(master, true);
                        component.setType((ComponentIcon) event.getDragboard().getContent(DragContainerController.CREATE_COMPONENT));
                        component.createComponent();
                        
                        // Add component to canvas
                        canvas.getChildren().add(component);
                        
                        // Bind node visibility
                        component.node_grid.visibleProperty().bind(master.nodeVisibility);
                        
                        // Force canvas to apply CSS and update
                        canvas.applyCss();
                        canvas.layout();
                        
                        // Move component to point in scene and make visible
                        component.relocateToPointInScene(new Point2D(event.getSceneX(), event.getSceneY()));
                        component.setVisible(true);
                        
                        // Set drop compelte
                        event.setDropCompleted(true);
                        
                        // Comsume event
                        event.consume();
                        
                    }
                    catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                else if (event.getDragboard().getContentTypes().contains(DragContainerController.MOVE_COMPONENT)) {
                    
                    // Consume event
                    event.consume();
                    
                }
                else if (event.getDragboard().getContentTypes().contains(DragContainerController.CREATE_CONNECTION)) {
                    
                    // Get the nearest node and set up the conenction.
                    nearestNode(event).ifPresent(n -> {
                        
                        // Check  nodes are free
                        if (!master.getModel().isConnected(master.canvas.dragConnection.start.node) & !master.getModel().isConnected(n.node)) {
                            
                            // Create a new connection
                            ConnectionController connection = new ConnectionController(master);
                        
                            // Add connection to canvas
                            master.canvas.getChildren().add(0,connection);
                            
                            // Bind connection to the start and end nodes.
                            connection.bindStart(master.canvas.dragConnection.start);
                            connection.bindEnd(n);
                        
                            // Force canvas to apply CSS and update
                            master.canvas.applyCss();
                            master.canvas.layout();

                            // Make connection visible
                            connection.setVisible(true);

                            // Set drop compelte
                            event.setDropCompleted(true);

                        }
                        
                    });
                    
                    // Consume the event
                    event.consume();
                }
                else if (event.getDragboard().getContentTypes().contains(DragContainerController.SELECT)) {
                    
                    // Get all elements in the lassoo
                    getComponents().filter(c -> lassoo.contains(lassoo.sceneToLocal(c.localToScene(c.centerInLocal.getValue()))));
                    
                    // Hide selection tool
                    lassoo.setVisible(false);
                    
                    // Set drop complete
                    event.setDropCompleted(true);
                    
                    // Consume event
                    event.consume();
                    
                }
                else {
                    // Catch incase
                    event.consume();
                }
            }
            
        });
        
    }
    
    /**
     * Clears the canvas
     */
    private void clearCanvas() {
        canvas.getChildren().removeAll(getConnections().collect(Collectors.toList()));
        canvas.getChildren().removeAll(getComponents().collect(Collectors.toList()));
    }
    
    /**
     * Gets the nearest node to the event that is not disabled.
     * @param event the event
     * @return an option containing the nearest node, if on is found.
     */
    private Optional<NodeController> nearestNode(DragEvent event) {
        Optional<NodeController> node = getNodes().filter(n -> !n.isDisabled()).min(Comparator.comparing(n -> n.getDistance(event.getSceneX(), event.getSceneY())));
        if (node.isPresent()) {
            if (node.get().getDistance(event.getSceneX(), event.getSceneY()) < 100) {
                return node;
            }
        }
        return Optional.empty();
    }
    
    /**
     * Gets a stream of all the paths on the canvas excluding the drag connection.
     * @return a stream of all the path elements on the canvas.
     */
    private Stream<ConnectionController> getConnections() {
        return canvas.getChildren().stream().filter(n -> n instanceof ConnectionController).map(n -> (ConnectionController)n).filter(n -> !n.equals(dragConnection));
    }
    
    /**
    protected Optional<ComponentController> getCOmponent(NodeController n) {
        getComponents().filter(c -> c.node_grid.getChildren());
    }
    */
    
    /**
     * Gets a stream of all the components on the canvas excluding the drag component.
     * @return a stream of the components on the canvas.
     */
    protected Stream<ComponentController> getComponents() {
        return canvas.getChildren().stream().filter(n -> n instanceof ComponentController).map(n -> (ComponentController)n).filter(n -> !n.equals(dragIcon));
    }
    
        
    /**
     * Gets a stream of all the component nodes on the canvas.
     * @return Returns a stream of all the component nodes on the canvas.
     */
    private Stream<NodeController> getNodes() {
        return getComponents().map(n -> n.node_grid.getChildren().stream().filter(m -> m instanceof NodeController).map(m -> (NodeController)m)).flatMap(Function.identity());
    }
    
    /**
     * Disables nodes that an ineligible to be connected to this node.
     * @param node 
     */
    protected void disableIneligibleNodes(NodeController node) {
        // Need to inclue nodes that are already connected.
        getNodes().filter(n -> (!(n.node.getClass().equals(node.node.getClass())) | (n.node.port.equals(node.node.port)))).forEach(n -> {
            n.disableProperty().set(true);
        });
        getComponents().filter(c -> c.node_grid.getChildren().contains(node)).forEach(c -> {
            c.node_grid.getChildren().stream().forEach(n -> n.setDisable(true));
        });
        getConnections().forEach(c -> {
            c.start.disableProperty().set(true);
            c.end.disableProperty().set(true);
        });
    }
    
    /**
     * Enable all nodes on the canvas.
     */
    protected void enableAllNodes() {
        getNodes().forEach(n -> n.disableProperty().set(false));
    }
    
    /**
     * Remove component from the canvas and model()
     */
    protected void remove(Node node) {
        if (node instanceof ComponentController) {
            ComponentController icon = (ComponentController)node;
            master.getModel().removeComponent(icon.component);
            canvas.getChildren().remove(icon);
            // remove any connections that were deleted from the model as part of this operration
            canvas.getChildren().removeAll(getConnections().filter(c -> !(master.getModel().connectionsReadOnly.contains(c.connection))).collect(Collectors.toSet()));
        }
        else if (node instanceof ConnectionController) {
            master.getModel().removeConnection(((ConnectionController)node).connection);
            canvas.getChildren().remove(node);
       }
    }
    
    /**
     * Gets a stream of all the canvas path elements in the same flow path and the flow node.
     * @param node The starting node
     * @return A stream on canvas path objects.
     */
    private Stream<ConnectionController> getPath(NodeController node) {
        if (node.node instanceof thermocycle.FlowNode) {
            // set of flow nodes in the same path
            Set path = master.getModel().pathsReadOnly.stream().filter(p -> p.contains((thermocycle.FlowNode) node.node)).collect(Collectors.toSet());
            Set canvasNodes = getNodes().filter(n -> path.contains(n.node)).collect(Collectors.toSet());
            return getConnections().filter(c -> canvasNodes.contains(c.start) | canvasNodes.contains(c.end));
        }
        return Stream.empty();
    }
    
    /**
     * This function builds the GUI from the underlying model
     */
    protected void buildFromModel() {
        
        master.getModel().componentsReadOnly.stream().forEach(c -> {
            try {
                // Create a new canvas icon
                ComponentController component = new ComponentController(master, true);
                //component.getStyleClass().add("icon-componnt");
                canvas.getChildren().add(component);
                
                // Put the canvas icon at the drop co-ordinated
                component.relocateToPointInScene(this.localToScene(double2point(layout.get(c))));
                
                // Show the component
                component.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                // Do something
            }
        });
        master.getModel().connectionsReadOnly.stream().forEach(c -> {
            // Create new canvas path
            ConnectionController connection = new ConnectionController(master);
            canvas.getChildren().add(0,connection);
            
            List<NodeController> nodes = getNodes().filter(n -> master.getModel().containsNode(c, n.node)).collect(Collectors.toList());
            if (nodes.size() == 2) {
                //connection.bindEnds(nodes.get(0), nodes.get(1), c);
            }
            else {
                System.err.println("Incorrect number of connected nodes.");
            }
            
            // Show the connection
            connection.setVisible(true);
        });
    }
    
    /**
     * Saves the canvas layout to the output stream
     * @param stream
     * @throws IOException 
     */
    public void saveLayout(ObjectOutputStream stream) throws IOException {
        layout.clear();
        getComponents().forEach(c -> {
            //layout.put(c.component, point2double(c));
        });
        stream.writeObject(layout);
    }
    
    /**
     * 
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    public void loadLayout(ObjectInputStream stream) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        layout.clear();
        layout.putAll((Map<Component,Double[]>)stream.readObject());
    }
    
    public static Double[] point2double(Point2D point) {
        Double[] location = new Double[2];
        location[0] = point.getX();
        location[1] = point.getY();
        return location;
    }
    
    public static Point2D double2point(Double[] location) {
        Point2D point = new Point2D(location[0], location[1]);
        return point;
    }
    
}
