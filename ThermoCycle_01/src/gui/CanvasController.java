/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.jfoenix.controls.JFXDrawer;
import gui.DragContainerController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.OptionalDouble;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Chris
 */
public class CanvasController extends VBox implements Serializable {
    
    // FXML variables
    @FXML private AnchorPane canvas;
    @FXML private JFXDrawer toolbox;
    @FXML private StackPane infobox;
    @FXML private AnchorPane consolePane;
    @FXML private MenuItem fileOpen;
    @FXML private MenuItem fileSave;
    @FXML private MenuItem fileSaveas;
    @FXML private MenuItem editDelete;
    @FXML private MenuItem cycleSolve;
    private ToolboxController toolboxContent;
    protected InfoboxController infoboxContent;
    protected ToolboxIconController dragIcon;
    protected ToolboxPathController dragConnection;
    private ContextMenu menu;
    
    // Variables
    private boolean lockOpen;
    
    // Dialogues
    Stage fileBrowserDialogue = new Stage();
    FileChooser fileChooser = new FileChooser();                
    File file;
    
    // Event handlers
    protected EventHandler iconDragOverCanvas;
    protected EventHandler iconDragDroppedCanvas;
    protected EventHandler connectionDragOverCanvas;
    protected ChangeListener numericField;
    
    // Model varaibles
    protected thermocycle.Cycle model = null;
    
    /**
     * Constructor
     */
    public CanvasController() {
        
        // Setup model
        model = new thermocycle.Cycle("ThermoCycle");
        
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
        
        // Setup toolbox
        toolboxContent = new ToolboxController(this);
        toolbox.setSidePane(toolboxContent);
        toolbox.setOverLayVisible(false);
        toolbox.setBackground(null);
        toolbox.getStyleClass().add("canvas");
        
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
        
        // Setup infobox
        infoboxContent = new InfoboxController(this);
        infobox.getChildren().add(infoboxContent);
        infoboxContent.showDetails(CanvasController.this);
        
        // Set up console
        ConsoleController console = new ConsoleController(this);
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
        
        //Set up menu handlers
        buildMenuHandlers();
        
        // Set up dragIcon
        dragIcon = new ToolboxIconController();
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
     * Build menu handlers
     */
    private void buildMenuHandlers() {
        fileSave.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (file != null) {
                    fileChooser.setTitle("Save Model File");
                    file = fileChooser.showOpenDialog(fileBrowserDialogue);
                }
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
                    os.writeObject(model);
                }
                catch(IOException e) {
                    System.err.println("I/O error. " + e.getMessage());
                }
                event.consume();
            }
        });
        fileSaveas.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Save Model File");
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
                    os.writeObject(model);
                }
                catch(IOException e) {
                    System.err.println("I/O error. " + e.getMessage());
                }
                event.consume();
            }
        });
        fileOpen.setOnAction(new EventHandler() {
            /**
             * Need to work out how to re build the gui canvas.
             * @param event 
             */
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Open Model File");
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
                    model = (thermocycle.Cycle)is.readObject();
                }
                catch(ClassNotFoundException e) {
                    System.err.println("Class not found. " + e.getMessage());
                }
                catch(IOException e) {
                    System.err.println("I/O error. " + e.getMessage());
                }
                event.consume();
            }
        });
        editDelete.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                // TODO: How do you work out what to delete?
                event.consume();
            }
        });
        cycleSolve.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                model.solve();
                event.consume();
            }
        });
    }
    
    /**
     * Build click handlers for the canvas
     */
    private void buildClickHandlers() {
        
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Solve");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                model.solve();
                event.consume();
            }
        });
        menu = new ContextMenu();
        item = new MenuItem("Show Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.getChildren().stream().filter(c -> c instanceof CanvasIconController).forEach((c -> ((CanvasIconController)c).node_grid.setVisible(true)));
                event.consume();
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Hide Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.getChildren().stream().filter(c -> c instanceof CanvasIconController).forEach((c -> ((CanvasIconController)c).node_grid.setVisible(false)));
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Model summary");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
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
                    infoboxContent.showDetails(CanvasController.this);
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
                DragContainerController container = (DragContainerController)event.getDragboard().getContent(DragContainerController.AddNode);
                // Create the new CanvasIconController 
                if (container != null) {
                    System.out.println("AddNode operations");
                    // If dragging within canvas then container will be null (no new component created).
                    if (container.getValue("scene_coords") != null) {
                        // ToolboxIconController node;
                        try {
                            // Create a new canvas icon
                            CanvasIconController component = new CanvasIconController(CanvasController.this, IconType.valueOf(container.getValue("type").toString()));
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
                    System.out.println("AddLink operations");
                    
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
                            if (c instanceof CanvasIconController) {
                                if (((CanvasIconController) c).hashCode() == startIconId) {
                                    for (Node n: ((CanvasIconController) c).node_grid.getChildren()) {
                                        if (n instanceof CanvasNodeController) {
                                            if (((CanvasNodeController) n).hashCode() == startNodeId) {
                                                startNode = (CanvasNodeController)n;
                                            }
                                        }
                                    };
                                }
                                if (((CanvasIconController) c).hashCode() == endIconId) {
                                    for (Node n: ((CanvasIconController) c).node_grid.getChildren()) {
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
                            CanvasPathController connection = new CanvasPathController(CanvasController.this);
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
        // This is added to canvas when drag detected in ToolboxIconController
        iconDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                dragIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        
        // Dropped over canvas drag handler
        // This is added to canvas when drag detected in ToolboxIconController
        iconDragDroppedCanvas = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("Canvas: Icon drag dropped hander");
                
                // Remove both event handlers that were added in ToolboxIconController when drag was detected
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
                dragConnection.dragTo(event.getX(), event.getY());
                event.consume();
            }
        };
        
    }
    
    /**
     * Remove component from model
     */
    protected void remove(Node node) {
        if (node instanceof CanvasIconController) {
            CanvasIconController icon = (CanvasIconController)node;
            model.removeComponent(icon.component);
            canvas.getChildren().remove(icon);
            // remove any connections that were deleted from the model as part of this operration
            canvas.getChildren().removeAll(getConnections().filter(c -> !(model.connectionsReadOnly.contains(c.connection))).collect(Collectors.toSet()));
        }
        else if (node instanceof CanvasPathController) {
            model.removeConnection(((CanvasPathController)node).connection);
            canvas.getChildren().remove(node);
       }
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
    private Stream<CanvasIconController> getComponents() {
        return canvas.getChildren().stream().filter(n -> n instanceof CanvasIconController).map(n -> (CanvasIconController)n);
    }
    
    /**
     * Gets a stream of all the canvas nodes on the canvas.
     * @return Returns a stream of all the canvas nodes on the canvas.
     */
    private Stream<CanvasNodeController> getNodes() {
        /**List <CanvasNode> nodeList = new ArrayList();
        getComponents().forEach(c -> {
            c.node_grid.getChildren().stream().filter(n -> n instanceof CanvasNode).map(n -> ((CanvasNode)n)).forEach(n -> {
                nodeList.add((CanvasNode)n);
                
            });
        });
        */
        return getComponents().map(n -> n.node_grid.getChildren().stream().filter(m -> m instanceof CanvasNodeController).map(m -> (CanvasNodeController)m)).flatMap(Function.identity());
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
