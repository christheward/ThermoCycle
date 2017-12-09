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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import static javafx.scene.input.KeyCode.X;
import static javafx.scene.input.KeyCode.Y;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class Graph extends AnchorPane {

    @FXML private LineChart<Number, Number> lineGraph;
    private ObservableList<XYChart.Series<Number, Number>> dataset = null;
    
    // Constructor
    public Graph() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Graph.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        lineGraph.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        dataset = FXCollections.observableArrayList();
        lineGraph.setData(dataset);
        lineGraph.setVisible(true);
    }
    
    /**
     * Initializes the controller class.
     */
    @FXML private void initialize() {
    }    
    
    public void setSeries(XYChart.Series series) {
        /*
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(14, 22));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));
        series.getData().add(new XYChart.Data(11, 1));
        */
        lineGraph.getData().add(series);
    }
    
    public void setTitle(String title) {
        lineGraph.setTitle(title);
    }
    public void setXAxis(String title) {
        lineGraph.getXAxis().setLabel(title);
    }
    public void setYAxis(String title) {
        lineGraph.getYAxis().setLabel(title);
    }
    public void clear() {
        dataset.clear();
    }
}
