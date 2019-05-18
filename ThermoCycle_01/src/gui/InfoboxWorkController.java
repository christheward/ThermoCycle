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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import thermocycle.WorkNode;

/**
 *
 * @author Chris
 */
public class InfoboxWorkController extends AnchorPane {

    // FXML Variables
    @FXML private TableView<BoundaryWork> workTable;
    @FXML private TableColumn<BoundaryWork, String> workNameColumn;
    @FXML private TableColumn<BoundaryWork, Number> workValueColumn;
    @FXML private TableColumn<BoundaryWork, String> workUnitsColumn;
    @FXML private TableColumn<BoundaryWork, Boolean> workClearColumn;
    
    // Set master
    private final MasterSceneController master;
    
    // Table data
    private final ObservableList<BoundaryWork> workTableData;
    
    // Model variables
    protected ReadOnlyObjectWrapper<WorkNode> node;
    
    /**
     * Constructor
     * 
     */
    public InfoboxWorkController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InfoboxWork.fxml"));
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
                    if (((CanvasNodeController) newValue).node instanceof WorkNode) {
                        InfoboxWorkController.this.node.setValue( (WorkNode) ((CanvasNodeController) newValue).node);
                    }
                }
            }
        });
        
        // Every time component changes update data
        node.addListener(new ChangeListener<WorkNode>() {
            @Override
            public void changed(ObservableValue<? extends WorkNode> observable, WorkNode oldValue, WorkNode newValue) {
                if (node.isNotNull().getValue()) {
                    // Set mass data
                    workTableData.clear();
                    BoundaryWork heatBoundary = new BoundaryWork();
                    master.getModel().getBoundaryConditionWork(node.getValue()).ifPresent(bc -> heatBoundary.setBoundaryCondition(bc));
                    workTableData.add(heatBoundary);
                }
            }
        });
        
        
        // Initialise form data
        workTableData = FXCollections.observableList(new ArrayList());
        workNameColumn.setCellValueFactory(cellData -> cellData.getValue().workProperty());
        workValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        workValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        //workValueColumn.setOnEditCommit(event -> event.getRowValue().setBoundaryCondition(master.getModel().setBoundaryConditionWork(node.getValue(), new double[] {event.getNewValue().doubleValue()})));
        workValueColumn.setEditable(true);
        workUnitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        workClearColumn.setCellValueFactory(cellData -> cellData.getValue().presentProperty());
        workClearColumn.setCellFactory(new Callback<TableColumn<BoundaryWork,Boolean>,TableCell<BoundaryWork,Boolean>>() {
            @Override
            public TableCell<BoundaryWork, Boolean> call(TableColumn<BoundaryWork, Boolean> param) {
                final ButtonCell<BoundaryWork, Boolean> cell = new ButtonCell();
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
        workTable.setItems(workTableData);
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
        
}
