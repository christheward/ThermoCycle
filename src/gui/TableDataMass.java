/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import thermocycle.BoundaryCondition;
import thermocycle.BoundaryConditionMass;
import thermocycle.Cycle;
import thermocycle.FlowNode;
import thermocycle.UnitsControl;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TableDataMass extends TableData {
    
    /**
     * The flow node
     */
    private final FlowNode node;
    
    /**
     * Constructor
     * @param type the type of boundary condition.
     */
    public TableDataMass(FlowNode node) {
        super();
        this.node = node;
        this.name.setValue("Mass");
        this.unitsType.setValue(UnitsType.FLOW_RATE);
        this.units.setValue(UnitsType.FLOW_RATE.getUnits(UnitsControl.UnitsSystem.SI));
    }
    
    /**
     * Sets the boundary condition.
     * @param boundary the boundary condition.
     */
    @Override
    public void setBoundaryCondition(BoundaryCondition bc) {
        if (bc instanceof BoundaryConditionMass) {
            if ((((BoundaryConditionMass) bc).node).equals(node)) {
                boundaryCondition.setValue(bc);
            }
        }
    }
    
    @Override
    public void createBoundaryCondition(Cycle model, double value) {
        boundaryCondition.setValue(model.setBoundaryConditionMass(node, Arrays.asList(new Double[] {value})));
    }
    
}
