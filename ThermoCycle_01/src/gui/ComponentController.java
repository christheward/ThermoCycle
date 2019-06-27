/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ListIterator;
import java.util.stream.Collectors;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import static javafx.scene.input.MouseButton.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public class ComponentController extends AnchorPane {
    
    // FXML variables
    @FXML protected AnchorPane base;
    @FXML protected AnchorPane icon;
    @FXML protected GridPane node_grid;
    @FXML protected Label name;
    private ContextMenu menu;
    private Tooltip tip;
    
    // Set master
    protected final MasterSceneController master;
    
    // Properties
    protected ObjectProperty<Point2D> centerInLocal;
    private ObjectProperty<Point2D> centerInParent;
    
    // Model variables
    protected Component component;
    
    // Component type
    private ComponentIcon iType;
    
    
    /**
     * Constructor
     */
    public ComponentController(MasterSceneController master, boolean modelComponent) {
        
        // Set master
        this.master = master;
        
        // Create properties
        centerInLocal = new SimpleObjectProperty();
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Component.fxml"));
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        if (modelComponent) {
            // Model component handlers
            buildDragHandlersForCanvas();
            buildClickHandlersForCanvas();
        }
        else {
            // Toolbox component handlers
            buildDragHandlersForToolbox();
        }
        
    }
    
    /**
     * Initializer
     */
    @FXML
    private void initialize() {
        // Create bindings
        centerInLocal.bind(new ObjectBinding<Point2D>() {
            {
                bind(ComponentController.this.widthProperty(), ComponentController.this.heightProperty());
            }
            @Override
            protected Point2D computeValue() {
                return new Point2D(icon.widthProperty().getValue()/2.0, icon.heightProperty().getValue()/2.0);
            }
        });
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
     */
    protected void createComponent() {
        //Create component based on the icon type.
        switch (iType) {
            case COMBUSTOR:
                component = master.getModel().createCombustor(iType.name);
                break;
            case COMPRESSOR:
                component = master.getModel().createCompressor(iType.name);
                break;
            case HEAT_EXCHANGER:
                component = master.getModel().createHeatExchanger(iType.name);
                break;
            case HEAT_SINK:
                component = master.getModel().createHeatSink(iType.name);
                break;
            case TURBINE:
                component = master.getModel().createTurbine(iType.name);
                break;
        }
        
        // Adds nodes to the component
        ListIterator<thermocycle.FlowNode> lif = component.flowNodes.values().stream().collect(Collectors.toList()).listIterator();
        while (lif.hasNext()) {
            int idx = lif.nextIndex();
            thermocycle.FlowNode fn = lif.next();
            node_grid.add(new NodeController(master, ComponentController.this, fn), iType.flownodes[idx][0], iType.flownodes[idx][1]);
        }
        ListIterator<thermocycle.WorkNode> liw = component.workNodes.values().stream().collect(Collectors.toList()).listIterator();
        while (liw.hasNext()) {
            int idx = liw.nextIndex();
            thermocycle.WorkNode wn = liw.next();
            node_grid.add(new NodeController(master, ComponentController.this, wn), iType.worknodes[idx][0], iType.worknodes[idx][1]);
        }
        ListIterator<thermocycle.HeatNode> lih = component.heatNodes.values().stream().collect(Collectors.toList()).listIterator();
        while (lih.hasNext()) {
            int idx = lih.nextIndex();
            thermocycle.HeatNode hn = lih.next();
            node_grid.add(new NodeController(master, ComponentController.this, hn), iType.heatnodes[idx][0], iType.heatnodes[idx][1]);
        }
    }
    
    /**
     * Sets the icon type.
     * @param iType The icon type to set.
     */
    protected final void setType(ComponentIcon iType) {
        this.iType = iType;
        icon.getStyleClass().clear();
        icon.getStyleClass().add(iType.css);
        icon.getStyleClass().add("icon");
        icon.getStyleClass().add("icon-toolbox");
        name.setText(iType.name);
    }
    
    /**
     * Relocates the toolbox icon to the specified point in scene co-ordinates.
     * @param scenePoint the point to relocate to in the scene co-ordinates.
     */
    protected final void relocateToPointInScene(Point2D scenePoint) {
        Point2D parentPoint = getParent().sceneToLocal(scenePoint);
        this.relocate((int) (parentPoint.getX() - (centerInLocal.getValue().getX())), (int) (parentPoint.getY() - centerInLocal.getValue().getY()));
    }
    
    /**
     * Builds the drag handlers for this object.
     */
    private final void buildDragHandlersForToolbox() {
        
       this.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.CREATE_COMPONENT,iType);
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Not sure if these are needed
                startFullDrag();
                //setMouseTransparent(true);
                
                // Prepare the canvas component
                master.canvas.dragIcon.setType(iType);
                master.canvas.dragIcon.relocateToPointInScene(new Point2D (event.getSceneX(), event.getSceneY()));
                master.canvas.dragIcon.setVisible(true);
                
                // Consume event
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                // Hide canvas drag icon
                master.canvas.dragIcon.setVisible(false);
                
                // Consume event
                event.consume();
                
            }
        });
        
    }
    
    private final void buildDragHandlersForCanvas() {
        
       this.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Create clipboard and add data to it so that the icon type can be idnetified when the object is dropped.
                ClipboardContent content = new ClipboardContent();
                content.put(DragContainerController.MOVE_COMPONENT,component);
                
                // Start drag and drop operation and add data to dragboard
                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
                dragboard.setContent(content);
                
                // Not sure if these are needed
                startFullDrag();
                //setMouseTransparent(true);
                
                // Consume event to make make sure canvas drag detected event isn't fired.
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                //setMouseTransparent(false);
            }
        });
        
    }
    
    private void buildClickHandlersForCanvas() {
        
        ComponentController.this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 1) {
                        master.setFocus(ComponentController.this);
                    }
                    
                }
            }
        });
        
        ComponentController.this.name.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               // Check for double click on label
               if (event.getButton().equals(PRIMARY)) {
                   if (event.getClickCount() == 2) {
                       ComponentController.this.name.focusedProperty().addListener(new ChangeListener<Boolean>() {
                           @Override
                           public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                               if (newValue == false) {
                                   ComponentController.this.name.setVisible(true);
                               }
                           }
                       });
                       ComponentController.this.name.setVisible(false);
                       ComponentController.this.name.requestFocus();
                   }
               }
           }
            
        });
        
    }
    
}
