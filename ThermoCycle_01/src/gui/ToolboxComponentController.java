/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import static gui.ThermoCycleClipboardContent.*;
import java.io.IOException;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
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
    private ObjectProperty<Point2D> centerInLocal;
    private ObjectProperty<Point2D> centerInParent;
    
    // Drag handlers
    protected EventHandler<DragEvent> componentDraggedOverCanvas;
    protected EventHandler<DragEvent> componentDroppedOnCanvas;
    protected EventHandler<DragEvent> componentDragDoneOnCanvas;
    
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
        centerInParent = new SimpleObjectProperty();
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ToolboxComponent.fxml"));
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Build drag handlers
        buildDragHandlers();
    }
    
    /**
     * Initializer
     */
    @FXML private void initialize() {
        
        // Create bindings
        centerInLocal.bind(new ObjectBinding<Point2D>() {
            {
                bind(ToolboxComponentController.this.widthProperty(), ToolboxComponentController.this.heightProperty());
            }
            @Override
            protected Point2D computeValue() {
                return new Point2D(icon.getWidth()/2.0, icon.getHeight()/2.0);
            }
        });
        
        centerInParent.bind(new ObjectBinding<Point2D>() {
            {
                bind(centerInLocal, ToolboxComponentController.this.localToParentTransformProperty());
            }
            @Override
            protected Point2D computeValue() {
                return ToolboxComponentController.this.localToParent(centerInLocal.getValue().getX(), centerInLocal.getValue().getY());
            }
        });
        
    }
    
    /**
     * Gets the component type.
     * @return Returns the ComponentIcon.
     */
    protected ComponentIcon getType() {
        return iType;
    }
    
    /**
     * Sets the icon types
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
     * @param p The point to relocate to in the scene co-ordinates.
     */
    protected final void relocateToPointInScene(Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);
        this.relocate((int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)), (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2)));
    }

    /**
     * Gets the centre of this component in the parents co-ordinate system.
     * @return The centre point of the component in the parents co-ordinate system.
     */
    protected final Point2D getCenterPointInParent() {
        return centerInParent.getValue();
    }
    
    private final void buildDragHandlers() {
        
       this.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Put data in clipboard to identify icon type when dropped on canvas
                ThermoCycleClipboardContent content = new ThermoCycleClipboardContent();
                content.putAction(OPERATION.CREATE);
                content.putComponentType(iType);
                
                // Start drag and drop operation
                startDragAndDrop(TransferMode.ANY).setContent(content);
                ToolboxComponentController.this.startFullDrag();
                
                // Note sure if these are needed
                startFullDrag();
                setMouseTransparent(true);
                
                // Prepare the canvas component
                master.canvas.dragIcon.setType(iType);
                master.canvas.dragIcon.relocateToPointInScene(new Point2D (event.getSceneX(), event.getSceneY()));
                master.canvas.dragIcon.setVisible(true);
                
                // Consume event to make make sure canvas drag detected isn't fired
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
