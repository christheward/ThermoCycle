/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import javafx.beans.binding.ObjectBinding;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public class CanvasComponentController extends ToolboxComponentController {
    
    // FXML variables
    private ContextMenu menu;
    private Tooltip tip;
    
    // Model variables
    protected Component component;
    
    /**
     * Constructor
     * @param master the master scene controller
     * @param iType the component icon type
     * @throws Exception 
     */
    public CanvasComponentController(MasterSceneController master, ComponentIcon iType) throws Exception {
        
        // Construct parent class
        super(master);
        
        // set icon type
        setType(iType);
        icon.getStyleClass().add("icon-component");  // OVerrides some styles for the toolbox components.
        
        // Load FXML
        System.out.println("Loading " + this.getClass());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ToolboxComponent.fxml"));
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        System.out.println("Loaded");
        
        // create model component
        createComponent();
        
        // adds nodes
        addNodes();
        
        // Bind name visibility
        this.name.visibleProperty().bind(master.nameVisibility);
    }
    
    /**
     * Constructor
     * @param master the master canvas controller
     * @param component the thermocycle component
     * @throws Exception 
     */
    public CanvasComponentController(MasterSceneController master, thermocycle.Component component) throws Exception {
        
        // construct parent
        super(master);
        
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
        
        // Bind name visibility
        this.name.visibleProperty().bind(master.nameVisibility);
        
    }
    
    /**
     * Initializer
     */
    @FXML
    private void initialize() {
        
        // Create bindings
        centerInLocal.bind(new ObjectBinding<Point2D>() {
            {
                bind(CanvasComponentController.this.widthProperty(), CanvasComponentController.this.heightProperty());
            }
            @Override
            protected Point2D computeValue() {
                System.out.println("CenterInLocal binding trigger for " + CanvasComponentController.this.name);
                System.out.println("Height = " + icon.heightProperty().getValue());
                System.out.println("Width = " + icon.widthProperty().getValue());
                System.out.println("Hover = " + CanvasComponentController.this.hoverProperty().getValue());
                
                return new Point2D(icon.getWidth()/2.0, icon.getHeight()/2.0);
            }
        });
        
        // build handlers
        buildDragHandlers();
        buildClickHandlers();
        
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
    private void createComponent() {
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
                // DO somethign with error;
        }
    }
    
    /**
     * Adds the input/output work/heat/and flow nodes to the CanvasComponentController 
     * @throws Exception 
     */
    private final void addNodes() {
        try {
            ListIterator<thermocycle.FlowNode> lif = component.flowNodes.values().stream().collect(Collectors.toList()).listIterator();
            while (lif.hasNext()) {
                int idx = lif.nextIndex();
                thermocycle.FlowNode fn = lif.next();
                node_grid.add(new CanvasNodeController(master, CanvasComponentController.this, fn), getType().flownodes[idx][0], getType().flownodes[idx][1]);
            }
            ListIterator<thermocycle.WorkNode> liw = component.workNodes.values().stream().collect(Collectors.toList()).listIterator();
            while (liw.hasNext()) {
                int idx = liw.nextIndex();
                thermocycle.WorkNode wn = liw.next();
                node_grid.add(new CanvasNodeController(master, CanvasComponentController.this, wn), getType().worknodes[idx][0], getType().worknodes[idx][1]);
            }
            ListIterator<thermocycle.HeatNode> lih = component.heatNodes.values().stream().collect(Collectors.toList()).listIterator();
            while (lih.hasNext()) {
                int idx = lih.nextIndex();
                thermocycle.HeatNode hn = lih.next();
                node_grid.add(new CanvasNodeController(master, CanvasComponentController.this, hn), getType().heatnodes[idx][0], getType().heatnodes[idx][1]);
            }
        }
        catch (Exception ex) {
            // Do somethign with error.
        }
    }
    
    /**
     * Builds the CanvasComponentController drag handlers
     */
    private void buildDragHandlers() {
        
        this.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.MOVE_COMPONENT,CanvasComponentController.this.component);
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Not sure if these are needed
                startFullDrag();
                setMouseTransparent(true);
                
                System.out.println("Added to container");
                System.out.println(content.get(DragContainerController.MOVE_COMPONENT));
                
                // Consume event to make make sure canvas drag detected event isn't fired.
                event.consume();
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
        
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
        MenuItem item = new MenuItem("Delete");
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
