/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Optional;
import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import thermocycle.Connection;

/**
 *
 * @author Chris
 */
public class ConnectionController extends Path {
    
    // FXML variables
    protected NodeController start;
    protected NodeController end;
    private ContextMenu menu;
    private final MasterSceneController master;
    
    // Model variables
    protected Connection connection;
    
    /**
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
    */
    
    /**
     * Constructor
     * @param master
     */
    public ConnectionController(MasterSceneController master) {
        
        // Set the master
        this.master = master;
        
        // Create path
        getElements().add(new MoveTo());
        getElements().add(new LineTo());
        
        // Ensure focus cannot be given automatically
        this.focusTraversableProperty().setValue(false);
        
    }
    
    /**
     * Bind the connection to the start node.
     * @param node the node to bind to.
     */
    protected void bindStart(NodeController node) {
        
        // Set the star nide
        start = node;
        
        // Set the style
        setStyle(start);
        
        // Bind first path element to start node
        firstElement().xProperty().bind(new DoubleBinding() {
            {
                bind(start.localToSceneTransformProperty(),start.widthProperty(),start.heightProperty());
            }
            @Override
            protected double computeValue() {
                return master.canvas.sceneToLocal(start.localToSceneTransformProperty().getValue().transform(start.widthProperty().getValue()/2.0, start.heightProperty().getValue()/2.0)).getX();
            }
        });
        firstElement().yProperty().bind(new DoubleBinding() {
            {
                bind(start.localToSceneTransformProperty(),start.widthProperty(),start.heightProperty());
            }
            @Override
            protected double computeValue() {
                return master.canvas.sceneToLocal(start.localToSceneTransformProperty().getValue().transform(start.widthProperty().getValue()/2.0, start.heightProperty().getValue()/2.0)).getY();
            }
        });
        
    }
    
    /**
     * Set connection
     * @param connection the model connection.
     */
    protected void bindEnd(NodeController node, Connection connection) {
        
        // Set the end node
        end = node;
        
        // Set the connection
        this.connection = connection;
        
        // Bind last path element to end node
        lastElement().xProperty().bind(new DoubleBinding() {
            {
                bind(end.localToSceneTransformProperty(),end.widthProperty(),end.heightProperty());
            }
            @Override
            protected double computeValue() {
                return master.canvas.sceneToLocal(end.localToSceneTransformProperty().getValue().transform(end.widthProperty().getValue()/2.0, end.heightProperty().getValue()/2.0)).getX();
            }
        });
        lastElement().yProperty().bind(new DoubleBinding() {
            {
                bind(end.localToSceneTransformProperty(),end.widthProperty(),end.heightProperty());
            }
            @Override
            protected double computeValue() {
                return master.canvas.sceneToLocal(end.localToSceneTransformProperty().getValue().transform(end.widthProperty().getValue()/2.0, end.heightProperty().getValue()/2.0)).getY();
            }
        });

        // Creat context menu
        buildContextMenu();
        buildClickHandlers();
        
    }
    
    /**
     * Bind the connection to the end node and create the connection if it doesn't exit.
     * @param node the end node.
     * @return true if the connection is sucessfully created
     */
    protected boolean bindEnd(NodeController node) {
        
        // Set the end node
        end = node;
        
        // If the connections is suceessfully created
        Optional<Connection> c = master.getModel().createConnection(start.node, end.node);
        
        if (c.isPresent()) {
            
            // Create connection
            connection = c.get();
            
            // Bind last path element to end node
            lastElement().xProperty().bind(new DoubleBinding() {
                {
                    bind(end.localToSceneTransformProperty(),end.widthProperty(),end.heightProperty());
                }
                @Override
                protected double computeValue() {
                    return master.canvas.sceneToLocal(end.localToSceneTransformProperty().getValue().transform(end.widthProperty().getValue()/2.0, end.heightProperty().getValue()/2.0)).getX();
                }
            });
            lastElement().yProperty().bind(new DoubleBinding() {
                {
                    bind(end.localToSceneTransformProperty(),end.widthProperty(),end.heightProperty());
                }
                @Override
                protected double computeValue() {
                    return master.canvas.sceneToLocal(end.localToSceneTransformProperty().getValue().transform(end.widthProperty().getValue()/2.0, end.heightProperty().getValue()/2.0)).getY();
                }
            });
            
            // Creat context menu
            buildContextMenu();
            buildClickHandlers();
            
            return true;
            
        }
        else {
        
            return false;
        
        }
    }
    
    /**
     * Builds the mouse click handlers for the connection.
     */
    private void buildClickHandlers() {
        this.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    menu.show(ConnectionController.this, event.getScreenX(), event.getScreenY());
                }
                else {
                    master.setFocus(ConnectionController.this.start);
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
                master.canvas.remove(ConnectionController.this);
                event.consume();
            }
        });
        menu.getItems().add(item);
    }
    
    /**
     * Sets the style for the path.
     * @param canvasNode The canvas node that defines the type of connection.
     */
    protected void setStyle(NodeController canvasNode) {
        getStyleClass().clear();
        if (canvasNode.node instanceof thermocycle.FlowNode) {
            getStyleClass().add("path-flow");
        }
        else if (canvasNode.node instanceof thermocycle.HeatNode) {
            getStyleClass().add("path-heat");
        }
        else if (canvasNode.node instanceof thermocycle.WorkNode) {
            getStyleClass().add("path-work");
        }
    }
    
    /**
     * Drag line to new scene location
     * @param x X co-ordinate to drag to
     * @param y Y co-ordinate to drag to
     */
    protected void dragTo(double x, double y) {
        lastElement().setX(master.canvas.sceneToLocal(x, y).getX());
        lastElement().setY(master.canvas.sceneToLocal(x, y).getY());
    }
    
    /**
     * Get the first element in the path, which shoudl allwasy be of type MoveTo.
     * @return the first element in the path.
     */
    private MoveTo firstElement() {
        return (MoveTo) getElements().get(0);
    }
    
    /**
     * Get the last element in path, which should allways be of type LineTo.
     * @return the last element in the path;
     */
    private LineTo lastElement() {
        return (LineTo) getElements().get(getElements().size()-1);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        master.canvas.getComponents().filter(c -> c.component.contiansNode(start.node).isPresent()).findFirst().ifPresent(c -> {
            sb.append(c.component);
            sb.append(" ");
            sb.append(c.component.contiansNode(start.node).get());
        });
        sb.append(" <-> ");
        master.canvas.getComponents().filter(c -> c.component.contiansNode(end.node).isPresent()).findFirst().ifPresent(c -> {
            sb.append(c.component);
            sb.append(" ");
            sb.append(c.component.contiansNode(end.node).get());
        });
        return sb.toString();
    }
    
}
