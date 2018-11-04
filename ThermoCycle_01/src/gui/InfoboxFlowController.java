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
import javafx.scene.layout.VBox;
import thermocycle.Fluid;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris
 */
public class InfoboxFlowController extends AnchorPane {

    // FXML Variables
    @FXML private TableView<PropertyView> table;
    @FXML private TableColumn<PropertyView, Object> propertyColumn;
    @FXML private TableColumn<PropertyView, Double> valueColumn;
    @FXML private TableColumn<PropertyView, String> unitsColumn;
    @FXML private ComboBox<Fluid> selectFluid;
    @FXML private ComboBox<Property> selectProperty;
    @FXML private TextField massInput;
    @FXML private TextField propertyInput;
    @FXML private Label propertyUnits;
    @FXML private Button buttonClearMass;
    @FXML private Button buttonClearProperty;
    @FXML private Button buttonClearState;
    @FXML private VBox fluidSettings;
    
    private final CanvasController canvas;
    
    // Model variables
    protected thermocycle.FlowNode node;
    
    // Table data
    private final ObservableList<PropertyView> propertyTable;
    private final ObservableList<Property> propertyList;
    
    /**
     * Constructor
     * 
     */
    public InfoboxFlowController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxFlow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
        // Initialise form data
        propertyList = FXCollections.observableList(new ArrayList<>());
        propertyTable = FXCollections.observableList(new ArrayList<>());
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        selectProperty.setItems(propertyList);
        
        table.setItems(propertyTable);
                
        // Build event handlers
        buildClickHandlers();
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
    
    
    public void showDetails(thermocycle.FlowNode node) {
        
        // Set component
        this.node = node;
        
        // Get fluid
        selectFluid.getItems().addAll(canvas.model.fluidsReadOnly);
        
        // Populate the state table
        populate();
        
    }
    
    /**
     * Populates the state table with properties and values.
     */
    private void populate() {
        
        // Hide or show the flow infomation
        if (canvas.model.isFluidSet(node)) {
            selectFluid.getSelectionModel().select(canvas.model.getFluid(node));
            fluidSettings.setVisible(true);
        }
        else {
            fluidSettings.setVisible(false);
        }
        
        // Update text fields
        updateTextFields();
        
        // Property value
        //propertyInput.setText(CanvasController.displayOptionalDouble(canvas.model.getState(node, selectProperty.getSelectionModel().getSelectedItem())));
        
        // Re-populate table
        propertyTable.clear();
        canvas.model.getAllowableProperties(node).forEach(p -> {
            propertyTable.add(new PropertyView(p, canvas.model.getState(node, p)));
        });
        
    }
    
    /**
     * Build click handlers for combo boxes and buttons
     */
    private void buildClickHandlers() {
        selectFluid.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setFluid(node, selectFluid.getSelectionModel().getSelectedItem());
                propertyList.addAll(canvas.model.getAllowableProperties(node));
                populate();
                event.consume();
            }            
        });
        
        massInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setMass(node, Double.valueOf(massInput.getText()));
                populate();
                event.consume();
            }
        });
        
        buttonClearMass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.clearMass(node);
                populate();
                event.consume();
            }
        });
        
        selectProperty.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                propertyInput.setText(CanvasController.displayOptionalDouble(canvas.model.getState(node, selectProperty.getSelectionModel().getSelectedItem())));
                propertyUnits.setText(selectProperty.getSelectionModel().getSelectedItem().units);
                populate();
                event.consume();
            }    
        });
        
        propertyInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.setState(node, selectProperty.getSelectionModel().getSelectedItem(), Double.valueOf(propertyInput.getText()));
                populate();
                event.consume();
            }
        });
        
        buttonClearProperty.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.clearState(node, selectProperty.getSelectionModel().getSelectedItem());
                populate();
                event.consume();
            }
        });
        
        buttonClearState.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                canvas.model.clearState(node);
                populate();
                event.consume();
            }
        });

    }
    
    private void updateTextFields() {
        if (!selectProperty.getSelectionModel().isEmpty()) {
            propertyInput.setText(CanvasController.displayOptionalDouble(canvas.model.getState(node, selectProperty.getSelectionModel().getSelectedItem())));
        }
        else {
            propertyInput.setText("");
        }
        massInput.setText(CanvasController.displayOptionalDouble(canvas.model.getMass(node)));
    }
    
}
