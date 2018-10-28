/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import thermocycle.Attributes.Attribute;
import thermocycle.FlowNode;
import thermocycle.HeatNode;
import thermocycle.Properties;
import thermocycle.WorkNode;

/**
 *
 * @author Chris
 */
public class InfoboxController extends AnchorPane{
    
    // FXML variables
    @FXML private VBox contents;
    private final CanvasController canvas;
    
    // Comon objects
    final ComboBox fluidsComboBox;
    
    // Event handlers
    
    /**
     * Constructor
     * @param canvas The parent canvas 
     */
    public InfoboxController(CanvasController canvas) {
        
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
        
        fluidsComboBox = new ComboBox(canvas.model.fluidsReadOnly);
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
    
    public void showDetails(Node node) {
        if (node instanceof CanvasController) {
            contents.getChildren().clear();
            DoubleProperty ambient_pressure = new SimpleDoubleProperty(canvas.model.getAmbient(Properties.Property.PRESSURE));
            DoubleProperty ambient_temperature = new SimpleDoubleProperty(canvas.model.getAmbient(Properties.Property.TEMPERATURE));
            
            contents.getChildren().add(new ListView<thermocycle.Component>(canvas.model.componentsReadOnly));
            contents.getChildren().add(new ListView<thermocycle.Fluid>(canvas.model.fluidsReadOnly));
            ListView pathsView = new ListView<Set<thermocycle.FlowNode>>(canvas.model.pathsReadOnly);
                        
            
            contents.getChildren().add(pathsView);
            
        }
        else if (node instanceof CanvasNodeController) {
            thermocycle.Node n = (((CanvasNodeController) node).node);
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
                contents.getChildren().add(new Label(canvas.model.getMass((FlowNode)n).toString()));
                
                final ObservableList<StateTable> tableData;
                tableData = FXCollections.observableList(new ArrayList<>());
                canvas.model.getAllowablePrroperties((FlowNode)n).forEach(p -> {
                    // what happens if the properrty hasn't been set yet (i.e. doesn't exist).
                    tableData.add(new StateTable(p, canvas.model.getState((FlowNode) n, p)));
                });
                
                TableView table = new TableView();
                table.setEditable(true);
                TableColumn propertyCol = new TableColumn("Property");
                TableColumn valueCol = new TableColumn("Value");
                TableColumn unitsCol = new TableColumn("Units");
                table.getColumns().addAll(propertyCol,valueCol,unitsCol);
                
                propertyCol.setCellValueFactory(new PropertyValueFactory<StateTable,String>("property"));
                valueCol.setCellValueFactory(new PropertyValueFactory<StateTable,String>("value"));
                
                
                contents.getChildren().add(table);
            }
        }
        else if (node instanceof CanvasIconController) {
            thermocycle.Component component = ((CanvasIconController) node).component;
            contents.getChildren().clear();
            contents.getChildren().add(new Label(component.getClass().getSimpleName()));
            contents.getChildren().add(new Label("Attributes"));
            canvas.model.getAttributes(component).stream().forEach(a -> {
                contents.getChildren().add(new Label(a.name()));
                contents.getChildren().add(new Label(canvas.model.getAttribute(component, a).toString()));
                contents.getChildren().add(new Label(a.units));
            });
            
            TableView table = new TableView();
            table.setEditable(true);
            TableColumn attributeCol = new TableColumn("Attribute");
            TableColumn valueCol = new TableColumn("Value");
            TableColumn unitsCol = new TableColumn("Units");
            table.getColumns().addAll(attributeCol,valueCol,unitsCol);
            
            final ObservableList<AttributeTable> tableData;
            tableData = FXCollections.observableList(new ArrayList<>());
            canvas.model.getAttributes(component).forEach(a -> {
                tableData.add(new AttributeTable(a, canvas.model.getAttribute(component, a)));
            });
            
            attributeCol.setCellValueFactory(new PropertyValueFactory<AttributeTable,String>("attribute"));
            unitsCol.setCellValueFactory(new PropertyValueFactory<AttributeTable,String>("attrinute"));
            valueCol.setCellValueFactory(new PropertyValueFactory<AttributeTable,String>("value"));
            valueCol.setOnEditCommit(new EventHandler<CellEditEvent<Attribute,String>>() {
                @Override
                public void handle(CellEditEvent<Attribute, String> event) {
                    canvas.model.setAttribute(component, event.getRowValue(), OptionalDouble.of(Double.valueOf(event.getNewValue())));
                    System.out.println("Edit cell");
                    
                    //event.getTableView().getItems().get(event.getTablePosition().getRow())
                    event.consume();
                }
                
            });
            
            table.setEditable(true);
            table.setItems(tableData);
            contents.getChildren().add(table);
        }
        else if (node instanceof CanvasPathController) {
            contents.getChildren().clear();
            thermocycle.Node start = ((CanvasPathController)node).start.node;
            if (start instanceof FlowNode) {
                //((thermocycle.FlowNode)start);
                contents.getChildren().add(new Label("Fluid: "));
                final ComboBox comboBox = new ComboBox(canvas.model.fluidsReadOnly);
                comboBox.getSelectionModel().select(canvas.model.getFluid((FlowNode)start));
                //comboBox.set
                contents.getChildren().add(comboBox);
                comboBox.valueProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        canvas.model.setFluid((FlowNode)start, (thermocycle.Fluid)newValue);
                        System.out.println(newValue.toString());
                        System.out.println("fluid set");
                    }
                    
                });
            }
            else if (start instanceof WorkNode) {
                
            }
            else if (start instanceof HeatNode) {
                
            }
        }
    }
    
}