/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import thermocycle.BoundaryCondition;
import thermocycle.BoundaryConditionWork;
import thermocycle.Cycle;
import thermocycle.UnitsControl;
import thermocycle.UnitsControl.UnitsType;
import thermocycle.WorkNode;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TableDataWork extends TableData {
    
    /**
     * The work node
     */
    private final WorkNode node;
    
    /**
     * Constructor
     * @param type the type of boundary condition.
     */
    public TableDataWork(WorkNode node) {
        super();
        this.node = node;
        this.name.setValue("Work");
        this.unitsType.setValue(UnitsType.POWER);
        this.units.setValue(UnitsType.POWER.getUnits(UnitsControl.UnitsSystem.SI));
    }
    
    /**
     * Sets the boundary condition.
     * @param boundary the boundary condition.
     */
    @Override
    public void setBoundaryCondition(BoundaryCondition bc) {
        if (bc instanceof BoundaryConditionWork) {
            if ((((BoundaryConditionWork) bc).node).equals(node)) {
                this.boundaryCondition.setValue(bc);
            }
        }
    }
    
    @Override
    public void createBoundaryCondition(Cycle model, double value) {
        boundaryCondition.setValue(model.setBoundaryConditionWork(node, Arrays.asList(new Double[] {value})));
        //BoundaryConditionWork bc = new BoundaryConditionWork(node, Arrays.asList(new Double[] {value}));
        //setBoundaryCondition(bc);
    }
    
}
