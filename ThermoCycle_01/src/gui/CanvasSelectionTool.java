/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class CanvasSelectionTool extends Rectangle {
        
    // Properties
    private DoubleProperty xDragStart;
    private DoubleProperty yDragStart;
    private DoubleProperty xDragTo;
    private DoubleProperty yDragTo;
    
    
    // Event handlers
    private EventHandler<DragEvent> dragOverCanvas;
    private EventHandler<DragEvent> droppedOnCanvas;
    
    public CanvasSelectionTool() {
        
        // Create properties
        xDragStart = new SimpleDoubleProperty(0.0);
        yDragStart = new SimpleDoubleProperty(0.0);
        xDragTo = new SimpleDoubleProperty(0.0);
        yDragTo = new SimpleDoubleProperty(0.0);
        
        // Set up bindings
        this.widthProperty().bind(Bindings.createDoubleBinding(() -> {
            return Math.abs(xDragStart.getValue() - xDragTo.getValue());
        }, xDragStart, xDragTo));
        this.heightProperty().bind(Bindings.createDoubleBinding(() -> {
            return Math.abs(yDragStart.getValue() - yDragTo.getValue());
        }, yDragStart, yDragTo));
        this.xProperty().bind(Bindings.min(xDragStart, xDragTo));
        this.yProperty().bind(Bindings.min(yDragStart, yDragTo));
        
        // Set up visuals
        this.getStrokeDashArray().addAll(20.0, 5.0);
        this.setFill(null);
        this.setStrokeWidth(2);
        this.setStroke(Color.LIGHTGRAY);
        
        // Build handlers
        buildDragHandlers();
        
    }
    
    /**
     * Builds the selection tool drag handlers
     */
    private final void buildDragHandlers() {
        
        // This is added to the canvas when drag detected in CanvasController
        dragOverCanvas = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                CanvasSelectionTool.this.DragTo(event.getX(), event.getY());
                event.consume();
            }
        };
        
        // Drag dropped
        droppedOnCanvas = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                CanvasSelectionTool.this.setVisible(false);
                event.consume();
            }
        };
        
    }
    
    // Set drag start co-ordiantes
    public void StartDrag(Double x, Double y) {
        xDragStart.setValue(x);
        yDragStart.setValue(y);
    }
    
    // Set drag to co-ordinates
    public void DragTo(Double x, Double y) {
        xDragTo.setValue(x);
        yDragTo.setValue(y);
    }
    
}
