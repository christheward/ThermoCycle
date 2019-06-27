/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import thermocycle.FlowNode;
import thermocycle.HeatNode;
import thermocycle.WorkNode;

/**
 *
 * @author Chris
 */
public class InfoboxBaseController extends StackPane {
    
    // FXML variables
    
    // GUI variables
    private final MasterSceneController master;
    private final InfoboxCycleController infoCycle;
    private final InfoboxHeatController infoHeat;
    private final InfoboxWorkController infoWork;
    private final InfoboxFlowController infoFlow;
    private final InfoboxComponentController infoComponent;
    
    /**
     * Constructor.
     * @param master The master scene
     */
    public InfoboxBaseController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Create infobox elements
        infoCycle = new InfoboxCycleController(master);
        infoHeat = new InfoboxHeatController(master);
        infoWork = new InfoboxWorkController(master);
        infoFlow = new InfoboxFlowController(master);
        infoComponent = new InfoboxComponentController(master);
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/InfoboxBase.fxml"));
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
        
        // Create cycle infobox panel
        this.getChildren().addAll(infoCycle, infoHeat, infoWork, infoFlow, infoComponent);
        
        //Bindings.when(master.focusedProperty().)
        infoCycle.visibleProperty().bind(new BooleanBinding() {
            {
                bind(master.focusProperty());
            }
            @Override
            protected boolean computeValue() {
                return Bindings.when(master.focusProperty().isNotNull()).then(master.getFocus() instanceof CanvasController).otherwise(false).getValue();
            }
        });
        infoComponent.visibleProperty().bind(new BooleanBinding() {
            {
                bind(master.focusProperty());
            }
            @Override
            protected boolean computeValue() {
                return Bindings.when(master.focusProperty().isNotNull()).then(master.getFocus() instanceof ComponentController).otherwise(false).getValue();
            }
        });
        infoHeat.visibleProperty().bind(new BooleanBinding() {
            {
                bind(master.focusProperty());
            }
            @Override
            protected boolean computeValue() {
                if (master.focusProperty().isNotNull().getValue()) {
                    if (master.getFocus() instanceof NodeController) {
                        return (((NodeController) master.getFocus()).node instanceof HeatNode);
                    }
                }
                return false;
            }
        });
        infoWork.visibleProperty().bind(new BooleanBinding() {
            {
                bind(master.focusProperty());
            }
            @Override
            protected boolean computeValue() {
                if (master.focusProperty().isNotNull().getValue()) {
                    if (master.getFocus() instanceof NodeController) {
                        return (((NodeController) master.getFocus()).node instanceof WorkNode);
                    }
                }
                return false;
            }
        });
        infoFlow.visibleProperty().bind(new BooleanBinding() {
            {
                bind(master.focusProperty());
            }
            @Override
            protected boolean computeValue() {
                if (master.focusProperty().isNotNull().getValue()) {
                    if (master.getFocus() instanceof NodeController) {
                        return (((NodeController) master.getFocus()).node instanceof FlowNode);
                    }
                }
                return false;
            }
        });
    }
    
}
