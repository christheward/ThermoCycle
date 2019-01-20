/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import static gui.CanvasConnectionController.Direction.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import thermocycle.Connection;

/**
 *
 * @author Chris
 */
public class CanvasConnectionController extends ToolboxConnectionController {
    
    // FXML variables
    protected CanvasNodeController start;
    protected CanvasNodeController end;
    private ContextMenu menu;
    private final MasterSceneController master;
    
    // varaibles
    private static double length = 10;
    private static double radius = 20;
    
    // Model variables
    protected Connection connection;
    
    /**
     * Grid position enum
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
        static {
            UP.opposite = DOWN;
            DOWN.opposite = UP;
            LEFT.opposite = RIGHT;
            RIGHT.opposite = LEFT;
            UP.clockwise = RIGHT;
            DOWN.clockwise = LEFT;
            LEFT.clockwise = UP;
            RIGHT.clockwise = DOWN;
            UP.clockwise = LEFT;
            DOWN.clockwise = RIGHT;
            LEFT.clockwise = DOWN;
            RIGHT.clockwise = UP;
            UP.vector = new Point2D(0,1);
            DOWN.vector = new Point2D(0,-1);
            LEFT.vector = new Point2D(-1,0);
            RIGHT.vector = new Point2D(1,0);
        }
        private Direction opposite;
        private Direction clockwise;
        private Direction counterClockwise;
        private Point2D vector;
        protected Direction getOpposite() {
            return opposite;
        }
        protected static Direction upOrDown(double input) {
            return (input > 0) ? UP : DOWN;
        }
        protected static Direction leftOrRight(double input) {
            return (input > 0) ? RIGHT : LEFT;
        }
    }
    
    /**
     * Constructor
     * @param start
     * @param end
     */
    public CanvasConnectionController(MasterSceneController master) {
        super();
        this.master = master;
    }
    
    /**
     * Binds the ends of the connection to a CanvasNodeController.
     * @param startNode The CanvasNodeController to bind the connection to.
     * @param endNode The CanvasNodeController to bind the connection to.
     * @param connection The underlying model connection represented by this component.
     */
    protected void bindEnds(CanvasNodeController startNode, CanvasNodeController endNode, Connection connection) {
        // Asign nodes
        start = startNode;
        end = endNode;
        // Set up path
        getElements().clear();
        getElements().add(new MoveTo());
        getElements().add(new LineTo());
        // Set the style
        setStyle(start);
        // Bind the start
        moveStart();
        start.localToSceneTransformProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                moveStart();
            }
        });
        // Bind the end
        moveEnd();
        end.localToSceneTransformProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                moveEnd();
            }
        });
        // Create conection in model
        if (connection == null) {
            this.connection = master.getModel().createConnection(start.node, end.node);
        }
        else {
            this.connection = connection;
        }
        // Creat context menu
        buildContextMenu();
        buildClickHandlers();
    }
    
    /**
     * Move the start of the path to the start CanvasNodeController
     */
    private void moveStart() {
        Point2D point = sceneToLocal(start.getLocationInScene());
        ((MoveTo)first()).setX(point.getX());
        ((MoveTo)first()).setY(point.getY());
    }
    
    /**
     * Move the start of the path to the end CanvasNodeController
     */
    private void moveEnd() {
        Point2D point = sceneToLocal(end.getLocationInScene());
        ((LineTo)last()).setX(point.getX());
        ((LineTo)last()).setY(point.getY());
    }
    
    /**
     * Test to see if this is a flow connection. This needs to be it's own function because the node type is not known when creating the context menu.
     * 
     */
    
    /**
     * Builds the mouse click handlers for the connection.
     */
    private void buildClickHandlers() {
        this.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    menu.show(CanvasConnectionController.this, event.getScreenX(), event.getScreenY());
                }
                else {
                    master.setFocus(CanvasConnectionController.this.start);
                }
                event.consume();
            }
        });
    }
    
    /**
     * Builds the context menu for connection
     */
    private void buildContextMenu() {
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Remove connection");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.canvas.remove(CanvasConnectionController.this);
                event.consume();
            }
        });
        menu.getItems().add(item);
    }
    
    private void generatePath() {

        Point2D startOffset = start.getLocationInScene().add(start.getDirection().vector.multiply(length));
        Point2D endOffset = end.getLocationInScene().subtract(end.getDirection().vector.multiply(length));
        Point2D diff = endOffset.subtract(startOffset);
        
        intersection(start.getLocationInScene(), start.getDirection().vector, end.getLocationInScene(), end.getDirection().opposite.vector);
        
        // free to continue in the same direction
        // infinite cost to reverse
        // small cost to turn
        // how to make sure it's symmetric???
        
        this.getElements().clear();
        MoveTo elem = new MoveTo();
        elem.setX(start.getLocationInScene().getX());
        if (start.getDirection().equals(end.getDirection())) {
            // Facing the same direction
            if (start.getLocationInScene().dotProduct(start.getDirection().vector) > end.getLocationInScene().dotProduct(end.getDirection().vector)) {
                // start leg is shorter
            }
        }
        else if (start.getDirection().equals(end.getDirection().opposite)) {
            // Facing each other
            
        }
        
        if (diff.getX() < 0) {
            if (diff.getY() < 0) {
                // LEFT-DOWN
                
            }
            else {
                // LEFT-UP
            }
        }
        else {
            if (diff.getY() < 0) {
                // RIGHT-DOWN
            }
            else {
                // RIGHT-UP
            }
        }
    }
    
    /**
     * Find the intersection point of two lines.
     * @param point1
     * @param vector1
     * @param point2
     * @param vector2
     * @return 
     */
    private Point2D intersection(Point2D point1, Point2D vector1, Point2D point2, Point2D vector2) {
        Point2D intersection = null;
        if (vector1.crossProduct(vector2).getZ() == 0) {
            if (point2.subtract(point1).crossProduct(vector1).getZ() == 0) {
                //  Co-linear
            }
            else {
                // Parallel
            }
        }
        else {
            Point2D point1b = point1.add(vector1);
            Point2D point2b = point2.add(vector2);
            double x = ((point1.getX()*point2.getY() - point1.getY()*point1b.getX())*(point1.getX() - point2b.getX()) - ((point1.getX() - point2.getX())*(point2.getX()*point2b.getY() - point2.getY()*point2b.getX())))/((point1.getX() - point1b.getX())*(point2.getY() - point2b.getY()) - (point1.getY() - point1b.getY())*(point2.getX() - point2b.getX()));
            double y = ((point1.getX()*point2.getY() - point1.getY()*point1b.getX())*(point2.getY() - point2b.getY()) - ((point1.getY() - point2.getY())*(point2.getX()*point2b.getY() - point2.getY()*point2b.getX())))/((point1.getX() - point1b.getX())*(point2.getY() - point2b.getY()) - (point1.getY() - point1b.getY())*(point2.getX() - point2b.getX()));
            intersection = new Point2D(x, y);
        }
        return intersection;
    }
    
}
