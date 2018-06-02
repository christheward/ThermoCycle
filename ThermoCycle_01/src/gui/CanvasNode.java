/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.CanvasPath.Direction;
import static gui.CanvasPath.Direction.*;
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
public final class CanvasNode extends AnchorPane implements Serializable {
    
    
    
    // FXML variables
    @FXML private AnchorPane base;
    @FXML private Circle circle;
    private ContextMenu menu;
    private Tooltip tip;
    private final Canvas canvas;
    private final CanvasIcon canvasIcon;
            
    // Event handlers
    protected EventHandler connectionDragDroppedNode;
            
    // Model variables
    protected thermocycle.Node node;
    
    /**
     * Constructor
     * @param node The model Node represented by CanvasNode
     * @throws Exception 
     */
    public CanvasNode(Canvas canvas, CanvasIcon canvasIcon, thermocycle.Node node) throws Exception {
        this.canvas = canvas;
        this.canvasIcon = canvasIcon;
        this.node = node;
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CanvasNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @FXML private void initialize() {
        setNodeStyle();
        buildNodeClickHandlers();
        buildNodeDragHandlers();
        buildTooltip();
    }
    
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
    
    private void buildTooltip() {
        tip = new Tooltip();
        tip.setText(node.getClass().getSimpleName() + " " + canvas.model.getNodePort(node));
        Tooltip.install(base, tip);
    }
    
    private void buildNodeClickHandlers() {
        base.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Node Click");
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(base, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    canvas.infoboxContent.showDetails(CanvasNode.this);
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
                System.out.println("CanvasNode: Drag detection");
                
                // Get the source object
                CanvasNode source = (CanvasNode) event.getSource();
                
                // Set drag handlers for the uunderlying canvas
                canvas.setOnDragDropped(null);
                canvas.setOnDragOver(null);
                base.setOnDragDropped(null);
                canvas.setOnDragOver(canvas.connectionDragOverCanvas);
                base.setOnDragDropped(connectionDragDroppedNode);
                
                // Bind the start to the node
                canvas.dragConnection.startDrag(source);
                
                // Put icon type in clipboard
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData("startIcon", source.canvasIcon.hashCode());
                container.addData("startNode", source.hashCode());
                content.put(DragContainer.AddLink, container);
                
                // Start the drag operation
                canvas.dragConnection.startDragAndDrop(TransferMode.ANY).setContent(content);
                canvas.dragConnection.setVisible(true);
                canvas.dragConnection.setMouseTransparent(true);
                
                // Disable inligible nodes
                canvas.disableIneligibleNodes(CanvasNode.this);
                //canvas.getNodes().filter(n -> (!(n.node.getClass().equals(CanvasNode.this.node.getClass())) | (n.node.port.equals(CanvasNode.this.node.port)))).forEach(n -> {
                //    n.disableProperty().set(true);
                //});
                
                // Consume event
                event.consume();
            }
        });
        
        connectionDragDroppedNode = new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                System.out.println("CanvasNode: Drag dropped");
                
                // Get source object
                CanvasNode source = (CanvasNode)event.getSource();
                
                // Remomve drag handlers
                canvas.setOnDragOver(null);
                
                // Add drop coordinates to drag container
                ClipboardContent content = new ClipboardContent();
                DragContainer container = (DragContainer)event.getDragboard().getContent(DragContainer.AddLink);
                container.addData("endIcon", source.canvasIcon.hashCode());
                container.addData("endNode", source.hashCode());
                content.put(DragContainer.AddLink, container);
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
                //((GridPane)CanvasNode.this.getParent()).setGridLinesVisible(true);
                canvasIcon.node_grid.setGridLinesVisible(true);
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Info");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.infoboxContent.showDetails(CanvasNode.this);
                event.consume();
            }
        });
        menu.getItems().add(item);
    }
    
    /**
     * Gets the location of the CanvasNode in the scene
     * @return Returns the center of the CanvasNode,
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
