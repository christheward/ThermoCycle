/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import static gui.NodeController.Direction.*;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Chris
 */
public final class NodeController extends AnchorPane {
    
    public enum Direction {
        RIGHT,
        LEFT,
        UP,
        DOWN;
        
        public static Direction getOpposite(Direction direction) {
            switch (direction) {
                case RIGHT:
                    return LEFT;
                case LEFT:
                    return RIGHT;
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                default:
                    return UP;
            }
        }
        
    }
        
        
    // FXML variables
    //@FXML private AnchorPane base;
    @FXML private Circle circle;
    
    // GUI variables
    private Tooltip tip;
    private final MasterSceneController master;
    private final ComponentController canvasIcon;
    
    // Model variables
    protected thermocycle.Node node;
    
    /**
     * Constructor
     * @param node The model Node represented by CanvasNode
     * @throws Exception 
     */
    public NodeController(MasterSceneController master, ComponentController canvasIcon, thermocycle.Node node) {
        
        // Set master
        this.master = master;
        
        // Set parent canvas icon
        this.canvasIcon = canvasIcon;
        
        // Set model node
        this.node = node;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Node.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // SEt the node style
        setNodeStyle();
        
        // Build handlers
        buildNodeClickHandlers();
        buildNodeDragHandlers();
        buildTooltip();
        
    }
    
    /**
     * Initializer
     */
    private void initialize() {
    }
    
      /**
       * Sets the node style based on node type
       */
    private void setNodeStyle() {
        circle.getStyleClass().add("node");
        if (node instanceof thermocycle.FlowNode) {
            circle.getStyleClass().add("node-flow");
        }
        else if (node instanceof thermocycle.HeatNode) {
            circle.getStyleClass().add("node-heat");
        }
        else if (node instanceof thermocycle.WorkNode) {
            circle.getStyleClass().add("node-work");
        }
    }
    
    /**
     * Builds the tool tip for this node
     */
    private void buildTooltip() {
        tip = new Tooltip();
        tip.setText(node.getClass().getSimpleName() + " " + master.getModel().getNodePort(node).name());
        Tooltip.install(this, tip);
    }
    
    /**
     * Builds the click handlers for this node
     */
    private void buildNodeClickHandlers() {
        this.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    master.setFocus(NodeController.this);
                }
                event.consume();
            }
        });
    }
        
    /**
     * Builds drag handlers for this node
     */
    private void buildNodeDragHandlers() {
        
        this.setOnDragDetected(new EventHandler<MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.CREATE_CONNECTION,NodeController.this.node);
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Start drag and drop operation
                startFullDrag();
                
                // Prepare the cnavas connection
                master.canvas.dragConnection.bindStart(NodeController.this);
                master.canvas.dragConnection.setVisible(true);
                master.canvas.disableIneligibleNodes(NodeController.this);
                
                // Consume event
                event.consume();
                
            }
        });
        
        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().getContentTypes().contains(DragContainerController.CREATE_CONNECTION)) {
                    // Highlight the fact
                    master.canvas.dragConnection.requestFocus();
                }
            }
        });
        
        this.setOnDragDropped(new EventHandler<DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                
                // Acceptable transferable modes
                event.acceptTransferModes(TransferMode.ANY);
                
                // Only accept create connection events
                if (event.getDragboard().getContentTypes().contains(DragContainerController.CREATE_CONNECTION)) {
                    
                    // Create a new connection
                    ConnectionController connection = new ConnectionController(master);
                    
                    // Add connection to canvas
                    master.canvas.getChildren().add(0,connection);
                    
                    // BInd connection to the two nodes
                    connection.bindStart(master.canvas.dragConnection.start);
                    connection.bindEnd(NodeController.this);
                    
                    // Force canvas to apply CSS and update
                    master.canvas.applyCss();
                    master.canvas.layout();
                    
                    // Make connection visible
                    connection.setVisible(true);

                    // Set drop compelte
                    event.setDropCompleted(true);
                    
                }
                
                // Consume event
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                // Hide canvas drag connetion
                master.canvas.dragConnection.setVisible(false);
                
                // Activate all nodes again
                master.canvas.enableAllNodes();
                
                // Consume event
                event.consume();
                
            }
            
        });
        
    }
    
    /**
     * Gets the location of the CanvasNodeController in the scene
     * @return Returns the center of the CanvasNodeController,
     */
    protected Point2D getLocationInScene() {
        Bounds bounds = getBoundsInLocal();
        return localToScene(bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight()/2);
    }
    
    /**
     * Gets the nodes position in the canvas icon's node grid
     */
    protected Direction getDirection() {
        Direction direction;
        int Row = GridPane.getRowIndex(this);
        int Column = GridPane.getColumnIndex(this);
        int max = 7;
        direction = RIGHT;
        if (Column == 1) {
            direction = LEFT;
        }
        else if (Column == max) {
            direction = RIGHT;
        }
        else if (Row == 1) {
            direction = UP;
        }
        else if (Row == max) {
            direction = DOWN;
        }
        return direction;
    }
    
}
