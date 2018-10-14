/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.swing.table.DefaultTableModel;
import thermocycle.FlowNode;
import thermocycle.HeatNode;
import thermocycle.Properties;
import thermocycle.WorkNode;

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
            DoubleProperty ambient_pressure = new SimpleDoubleProperty(canvas.model.getAmbient(Properties.Property.PRESSURE));
            DoubleProperty ambient_temperature = new SimpleDoubleProperty(canvas.model.getAmbient(Properties.Property.TEMPERATURE));
            
            TableView table = new TableView();
            table.setEditable(true);
            TableColumn propertyCol = new TableColumn("Property");
            propertyCol.setMinWidth(100);
            propertyCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        
            TableColumn valueCol = new TableColumn("Value");
            TableColumn unitsCol = new TableColumn("Units");
            table.getColumns().addAll(propertyCol,valueCol,unitsCol);
            
            
            contents.getChildren().add(new ListView<thermocycle.Component>(canvas.model.componentsReadOnly));
            contents.getChildren().add(new ListView<thermocycle.Fluid>(canvas.model.fluidsReadOnly));
            ListView pathsView = new ListView<Set<thermocycle.FlowNode>>(canvas.model.pathsReadOnly);
            contents.getChildren().add(pathsView);
        }
        else if (node instanceof CanvasNode) {
            thermocycle.Node n = (((CanvasNode) node).node);
            contents.getChildren().clear();
            contents.getChildren().add(new Label(n.getClass().getSimpleName()));
            contents.getChildren().add(new Label(n.port.name()));
            if (n instanceof HeatNode) {
                contents.getChildren().add(new Label(((HeatNode) n).toString()));
                TextField input = new TextField(((HeatNode) n).toString());
                input.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if(event.getCode().equals(KeyCode.ENTER)) {
                             // check numeric
                             try {
                                double value = Double.parseDouble(input.getText());
                                canvas.model.setHeat((HeatNode) n, value);
                             }
                             catch (NumberFormatException e) {
                                 // Send message that it's not numeric
                             }
                             event.consume();
                        }
                    }
                });
            }
            else if (n instanceof WorkNode) {
                contents.getChildren().add(new Label(((WorkNode) n).toString()));
                TextField input = new TextField(((WorkNode) n).toString());
                input.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if(event.getCode().equals(KeyCode.ENTER)) {
                             // check numeric
                             try {
                                double value = Double.parseDouble(input.getText());
                                canvas.model.setWork((WorkNode) n, value);
                             }
                             catch (NumberFormatException e) {
                                 // Send message that it's not numeric
                             }
                             event.consume();
                        }
                    }
                });
            }
            else if (n instanceof FlowNode) {
                //canvas.model.getMass((FlowNode)n);
                contents.getChildren().add(new Label(canvas.model.getMass((FlowNode)n).toString()));
            }
        }
        else if(node instanceof CanvasIcon) {
            thermocycle.Component component = ((CanvasIcon) node).component;
            contents.getChildren().clear();
            contents.getChildren().add(new Label(component.getClass().getSimpleName()));
            contents.getChildren().add(new Label("Attributes"));
            canvas.model.getAttributes(component).stream().forEach(a -> {
                contents.getChildren().add(new Label(a.name()));
                contents.getChildren().add(new Label(canvas.model.getAttribute(component, a).toString()));
                contents.getChildren().add(new Label(a.units));
            });
            
            TableView table = new TableView();
            contents.getChildren().add(table);
            TableColumn attributeName = new TableColumn("Attribute");
            TableColumn attributeValue = new TableColumn("Value");
            table.getColumns().addAll(attributeName, attributeValue);
            
            component.attributes.keySet().forEach(a -> {
                //stableModel.addRow([a.fullName, a.units]);
            });

        }
    }
    
}
