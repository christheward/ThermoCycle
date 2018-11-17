/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoboxBase.fxml"));
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
        hideAll();
        
    }
    
    /**
     * Hides all the children on the infobox.
     */
    private final void hideAll() {
        this.getChildren().stream().forEach(c -> {
            c.setVisible(false);
        });
    }
    
    /**
     * Shows details for the GUI node selected.
     * @param node The GUI node to show details for.
     */
    public void showDetails(Node node) {
        hideAll();
        
        if (node instanceof CanvasController) {
            infoCycle.setVisible(master.isModel());
        }
        else if (node instanceof CanvasNodeController) {
            thermocycle.Node n = (((CanvasNodeController) node).node);
            if (n instanceof HeatNode) {
                infoHeat.showDetails((HeatNode)n);
                infoHeat.setVisible(true);
            }
            else if (n instanceof WorkNode) {
                infoWork.showDetails((WorkNode)n);
                infoWork.setVisible(true);
            }
            else if (n instanceof FlowNode) {
                infoFlow.showDetails((FlowNode)n);
                infoFlow.setVisible(true);
            }
        }
        else if (node instanceof CanvasComponentController) {
            infoComponent.showDetails(((CanvasComponentController) node).component);
            infoComponent.setVisible(true);
        }
        else if (node instanceof CanvasPathController) {
            thermocycle.Node n = ((CanvasPathController)node).start.node;
            if (n instanceof HeatNode) {
                infoHeat.showDetails((HeatNode)n);
                infoHeat.setVisible(true);
            }
            else if (n instanceof WorkNode) {
                infoWork.showDetails((WorkNode)n);
                infoWork.setVisible(true);
            }
            else if (n instanceof FlowNode) {
                infoFlow.showDetails((FlowNode)n);
                infoFlow.setVisible(true);
            }
        }
    }
    
    /**
     * Connects the infobox to the underlying model.
     */
    protected void connectNewModel() {
        infoCycle.connectNewModel();
    }
    
}
