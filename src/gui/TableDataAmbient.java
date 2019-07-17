/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import thermocycle.BoundaryCondition;
import thermocycle.BoundaryConditionAmbient;
import thermocycle.Cycle;
import thermocycle.Property;
import thermocycle.UnitsControl;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TableDataAmbient extends TableData {
    
    /**
     * The boundary condition type.
     */
    private final Property property;
    
    /**
     * Constructor
     * @param type the type of boundary condition.
     */
    public TableDataAmbient(Property property) {
        super();
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
        if (bc instanceof BoundaryConditionAmbient) {
            if (((BoundaryConditionAmbient) bc).property.equals(property)) {
                this.boundaryCondition.setValue(bc);
            }
        }
    }
    
    @Override
    public void createBoundaryCondition(Cycle model, double value) {
        boundaryCondition.setValue(model.setBoundaryConditionAmbient(property, Arrays.asList(new Double[] {value})));
    }
    
}
