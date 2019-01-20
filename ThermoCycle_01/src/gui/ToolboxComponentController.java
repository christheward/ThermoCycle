/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    @FXML protected TextField input;
    private Point2D centerInLocal;
    
    // Properties
    private DoubleProperty xCenterInLocal;
    private DoubleProperty yCenterInLocal;
    private DoubleProperty xCenterInParent;
    private DoubleProperty yCenterInParent;
    
    // Model variables
    private ComponentIcon iType;
    
    /**
     * Constructor
     */
    public ToolboxComponentController() {
        
        // Create properties
        xCenterInLocal = new SimpleDoubleProperty();
        yCenterInLocal = new SimpleDoubleProperty();
        xCenterInParent = new SimpleDoubleProperty();
        yCenterInParent = new SimpleDoubleProperty();
        
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
    
    /**
     * Initializer
     */
    @FXML private void initialize() {
        
        // Create bindings
        xCenterInLocal.bind(this.widthProperty().divide(2.0));
        yCenterInLocal.bind(this.heightProperty().divide(2.0));
        
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
        return localToParent(new Point2D ((int)((getBoundsInLocal().getMinX() + getBoundsInLocal().getMaxX()) / 2), (int)((getBoundsInLocal().getMinX() + getBoundsInLocal().getMaxX()) / 2)));
    }
    
}
