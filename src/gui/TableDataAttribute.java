/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import thermocycle.Attribute;
import thermocycle.BoundaryCondition;
import thermocycle.BoundaryConditionAttribute;
import thermocycle.Component;
import thermocycle.Cycle;
import thermocycle.UnitsControl.UnitsSystem;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TableDataAttribute extends TableData {
    
    /**
     * The boundary condition type.
     */
    private final Component component;
    private final Attribute attribute;
    
    /**
     * Constructor
     * @param type the type of boundary condition.
     */
    public TableDataAttribute(Component component, Attribute attribute) {
        super();
        this.component = component;
        this.attribute = attribute;
        this.name.setValue(attribute.name);
        this.unitsType.setValue(attribute.type);
        this.units.setValue(attribute.type.getUnits(UnitsSystem.SI));
    }
    
    /**
     * Sets the boundary condition.
     * @param boundary the boundary condition.
     */
    @Override
    public void setBoundaryCondition(BoundaryCondition bc) {
        if (bc instanceof BoundaryConditionAttribute) {
            if (((BoundaryConditionAttribute) bc).component.equals(component)) {
                if (((BoundaryConditionAttribute) bc).attribute.equals(attribute)) {
                    boundaryCondition.setValue(bc);
                }
            }
        }
    }

    @Override
    public void createBoundaryCondition(Cycle model, double value) {
        System.out.println("Creating Attribute BC");
        boundaryCondition.setValue(model.setBoundaryConditionAttribute(component, attribute, Arrays.asList(new Double[] {value})));
        //BoundaryConditionAttribute bc = new BoundaryConditionAttribute(component, attribute, Arrays.asList(new Double[] {value}));
        //setBoundaryCondition(bc);
    }
    
}
