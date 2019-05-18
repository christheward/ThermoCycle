/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import thermocycle.FlowNode;
import thermocycle.HeatNode;

/**
 *
 * @author Chris
 */
public class InfoboxHeatController extends AnchorPane {

    // FXML Variables
    @FXML private TableView<BoundaryHeat> heatTable;
    @FXML private TableColumn<BoundaryHeat, String> heatNameColumn;
    @FXML private TableColumn<BoundaryHeat, Number> heatValueColumn;
    @FXML private TableColumn<BoundaryHeat, String> heatUnitsColumn;
    @FXML private TableColumn<BoundaryHeat, Boolean> heatClearColumn;
    
    // Set master
    private final MasterSceneController master;
    
    // Table data
    private final ObservableList<BoundaryHeat> heatTableData;
    
    // Model variables
    private ReadOnlyObjectWrapper<HeatNode> node;
    
    
    /**
     * Constructor
     * 
     */
    public InfoboxHeatController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InfoboxHeat.fxml"));
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
                    if (((CanvasNodeController) newValue).node instanceof HeatNode) {
                        InfoboxHeatController.this.node.setValue( (HeatNode) ((CanvasNodeController) newValue).node);
                    }
                }
            }
        });
        
        // Every time component changes update data
        node.addListener(new ChangeListener<HeatNode>() {
            @Override
            public void changed(ObservableValue<? extends HeatNode> observable, HeatNode oldValue, HeatNode newValue) {
                if (node.isNotNull().getValue()) {
                    // Set mass data
                    heatTableData.clear();
                    BoundaryHeat heatBoundary = new BoundaryHeat();
                    master.getModel().getBoundaryConditionHeat(node.getValue()).ifPresent(bc -> heatBoundary.setBoundaryCondition(bc));
                    heatTableData.add(heatBoundary);
                }
            }
        });
        
        // Initialise form data
        heatTableData = FXCollections.observableList(new ArrayList());
        heatNameColumn.setCellValueFactory(cellData -> cellData.getValue().heatProperty());
        heatValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        heatValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        //heatValueColumn.setOnEditCommit(event -> event.getRowValue().setBoundaryCondition(master.getModel().setBoundaryConditionHeat(node.getValue(), new double[] {event.getNewValue().doubleValue()})));
        heatValueColumn.setEditable(true);
        heatUnitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        heatClearColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        heatClearColumn.setCellFactory(new Callback<TableColumn<BoundaryHeat,Boolean>,TableCell<BoundaryHeat,Boolean>>() {
            @Override
            public TableCell<BoundaryHeat, Boolean> call(TableColumn<BoundaryHeat, Boolean> param) {
                final ButtonCell<BoundaryHeat, Boolean> cell = new ButtonCell();
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
        heatTable.setItems(heatTableData);
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
        
}
