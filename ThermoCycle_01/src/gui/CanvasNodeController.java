/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.CanvasPathController.Direction;
import static gui.CanvasPathController.Direction.*;
import java.io.IOException;
import java.io.Serializable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
public final class CanvasNodeController extends AnchorPane implements Serializable {
    
    // FXML variables
    @FXML private AnchorPane base;
    @FXML private Circle circle;
    
    // GUI variables
    private ContextMenu menu;
    private Tooltip tip;
    private final MasterSceneController master;
    private final CanvasComponentController canvasIcon;
            
    // Event handlers
    protected EventHandler connectionDragDroppedNode;
            
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
        
        this.canvasIcon = canvasIcon;
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
        else {
            // Error
        }
    }
    
    /**
     * Builds the tool tip for this node
     */
    protected void buildTooltip() {
        tip = new Tooltip();
        Tooltip.install(base, tip);
        tip.setOnShowing(new EventHandler() {
            @Override
            public void handle(Event event) {
                tip.setText(node.getClass().getSimpleName() + " " + master.getModel().getNodePort(node) + "\n" + node.toString());
            }
        });
    }
    
    /**
     * Builds the click handlers for this node
     */
    private void buildNodeClickHandlers() {
        base.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(base, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    master.infobox.showDetails(CanvasNodeController.this);
                }
                event.consume();
            }
        });
    }
        
    private void buildNodeDragHandlers() {
        
        //drag detection for node dragging
        base.setOnDragDetected(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Get the source object
                CanvasNodeController source = (CanvasNodeController) event.getSource();
                
                // Set drag handlers for the uunderlying canvas
                master.canvas.setOnDragDropped(null);
                master.canvas.setOnDragOver(null);
                base.setOnDragDropped(null);
                master.canvas.setOnDragOver(master.canvas.connectionDragOverCanvas);
                base.setOnDragDropped(connectionDragDroppedNode);
                
                // Bind the start to the node
                master.canvas.dragConnection.startDrag(source);
                
                // Put icon type in clipboard
                ClipboardContent content = new ClipboardContent();
                DragContainerController container = new DragContainerController();
                container.addData("startIcon", source.canvasIcon.hashCode());
                container.addData("startNode", source.hashCode());
                content.put(DragContainerController.AddLink, container);
                
                // Start the drag operation
                master.canvas.dragConnection.startDragAndDrop(TransferMode.ANY).setContent(content);
                master.canvas.dragConnection.setVisible(true);
                master.canvas.dragConnection.setMouseTransparent(true);
                
                // Disable inligible nodes
                master.canvas.disableIneligibleNodes(CanvasNodeController.this);
                
                // Consume event
                event.consume();
            }
        });
        
        connectionDragDroppedNode = new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                
                // Get source object
                CanvasNodeController source = (CanvasNodeController)event.getSource();
                
                // Remomve drag handlers
                master.canvas.setOnDragOver(null);
                
                // Add drop coordinates to drag container
                ClipboardContent content = new ClipboardContent();
                DragContainerController container = (DragContainerController)event.getDragboard().getContent(DragContainerController.AddLink);
                container.addData("endIcon", source.canvasIcon.hashCode());
                container.addData("endNode", source.hashCode());
                content.put(DragContainerController.AddLink, container);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                
                // Consume event
                event.consume();
            }
        };
    }
    
    private void buildContextMenu() {
        menu = new ContextMenu();
        
        MenuItem item = new MenuItem("Move Node");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvasIcon.node_grid.setGridLinesVisible(true);
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Info");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.infobox.showDetails(CanvasNodeController.this);
                event.consume();
            }
        });
        menu.getItems().add(item);
    }
    
    /**
     * Gets the location of the CanvasNodeController in the scene
     * @return Returns the center of the CanvasNodeController,
     */
    protected Point2D sceneLocation() {
        Bounds bounds = getBoundsInLocal();
        return localToScene(bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight()/2);
    }
    
    /**
     * Gets the nodes position in the CanvasIcons node_gris
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
