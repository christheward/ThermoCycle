package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import thermocycle.FlowNode;
import thermocycle.Properties.Property;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class GraphController extends VBox {

    // FXML Variables
    @FXML private LineChart<Number, Number> lineGraph;
    @FXML private NumberAxis xaxis;
    @FXML private NumberAxis yaxis;
    @FXML private ComboBox<Property> xproperty;
    @FXML private ComboBox<Property> yproperty;
    
    // GUI Varaiables
    private final MasterSceneController master;
    
    // Dataset variables
    private final ObservableList<XYChart.Series<Number, Number>> dataset;
    private final ObservableList<Property> propertyList;
    
    // Constructor
    public GraphController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Graph.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Initialise dataset
        dataset = FXCollections.observableArrayList();
        
        // Set line graph properties
        lineGraph.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        lineGraph.setData(dataset);
        lineGraph.setVisible(true);
        lineGraph.setTitle("Thermodynamic Cycle");
        
        // Fill the combo box with properties
        propertyList = FXCollections.observableArrayList(Property.values());
        xproperty.setItems(propertyList);
        yproperty.setItems(propertyList);
        xproperty.getSelectionModel().select(Property.TEMPERATURE);
        yproperty.getSelectionModel().select(Property.ENTROPY);
        
        // Set combobox handlers
        buildHandlers();
        
        // Set data
        getData();
    }
    
    /**
     * Initializes the controller class.
     */
    @FXML private void initialize() {
    }
    
    /**
     * Gets the data from the model
     */
    private void getData() {
        // Clear existing data
        dataset.clear();
        // Check model
        if (master.modelAbsent.not().get()) {
            master.getModel().componentsReadOnly.forEach(c -> {
                // Get component data
                List<List<FlowNode>> componentData = master.getModel().plotData(c);
                componentData.forEach(path -> {
                    // Create new series
                    XYChart.Series series = new XYChart.Series();
                    path.forEach(n -> {
                        //OptionalDouble x = master.getModel().getProperty(n, xproperty.getSelectionModel().getSelectedItem());
                        //OptionalDouble y = master.getModel().getProperty(n, yproperty.getSelectionModel().getSelectedItem());
                        //series.getData().add(new XYChart.Data(x.orElse(Double.NaN), y.orElse(Double.NaN)));
                        //series.setName(c.getClass().getName());
                    });
                    // Add series to dataset
                    dataset.add(series);
                });
            });
        }
    }
    
    /**
     * Sets the graphs title
     * @param title The title of thee graph
     */
    protected void setTitle(String title) {
        lineGraph.setTitle(title);
    }
    
    private void buildHandlers() {
        xproperty.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getData();
                Property xprop = xproperty.getSelectionModel().getSelectedItem();
                xaxis.setLabel(xprop.fullName + " [" + xprop.units + "]");
            }
        });
        yproperty.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getData();
                Property yprop = yproperty.getSelectionModel().getSelectedItem();
                yaxis.setLabel(yprop.fullName + " [" + yprop.units + "]");        
            }
        });
    }
    
}
