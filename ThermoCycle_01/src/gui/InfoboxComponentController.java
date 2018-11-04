/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import thermocycle.Attributes.Attribute;

/**
 *
 * @author Chris
 */
public class InfoboxComponentController extends AnchorPane {

    // FXML Variables
    @FXML private TableView<AttributeView> table;
    @FXML private TableColumn<AttributeView, Object> attributeColumn;
    @FXML private TableColumn<AttributeView, Double> valueColumn;
    @FXML private TableColumn<AttributeView, String> unitsColumn;
    @FXML private ComboBox<Attribute> selectAttribute;
    @FXML private TextField attributeInput;
    @FXML private Label attributeUnits;
    @FXML private Button buttonClearAttribute;
    private final CanvasController canvas;
    
    // Model variables
    protected thermocycle.Component component;
    
    // Table data
    private final ObservableList<AttributeView> attributeTable;
    private final ObservableList<Attribute> attributeList;
    
    /**
     * Constructor
     * 
     */
    public InfoboxComponentController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
        // Initialise form data
        attributeList = FXCollections.observableList(new ArrayList<>());
        attributeTable = FXCollections.observableList(new ArrayList<>());
        attributeColumn.setCellValueFactory(new PropertyValueFactory<>("attribute"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        selectAttribute.setItems(attributeList);
        table.setItems(attributeTable);
        
        // Build event handlers
        buildClickHandlers();
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
    
    public void showDetails(thermocycle.Component component) {
        
        // Set component
        this.component = component;
        
        // Clear table
        attributeTable.clear();
        
        // Re-populate table
        canvas.model.getAttributes(component).forEach(a -> {
            attributeTable.add(new AttributeView(a, canvas.model.getAttribute(component, a)));
        });
        
        // Get available Attributes
        attributeList.addAll(canvas.model.getAttributes(component));
        
    }
    
    /**
     * Populates the heat flux values.
     */
    private void populate() {
        
        // Update text fields
        updateTextFields();
        
        // Re-populate table
        attributeTable.clear();
        canvas.model.getAttributes(component).forEach(a -> {
            attributeTable.add(new AttributeView(a, canvas.model.getAttribute(component, a)));
        });
        
    }
    
    private void buildClickHandlers() {
        attributeInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!selectAttribute.getSelectionModel().isEmpty()) {
                    canvas.model.setAttribute(component, selectAttribute.getSelectionModel().getSelectedItem(), Double.valueOf(attributeInput.getText()));
                    populate();
                }
                event.consume();
            }
        });
        
        buttonClearAttribute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!selectAttribute.getSelectionModel().isEmpty()) {
                    canvas.model.clearAttribute(component, selectAttribute.getSelectionModel().getSelectedItem());
                    populate();
                }
                event.consume();
            }
        });
        
        selectAttribute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                attributeInput.setText(CanvasController.displayOptionalDouble(canvas.model.getAttribute(component, selectAttribute.getSelectionModel().getSelectedItem())));
                attributeUnits.setText(selectAttribute.getSelectionModel().getSelectedItem().units);
                populate();
                event.clone();
            }    
        });
    }
    
    private void updateTextFields() {
        if (!selectAttribute.getSelectionModel().isEmpty()) {
            attributeInput.setText(CanvasController.displayOptionalDouble(canvas.model.getAttribute(component, selectAttribute.getSelectionModel().getSelectedItem())));
        }
        else {
            attributeInput.setText("");
        }
    }
}
