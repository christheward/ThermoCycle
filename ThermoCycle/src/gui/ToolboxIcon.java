/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Chris
 */
public class ToolboxIcon extends AnchorPane{
    
    // FXML variables
    @FXML protected AnchorPane base;
    @FXML protected AnchorPane icon;
    @FXML protected GridPane node_grid;
    @FXML protected Label label;
    
    // Variables
    protected IconType iType;
    
    // Constructor
    public ToolboxIcon() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ToolboxIcon.fxml"));
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        try { 
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @FXML private void initialize() {
    }
    
    public IconType getType() {return iType;}
    
    public void relocateToPoint (Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);
        relocate ((int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)), (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2)));
    }
    
    final public void setType(IconType iType) {
        this.iType = iType;
        icon.getStyleClass().clear();
        icon.getStyleClass().add("dragicon");
        icon.getStyleClass().add(iType.css);
        label.setText(iType.name);
    }
}
