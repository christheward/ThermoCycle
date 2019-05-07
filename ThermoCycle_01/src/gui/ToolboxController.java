/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
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
    private final MasterSceneController master;
    
    /**
     * Constructor
     * @param canvas
     */
    public ToolboxController(MasterSceneController master) {

        // Set master
        this.master = master;
        
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
        
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Populate toolbox with componet icons
        for (ComponentIcon componentType : ComponentIcon.values()) {
            // Check component is not null (unknown component)
            if (componentType.type != null) {
                // Check type is a sub-class of component
                if (Component.class.isAssignableFrom(componentType.type)) {
                    ToolboxComponentController icon = new ToolboxComponentController(master); 
                    icon.setType(componentType);
                    contents.getChildren().add(icon);
                }
            }
        }
        
    }
    
}
