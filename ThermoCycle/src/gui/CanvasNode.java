/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Chris
 */
public class CanvasNode extends AnchorPane{
    
    // FXML variables
    @FXML private AnchorPane base;
    @FXML private Circle circle;
    private ContextMenu menu = null;
    private Tooltip tip = null;
    private CanvasConnection mDragLink = null;
    
    // Event handlers
    private EventHandler  mContextRightClick;
    private EventHandler <MouseEvent> mLinkHandleDragDetected = null;
    private EventHandler <DragEvent> mLinkHandleDragDropped = null;
    private EventHandler <DragEvent> mContextLinkDragOver = null;
    private EventHandler <DragEvent> mContextLinkDragDropped = null;
    
    // Model variables
    private thermocycle.Node node = null;
    
    // Constructor
    public CanvasNode(thermocycle.Node node) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CanvasNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        circle.getStyleClass().add("node");
        buildNodeClickHandlers();
        buildNodeDragHandlers();
        circle.setOnDragDetected(mLinkHandleDragDetected);
        circle.setOnDragDropped(mLinkHandleDragDropped);
        setNode(node);
    }
    
    @FXML private void initialize() {
        mDragLink = new CanvasConnection();
        mDragLink.setVisible(false);
        /*
        parentProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                //canvas = (AnchorPane) getParent();
            }
        });
        */
    }
    
    private AnchorPane getCanvas() {
        return (AnchorPane)(getParent().getParent().getParent().getParent().getParent());
    }
    
    private final void setNode(thermocycle.Node node) throws Exception {
        this.node = node;
        if (node instanceof thermocycle.FlowNode) {circle.getStyleClass().add("node-flow");}
        else if (node instanceof thermocycle.HeatNode) {circle.getStyleClass().add("node-heat");}
        else if (node instanceof thermocycle.WorkNode) {circle.getStyleClass().add("node-work");}
        else {throw new Exception();}
        tip = new Tooltip();
        tip.setText(node.getLet().name());
        Tooltip.install(circle, tip);
    }
    
    private void buildNodeClickHandlers() {
        circle.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(circle, event.getScreenX(), event.getScreenY());
                }
                event.consume();
            }
        });
    }
    
    private void buildContextMenu() {
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Move Node");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
            }
        });
        menu.getItems().add(item);
    }
    
    private void buildNodeDragHandlers() {
        
        mLinkHandleDragDetected = new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);                 
                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mLinkHandleDragDropped);

                // Add connection to canvas
                getCanvas().getChildren().add(0,mDragLink);                  
                mDragLink.setVisible(true);
                
                Point2D p = new Point2D(getLayoutX() + (getWidth() / 2.0), getLayoutY() + (getHeight() / 2.0));
                mDragLink.setStart(p);
                
                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer ();
                
                Circle circle_handle = (Circle) event.getSource();
                CanvasNode parent = (CanvasNode) circle_handle.getParent().getParent();
                container.addData("source", "Node");
                content.put(DragContainer.AddLink, container);
                
                parent.startDragAndDrop(TransferMode.ANY).setContent(content);
                
                if (container != null) {
                    System.out.println(container.getData());
                }
                
                event.consume();
            }
        };
        
        mLinkHandleDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                event.setDropCompleted(true);
                event.consume();                    
            }
        };
        
        mContextLinkDragOver = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                //Relocate user-draggable link
                if (!mDragLink.isVisible()) {
                    mDragLink.setVisible(true);
                    mDragLink.setEnd(new Point2D(event.getX(), event.getY()));
                }
                event.consume();
            }
        };
        
        mLinkHandleDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                
                //get the drag data.  If it's null, abort.  
                //This isn't the drag event we're looking for.
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);
                if (container == null) return;
                
                Circle circle_handle = (Circle) event.getSource();
                CanvasNode parent = (CanvasNode) circle_handle.getParent().getParent();
                
                ClipboardContent content = new ClipboardContent();
                container.addData("target", "also unnessisary");
                content.put(DragContainer.AddLink, container);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                
                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);
                getCanvas().getChildren().remove(0);
                
                event.consume();
            }
        };
    };
}
