/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Chris
 */
public class RootLayout extends AnchorPane{

    // FXML variables
    @FXML private SplitPane base_pane;
    @FXML protected AnchorPane canvas;
    @FXML private VBox toolbox;
    private ContextMenu menu = null;
    private ToolboxIcon mDragOverIcon = null;
    
    // Event handlers
    private EventHandler iconDragOverRoot = null;
    private EventHandler iconDragDropped = null;
    private EventHandler iconDragOverCanvas = null;
    
    // Model varaibles
    private thermocycle.Cycle model = null;
    
    // Constructor
    public RootLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RootLayout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        model = new thermocycle.Cycle("test");
    }

    @FXML private void initialize() {
        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root AnchorPane so it can be 
        //visible on both sides of the split pane.
        mDragOverIcon = new ToolboxIcon();
        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon); 
        //populate toolbx
        
        for (IconType diType : IconType.values()) {
            if (thermocycle.Component.class.isAssignableFrom(diType.type)) {
                ToolboxIcon icn = new ToolboxIcon();           
                addDragDetection(icn);
                icn.setType(diType);
                toolbox.getChildren().add(icn);
            }
        }
        buildDragHandlers();
        buildClickHandlers();
    }
    
    private void addDragDetection(ToolboxIcon dragIcon) {
        dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                // set the other drag event handles on their respective objects
                base_pane.setOnDragOver(iconDragOverRoot);
                canvas.setOnDragOver(iconDragOverCanvas);
                canvas.setOnDragDropped(iconDragDropped);
                // get a reference to the clicked ToolboxIcon object
                ToolboxIcon icn = (ToolboxIcon) event.getSource();
                //begin drag ops
                mDragOverIcon.setType(icn.getType());
                mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData ("type", mDragOverIcon.getType().toString());
                content.put(DragContainer.AddNode, container);
                mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
                mDragOverIcon.setVisible(true);
                mDragOverIcon.setMouseTransparent(true);
                event.consume();
            }
        });
    }
    
    private void buildDragHandlers() {
        //drag over transition to move widget form left pane to right pane
        iconDragOverRoot = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Point2D p = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
                if (!canvas.boundsInLocalProperty().get().contains(p)) {
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }
                event.consume();
            }
        };
        iconDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        iconDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.setDropCompleted(true);
                canvas.removeEventHandler(DragEvent.DRAG_OVER, iconDragOverCanvas);
                canvas.removeEventHandler(DragEvent.DRAG_DROPPED, iconDragDropped);
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, iconDragOverRoot);
                mDragOverIcon.setVisible(false);
                event.consume();
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };
        this.setOnDragDone (new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {
                canvas.removeEventHandler(DragEvent.DRAG_OVER, iconDragOverCanvas);
                canvas.removeEventHandler(DragEvent.DRAG_DROPPED, iconDragDropped);
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, iconDragOverRoot);
                mDragOverIcon.setVisible(false);
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
                if (container != null) {
                    if (container.getValue("scene_coords") != null) {
                        ToolboxIcon node;
                        try {
                            node = new DraggableIcon(IconType.valueOf(container.getValue("type")), model);
                            canvas.getChildren().add(node);
                            Point2D cursorPoint = container.getValue("scene_coords");
                            node.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
                        } catch (Exception ex) {
                            Logger.getLogger(RootLayout.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                event.consume();
            }
        });
    }
    
    private void buildClickHandlers() {
        canvas.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(canvas, event.getScreenX(), event.getScreenY());
                }
                event.consume();
            }
        });
    }
    
    private void buildContextMenu() {
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Show Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.getChildren().forEach(c -> {
                    if (c instanceof DraggableIcon) {((DraggableIcon) c).node_grid.setVisible(true);}
                });
            }
        });
        menu.getItems().add(item);
        item = new MenuItem("Hide Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.getChildren().forEach(c -> {
                    if (c instanceof DraggableIcon) {((DraggableIcon) c).node_grid.setVisible(false);}
                });
            }
        });
        menu.getItems().add(item);
    }
}
