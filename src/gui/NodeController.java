/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Chris
 */
public final class NodeController extends AnchorPane {
        
    // FXML variables
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
        
        // Set the node style
        setNodeStyle();
        
        // Build handlers
        buildNodeClickHandlers();
        buildNodeDragHandlers();
        buildTooltip();
        
        // Set currsor type
        cursorProperty().setValue(Cursor.HAND);
        
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
                
                // Check to see if node is alraedy connected
                if (!master.getModel().isConnected(NodeController.this.node)) {
                    
                    // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                    ClipboardContent content = new ClipboardContent();
                    content.put(CanvasController.CREATE_CONNECTION,NodeController.this.node);

                    // Start drag and drop operation and add data to dragboard
                    Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                    dragboard.setContent(content);

                    // Start drag and drop operation
                    startFullDrag();

                    // Prepare the cnavas connection
                    master.canvas.dragConnection.bindStart(NodeController.this);
                    master.canvas.dragConnection.setVisible(true);
                    master.canvas.disableIneligibleNodes(NodeController.this);

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
     * Gets the distance between the centre of this node and the scene location.
     * @param sceneX the x location of the node in the scene
     * @param sceneY the y location of the node in the scene.
     * @return the distance between the node and the scene location.
     */
    protected double getDistance(double sceneX, double sceneY) {
        return getLocationInScene().distance(sceneX, sceneY);
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
    /**
    protected ConnectionController.Direction getDirection() {
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
    */
}
