/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public class InfoboxComponentController extends AnchorPane {

    // FXML variables
    @FXML private TextField nameInput;
    @FXML private TableView<BoundaryAttribute> attributeTable;
    @FXML private TableColumn<BoundaryAttribute, String> attributeAttributeColumn;
    @FXML private TableColumn<BoundaryAttribute, Number> attributeValueColumn;
    @FXML private TableColumn<BoundaryAttribute, String> attributeUnitsColumn;
    @FXML private TableColumn<BoundaryAttribute, Boolean> attributeClearColumn;
    
    // GUI variables
    private final MasterSceneController master;
    
    // Table data
    private final ObservableList<BoundaryAttribute> attributeTableData;
    
    // Properties
    private final ReadOnlyObjectWrapper<Component> component;
    
    /**
     * Constructor
     * 
     */
    public InfoboxComponentController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InfoboxComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Setup properties
        component = new ReadOnlyObjectWrapper();
        
        // If made visible update component
        this.master.focusProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                if (newValue instanceof ComponentController) {
                    InfoboxComponentController.this.component.setValue(null);
                    InfoboxComponentController.this.component.setValue(((ComponentController) newValue).component);
                }
            }
        });
        
        // Every time component changes update data
        component.addListener(new ChangeListener<Component>() {
            @Override
            public void changed(ObservableValue<? extends Component> observable, Component oldValue, Component newValue) {
                if (component.isNotNull().getValue()) {
                    nameInput.setText(master.getModel().getName(component.getValue()));
                    attributeTableData.clear();
                    component.getValue().getAllowableAtributes().stream().forEach(a -> {
                        // if boundary exists in mosel, use it
                        BoundaryAttribute boundary = new BoundaryAttribute(a);
                        master.getModel().getBoundaryConditionAttribute(component.getValue(), a).ifPresent(bc -> boundary.setBoundaryCondition(bc));
                        attributeTableData.add(boundary);
                    });                    
                }
            }
        });
        
        // Initialise form data
        attributeTableData = FXCollections.observableList(new ArrayList());
        attributeTable.setItems(attributeTableData);
        attributeAttributeColumn.setCellValueFactory(cellData -> cellData.getValue().attributeProperty().asString());
        attributeValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        attributeValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        //attributeValueColumn.setOnEditCommit(event -> event.getRowValue().setBoundaryCondition(master.getModel().setBoundaryConditionAttribute(component.getValue(), event.getRowValue().attributeProperty().getValue(), new double[] {event.getNewValue().doubleValue()})));
        attributeUnitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        attributeClearColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        attributeClearColumn.setCellFactory(new Callback<TableColumn<BoundaryAttribute,Boolean>,TableCell<BoundaryAttribute,Boolean>>() {
            @Override
            public TableCell<BoundaryAttribute, Boolean> call(TableColumn<BoundaryAttribute, Boolean> param) {
                final ButtonCell<BoundaryAttribute, Boolean> cell = new ButtonCell();
                cell.button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        master.getModel().removeBoundaryCondition(cell.getTableView().getItems().get(cell.getIndex()).boundaryProperty().getValue());
                        cell.getTableView().getItems().get(cell.getIndex()).clearBoundaryCondition();
                    }
                });
                cell.button.disableProperty().bind(cell.itemProperty().isNotEqualTo(true));
                return cell;
            }
        });
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
        
        // Build event handlers
        buildClickHandlers();
        
    }
        
    private void buildClickHandlers() {
        
        nameInput.setOnAction(new EventHandler<ActionEvent> () {
            @Override
            public void handle(ActionEvent event) {
                master.getModel().setName(component.getValue(), nameInput.getText());
                master.requestFocus();
            }
        });
        
    }
    
}
