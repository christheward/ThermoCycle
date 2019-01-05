/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.DragContainerController;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public class ToolboxController extends StackPane{
    
    // FXML variables
    @FXML private VBox contents;
    @FXML protected ImageView pin;
    
    // GUI variables
    private final CanvasController canvas;
    
    /**
     * Constructor
     * @param canvas
     */
    public ToolboxController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Toolbox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Populate toolbox with componet icons
        for (ComponentIcon componentType : ComponentIcon.values()) {
            if (componentType.type != null) {
                if (Component.class.isAssignableFrom(componentType.type)) {
                    ToolboxComponentController icon = new ToolboxComponentController(); 
                    addDragDetection(icon);
                    icon.setType(componentType);
                    contents.getChildren().add(icon);
                }
            }
        }
        
    }
    
    /**
     * Add drag detection handlers to an icon
     * @param icon The toolbox icon to add drag detection handlers to.
     */
    private void addDragDetection(ToolboxComponentController icon) {
        
        icon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                
                // Get the source object
                ToolboxComponentController source = (ToolboxComponentController)event.getSource();
                
                // Set drag handlers for the uunderlying canvas
                canvas.setOnDragOver(canvas.iconDragOverCanvas);
                canvas.setOnDragDropped(canvas.iconDragDroppedCanvas);
                
                // Prepare the transparent drag icon on the parent canvas
                canvas.dragIcon.setType(source.getType());
                canvas.dragIcon.relocateToPointInScene(new Point2D (event.getSceneX(), event.getSceneY()));
                
                // Put data in clipboard to identify icon type
                ClipboardContent content = new ClipboardContent();
                DragContainerController container = new DragContainerController();
                container.addData("type", canvas.dragIcon.getType().toString());
                content.put(DragContainerController.AddNode, container);
                
                // Start the drag operation
                canvas.dragIcon.startDragAndDrop(TransferMode.ANY).setContent(content);
                canvas.dragIcon.setVisible(true);
                canvas.dragIcon.setMouseTransparent(true);
                
                // Consume event
                event.consume();
                
            }
        });
    }
    
    protected void lock() {
        pin.setRotate(-45);
    }
    
    protected void unlock() {
        pin.setRotate(45);
    }
    
}
