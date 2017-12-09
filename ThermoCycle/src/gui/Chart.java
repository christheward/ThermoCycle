package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class Chart extends AnchorPane {

    @FXML private PieChart pieChart;
    private ObservableList<Data> dataset = null;
    
    // Constructor
    public Chart() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Chart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        //dataset = FXCollections.observableArrayList(new ArrayList<>());
        dataset = FXCollections.observableArrayList();
        pieChart.setData(dataset);
        pieChart.setVisible(true);
    }
    
    
    /**
     * Initializes the controller class.
     */
    @FXML private void initialize() {
    }
    public void add(String name, double value) {
        dataset.add(new Data(name, value));
    }
    public void setTitle(String title) {
        pieChart.setTitle(title);
    }
    public void clear() {
        dataset.clear();
    }
}
