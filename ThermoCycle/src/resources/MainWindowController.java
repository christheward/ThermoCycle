/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import thermocycle.Component;
import thermocycle.Connection;
import thermocycle.Cycle;
import thermocycle.Fluid;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class MainWindowController implements Initializable {

    private Cycle model;
    protected ListProperty<Fluid> fluidsObs = new SimpleListProperty<>();
    protected ListProperty<Component> componentsObs = new SimpleListProperty<>();
    protected ListProperty<Connection> connectionsObs = new SimpleListProperty<>();
    ContextMenu menu = new ContextMenu();
    
    @FXML
    private MenuItem fileExit;
    @FXML
    private MenuItem helpAbout;
    @FXML
    private Canvas canvas;
    @FXML
    private TextArea console;
    @FXML
    private MenuItem fileNew;
    @FXML
    private ListView fluids;
    @FXML
    private ListView components;
    @FXML
    private ListView connections;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new Cycle("test");
        model.createIdealGas("test1", 1.4, 287.0);
        model.createCombustor("comb");
        /*
        fluidsObs.set(FXCollections.observableArrayList(model.getFluids()));
        fluids.itemsProperty().bind(fluidsObs);
        model.createIdealGas("test2", 1.3, 247.0);
        componentsObs.set(FXCollections.observableArrayList(model.getComponents()));
        components.itemsProperty().bind(componentsObs);
        connectionsObs.set(FXCollections.observableArrayList(model.getConnections()));
        connections.itemsProperty().bind(connectionsObs);
        */
        menu.getItems().add(new MenuItem("tett"));
        canvas.setOnContextMenuRequested(e -> menu.show(canvas, e.getScreenX(), e.getScreenY()));
        
        
    }
    
    @FXML
    void handlerHelpAbout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("AboutWindow.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.setTitle("About ThermoCycle");
        stage.show();
    }
    
    @FXML
    private void handleFileExit(ActionEvent event) {
        Stage stage = (Stage) canvas.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @FXML
    private void handlerCanvas(MouseEvent event) {
        
    }

    @FXML
    private void handlerFileNew(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewWindow.fxml"));
        Parent root = (Parent)loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        NewWindowController controller = (NewWindowController)loader.getController();
        
        controller.setModel(model);
        
        stage.setScene(scene);
        stage.setTitle("Create New Cycle");
        stage.show();
        
    }
    
}
