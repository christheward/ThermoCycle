/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import thermocycle.FlowNode;
import thermocycle.HeatNode;
import thermocycle.WorkNode;

/**
 *
 * @author Chris
 */
public class InfoboxController extends AnchorPane{
    
    // FXML variables
    @FXML private VBox contents;
    private final CanvasController canvas;
    
    // Event handlers
    
    /**
     * Constructor
     * @param canvas The parent canvas 
     */
    public InfoboxController(CanvasController canvas) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Infobox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.canvas = canvas;
        
    }
    
    /**
     * Initialiser
     */
    public void initialize() {
    }
    
    public void showDetails(Node node) {
        if (node instanceof CanvasController) {
            
            contents.getChildren().clear();
            InfoboxCycleController ifc = new InfoboxCycleController(canvas);
            contents.getChildren().add(ifc);
            
            /**
            TextField pressureField = new TextField();
            TextField temperatureField = new TextField();
            
            pressureField.setText(String.valueOf(canvas.model.getAmbient(Properties.Property.PRESSURE)));
            temperatureField.setText(String.valueOf(canvas.model.getAmbient(Properties.Property.TEMPERATURE)));
            
            contents.getChildren().add(new Label(Properties.Property.PRESSURE.name()));
            contents.getChildren().add(pressureField);
            contents.getChildren().add(new Label(Properties.Property.TEMPERATURE.name()));
            contents.getChildren().add(temperatureField);
            
            pressureField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        pressureField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            pressureField.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    canvas.model.setAmbient(Double.valueOf(pressureField.getText()),Double.valueOf(temperatureField.getText()));
                }
            });
            temperatureField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        temperatureField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            temperatureField.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    canvas.model.setAmbient(Double.valueOf(pressureField.getText()),Double.valueOf(temperatureField.getText()));
                }
            });
            
            contents.getChildren().add(new ListView<thermocycle.Component>(canvas.model.componentsReadOnly));
            contents.getChildren().add(new ListView<thermocycle.Fluid>(canvas.model.fluidsReadOnly));
            ListView pathsView = new ListView<Set<thermocycle.FlowNode>>(canvas.model.pathsReadOnly);
                        
            contents.getChildren().add(pathsView);
            **/
            
        }
        else if (node instanceof CanvasNodeController) {
            thermocycle.Node n = (((CanvasNodeController) node).node);
            contents.getChildren().clear();
            if (n instanceof HeatNode) {
                InfoboxHeatController ifc = new InfoboxHeatController(canvas);
                ifc.showDetails((HeatNode)n);
                contents.getChildren().add(ifc);
            }
            else if (n instanceof WorkNode) {
                InfoboxWorkController ifc = new InfoboxWorkController(canvas);
                ifc.showDetails((WorkNode)n);
                contents.getChildren().add(ifc);
            }
            else if (n instanceof FlowNode) {
                InfoboxFlowController ifc = new InfoboxFlowController(canvas);
                ifc.showDetails((FlowNode)n);
                contents.getChildren().add(ifc);
            }
        }
        else if (node instanceof CanvasIconController) {
            contents.getChildren().clear();
            InfoboxComponentController iac = new InfoboxComponentController(canvas);
            iac.showDetails(((CanvasIconController) node).component);
            contents.getChildren().add(iac);
        }
        else if (node instanceof CanvasPathController) {
            contents.getChildren().clear();
            thermocycle.Node start = ((CanvasPathController)node).start.node;
            if (start instanceof FlowNode) {
                InfoboxFlowController ifc = new InfoboxFlowController(canvas);
                ifc.showDetails((FlowNode)start);
                contents.getChildren().add(ifc);
            }
            else if (start instanceof WorkNode) {
                InfoboxWorkController ifc = new InfoboxWorkController(canvas);
                ifc.showDetails((WorkNode)start);
                contents.getChildren().add(ifc);
            }
            else if (start instanceof HeatNode) {
                InfoboxHeatController ifc = new InfoboxHeatController(canvas);
                ifc.showDetails((HeatNode)start);
                contents.getChildren().add(ifc);
            }
        }
    }
}