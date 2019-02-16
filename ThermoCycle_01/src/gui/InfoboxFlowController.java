/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.binding.ListBinding;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thermocycle.FlowNode;
import thermocycle.Fluid;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris
 */
public class InfoboxFlowController extends AnchorPane {
    
    /**
     * The logger instance.
     */
    private static final Logger logger = LogManager.getLogger("GUILog");
    
    // FXML variables
    @FXML private ComboBox<Fluid> selectFluid;
    @FXML private VBox fluidSettings;
    @FXML private TableView<BoundaryMass> massTable;
    @FXML private TableColumn<BoundaryMass, String> massNameColumn;
    @FXML private TableColumn<BoundaryMass, Number> massValueColumn;
    @FXML private TableColumn<BoundaryMass, String> massUnitsColumn;
    @FXML private TableColumn<BoundaryMass, Boolean> massClearColumn;
    @FXML private TableView<BoundaryProperty> propertyTable;
    @FXML private TableColumn<BoundaryProperty, Property> propertyNameColumn;
    @FXML private TableColumn<BoundaryProperty, Number> propertyValueColumn;
    @FXML private TableColumn<BoundaryProperty, String> propertyUnitsColumn;
    @FXML private TableColumn<BoundaryProperty, Boolean> propertyClearColumn;
        
    // GUI variables
    private final MasterSceneController master;
    
    // Table data
    private final ObservableList<BoundaryMass> massTableData;
    private final ObservableList<BoundaryProperty> propertyTableData;
    
    // Properties
    private final ReadOnlyObjectWrapper<FlowNode> node;
    
    /**
     * Constructor
     * 
     */
    public InfoboxFlowController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InfoboxFlow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Setup properties
        node = new ReadOnlyObjectWrapper();
        
        // If made visible update component
        this.master.focusProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                if (newValue instanceof CanvasNodeController) {
                    if (((CanvasNodeController) newValue).node instanceof FlowNode) {
                        InfoboxFlowController.this.node.setValue( (FlowNode) ((CanvasNodeController) newValue).node);
                    }
                }
            }
        });
        
        // Every time component changes update data
        node.addListener(new ChangeListener<FlowNode>() {
            @Override
            public void changed(ObservableValue<? extends FlowNode> observable, FlowNode oldValue, FlowNode newValue) {
                if (node.isNotNull().getValue()) {
                    // Set fluid
                    master.getModel().getFluid(node.getValue()).ifPresent(f -> selectFluid.getSelectionModel().select(f));
                    // Set mass data
                    massTableData.clear();
                    BoundaryMass massBoundary = new BoundaryMass();
                    master.getModel().getBoundaryConditionMass(node.getValue()).ifPresent(bc -> massBoundary.setBoundaryCondition(bc));
                    massTableData.add(massBoundary);
                    // Set properties
                    propertyTableData.clear();
                    node.getValue().getAllowableProperties().stream().forEach(p -> {
                        // if boundary exists in model, use it
                        BoundaryProperty propertyBoundary = new BoundaryProperty(p);
                        master.getModel().getBoundaryConditionProperty(node.getValue(), p).ifPresent(bc -> {
                            propertyBoundary.setBoundaryCondition(bc);
                        });
                        propertyTableData.add(propertyBoundary);
                    });                    
                }
            }
        });
        
        selectFluid.itemsProperty().bind(new ListBinding<Fluid>() {
            {
                bind(master.modelAbsent);
            }
            @Override
            protected ObservableList<Fluid> computeValue() {
                return (master.modelAbsent.getValue()) ? FXCollections.observableList(new ArrayList<>()) : master.getModel().fluidsReadOnly;
            }
        });
        
        selectFluid.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Fluid>() {
            @Override
            public void changed(ObservableValue<? extends Fluid> observable, Fluid oldValue, Fluid newValue) {
                if (selectFluid.getSelectionModel().selectedItemProperty().isNotNull().getValue()) {
                    // Set fluid in model
                    master.getModel().setFluid(node.getValue(), selectFluid.getSelectionModel().selectedItemProperty().getValue());
                    // Set properties
                    propertyTableData.clear();
                    node.getValue().getAllowableProperties().stream().forEach(p -> {
                        // if boundary exists in model, use it
                        BoundaryProperty propertyBoundary = new BoundaryProperty(p);
                        master.getModel().getBoundaryConditionProperty(node.getValue(), p).ifPresent(bc -> {
                            propertyBoundary.setBoundaryCondition(bc);
                        });
                        propertyTableData.add(propertyBoundary);
                    });                    
                }
            }
        });
        
        fluidSettings.disableProperty().bind(selectFluid.getSelectionModel().selectedItemProperty().isNull());
        
        // Initialise form data
        massTableData = FXCollections.observableList(new ArrayList());
        massNameColumn.setCellValueFactory(cellData -> cellData.getValue().massProperty());
        massValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        massValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        massValueColumn.setOnEditCommit(event -> event.getRowValue().setBoundaryCondition(master.getModel().setBoundaryConditionMass(node.getValue(), new double[] {event.getNewValue().doubleValue()})));
        massValueColumn.setEditable(true);
        massUnitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        massClearColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        massClearColumn.setCellFactory(new Callback<TableColumn<BoundaryMass,Boolean>,TableCell<BoundaryMass,Boolean>>() {
            @Override
            public TableCell<BoundaryMass, Boolean> call(TableColumn<BoundaryMass, Boolean> param) {
                final ButtonCell<BoundaryMass, Boolean> cell = new ButtonCell();
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
        
        propertyTableData = FXCollections.observableList(new ArrayList());
        propertyNameColumn.setCellValueFactory(cellData -> cellData.getValue().propertyProperty());
        propertyValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        propertyValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        propertyValueColumn.setOnEditCommit(event -> event.getRowValue().setBoundaryCondition(master.getModel().setBoundaryConditionProperty(node.getValue(), event.getRowValue().propertyProperty().getValue(), new double[] {event.getNewValue().doubleValue()})));
        propertyValueColumn.setEditable(true);
        propertyUnitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        propertyClearColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        propertyClearColumn.setCellFactory(new Callback<TableColumn<BoundaryProperty,Boolean>,TableCell<BoundaryProperty,Boolean>>() {
            @Override
            public TableCell<BoundaryProperty, Boolean> call(TableColumn<BoundaryProperty, Boolean> param) {
                final ButtonCell<BoundaryProperty, Boolean> cell = new ButtonCell();
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
        
        // Set up links
        massTable.setItems(massTableData);
        propertyTable.setItems(propertyTableData);
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
    
}
