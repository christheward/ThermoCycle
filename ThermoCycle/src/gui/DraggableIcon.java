/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ListIterator;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
public class DraggableIcon extends ToolboxIcon{
    
    // FXML variables
    private ContextMenu menu = null;
    private Point2D mDragOffset = new Point2D(0.0, 0.0);
    
    // Event handlers
    private EventHandler  mContextDragOver;
    private EventHandler  mContextDragDropped;
    private EventHandler  mContextRightClick;
    
    // Model variables
    final protected thermocycle.Cycle model;
    private thermocycle.Component component;
    
    // Constructor
    public DraggableIcon(IconType iType, thermocycle.Cycle model) throws Exception {
        super();
        this.model = model;
        setType(iType);
        createComponent();
        addNodes();
    }
    
    @FXML private void initialize() {
        buildNodeDragHandlers();
        buildNodeClickHandlers();
    }
    
    private void createComponent() throws Exception {
        switch (iType) {
            case COMBUSTOR:
                component = model.createCombustor("Test");
                break;
            case COMPRESSOR:
                component = model.createCompressor("Test");
                break;
            case HEAT_EXCHANGER:
                component = model.createHeatExchanger("Test");
                break;
            case HEAT_SINK:
                component = model.createHeatSink("Test");
                break;
            case TURBINE:
                component = model.createTurbine("Test");
                break;
            default:
                throw new Exception("Unknown component type!");
        }
    }
    
    protected AnchorPane getCanvas() {
        return (AnchorPane)base.getParent();
    }
    
    private void addNodes() throws Exception {
        ListIterator<thermocycle.FlowNode> lif = component.getFlowNodes().listIterator();
        while (lif.hasNext()) {
            int idx = lif.nextIndex();
            thermocycle.FlowNode fn = lif.next();
            node_grid.add(new CanvasNode(fn), iType.flownodes[idx][0], iType.flownodes[idx][1]);
        }
        ListIterator<thermocycle.WorkNode> liw = component.getWorkNodes().listIterator();
        while (liw.hasNext()) {
            int idx = liw.nextIndex();
            thermocycle.WorkNode wn = liw.next();
            node_grid.add(new CanvasNode(wn), iType.worknodes[idx][0], iType.worknodes[idx][1]);
        }
        ListIterator<thermocycle.HeatNode> lih = component.getHeatNodes().listIterator();
        while (lih.hasNext()) {
            int idx = lih.nextIndex();
            thermocycle.HeatNode hn = lih.next();
            node_grid.add(new CanvasNode(hn), iType.heatnodes[idx][0], iType.heatnodes[idx][1]);
        }
    }
    
    private void buildNodeDragHandlers() {
        //drag detection for node dragging
        icon.setOnDragDetected ( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                getParent().setOnDragOver (mContextDragOver);
                getParent().setOnDragDropped (mContextDragDropped);
                //begin drag ops
                mDragOffset = new Point2D(event.getX(), event.getY());
                relocateToPoint (new Point2D(event.getSceneX(), event.getSceneY()));
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData ("type", iType.toString());
                content.put(DragContainer.DragNode, container);
                startDragAndDrop (TransferMode.ANY).setContent(content);                  
                event.consume();                    
            }
        });
        //dragover to handle node dragging in the right pane view
        mContextDragOver = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);                
                relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        //dragdrop for node dragging
        mContextDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                event.setDropCompleted(true);
                event.consume();
            }
        };
    }
    
    private void buildNodeClickHandlers() {
        icon.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(icon, event.getScreenX(), event.getScreenY());
                }
                event.consume();
            }
        });
    }
    
    private void buildContextMenu() {
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Rename");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                label.setText("Test");
                event.consume();
            }    
        });
        menu.getItems().add(item);
        menu.getItems().add(new SeparatorMenuItem());
        item = new MenuItem("Delete");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                getCanvas().getChildren().remove(base);
                event.consume();
            }
        });
        menu.getItems().add(item);
        if (node_grid.isVisible()) {
            item = new MenuItem("Hide Nodes");
            item.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    node_grid.setVisible(false);
                }
            });
        }
        else {
            item = new MenuItem("Show Nodes");
            item.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    node_grid.setVisible(true);
                }
            });
        }
        menu.getItems().add(item);
        item = new MenuItem("Move Nodes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                //AnchorPane parent  = (AnchorPane) base.getParent();
                //parent.getChildren().remove(base);
                event.consume();
            }
        });
        menu.getItems().add(item);   
    }    
}
