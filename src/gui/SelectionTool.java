/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class SelectionTool extends Rectangle {
        
    // Properties
    private DoubleProperty xDragFrom;
    private DoubleProperty yDragFrom;
    private DoubleProperty xDragTo;
    private DoubleProperty yDragTo;
    
    
    
    public SelectionTool() {
        
        // Create properties
        xDragFrom = new SimpleDoubleProperty(0.0);
        yDragFrom = new SimpleDoubleProperty(0.0);
        xDragTo = new SimpleDoubleProperty(0.0);
        yDragTo = new SimpleDoubleProperty(0.0);
        
        // Set up bindings
        this.widthProperty().bind(Bindings.createDoubleBinding(() -> {
            return Math.abs(xDragFrom.getValue() - xDragTo.getValue());
        }, xDragFrom, xDragTo));
        this.heightProperty().bind(Bindings.createDoubleBinding(() -> {
            return Math.abs(yDragFrom.getValue() - yDragTo.getValue());
        }, yDragFrom, yDragTo));
        this.xProperty().bind(Bindings.min(xDragFrom, xDragTo));
        this.yProperty().bind(Bindings.min(yDragFrom, yDragTo));
        
        // Set up visuals
        this.getStrokeDashArray().addAll(20.0, 5.0);
        this.setFill(null);
        this.setStrokeWidth(2);
        this.setStroke(Color.LIGHTGRAY);
        
    }
    
    // Set drag start co-ordiantes
    public void DragFrom(Double x, Double y) {
        xDragFrom.setValue(x);
        yDragFrom.setValue(y);
    }
    
    // Set drag to co-ordinates
    public void DragTo(Double x, Double y) {
        xDragTo.setValue(x);
        yDragTo.setValue(y);
    }
    
}
