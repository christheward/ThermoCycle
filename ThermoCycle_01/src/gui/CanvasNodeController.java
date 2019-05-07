/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.CanvasConnectionController.Direction;
import static gui.CanvasConnectionController.Direction.*;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
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
public final class CanvasNodeController extends AnchorPane {
    
    // FXML variables
    //@FXML private AnchorPane base;
    @FXML private Circle circle;
    
    // GUI variables
    private Tooltip tip;
    private final MasterSceneController master;
    private final CanvasComponentController canvasIcon;
    
    // Model variables
    protected thermocycle.Node node;
    
    /**
     * Constructor
     * @param node The model Node represented by CanvasNode
     * @throws Exception 
     */
    public CanvasNodeController(MasterSceneController master, CanvasComponentController canvasIcon, thermocycle.Node node) throws Exception {
        
        // Set master
        this.master = master;
        
        // Set parent canvas icon
        this.canvasIcon = canvasIcon;
        
        // Set model node
        this.node = node;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CanvasNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        setNodeStyle();
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
                    master.setFocus(CanvasNodeController.this);
                }
                event.consume();
            }
        });
    }
        
    /**
     * Builds drag handlers for this node
     */
    private void buildNodeDragHandlers() {
        
        //drag detection for node dragging
        this.setOnDragDetected(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Put data in clipboard to identify icon type when dropped on canvas
                DragContainerController dragContainer = new DragContainerController();
                dragContainer.addData(DragContainerController.DATA_TYPE.NODE, CanvasNodeController.this);
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.CREATE_CONNECTION, dragContainer);
                
                // Start drag and drop operation
                startDragAndDrop(TransferMode.ANY).setContent(content);
                
                // Prepare the cnavas connection
                master.canvas.dragConnection.startDrag((CanvasNodeController) event.getSource());
                master.canvas.dragConnection.setVisible(true);
                master.canvas.disableIneligibleNodes(CanvasNodeController.this);
                
                // Consume event
                event.consume();
                
            }
        });
        
        this.setOnDragDropped(new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                
                // Acceptable transferable modes
                event.acceptTransferModes(TransferMode.NONE);
                
                // Only accept create connection events
                if (event.getDragboard().getContent(DragContainerController.CREATE_CONNECTION) != null) {
                    
                    // Prepare drag clipboard
                    DragContainerController dragContainer = new DragContainerController();
                    dragContainer.addData((DragContainerController) event.getDragboard().getContent(DragContainerController.CREATE_CONNECTION));
                    ClipboardContent content = new ClipboardContent();
                    content.put(DragContainerController.CREATE_CONNECTION, dragContainer);
                
                    event.setDropCompleted(true);
                    
                }
                
                // Consume event
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("Toolbox connection drag done.");
                master.canvas.dragConnection.setVisible(false);
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
