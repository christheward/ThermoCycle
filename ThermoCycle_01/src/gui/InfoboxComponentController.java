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
import thermocycle.Attributes.Attribute;

/**
 *
 * @author Chris
 */
public class InfoboxComponentController extends AnchorPane {

    // FXML variables
    @FXML private TableView<AttributeView> table;
    @FXML private TableColumn<AttributeView, Object> attributeColumn;
    @FXML private TableColumn<AttributeView, Double> valueColumn;
    @FXML private TableColumn<AttributeView, String> unitsColumn;
    @FXML private ComboBox<Attribute> selectAttribute;
    @FXML private TextField attributeInput;
    @FXML private Label attributeUnits;
    @FXML private Button buttonClearAttribute;
    
    // GUI variables
    private final MasterSceneController master;
    private final ObservableList<AttributeView> attributeTable;
    private final ObservableList<Attribute> attributeList;
    
    // Model variables
    protected thermocycle.Component component;
    
    /**
     * Constructor
     * 
     */
    public InfoboxComponentController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Initialise form data
        attributeList = FXCollections.observableList(new ArrayList<>());
        attributeTable = FXCollections.observableList(new ArrayList<>());
        attributeColumn.setCellValueFactory(new PropertyValueFactory<>("attribute"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("output"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        selectAttribute.setItems(attributeList);
        
        // Set up links
        table.setItems(attributeTable);
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
        
        // Build event handlers
        buildClickHandlers();
        
    }
    
    public void showDetails(thermocycle.Component component) {
        
        // Set component
        this.component = component;
        
        // Get available Attributes
        attributeList.clear();
        attributeList.addAll(master.getModel().getAttributes(component));
        
        refresh();
        
    }
    
    /**
     * Populates the heat flux values.
     */
    private void refresh() {
        
        // Re-refresh attribute boundary condition table
        attributeTable.clear();
        master.getModel().getAttributes(component).forEach(a -> {
            OptionalDouble value = master.getModel().getAttributeBoundaryCondition(component, a);
            if (value.isPresent()) {
                attributeTable.add(new AttributeView(a, value));
            }
        });

        // Update text fields
        if (!selectAttribute.getSelectionModel().isEmpty()) {
            attributeInput.setText(MasterSceneController.displayOptionalDouble(master.getModel().getAttributeBoundaryCondition(component, selectAttribute.getSelectionModel().getSelectedItem())));
            attributeUnits.setText(selectAttribute.getSelectionModel().getSelectedItem().units);
        }
        else {
            attributeInput.setText("");
            attributeUnits.setText("-");
        }
        
    }
    
    private void buildClickHandlers() {
        
        attributeInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!selectAttribute.getSelectionModel().isEmpty()) {
                    master.getModel().setAttribute(component, selectAttribute.getSelectionModel().getSelectedItem(), Double.valueOf(attributeInput.getText()));
                    refresh();
                }
                event.consume();
            }
        });
        
        buttonClearAttribute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!selectAttribute.getSelectionModel().isEmpty()) {
                    master.getModel().removeBoundaryCondition(component, selectAttribute.getSelectionModel().getSelectedItem());
                    refresh();
                }
                event.consume();
            }
        });
        
        selectAttribute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refresh();
                event.clone();
            }    
        });
    }
}
