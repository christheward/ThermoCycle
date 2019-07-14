/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import thermocycle.BoundaryCondition;
import thermocycle.BoundaryConditionHeat;
import thermocycle.Cycle;
import thermocycle.HeatNode;
import thermocycle.UnitsControl.UnitsSystem;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class TableDataHeat extends TableData {
    
    /**
     * The heat node
     */
    private final HeatNode node;
    
    /**
     * Constructor
     * @param type the type of boundary condition.
     */
    public TableDataHeat(HeatNode node) {
        super();
        this.node = node;
        this.name.setValue("Heat");
        this.unitsType.setValue(UnitsType.POWER);
        this.units.setValue(UnitsType.POWER.getUnits(UnitsSystem.SI));
    }
    
    /**
     * Sets the boundary condition.
     * @param boundary the boundary condition.
     */
    @Override
    public void setBoundaryCondition(BoundaryCondition bc) {
        if (bc instanceof BoundaryConditionHeat) {
            if ((((BoundaryConditionHeat) bc).node).equals(node)) {
                boundaryCondition.setValue(bc);
            }
        }
    }

    @Override
    public void createBoundaryCondition(Cycle model, double value) {
        boundaryCondition.setValue(model.setBoundaryConditionHeat(node, Arrays.asList(new Double[] {value})));
        //BoundaryConditionHeat bc = new BoundaryConditionHeat(node, Arrays.asList(new Double[] {value}));
        //setBoundaryCondition(bc);
    }
    
}
