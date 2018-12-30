/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.jfoenix.controls.JFXDrawer;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Chris
 */
public class CanvasController extends AnchorPane {
    
    // FXML variables
    @FXML private AnchorPane canvas;
    @FXML private JFXDrawer draw;
    
    // GUI variables
    private final MasterSceneController master;
    private ContextMenuController contextMenu;
    private ToolboxController toolbox;
    protected ToolboxComponentController dragIcon;
    protected ToolboxPathController dragConnection;
    
    // Variables
    private boolean lockOpen;
    
    // Event handlers
    protected EventHandler iconDragOverCanvas;
    protected EventHandler iconDragDroppedCanvas;
    protected EventHandler connectionDragOverCanvas;
    protected ChangeListener numericField;
    
    /**
     * Constructor
     */
    public CanvasController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Canvas.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // Create context menu
        contextMenu = new ContextMenuController(master);
        
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Add style
        canvas.getStyleClass().add("canvas");
        
        // Set up draw and add tooolbox to draw
        toolbox = new ToolboxController(this);
        draw.setSidePane(toolbox);
        draw.setOverLayVisible(false);
        draw.setBackground(null);
        draw.getStyleClass().add("canvas");
        
        // Set up draw control
        draw.setOnMouseEntered(new EventHandler() {
            @Override
            public void handle(Event event) {
                draw.open();
                event.consume();
            }
        });
        draw.setOnMouseExited(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!lockOpen) {
                    draw.close();
                }
                event.consume();
            }
        });
        toolbox.pin.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (lockOpen) {
                    lockOpen = false;
                    toolbox.unlock();
                }
                else {
                    lockOpen = true;
                    toolbox.lock();
                }
                event.consume();
            }
        });
        
        // Set up drag handlers
        buildDragHandlers();
        
        //Set up click handlers
        buildClickHandlers();
        
        // Set up dragIcon
        dragIcon = new ToolboxComponentController();
        dragIcon.setVisible(false);
        dragIcon.setOpacity(0.5);
        canvas.getChildren().add(dragIcon);
        
        // Set up dragConnection
        dragConnection = new ToolboxPathController();
        dragConnection.setVisible(false);
        dragConnection.setOpacity(0.35);
        canvas.getChildren().add(dragConnection);
        
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
                    master.infobox.showDetails(CanvasController.this);
                }
                event.consume();
            }
        });
        
    }
    
    /**
     * Build drag handlers for the canvas
     */
    private void buildDragHandlers() {
        
        // Set drag done drag handler
        canvas.setOnDragDone(new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                
                // Hide the drag icon and drag connection
                dragIcon.setVisible(false);
                dragConnection.setVisible(false);
                
                // For AddNode operations
                DragContainerController container = (DragContainerController)event.getDragboard().getContent(DragContainerController.AddNode);
                // Create the new CanvasComponentController 
                if (container != null) {
                    // If dragging within canvas then container will be null (no new component created).
                    if (container.getValue("scene_coords") != null) {
                        // ToolboxComponentController node;
                        try {
                            // Create a new canvas icon
                            CanvasComponentController component = new CanvasComponentController(master, ComponentIcon.valueOf(container.getValue("type").toString()));
                            component.getStyleClass().add("icon-componnt");
                            canvas.getChildren().add(component);
                            
                            // Put the canvas icon at the drop co-ordinated
                            Point2D cursorPoint = container.getValue("scene_coords");
                            component.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
                            
                            // Show the component
                            component.setVisible(true);
                            
                        } catch (Exception ex) {
                            // Do something
                        }
                    }
                }
                
                // For AddLink operations
                container = (DragContainerController)event.getDragboard().getContent(DragContainerController.AddLink);
                if (container != null) {
                    
                    // These will return null if they are in the container
                    Integer startIconId = container.getValue("startIcon");
                    Integer startNodeId = container.getValue("startNode");
                    Integer endIconId = container.getValue("endIcon");
                    Integer endNodeId = container.getValue("endNode");
                    
                    CanvasNodeController startNode = null;
                    CanvasNodeController endNode = null;
                    
                    if (startIconId != null && startNodeId != null && endIconId != null && endNodeId != null) {
                        // Find start and end nodes
                        for (Node c: canvas.getChildren()) {
                            if (c instanceof CanvasComponentController) {
                                if (((CanvasComponentController) c).hashCode() == startIconId) {
                                    for (Node n: ((CanvasComponentController) c).node_grid.getChildren()) {
                                        if (n instanceof CanvasNodeController) {
                                            if (((CanvasNodeController) n).hashCode() == startNodeId) {
                                                startNode = (CanvasNodeController)n;
                                            }
                                        }
                                    };
                                }
                                if (((CanvasComponentController) c).hashCode() == endIconId) {
                                    for (Node n: ((CanvasComponentController) c).node_grid.getChildren()) {
                                        if (n instanceof CanvasNodeController) {
                                            if (((CanvasNodeController) n).hashCode() == endNodeId) {
                                                endNode = (CanvasNodeController)n;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((startNode != null) & (endNode != null)) {
                            // Create canvas connection
                            CanvasPathController connection = new CanvasPathController(master);
                            canvas.getChildren().add(0,connection);
                            connection.bindEnds(startNode, endNode);
                            connection.setVisible(true);
                        }
   
                    }
                }
                activateAllNodes();
                event.consume();
            }
        });
        
        // Drag over canvas drag handler
        // This is added to canvas when drag detected in ToolboxComponentController
        iconDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                dragIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        
        // Dropped over canvas drag handler
        // This is added to canvas when drag detected in ToolboxComponentController
        iconDragDroppedCanvas = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                // Remove both event handlers that were added in ToolboxComponentController when drag was detected
                canvas.removeEventHandler(DragEvent.DRAG_OVER, iconDragOverCanvas);
                canvas.removeEventHandler(DragEvent.DRAG_DROPPED, iconDragDroppedCanvas);
                
                // Add drop coordinates to drag container
                DragContainerController container = (DragContainerController)event.getDragboard().getContent(DragContainerController.AddNode);
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.AddNode, container);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                
                // Consume event
                event.consume();
            }
        };
        
        // Drag over canvas drag handler
        // This is added to canvas when drag detected in CanvasNodeController
        connectionDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                dragConnection.dragTo(event.getX(), event.getY() + master.menubar.getBoundsInLocal().getMaxY());
                event.consume();
            }
        };
        
    }
    
    /**
     * Remove component from model()
     */
    protected void remove(Node node) {
        if (node instanceof CanvasComponentController) {
            CanvasComponentController icon = (CanvasComponentController)node;
            master.getModel().removeComponent(icon.component);
            canvas.getChildren().remove(icon);
            // remove any connections that were deleted from the model as part of this operration
            canvas.getChildren().removeAll(getConnections().filter(c -> !(master.getModel().connectionsReadOnly.contains(c.connection))).collect(Collectors.toSet()));
        }
        else if (node instanceof CanvasPathController) {
            master.getModel().removeConnection(((CanvasPathController)node).connection);
            canvas.getChildren().remove(node);
       }
       master.infobox.showDetails(this);
    }
    
    
    /**
     * Gets a stream of all the canvas path nodes on the canvas.
     * @return Returns a stream of all the canvas path elements on the canvas.
     */
    private Stream<CanvasPathController> getConnections() {
        return canvas.getChildren().stream().filter(n -> n instanceof CanvasPathController).map(n -> (CanvasPathController)n);
    }
    
    /**
     * Disables nodes that an ineligible to be connnected to this node.
     * @param node 
     */
    protected void disableIneligibleNodes(CanvasNodeController node) {
        // Need to inclue node that are already connected.
        getNodes().filter(n -> (!(n.node.getClass().equals(node.node.getClass())) | (n.node.port.equals(node.node.port)))).forEach(n -> {
            n.disableProperty().set(true);
        });
    }
    
    /**
     * Activates all nodes
     */
    protected void activateAllNodes() {
        getNodes().forEach(n -> n.disableProperty().set(false));
    }
    
    /**
     * Gets a stream of all the canvas path elements in the same flow path and the flow node.
     * @param node The starting node
     * @return A stream on canvas path objects.
     */
    private Stream<CanvasPathController> getPath(CanvasNodeController node) {
        if (node.node instanceof thermocycle.FlowNode) {
            // set of flow nodes in the same path
            Set path = master.getModel().pathsReadOnly.stream().filter(p -> p.contains((thermocycle.FlowNode) node.node)).collect(Collectors.toSet());
            Set canvasNodes = getNodes().filter(n -> path.contains(n.node)).collect(Collectors.toSet());
            return getConnections().filter(c -> canvasNodes.contains(c.start) | canvasNodes.contains(c.end));
        }
        return Stream.empty();
    }
    
    /**
     * Gets a stream of all the components on the canvas.
     * @return Returns a stream of all the components on the canvas.
     */
    private Stream<CanvasComponentController> getComponents() {
        return canvas.getChildren().stream().filter(n -> n instanceof CanvasComponentController).map(n -> (CanvasComponentController)n);
    }
    
    /**
     * Gets a stream of all the component nodes on the canvas.
     * @return Returns a stream of all the component nodes on the canvas.
     */
    private Stream<CanvasNodeController> getNodes() {
        return getComponents().map(n -> n.node_grid.getChildren().stream().filter(m -> m instanceof CanvasNodeController).map(m -> (CanvasNodeController)m)).flatMap(Function.identity());
    }
    
    /**
     * Gets a stream of all the paths on the canvas
     * @return Returns a stream of all the paths on the canvas
     */
    private Stream<CanvasPathController> getPaths() {
        return canvas.getChildren().stream().filter(c -> c instanceof CanvasPathController).map(c -> (CanvasPathController)c);
    }
    
    /**
     * Clears the canvas
     */
    protected void clearCanvas() {
        canvas.getChildren().removeAll(canvas.getChildren().stream().filter(n -> n instanceof CanvasComponentController | n instanceof CanvasPathController).collect(Collectors.toSet()));
    }
    
    /**
     * Sets the node visibility for all nodes on the canvas.
     * @param visible true to show nodes and false to hide nodes
     */
    protected void setNodeVisibility(boolean visible) {
        canvas.getChildren().stream().filter(c -> c instanceof CanvasComponentController).forEach((c -> ((CanvasComponentController)c).node_grid.setVisible(visible)));
    }
    
    /**
     * This function builds the gui from the underlying model
     */
    protected void buildFromModel() {
        
        master.getModel().componentsReadOnly.stream().forEach(c -> {
            try {
                // Coopied from drag handlers - need to put this in a common function
                // Create a new canvas icon
                CanvasComponentController component = new CanvasComponentController(master, c);
                component.getStyleClass().add("icon-componnt");
                canvas.getChildren().add(component);
                
                // Put the canvas icon at the drop co-ordinated
                Point2D cursorPoint = new Point2D(100,100);
                component.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
                
                // Show the component
                component.setVisible(true);
            } catch (Exception ex) {
                //Logger.getLogger(CanvasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
