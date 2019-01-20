/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import thermocycle.Attributes;
import thermocycle.Component;

/**
 *
 * @author Chris
 */
public class InfoboxComponentController extends AnchorPane {

    // FXML variables
    @FXML private TextField nameInput;
    @FXML private TableView<BoundaryAttribute> table;
    @FXML private TableColumn<BoundaryAttribute, Boolean> checkColumn;
    @FXML private TableColumn<BoundaryAttribute, String> attributeColumn;
    @FXML private TableColumn<BoundaryAttribute, Number> valueColumn;
    @FXML private TableColumn<BoundaryAttribute, String> unitsColumn;
    
    // GUI variables
    private final MasterSceneController master;
    
    // Table data
    private final ObservableList<BoundaryAttribute> tableData;
    
    // Properties
    private ReadOnlyObjectWrapper<Component> component;
    
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
                if (newValue instanceof CanvasComponentController) {
                    InfoboxComponentController.this.component.setValue(((CanvasComponentController) newValue).component);
                }
            }
        });
        
        // Every time component changes update data
        component.addListener(new ChangeListener<Component>() {
            @Override
            public void changed(ObservableValue<? extends Component> observable, Component oldValue, Component newValue) {
                System.out.println("New component selected");
                if (component.isNotNull().getValue()) {
                    nameInput.setText(master.getModel().getName(component.getValue()));
                    tableData.clear();
                    component.getValue().getAllowableAtributes().stream().forEach(a -> {
                        // if boundary exists in mosel, use it
                        BoundaryAttribute boundary = new BoundaryAttribute(a);
                        master.getModel().getBoundaryConditionAtt(component.getValue(), a).ifPresent(bc -> boundary.setBoundaryCondition(bc));
                        tableData.add(boundary);
                    });                    
                }
            }
        });
        
        // Initialise form data
        tableData = FXCollections.observableList(new ArrayList());
        table.setItems(tableData);
        table.setEditable(true);
        
        // Attributes column
        attributeColumn.setCellValueFactory(cellData -> cellData.getValue().attributeProperty().asString());
        
        // Value Column
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        valueColumn.setOnEditCommit(event -> event.getRowValue().setBoundaryCondition(master.getModel().setBoundaryConditionAttribute(component.getValue(), event.getRowValue().attributeProperty().getValue(), event.getNewValue().doubleValue())));
        valueColumn.setEditable(true);
        
        // Units column
        unitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        
        // Clear column
        checkColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        checkColumn.setCellFactory(new Callback<TableColumn<BoundaryAttribute,Boolean>,TableCell<BoundaryAttribute,Boolean>>() {
            @Override
            public TableCell<BoundaryAttribute, Boolean> call(TableColumn<BoundaryAttribute, Boolean> param) {
                final ButtonCell<BoundaryAttribute, Boolean> cell = new ButtonCell(table);
                cell.button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
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
    
    private Callback<TableColumn<BoundaryAttribute,Boolean>,TableCell<BoundaryAttribute,Boolean>> getAttributeCellFactory() {
        return new Callback<TableColumn<BoundaryAttribute,Boolean>,TableCell<BoundaryAttribute,Boolean>>() {
            @Override
            public TableCell<BoundaryAttribute, Boolean> call(TableColumn<BoundaryAttribute, Boolean> param) {
                final ButtonCell<BoundaryAttribute, Boolean> cell = new ButtonCell(table);
                cell.button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        cell.getTableView().getItems().get(cell.getIndex()).clearBoundaryCondition();
                    }
                });
                cell.button.disableProperty().bind(cell.itemProperty().isNotEqualTo(true));
                return cell;
            }
        };
    }
    
}
