/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public class CanvasComponentController extends ToolboxComponentController{
    
    // FXML variables
    private final MasterSceneController master;
    private ContextMenu menu;
    private Tooltip tip;
    
    // Event handlers
    private EventHandler  iconDragOverCanvas;
    private EventHandler  iconDragDroppedCanvas;
    
    // Model variables
    protected Component component;
    
    /**
     * Constructor
     * @param iType The icon type.
     * @param model The thermocycle model.
     * @throws Exception 
     */
    public CanvasComponentController(MasterSceneController master, ComponentIcon iType) throws Exception {
        super();
        
        // Set master
        this.master = master;
        
        // set icon type
        setType(iType);
        icon.getStyleClass().add("icon-component");
        
        // create model component
        createComponent();
        
        // adds nodes
        addNodes();
        
        // build handlers
        buildDragHandlers();
        buildClickHandlers();
    }
    public CanvasComponentController(MasterSceneController master, thermocycle.Component component) throws Exception {
        super();
        
        // set master
        this.master = master;
        
        // set compoennt
        this.component = component;
        
        // set icon type
        List<ComponentIcon> items = Arrays.asList(ComponentIcon.values());
        setType(items.stream().filter(i -> i.type.equals(component.getClass())).findFirst().orElse(ComponentIcon.UNKNOWN));
        
        // Adds nodes
        addNodes();
        
        // build handlers
        buildDragHandlers();
        buildClickHandlers();
    }
    
    /**
     * Initializer
     */
    @FXML private void initialize() {
    }
    
    /**
     * Builds the tool tip for this component
     */
    protected void buildTooltip() {
        tip = new Tooltip();
        Tooltip.install(base, tip);
        tip.setOnShowing(new EventHandler() {
            @Override
            public void handle(Event event) {
                tip.setText(component.toString());
            }
        });
    }
    
    /**
     * Creates the thermodynamic cycle component. Must only be called after icon type has been set with setType() in the superclass.
     * @throws Exception 
     */
    private final void createComponent() throws Exception {
        switch (this.getType()) {
            case COMBUSTOR:
                component = master.getModel().createCombustor(getType().name);
                break;
            case COMPRESSOR:
                component = master.getModel().createCompressor(getType().name);
                break;
            case HEAT_EXCHANGER:
                component = master.getModel().createHeatExchanger(getType().name);
                break;
            case HEAT_SINK:
                component = master.getModel().createHeatSink(getType().name);
                break;
            case TURBINE:
                component = master.getModel().createTurbine(getType().name);
                break;
            default:
                throw new Exception("Unknown component type!");
        }
    }
    
    /**
     * Adds the input/output work/heat/and flow nodes to the CanvasComponentController 
     * @throws Exception 
     */
    private final void addNodes() throws Exception {
        ListIterator<thermocycle.FlowNode> lif = component.flowNodes.listIterator();
        while (lif.hasNext()) {
            int idx = lif.nextIndex();
            thermocycle.FlowNode fn = lif.next();
            node_grid.add(new CanvasNodeController(master, CanvasComponentController.this, fn), getType().flownodes[idx][0], getType().flownodes[idx][1]);
        }
        ListIterator<thermocycle.WorkNode> liw = component.workNodes.listIterator();
        while (liw.hasNext()) {
            int idx = liw.nextIndex();
            thermocycle.WorkNode wn = liw.next();
            node_grid.add(new CanvasNodeController(master, CanvasComponentController.this, wn), getType().worknodes[idx][0], getType().worknodes[idx][1]);
        }
        ListIterator<thermocycle.HeatNode> lih = component.heatNodes.listIterator();
        while (lih.hasNext()) {
            int idx = lih.nextIndex();
            thermocycle.HeatNode hn = lih.next();
            node_grid.add(new CanvasNodeController(master, CanvasComponentController.this, hn), getType().heatnodes[idx][0], getType().heatnodes[idx][1]);
        }
    }
    
    /**
     * Builds the CanvasComponentController drag handlers
     */
    private void buildDragHandlers() {
        
        //drag detection for node dragging
        icon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Apply the drag handlers to teh canvas
                master.canvas.setOnDragOver (iconDragOverCanvas);
                master.canvas.setOnDragDropped (iconDragDroppedCanvas);
                
                // Put icon type in clipboard
                ClipboardContent content = new ClipboardContent();
                DragContainerController container = new DragContainerController();
                container.addData("type", getType().toString());
                content.put(DragContainerController.MoveComponent, container);
                
                // Start drag operations
                relocateToPointInScene(new Point2D(event.getSceneX(), event.getSceneY()));
                startDragAndDrop(TransferMode.ANY).setContent(content);
                
                // Consume event
                event.consume();                    
            }
        });
        
        //dragover to handle node dragging in the canvas
        iconDragOverCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                //event.acceptTransferModes(TransferMode.ANY);                
                relocateToPointInScene(new Point2D( event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        
        //dragdrop for node dragging
        iconDragDroppedCanvas = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {
                master.canvas.setOnDragOver(null);
                master.canvas.setOnDragDropped(null);
                event.setDropCompleted(true);
                event.consume();
            }
        };
        
    }
    
    /**
     * Builds the mouse click handlers for CanvasComponentController
     */
    private void buildClickHandlers() {
        
        icon.setOnMouseClicked(new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    buildContextMenu();
                    menu.show(icon, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton().equals(MouseButton.PRIMARY)) {
                    master.setFocus(CanvasComponentController.this);
                }
                event.consume();
            }
        });
        
    }
    
    /**
     * Builds context menus for the CanvasComponentController
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
                        master.getModel().setName(component, name.getText());
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
                master.canvas.remove(CanvasComponentController.this);
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
