/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Chris
 */
public class ToolboxComponentController extends AnchorPane {
    
    // FXML variables
    @FXML protected AnchorPane base;
    @FXML protected AnchorPane icon;
    @FXML protected GridPane node_grid;
    @FXML protected Label name;
    
    // Set master
    protected final MasterSceneController master;
    
    // Properties
    protected ObjectProperty<Point2D> centerInLocal;
    private ObjectProperty<Point2D> centerInParent;
    protected ObjectBinding<Point2D> cilBinding;
    
    // Model variables
    private ComponentIcon iType;
    
    /**
     * Constructor
     */
    public ToolboxComponentController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Create properties
        centerInLocal = new SimpleObjectProperty();
        
        // Only load the FXML here if this is the class being instantiated. Prevent sub-classes being initilised twice.
        if (this.getClass().equals(ToolboxComponentController.class)) {
            // Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ToolboxComponent.fxml"));
            fxmlLoader.setRoot(this); 
            fxmlLoader.setController(this);
            try {
                fxmlLoader.load();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
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
                bind(ToolboxComponentController.this.hoverProperty(), ToolboxComponentController.this.widthProperty(), ToolboxComponentController.this.heightProperty());
            }
            @Override
            protected Point2D computeValue() {
                System.out.println("CenterInLocal binding trigger for " + ToolboxComponentController.this.name);
                System.out.println("Height = " + icon.heightProperty().getValue());
                System.out.println("Width = " + icon.widthProperty().getValue());
                System.out.println("Hover = " + ToolboxComponentController.this.hoverProperty().getValue());
                
                return new Point2D(icon.getWidth()/2.0, icon.getHeight()/2.0);
            }
        });
        
        /**
        cilBinding = Bindings.createObjectBinding(() -> new Point2D(icon.getWidth()/2.0, icon.getHeight()/2.0), ToolboxComponentController.this.hoverProperty(), ToolboxComponentController.this.widthProperty(), ToolboxComponentController.this.heightProperty());
        centerInLocal.bind(cilBinding);
        centerInLocal.addListener(new ChangeListener<Point2D>() {
            @Override
            public void changed(ObservableValue<? extends Point2D> observable, Point2D oldValue, Point2D newValue) {
                System.out.print(ToolboxComponentController.this.hoverProperty());
                System.out.println("Value changed.");
                centerInLocal.getValue();
                cilBinding.getValue();
            }
        });
        */
        
        this.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("Hover change. New: " + ToolboxComponentController.this.hoverProperty().getValue());
            }
        });
        
        /**
        ToolboxComponentController.this.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.print("Hover status changed.");
            }
        });
        */
        // Build drag handlers
        buildDragHandlers();
        
    }
    
    /**
     * Gets the component type.
     * @return Returns the ComponentIcon.
     */
    protected ComponentIcon getType() {
        return iType;
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
    private final void buildDragHandlers() {
        
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
                
                // Consume event to make make sure canvas drag detected event isn't fired.
                event.consume();
                
            }
        });
        
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                master.canvas.dragIcon.setVisible(false);
            }
        });
        
    }
    
}
