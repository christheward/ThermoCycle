/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import thermocycle.Cycle;
import thermocycle.FlowNode;
import thermocycle.Fluid;

/**
 *
 * @author Chris
 */
public class FluidSelectorController extends AnchorPane {
    
    // FXML variables
    @FXML private Label selectorLabel;
    @FXML private ChoiceBox<Fluid> fluidSelector;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor.
     * @param master The master scene
     */
    public FluidSelectorController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/FluidSelector.fxml"));
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
        // Link fluid selector to model fluids
        fluidSelector.itemsProperty().setValue(master.getModel().fluidsReadOnly);
    }
    
    /**
     * Clears the selection in the fluid selector.
     */
    public void clear() {
        fluidSelector.selectionModelProperty().getValue().clearSelection();
    }
    
    /**
     * Selects the fluid in the selector.
     * @param fluid the fluid to select.
     */
    public void selectFluid(Fluid fluid) {
        fluidSelector.selectionModelProperty().getValue().select(fluid);
    }
    
    /**
     * Sets the title for the fluid selector.
     * @param tirle 
     */
    public void setTitle(String tirle) {
        selectorLabel.setText(tirle);
    }
    
    /**
     * Get the currently selected fluid property.
     * @return the currently selected fluid property.
     */
    public ReadOnlyObjectProperty<Fluid> selectedProperty() {
        return fluidSelector.selectionModelProperty().getValue().selectedItemProperty();
    }
    
}
