/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 *
 * @author Chris
 */
public class CanvasConnection extends Pane{
    
    // FXML Variables
    @FXML private Pane root_pane;
    @FXML private CubicCurve cubic_curve;
    private ContextMenu menu = null;
    
    //private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
    //private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
    
    
    // Model variables
    private thermocycle.Cycle model = null;
    private thermocycle.Connection connection = null;
    
    // Constructor
    public CanvasConnection() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CanvasConnection.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try { 
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @FXML private void initialize() {
        //mControlOffsetX.set(100.0);
        //mControlOffsetY.set(50.0);
	//mControlDirectionX1.bind(new When (cubic_curve.startXProperty().greaterThan(cubic_curve.endXProperty())).then(-1.0).otherwise(1.0));
	//mControlDirectionX2.bind(new When (cubic_curve.startXProperty().greaterThan(cubic_curve.endXProperty())).then(1.0).otherwise(-1.0));
        cubic_curve.controlX1Property().bind(Bindings.add(cubic_curve.startXProperty(), 100));
        cubic_curve.controlX2Property().bind(Bindings.add(cubic_curve.endXProperty(), -100));
        cubic_curve.controlY1Property().bind(Bindings.add(cubic_curve.startYProperty(), 0));
        cubic_curve.controlY2Property().bind(Bindings.add(cubic_curve.endYProperty(), 0));
    }
    
    private void buildContextMenu() {
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Delete");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                model.removeConnection(connection);
                Pane parent = (Pane) root_pane.getParent();
                parent.getChildren().remove(root_pane);
                event.consume();
            }    
        });
        menu.getItems().add(item);   
    }
    
    public void setStart(Point2D startPoint) {
        cubic_curve.setStartX(startPoint.getX());
        cubic_curve.setStartY(startPoint.getY());
    }
    
    public void setEnd(Point2D endPoint) {
        cubic_curve.setEndX(endPoint.getX());
        cubic_curve.setEndY(endPoint.getY());
    }    
}
