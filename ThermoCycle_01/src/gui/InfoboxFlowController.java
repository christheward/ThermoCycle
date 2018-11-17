/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.OptionalDouble;
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

    // FXML variables
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
    @FXML private VBox fluidSettings;
    
    // GUI variables
    private final MasterSceneController master;
    private final ObservableList<PropertyView> propertyTable;
    private final ObservableList<Property> propertyList;
    
    // Model variables
    protected thermocycle.FlowNode node;
    
    /**
     * Constructor
     * 
     */
    public InfoboxFlowController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxFlow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Initialise form data
        propertyList = FXCollections.observableList(new ArrayList());
        propertyTable = FXCollections.observableList(new ArrayList());
        propertyColumn.setCellValueFactory(new PropertyValueFactory("property"));
        valueColumn.setCellValueFactory(new PropertyValueFactory("value"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory("units"));
        
        // Set up links
        selectProperty.setItems(propertyList);
        table.setItems(propertyTable);
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {

        // Build event handlers
        buildClickHandlers();
        
    }
    
    /**
     * Shows details for the 
     * @param node 
     */
    public void showDetails(thermocycle.FlowNode node) {
        
        // Set component
        this.node = node;
        
        // Set fluids list - can't put this in initialiser because model doesn't allways exit 
        selectFluid.setItems(master.getModel().fluidsReadOnly);
                
        // Set fluid
        if (master.getModel().isFluidSet(node)) {
            selectFluid.getSelectionModel().select(master.getModel().getFluid(node));
            propertyList.clear();
            propertyList.addAll(master.getModel().getAllowableProperties(selectFluid.getValue()));
        };
        
        refresh();
    }
    
    /**
     * Refreshes infobox values.
     */
    private void refresh() {
        
        if (master.getModel().isFluidSet(node)) {
            
            // Update mass flow boundary condition
            massInput.setText(MasterSceneController.displayOptionalDouble(master.getModel().getMassBoundaryCondition(node)));

            // Update propery boundary condition table
            if (!selectFluid.getSelectionModel().isEmpty()) {
                propertyTable.clear();
                master.getModel().getAllowableProperties(selectFluid.getValue()).forEach(p -> {
                    OptionalDouble value = master.getModel().getPropertyBoundaryCondition(node, p);
                    if (value.isPresent()) {
                        propertyTable.add(new PropertyView(p, value));
                    }
                });
            }
            
            // Update property boundary condition
            if (!selectProperty.getSelectionModel().isEmpty()) {
                propertyInput.setText(MasterSceneController.displayOptionalDouble(master.getModel().getPropertyBoundaryCondition(node, selectProperty.getSelectionModel().getSelectedItem())));
                propertyUnits.setText(selectProperty.getSelectionModel().getSelectedItem().units);
            }
            else {
                propertyInput.setText("");
                propertyUnits.setText("-");
            }
            
            // Show infomation
            fluidSettings.setVisible(true);
            
        }
        else {
            
            // Hide infomation
            fluidSettings.setVisible(false);
        }
        
    }
    
    /**
     * Build click handlers for combo boxes and buttons
     */
    private void buildClickHandlers() {
        
        selectFluid.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setFluid(node, selectFluid.getSelectionModel().getSelectedItem());
                // Update property list
                propertyList.clear();
                propertyList.addAll(master.getModel().getAllowableProperties(selectFluid.getValue()));
                refresh();
                event.consume();
            }        
        });
        
        massInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setMass(node, Double.valueOf(massInput.getText()));
                refresh();
                event.consume();
            }
        });
        
        buttonClearMass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().removeBoundaryCondition(node);
                refresh();
                event.consume();
            }
        });
        
        selectProperty.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refresh();
                event.consume();
            }    
        });
        
        propertyInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setState(node, selectProperty.getSelectionModel().getSelectedItem(), Double.valueOf(propertyInput.getText()));
                refresh();
                event.consume();
            }
        });
        
        buttonClearProperty.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().removeBoundaryCondition(node, selectProperty.getSelectionModel().getSelectedItem());
                refresh();
                event.consume();
            }
        });
        
    }
}
