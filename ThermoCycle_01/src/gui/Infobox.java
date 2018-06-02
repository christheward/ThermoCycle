/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Chris
 */
public class Infobox extends AnchorPane{
    
    // FXML variables
    @FXML private VBox contents;
    private final Canvas canvas;
    
    // Event handlers
    
    /**
     * Constructor
     * @param canvas The parent canvas 
     */
    public Infobox(Canvas canvas) {
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Infobox.fxml"));
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
     * Initialiser
     */
    public void initialize() {
    }
    
    public void showDetails(Node node) {
        if (node instanceof Canvas) {
            contents.getChildren().clear();
            contents.getChildren().add(new ListView<thermocycle.Component>(canvas.model.componentsReadOnly));
            contents.getChildren().add(new ListView<thermocycle.Fluid>(canvas.model.fluidsReadOnly));
            contents.getChildren().add(new ListView<Set<thermocycle.FlowNode>>(canvas.model.pathsReadOnly));
        }
        else if (node instanceof CanvasNode) {
            thermocycle.Node n = (((CanvasNode) node).node);
            contents.getChildren().clear();
            contents.getChildren().add(new Label(n.getClass().getSimpleName()));
            contents.getChildren().add(new Label(n.port.name()));
            if (n instanceof thermocycle.HeatNode) {
            }
            else if (n instanceof thermocycle.WorkNode) {  
            }
            else if (n instanceof thermocycle.FlowNode) {
                
            }
        }
        else if(node instanceof CanvasIcon) {
            thermocycle.Component component = ((CanvasIcon) node).component;
            contents.getChildren().clear();
            contents.getChildren().add(new Label(component.getClass().getSimpleName()));
            canvas.model.getAttributes(component).stream().forEach(a -> {
                contents.getChildren().add(new Label(a.name()));
            });
            TableView table = new TableView<>();
            contents.getChildren().add(table);
            //TableItem item = new TableItem();
            

        }
    }
    
}
