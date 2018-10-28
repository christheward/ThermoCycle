/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.io.Serializable;
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
public class ToolboxIconController extends AnchorPane implements Serializable {
    
    // FXML variables
    @FXML protected AnchorPane base;
    @FXML protected AnchorPane icon;
    @FXML protected GridPane node_grid;
    @FXML protected Label name;
    @FXML protected TextField input;
    
    
    // Variables
    protected IconType iType;
    
    /**
     * Constructor
     */
    public ToolboxIconController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ToolboxIcon.fxml"));
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
    }
    
    /**
     * Gets the icon type.
     * @return Returns the IconType.
     */
    protected IconType getType() {
        return iType;
    }
    
    /**
     * Relocates the toolbox icon to the specified point in scene co-ordinates.
     * @param p The point to relocate to in the scene co-ordinates.
     */
    protected final void relocateToPoint (Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);
        relocate((int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)), (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2)));
    }
    
    /**
     * Sets the icon types
     * @param iType The icon type to set.
     */
    protected final void setType(IconType iType) {
        this.iType = iType;
        icon.getStyleClass().clear();
        icon.getStyleClass().add(iType.css);
        icon.getStyleClass().add("icon");
        icon.getStyleClass().add("icon-toolbox");
        name.setText(iType.name);
    }
}
