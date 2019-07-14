/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import thermocycle.BoundaryCondition;
import thermocycle.BoundaryConditionProperty;
import thermocycle.Cycle;
import thermocycle.FlowNode;
import thermocycle.Property;
import thermocycle.UnitsControl;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TableDataProperty extends TableData {
    
    /**
     * The flow node
     */
    private final FlowNode node;
    
    /**
     * The boundary condition type.
     */
    private final Property property;
    
    /**
     * Constructor
     * @param type the type of boundary condition.
     */
    public TableDataProperty(FlowNode node, Property property) {
        super();
        this.node = node;
        this.property = property;
        this.name.setValue(property.name);
        this.unitsType.setValue(property.type);
        this.units.setValue(property.type.getUnits(UnitsControl.UnitsSystem.SI));
    }
    
    /**
     * Sets the boundary condition.
     * @param boundary the boundary condition.
     */
    @Override
    public void setBoundaryCondition(BoundaryCondition bc) {
        if (bc instanceof BoundaryConditionProperty) {
            if ((((BoundaryConditionProperty) bc).node).equals(node)) {
                if (((BoundaryConditionProperty) bc).property.equals(property)) {
                    this.boundaryCondition.setValue(bc);
                }
            }
        }
    }
    
    @Override
    public void createBoundaryCondition(Cycle model, double value) {
        boundaryCondition.setValue(model.setBoundaryConditionProperty(node, property, Arrays.asList(new Double[] {value})));
        //BoundaryConditionProperty bc = new BoundaryConditionProperty(node, property, Arrays.asList(new Double[] {value}));
        //setBoundaryCondition(bc);
    }
    
}
