/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import thermocycle.BoundaryCondition;
import thermocycle.UnitsControl.Units;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class DataTableController extends AnchorPane {
    
    // Cell height.
    static public final DoubleProperty cellHeight = new SimpleDoubleProperty(40.0);
    
    // FXML variables
    @FXML private TitledPane dataContainer;
    @FXML private TableView<TableData> dataTable;
    @FXML private TableColumn<TableData, String> nameColumn;
    @FXML private TableColumn<TableData, Number> valueColumn;
    @FXML private TableColumn<TableData, Units> unitsColumn;
    @FXML private TableColumn<TableData, BoundaryCondition> setColumn;
    
    // Data containers
    private final ObservableList<TableData> data;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor.
     * @param master The master scene
     */
    public DataTableController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Set data
        this.data = FXCollections.observableArrayList();
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DataTable.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
    }
    
    /**
     * Initialiser.
     */
    public void initialize() {
        
        // Set cell height
        dataTable.fixedCellSizeProperty().bind(cellHeight);
        dataTable.columnResizePolicyProperty().setValue(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Link table to data
        dataTable.setItems(data);
        
        // Set up data formatting for name column
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        // Set up data formatting for value column
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        valueColumn.setCellFactory(column -> {
            ValueCell c = new ValueCell();
            return c;
        });
        
        // Set up data formatting for units column
        unitsColumn.setCellValueFactory(cellData -> cellData.getValue().unitsProperty());
        unitsColumn.setCellFactory(column -> {
            UnitsCell c = new UnitsCell();
            return c;
        });
        
        // Set up data formatting for set column
        setColumn.setCellValueFactory(cellData -> cellData.getValue().boundaryConditionProperty());
        setColumn.setCellFactory(column -> {
            return new ClearCell();
        });
        
        // Scale height of data table with number of entries.
        dataTable.skinProperty().addListener(new ChangeListener<Skin>() {
            @Override
            public void changed(ObservableValue<? extends Skin> observable, Skin oldValue, Skin newValue) {
                dataTable.prefHeightProperty().bind(new DoubleBinding() {
                    {
                        bind(dataTable.itemsProperty(), dataTable.fixedCellSizeProperty(), ((TableHeaderRow) dataTable.lookup("TableHeaderRow")).heightProperty());
                    }
                    @Override
                    protected double computeValue() {
                        return dataTable.itemsProperty().getValue().size() * dataTable.fixedCellSizeProperty().getValue() + ((Pane) dataTable.lookup("TableHeaderRow")).heightProperty().getValue() + 2;
                    }
                });
                
                //((ScrollBar) dataTable.lookup(".scroll-bar:hotizontal")).setDisable(true);
                //((ScrollBar) dataTable.lookup(".scroll-bar:vertical")).setDisable(true);
                //dataTable.applyCss();
                dataTable.layout();
                
                // Remove original listener now that the height has been bound.
                dataTable.skinProperty().removeListener(this);
            }
        });
        
        disableProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    dataContainer.expandedProperty().setValue(false);
                }
                else {
                    dataContainer.expandedProperty().setValue(false);
                }
            }
        });
        
        dataTable.setRowFactory(tv -> {
            TableRow<TableData> row = new TableRow();
            row.setOnMouseClicked(new EventHandler <MouseEvent> () {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        // Create context menu
                        MenuItem mi = new MenuItem("Delete");
                        mi.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                row.getItem().clearBoundaryCondition();
                                master.getModel().removeBoundaryCondition(row.getItem().boundaryCondition.getValue());
                            }
                        });
                        ContextMenu contextMenu = new ContextMenu();
                        contextMenu.getItems().add(mi);
                        contextMenu.show(row, event.getScreenX(), event.getScreenY());
                    }
                    event.consume();
                }
            });
            return row;
        });
        
    }
    
    /**
     * Sets the title of the data table.
     * @param string the new data table title.
     */
    public void setTitle(String title) {
        dataContainer.setText(title);
    }
    
    /**
     * Clears all data from the data table
     */
    public void clearData() {
        data.clear();
    }
    
    /**
     * Adds a new data element to the data table.
     * @param td the new table data to add to the data table.
     * @return the table data object added to the data table.
     */
    public void addData(TableData td) {
        data.add(td);
    }
    
    /**
     * Gets the number of items in the data tables.
     * @return the number of items in the data table.
     */
    public int getSize() {
        return data.size();
    }
    
    /**
     * Gets the last element added to the data table.
     * @return the last element added to the data table
     */
    public Optional<TableData> getLast() {
        if (data.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(data.get(data.size()-1));
        }
    }
    
    /**
     * Class defining a cell for value.
     */
    class ValueCell extends TableCell<TableData, Number> {
        
        /**
         * Input text field.
         */
        private TextField textField;
        
        /**
         * Constructor
         */
        public ValueCell() {}
        
        @Override
        public void startEdit() {
            super.startEdit(); 
            // Create the text field if it'd not been created before.
            if (textField == null) {
                textField = new TextField(currentValue());
                //textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
                textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent t) {
                        if (t.getCode() == KeyCode.ENTER) {
                            try {
                                // Normally would commint the new number to ValueCell here; however, value is bound to boundary condition and units.
                                // So instead, just update the boundary condition.
                                TableData td = dataTable.getItems().get(getIndex());
                                td.createBoundaryCondition(master.getModel(), td.units.getValue().toSI(Double.parseDouble(textField.getText())));
                            }
                            catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        } else if (t.getCode() == KeyCode.ESCAPE) {
                            cancelEdit();
                        }
                    }
                });
            }
            // Display the text field in the cell.
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }
        
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            // Reset the value of the cell and hide the text field.
            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        
        @Override
        public void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            }
            else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(currentValue());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
                else {
                    setText(currentValue());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }
        
        /**
         * Gets the cell value as a string.
         * @return the cell value as a string.
         */
        private String currentValue() {
            return Objects.isNull(getItem()) ? "" : getItem().toString();
        }
    }
    
    /**
     * Class defining a cell for units.
     */
    class UnitsCell extends TableCell<TableData, Units> {
        
        /**
         * Choice box for selecting options
         */
        private ChoiceBox<Units> choiceBox;
        
        /**
         * Constructor.
         */
        public UnitsCell() {
        }
        
        @Override
        public void startEdit() {
            super.startEdit();
            if (choiceBox == null) {
                // Initialise choice box
                choiceBox = new ChoiceBox();
                choiceBox.itemsProperty().setValue(FXCollections.observableArrayList(getItem().getType().getUnits()));
                choiceBox.selectionModelProperty().getValue().select(getItem());

                // Add handler to choice box
                choiceBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        TableData td = dataTable.getItems().get(getIndex());
                        td.units.setValue(choiceBox.getSelectionModel().getSelectedItem());
                    }
                });
            }
            // Display the choice box in the cell.
            setGraphic(choiceBox);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            // Reste the value of the cell and hide the text field.
            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        
        @Override
        public void updateItem(Units item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            }
            else
            {
                if (isEditing()) {
                    if (choiceBox != null) {
                        choiceBox.selectionModelProperty().getValue().select(item);
                    }
                    setGraphic(choiceBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
                else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }
        
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
        
    }
    
    /**
     * Class defining cell for clearing
     */
    class ClearCell extends TableCell<TableData, BoundaryCondition> {
        
        /**
         * Button for clearing
         */
        private Button button;
        
        /**
         * Constructor.
         */
        public ClearCell() {
            
            // Initilaise button.
            button = new Button();
            button.setText("X");
            //button.setMaxHeight(cellHeight.doubleValue()); // Doesn't appear to work
            
            //setGraphicTextGap(0.0);
            //button.prefHeightProperty().bind(cellHeight);
            //button.setMinWidth(getWidth() - getGraphicTextGap()*2);
            
            // Set cell as button graphic
            setGraphic(button);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            
            // Bind button to boundary condition
            button.disableProperty().bind(itemProperty().isNull());
            
            // Set button action
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TableData td = dataTable.getItems().get(getIndex());
                    td.clearBoundaryCondition();
                    master.getModel().removeBoundaryCondition(itemProperty().getValue());
                }
            });
            
        }
        
    }
    
}
