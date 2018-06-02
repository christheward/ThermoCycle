/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.DragContainer;
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

/**
 *
 * @author Chris
 */
public class Toolbox extends StackPane{
    
    // FXML variables
    @FXML private VBox contents;
    @FXML protected ImageView pin;
    private final Canvas canvas;
    
    // Event handlers
    
    /**
     * Constructor
     */
    public Toolbox(Canvas canvas) {
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Toolbox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Populate toolbox with componet icons
        for (IconType iconType : IconType.values()) {
            if (thermocycle.Component.class.isAssignableFrom(iconType.type)) {
                ToolboxIcon icon = new ToolboxIcon(); 
                addDragDetection(icon);
                icon.setType(iconType);
                contents.getChildren().add(icon);
            }
        }
        
    }
    
    /**
     * Add drag detection handlers to an icon
     * @param icon The toolbox icon to add drag detection handlers to.
     */
    private void addDragDetection(ToolboxIcon icon) {
        
        icon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Toolbox: Drag detection");
                
                // Get the source object
                ToolboxIcon source = (ToolboxIcon)event.getSource();
                
                // Set drag handlers for the uunderlying canvas
                canvas.setOnDragOver(canvas.iconDragOverCanvas);
                canvas.setOnDragDropped(canvas.iconDragDroppedCanvas);
                
                // Prepare the transparent drag icon on the parent canvas
                canvas.dragIcon.setType(source.getType());
                canvas.dragIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
                
                // Put data in clipboard to identify icon type
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData("type", canvas.dragIcon.getType().toString());
                content.put(DragContainer.AddNode, container);
                
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
