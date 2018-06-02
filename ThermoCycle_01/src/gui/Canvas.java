/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.jfoenix.controls.JFXDrawer;
import gui.DragContainer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Chris
 */
public class Canvas extends VBox implements Serializable {
    
    // FXML variables
    @FXML private AnchorPane canvas;
    @FXML private JFXDrawer toolbox;
    @FXML private AnchorPane infobox;
    @FXML private AnchorPane consolePane;
    private Toolbox toolboxContent;
    protected Infobox infoboxContent;
    protected ToolboxIcon dragIcon;
    protected ToolboxPath dragConnection;
    private ContextMenu menu;
    
    // Variables
    private boolean lockOpen;
    
    // Event handlers
    protected EventHandler iconDragOverCanvas;
    protected EventHandler iconDragDroppedCanvas;
    protected EventHandler connectionDragOverCanvas;
    
    // Model varaibles
    protected thermocycle.Cycle model = null;
    
    /**
     * Constructor
     */
    public Canvas() {
        // Setup model
        model = new thermocycle.Cycle("test");
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Canvas.fxml"));
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
        
        canvas.getStyleClass().add("canvas");
        
        // Setup FXML
        toolboxContent = new Toolbox(this);
        toolbox.setSidePane(toolboxContent);
        toolbox.setOverLayVisible(false);
        toolbox.setBackground(null);
        toolbox.getStyleClass().add("canvas");
        
        infoboxContent = new Infobox(this);
        infobox.getChildren().add(infoboxContent);
        infoboxContent.showDetails(Canvas.this);
        
        // Setup toolbox control
        toolbox.setOnMouseEntered(new EventHandler() {
            @Override
            public void handle(Event event) {
                toolbox.open();
                event.consume();
            }
        });
        toolbox.setOnMouseExited(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!lockOpen) {
                    toolbox.close();
                }
                event.consume();
            }
        });
        toolboxContent.pin.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (lockOpen) {
                    lockOpen = false;
                    toolboxContent.unlock();
                }
                else {
                    lockOpen = true;
                    toolboxContent.lock();
                }
                event.consume();
            }
        });
        
        // Set up console
        Console console = new Console(this);
        consolePane.getChildren().add(console);
        AnchorPane.setTopAnchor(console, 0.0);
        AnchorPane.setBottomAnchor(console, 0.0);
        AnchorPane.setLeftAnchor(console, 0.0);
        AnchorPane.setRightAnchor(console, 0.0);
        console.setVisible(true);
                
        // Set up drag handlers
        buildDragHandlers();
        
        //Set up click handlers
        buildClickHandlers();
        
        // Set up dragIcon
        dragIcon = new ToolboxIcon();
        dragIcon.setVisible(false);
        dragIcon.setOpacity(0.5);
        canvas.getChildren().add(dragIcon);
        
        // Setup dragConnection
        dragConnection = new ToolboxPath();
        dragConnection.setVisible(false);
        dragConnection.setOpacity(0.35);
        canvas.getChildren().add(dragConnection);
        
    }
    
    /**
     * Build click handlers for the canvas
     */
    private void buildClickHandlers() {
        
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Show Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.getChildren().stream().filter(c -> c instanceof CanvasIcon).forEach((c -> ((CanvasIcon)c).node_grid.setVisible(true)));
                event.consume();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Hide Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.getChildren().stream().filter(c -> c instanceof CanvasIcon).forEach((c -> ((CanvasIcon)c).node_grid.setVisible(false)));
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Model summary");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                model.reportSetup();
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        canvas.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    menu.show(canvas, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    System.out.print("show canvas");
                    infoboxContent.showDetails(Canvas.this);
                }
                event.consume();
            }
        });
        
        
    }
    
    /**
     * Build drag handlers for the canvas
     */
    private void buildDragHandlers() {
        
        // Drag done drag handler
        canvas.setOnDragDone(new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                System.out.println("Canvas: Drag done");
                
                // Hide the drag icon and drag connection
                dragIcon.setVisible(false);
                dragConnection.setVisible(false);
                
                // For AddNode operations
                DragContainer container = (DragContainer)event.getDragboard().getContent(DragContainer.AddNode);
                // Create the new CanvasIcon 
                if (container != null) {
                    System.out.println("AddNode operations");
                    // If dragging within canvas then container will be null (no new component created).
                    if (container.getValue("scene_coords") != null) {
                        // ToolboxIcon node;
                        try {
                            // Create a new canvas icon
                            CanvasIcon component = new CanvasIcon(Canvas.this, IconType.valueOf(container.getValue("type").toString()));
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
                container = (DragContainer)event.getDragboard().getContent(DragContainer.AddLink);
                if (container != null) {
                    System.out.println("AddLink operations");
                    
                    // These will return null if they are in the container
                    Integer startIconId = container.getValue("startIcon");
                    Integer startNodeId = container.getValue("startNode");
                    Integer endIconId = container.getValue("endIcon");
                    Integer endNodeId = container.getValue("endNode");
                    
                    CanvasNode startNode = null;
                    CanvasNode endNode = null;
                    
                    if (startIconId != null && startNodeId != null && endIconId != null && endNodeId != null) {
                        // Find start and end nodes
                        for (Node c: canvas.getChildren()) {
                            if (c instanceof CanvasIcon) {
                                if (((CanvasIcon) c).hashCode() == startIconId) {
                                    for (Node n: ((CanvasIcon) c).node_grid.getChildren()) {
                                        if (n instanceof CanvasNode) {
                                            if (((CanvasNode) n).hashCode() == startNodeId) {
                                                startNode = (CanvasNode)n;
                                            }
                                        }
                                    };
                                }
                                if (((CanvasIcon) c).hashCode() == endIconId) {
                                    for (Node n: ((CanvasIcon) c).node_grid.getChildren()) {
                                        if (n instanceof CanvasNode) {
                                            if (((CanvasNode) n).hashCode() == endNodeId) {
                                                endNode = (CanvasNode)n;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((startNode != null) & (endNode != null)) {
                            // Create canvas connection
                            CanvasPath connection = new CanvasPath(Canvas.this);
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
        // This is added to canvas when drag detected in ToolboxIcon
        iconDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                dragIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        
        // Dropped over canvas drag handler
        // This is added to canvas when drag detected in ToolboxIcon
        iconDragDroppedCanvas = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("Canvas: Icon drag dropped hander");
                
                // Remove both event handlers that were added in ToolboxIcon when drag was detected
                canvas.removeEventHandler(DragEvent.DRAG_OVER, iconDragOverCanvas);
                canvas.removeEventHandler(DragEvent.DRAG_DROPPED, iconDragDroppedCanvas);
                
                // Add drop coordinates to drag container
                DragContainer container = (DragContainer)event.getDragboard().getContent(DragContainer.AddNode);
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                
                // Consume event
                event.consume();
            }
        };
        
        // Drag over canvas drag handler
        // This is added to canvas when drag detected in CanvasNode
        connectionDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                dragConnection.dragTo(event.getX(), event.getY());
                event.consume();
            }
        };
    }
    
    /**
     * Remove component from model
     */
    protected void remove(Node node) {
        if (node instanceof CanvasIcon) {
            CanvasIcon icon = (CanvasIcon)node;
            model.removeComponent(icon.component);
            canvas.getChildren().remove(icon);
            // remove any connections that were deleted from the model as part of this operration
            canvas.getChildren().removeAll(getConnections().filter(c -> !(model.connectionsReadOnly.contains(c.connection))).collect(Collectors.toSet()));
        }
        else if (node instanceof CanvasPath) {
            model.removeConnection(((CanvasPath)node).connection);
            canvas.getChildren().remove(node);
       }
    }
    
    
    /**
     * Gets a stream of all the canvas path nodes on the canvas.
     * @return Returns a stream of all the canvas path elements on the canvas.
     */
    private Stream<CanvasPath> getConnections() {
        return canvas.getChildren().stream().filter(n -> n instanceof CanvasPath).map(n -> (CanvasPath)n);
    }
    
    /**
     * Disables nodes that an ineligible to be connnected to this node.
     * @param node 
     */
    protected void disableIneligibleNodes(CanvasNode node) {
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
    private Stream<CanvasPath> getPath(CanvasNode node) {
        if (node.node instanceof thermocycle.FlowNode) {
            // set of flow nodes in the same path
            Set path = model.pathsReadOnly.stream().filter(p -> p.contains((thermocycle.FlowNode) node.node)).collect(Collectors.toSet());
            Set canvasNodes = getNodes().filter(n -> path.contains(n.node)).collect(Collectors.toSet());
            return getConnections().filter(c -> canvasNodes.contains(c.start) | canvasNodes.contains(c.end));
        }
        return Stream.empty();
    }
    
    /**
     * Gets a stream of all the canvas path nodes on the canvas.
     * @return Returns a stream of all the canvas path elements on the canvas.
     */
    private Stream<CanvasIcon> getComponents() {
        return canvas.getChildren().stream().filter(n -> n instanceof CanvasIcon).map(n -> (CanvasIcon)n);
    }
    
    /**
     * Gets a stream of all the canvas nodes on the canvas.
     * @return Returns a stream of all the canvas nodes on the canvas.
     */
    private Stream<CanvasNode> getNodes() {
        /**List <CanvasNode> nodeList = new ArrayList();
        getComponents().forEach(c -> {
            c.node_grid.getChildren().stream().filter(n -> n instanceof CanvasNode).map(n -> ((CanvasNode)n)).forEach(n -> {
                nodeList.add((CanvasNode)n);
                
            });
        });
        */
        return getComponents().map(n -> n.node_grid.getChildren().stream().filter(m -> m instanceof CanvasNode).map(m -> (CanvasNode)m)).flatMap(Function.identity());
    }    

}
