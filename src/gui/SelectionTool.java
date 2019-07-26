/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class SelectionTool extends Rectangle {
        
    // Properties
    private ObjectProperty<Point2D> dragFrom;
    private ObjectProperty<Point2D> dragTo;
    
    public SelectionTool() {
        
        // Create properties
        dragFrom = new SimpleObjectProperty(new Point2D(0.0, 0.0));
        dragTo = new SimpleObjectProperty(new Point2D(0.0, 0.0));
        
        this.widthProperty().bind(new DoubleBinding() {
            {
                bind(dragFrom, dragTo);
            }
            @Override
            protected double computeValue() {
                return Math.abs(dragFrom.getValue().getX() - dragTo.getValue().getX());
            }
        });
        this.heightProperty().bind(new DoubleBinding() {
            {
                bind(dragFrom, dragTo);
            }
            @Override
            protected double computeValue() {
                return Math.abs(dragFrom.getValue().getY() - dragTo.getValue().getY());
            }
        });
        this.xProperty().bind(new DoubleBinding() {
            {
                bind(dragFrom, dragTo);
            }
            @Override
            protected double computeValue() {
                return Math.min(dragFrom.getValue().getX(), dragTo.getValue().getX());
            }
        });
        this.yProperty().bind(new DoubleBinding() {
            {
                bind(dragFrom, dragTo);
            }
            @Override
            protected double computeValue() {
                return Math.min(dragFrom.getValue().getY(), dragTo.getValue().getY());
            }
        });
        
        // Set up visuals
        this.getStrokeDashArray().addAll(20.0, 5.0);
        this.setFill(null);
        this.setStrokeWidth(2);
        this.setStroke(Color.LIGHTGRAY);
        
    }
    
    // Set drag start co-ordiantes
    public void dragFrom(double x, double y) {
        dragFrom.setValue(getParent().sceneToLocal(new Point2D(x, y)));
    }
    
    // Set drag to co-ordinates
    public void dragTo(double x, double y) {
        dragTo.setValue(getParent().sceneToLocal(new Point2D(x, y)));
    }
    
}
