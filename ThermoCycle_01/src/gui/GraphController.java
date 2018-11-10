package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
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
import javafx.scene.layout.AnchorPane;
import thermocycle.Properties.Property;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class GraphController extends AnchorPane {

    // FXML Variables
    @FXML private LineChart<Number, Number> lineGraph;
    @FXML private NumberAxis xaxis;
    @FXML private NumberAxis yaxis;
    @FXML private ComboBox<Property> xproperty;
    @FXML private ComboBox<Property> yproperty;
    private final CanvasController canvas;
    
    // Dataset variables
    private final ObservableList<XYChart.Series<Number, Number>> dataset;
    private final ObservableList<Property> propertyList;
    
    // Constructor
    public GraphController(CanvasController canvas) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Graph.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
        // Initialise dataset
        dataset = FXCollections.observableArrayList();
        
        // Set line graph properties
        lineGraph.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        lineGraph.setData(dataset);
        lineGraph.setVisible(true);
        
        // Fill the combo box with properties
        propertyList = FXCollections.observableArrayList(Property.values());
        xproperty.setItems(propertyList);
        yproperty.setItems(propertyList);
        xproperty.getSelectionModel().select(Property.TEMPERATURE);
        yproperty.getSelectionModel().select(Property.ENTROPY);
        
        // Gets data from model
        getData();
        // Set combobox handlers
        buildHandlers();
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
        // Loop over all paths
        System.out.println("Paths");
        System.out.println(canvas.model.pathsReadOnly.size());
        canvas.model.pathsReadOnly.forEach(p -> {
            // Create new series
            XYChart.Series series = new XYChart.Series();
            // Loop over all nodes in path
            System.out.println("Nodes");
            System.out.println(p.size());
            p.forEach(n -> {
                OptionalDouble x = canvas.model.getState(n, xproperty.getSelectionModel().getSelectedItem());
                OptionalDouble y = canvas.model.getState(n, yproperty.getSelectionModel().getSelectedItem());
                System.out.println(x);
                System.out.println(y);
                series.getData().add(new XYChart.Data(x.orElse(Double.NaN), y.orElse(Double.NaN)));
            });
            // Add series to dataset
            dataset.add(series);
        });
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
