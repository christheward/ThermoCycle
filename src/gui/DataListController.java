/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class DataListController extends AnchorPane {
    
    // Cell height.
    static public final DoubleProperty cellHeight = new SimpleDoubleProperty(40.0);
    
    // FXML variables
    @FXML private TitledPane dataContainer;
    @FXML private ListView dataList;
    
    // The backeing list
    private IntegerProperty listSize;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor.
     * @param master The master scene
     */
    public DataListController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DataList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Set list size
        listSize = new SimpleIntegerProperty();
        
        // Bind list properties
        dataList.prefHeightProperty().bind(listSize.multiply(cellHeight));
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
        dataContainer.disableProperty().bind(listSize.isEqualTo(0));
        
    }
    
    /**
     * Initialiser.
     */
    public void initialize() {
        // Set cell height
        dataList.fixedCellSizeProperty().bind(cellHeight);
        
        /**
        dataList.setCellFactory(value); .setCellFactory(cell -> {
            cell;
            
            return cell;
        });
        */
        
        
    }
    
    /**
     * Set the cell factory for the list.
     * @param value 
     */
    public void setCellFactory(Callback value) {
        dataList.setCellFactory(value);
    }
    
    /**
     * Sets the title of the data table.
     * @param string the new data table title.
     */
    public void setTitle(String title) {
        dataContainer.setText(title);
    }
    
    /**
     * Links the list to this observable list.
     * @param data the observable list to link to the list.
     */
    public void addData(ObservableList data) {
        // Add data to list
        dataList.setItems(data);
        // Add new listener to data
        data.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                listSize.setValue(data.size());
            }
        });
        listSize.setValue(data.size());
    }
    
}
