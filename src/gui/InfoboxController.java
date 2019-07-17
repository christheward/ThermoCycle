/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import thermocycle.BoundaryConditionAmbient;
import thermocycle.BoundaryConditionAttribute;
import thermocycle.BoundaryConditionHeat;
import thermocycle.BoundaryConditionMass;
import thermocycle.BoundaryConditionProperty;
import thermocycle.BoundaryConditionWork;
import thermocycle.Component;
import thermocycle.Fluid;
import thermocycle.Property;

/**
 *
 * @author Chris
 */
public class InfoboxController extends AnchorPane {
    
    // FXML variables
    @FXML private VBox infoContainer;
    
    // GUI variables
    private final MasterSceneController master;
    
    /**
     * Constructor.
     * @param master The master scene
     */
    public InfoboxController(MasterSceneController master) {
        
        // Set master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Infobox.fxml"));
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
        
        // Define comparator for sorting alphabetically
        Comparator<Object> byName = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};
        
        // Bind disabled property
        disableProperty().bind(master.modelAbsent);
        
        // Bind infobox controller to focus property
        master.focusProperty().addListener(new ChangeListener<Node>() {
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                infoContainer.getChildren().clear();
                
                if (newValue instanceof CanvasController) {
                    // Create new data table
                    DataTableController ambientTable = new DataTableController(master);
                    // Set table title
                    ambientTable.setTitle("Ambient Conditions");
                    // Add data to table
                    List<Property> ambientProperties = new ArrayList();
                    ambientProperties.add(Fluid.PRESSURE);
                    ambientProperties.add(Fluid.TEMPERATURE);
                    ambientProperties.stream().forEach(p -> {
                        ambientTable.addData(new TableDataAmbient(p));
                        master.getModel().boundaryConditionsReadOnly.stream()
                            .filter(bc -> bc instanceof BoundaryConditionAmbient)
                            .map(bc -> (BoundaryConditionAmbient)bc)
                            .filter(bc -> bc.property.equals(p))
                            .findFirst()
                            .ifPresent(bc -> ambientTable.getLast().ifPresent(dt -> dt.setBoundaryCondition(bc)));
                    });
                    
                    // Add table to infobox
                    infoContainer.getChildren().add(ambientTable);
                    
                    // Create new components list
                    DataListController componentsList = new DataListController(master);
                    // Set list title
                    componentsList.setTitle("Components");
                    componentsList.addData(master.getModel().componentsReadOnly);
                    // Add list to infobox
                    infoContainer.getChildren().add(componentsList);
                    
                    // Creat new connections list
                    DataListController connectionsList = new DataListController(master);
                    // SEt list title
                    connectionsList.setTitle("Connections");
                    connectionsList.addData(master.getModel().connectionsReadOnly);
                    // Add list to infobox
                    infoContainer.getChildren().add(connectionsList);
                    
                    // Creat new boundary conditions list
                    DataListController boundaryConditionsList = new DataListController(master);
                    // SEt list title
                    boundaryConditionsList.setTitle("Boundary Conditions");
                    boundaryConditionsList.addData(master.getModel().boundaryConditionsReadOnly);
                    // Add list to infobox
                    infoContainer.getChildren().add(boundaryConditionsList);
                    
                }
                else if (newValue instanceof ComponentController) {
                    // Get compoennt
                    Component component = ((ComponentController) newValue).component;
                    // Create fluid selector
                    FluidSelectorController fluidSelector = new FluidSelectorController(master);
                    fluidSelector.setTitle("Set fluid:");
                    component.flowNodes.keySet().stream().findFirst().map(n -> component.flowNodes.get(n)).ifPresent(n -> {
                        master.getModel().getFluid(n).ifPresent(f -> {
                            fluidSelector.selectFluid(f);
                        });
                        fluidSelector.selectedProperty().addListener(new ChangeListener<Fluid>() {
                            @Override
                            public void changed(ObservableValue<? extends Fluid> observable, Fluid oldValue, Fluid newValue) {
                                if (fluidSelector.selectedProperty().isNotNull().getValue()) {
                                    master.getModel().setFluid(n, newValue);
                                    // Re make inbobox.
                                    InfoboxController.this.layout();
                                }
                            }
                        });
                    });
                    // Add selector to infobox
                    infoContainer.getChildren().add(fluidSelector);
                    
                    // Create new attribute table
                    DataTableController attributeTable = new DataTableController(master);
                    // Set table title
                    attributeTable.setTitle("Attributes");
                    // Add data to table
                    component.getAllowableAtributes().stream().sorted(byName).forEach(a -> {
                        // Add attribute to table
                        attributeTable.addData(new TableDataAttribute(component, a));
                        // If boundary condition already exists, link this to the table.
                        master.getModel().boundaryConditionsReadOnly.stream()
                                .filter(bc -> bc instanceof BoundaryConditionAttribute)
                                .map(bc -> (BoundaryConditionAttribute)bc)
                                .filter(bc -> bc.component.equals(((ComponentController) newValue).component))
                                .filter(bc -> bc.attribute.equals(a))
                                .findFirst()
                                .ifPresent(bc -> attributeTable.getLast().ifPresent(dt -> dt.setBoundaryCondition(bc)));
                    });
                    // Add table to infoBox
                    infoContainer.getChildren().add(attributeTable);
                    
                    // Add flow nodes
                    component.flowNodes.keySet().stream().forEach(n -> {
                        DataTableController flowTable = new DataTableController(master);
                        flowTable.setTitle(n);
                        // Add mass flow to table
                        flowTable.addData(new TableDataMass(component.flowNodes.get(n)));
                        // If boundary condition already exists, link this to the table.
                        master.getModel().boundaryConditionsReadOnly.stream()
                                .filter(bc -> bc instanceof BoundaryConditionMass)
                                .map(bc -> (BoundaryConditionMass)bc)
                                .filter(bc -> bc.node.equals(component.flowNodes.get(n)))
                                .findFirst()
                                .ifPresent(bc -> flowTable.getLast().ifPresent(dt -> dt.setBoundaryCondition(bc)));
                        // Add properties
                        component.flowNodes.get(n).getAllowableProperties().stream().forEach(p -> {
                            // Add property to table
                            flowTable.addData(new TableDataProperty(component.flowNodes.get(n), p));
                            // If boudnary condition already exists, link this to the table.
                            master.getModel().boundaryConditionsReadOnly.stream()
                                    .filter(bc -> bc instanceof BoundaryConditionProperty)
                                    .map(bc -> (BoundaryConditionProperty)bc)
                                    .filter(bc -> bc.node.equals(component.flowNodes.get(n)))
                                    .filter(bc -> bc.property.equals(p))
                                    .findFirst()
                                    .ifPresent(bc -> flowTable.getLast().ifPresent(dt -> dt.setBoundaryCondition(bc)));
                        });
                        // Link flow table to fluid selection
                        flowTable.disableProperty().bind(fluidSelector.selectedProperty().isNull());
                        // Add table to infoBox
                        infoContainer.getChildren().add(flowTable);
                    });
                    
                    // Add work nodes
                    component.workNodes.keySet().stream().forEach(n -> {
                        DataTableController workTable = new DataTableController(master);
                        workTable.setTitle(n);
                        workTable.addData(new TableDataWork(component.workNodes.get(n)));
                        // If boundary condition already exists, link this to the table.
                        master.getModel().boundaryConditionsReadOnly.stream()
                                .filter(bc -> bc instanceof BoundaryConditionWork)
                                .map(bc -> (BoundaryConditionWork)bc)
                                .filter(bc -> bc.node.equals(component.workNodes.get(n)))
                                .findFirst()
                                .ifPresent(bc -> workTable.getLast().ifPresent(dt -> dt.setBoundaryCondition(bc)));
                        // Add table to infoBox
                        infoContainer.getChildren().add(workTable);
                    });
                    
                    // Add heat nodes
                    component.heatNodes.keySet().stream().forEach(n -> {
                        DataTableController heatTable = new DataTableController(master);
                        heatTable.setTitle(n);
                        heatTable.addData(new TableDataHeat(component.heatNodes.get(n)));
                        // If boundary condition already exists, link this to the table.
                        master.getModel().boundaryConditionsReadOnly.stream()
                                .filter(bc -> bc instanceof BoundaryConditionHeat)
                                .map(bc -> (BoundaryConditionHeat)bc)
                                .filter(bc -> bc.node.equals(component.heatNodes.get(n)))
                                .findFirst()
                                .ifPresent(bc -> heatTable.getLast().ifPresent(dt -> dt.setBoundaryCondition(bc)));
                        // Add table to infoBox
                        infoContainer.getChildren().add(heatTable);
                    });
                }
            }
        });
    }
}
