package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class ChartController extends VBox {
    
    // FXML Variables
    @FXML private PieChart pieChart;
    
    // Dataset Variables
    private ObservableList<Data> dataset;

    // GUI Varaiables
    private final MasterSceneController master;

    
    // Constructor
    public ChartController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        /// FXML loader
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Chart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Initialise dataset
        dataset = FXCollections.observableArrayList();
        pieChart.setData(dataset);
        pieChart.setVisible(true);
        pieChart.setTitle("Exergy Analysis");
    }
    
    
    /**
     * Initializes the controller class.
     */
    @FXML private void initialize() {
    }
    
    protected void getData() {
        dataset.clear();
        if (master.modelReadOnly.isNotNull().get()) {
            master.getModel().componentsReadOnly.stream().forEach(c -> {
                System.out.println(c.exergyLoss());
                dataset.add(new Data(c.getClass().getName(), c.exergyLoss()));
            });
        }
    }
    
}
