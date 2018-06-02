/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 *
 * @author Chris
 */
public class CanvasIcon extends ToolboxIcon{
    
    // FXML variables
    private ContextMenu menu;
    private final Canvas canvas;
    
    // Event handlers
    private EventHandler  iconDragOverCanvas;
    private EventHandler  iconDragDroppedCanvas;
    
    // Model variables
    protected thermocycle.Component component;
    
    /**
     * Constructor
     * @param iType The icon type.
     * @param model The thermocycle model.
     * @throws Exception 
     */
    public CanvasIcon(Canvas canvas, IconType iType) throws Exception {
        super();
        this.canvas = canvas;
        setType(iType);
        icon.getStyleClass().add("icon-component");
        createComponent();
    }
    
    /**
     * Initializer
     */
    @FXML private void initialize() {
        buildNodeDragHandlers();
        buildNodeClickHandlers();
    }
    
    /**
     * Creates the thermodynamic cycle component. Must only be called after icon type has been set with setType() in the superclass.
     * @throws Exception 
     */
    private final void createComponent() throws Exception {
        switch (iType) {
            case COMBUSTOR:
                component = canvas.model.createCombustor(iType.name);
                break;
            case COMPRESSOR:
                component = canvas.model.createCompressor(iType.name);
                break;
            case HEAT_EXCHANGER:
                component = canvas.model.createHeatExchanger(iType.name);
                break;
            case HEAT_SINK:
                component = canvas.model.createHeatSink(iType.name);
                break;
            case TURBINE:
                component = canvas.model.createTurbine(iType.name);
                break;
            default:
                throw new Exception("Unknown component type!");
        }
        addNodes();
    }
    
    /**
     * Adds the input/output work/heat/and flow nodes to the CanvasIcon 
     * @throws Exception 
     */
    private void addNodes() throws Exception {
        ListIterator<thermocycle.FlowNode> lif = component.flowNodes.listIterator();
        while (lif.hasNext()) {
            int idx = lif.nextIndex();
            thermocycle.FlowNode fn = lif.next();
            CanvasNode node = new CanvasNode(canvas, CanvasIcon.this, fn);
            node.setOnDragDropped(node.connectionDragDroppedNode);
            node_grid.add(node, iType.flownodes[idx][0], iType.flownodes[idx][1]);
        }
        ListIterator<thermocycle.WorkNode> liw = component.workNodes.listIterator();
        while (liw.hasNext()) {
            int idx = liw.nextIndex();
            thermocycle.WorkNode wn = liw.next();
            node_grid.add(new CanvasNode(canvas, CanvasIcon.this, wn), iType.worknodes[idx][0], iType.worknodes[idx][1]);
        }
        ListIterator<thermocycle.HeatNode> lih = component.heatNodes.listIterator();
        while (lih.hasNext()) {
            int idx = lih.nextIndex();
            thermocycle.HeatNode hn = lih.next();
            node_grid.add(new CanvasNode(canvas, CanvasIcon.this, hn), iType.heatnodes[idx][0], iType.heatnodes[idx][1]);
        }
    }
    
    /**
     * Builds the CanvasIcon drag handlers
     */
    private void buildNodeDragHandlers() {
        
        //drag detection for node dragging
        icon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("CanvasIcon: Drag detected");
                
                // Apply the drag handlers to teh canvas
                canvas.setOnDragOver(null);
                canvas.setOnDragDropped(null);
                canvas.setOnDragOver (iconDragOverCanvas);
                canvas.setOnDragDropped (iconDragDroppedCanvas);
                
                // Put icon type in clipboard
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData("type", iType.toString());
                content.put(DragContainer.DragNode, container);
                
                // Start drag operations
                relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                startDragAndDrop(TransferMode.ANY).setContent(content);
                
                // Consume event
                event.consume();                    
            }
        });
        
        //dragover to handle node dragging in the canvas
        iconDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                System.out.println("CanvasIcon: Drag over canvas");
                event.acceptTransferModes(TransferMode.ANY);                
                relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        
        //dragdrop for node dragging
        iconDragDroppedCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                System.out.println("CanvasIcon: Drag dropped canvas");
                canvas.setOnDragOver(null);
                canvas.setOnDragDropped(null);
                event.setDropCompleted(true);
                event.consume();
            }
        };
        
    }
    
    /**
     * Builds the mouse click handlers for CanvasIcon
     */
    private void buildNodeClickHandlers() {
        icon.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("CanvasIcon: Click");
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(icon, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    canvas.infoboxContent.showDetails(CanvasIcon.this);
                }
                event.consume();
            }
        });
    }
    
    /**
     * Builds context menus for the CanvasIcon
     */
    private void buildContextMenu() {
        menu = new ContextMenu();
        MenuItem item = new MenuItem("Rename");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                input.focusedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                        if (!newPropertyValue) {
                            name.setVisible(true);
                            input.setVisible(false);
                        }
                    }
                });
                input.setPromptText(name.getText());
                name.setVisible(false);
                input.setVisible(true);
                input.requestFocus();
                input.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        name.setText(input.getText());
                        input.setVisible(false);
                        name.setVisible(true);
                        canvas.model.setName(component, name.getText(), false);
                    }
                    
                });
                event.consume();
            }
        });   
        menu.getItems().add(item);
        
        menu.getItems().add(new SeparatorMenuItem());
        item = new MenuItem("Delete");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                canvas.remove(CanvasIcon.this);
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
                    event.consume();
                }
            });
        }
        else {
            item = new MenuItem("Show Nodes");
            item.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    node_grid.setVisible(true);
                    event.consume();
                }
            });
        }
        menu.getItems().add(item);

        menu.getItems().add(new SeparatorMenuItem());
        item = new Menu("Set Attributes");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                icon.setRotate(icon.getRotate()+90);
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        menu.getItems().add(new SeparatorMenuItem());
        item = new MenuItem("Rotate CW");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                icon.setRotate(icon.getRotate()+90);
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        item = new MenuItem("Rotate CCW");
        item.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                icon.setRotate(icon.getRotate()-90);
                event.consume();
            }
        });
        menu.getItems().add(item);
        
        
    }
}
