/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionHeat extends BoundaryCondition {
    
    public final HeatNode node;
    
    public BoundaryConditionHeat(HeatNode node, List<Double> values) {
        super(values);
        this.node = node;
    }
    
    @Override
    protected void execute() {
        node.setHeat(getValue());
    }
    
    @Override
    public String getName() {
        return "Heat";
    }
    
    @Override
    public UnitsType getUnitsType(){
        return UnitsType.POWER;
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionHeat) {
            if (this.node.equals(((BoundaryConditionHeat)cnd).node)) {
                return true;
            }
        }
        return false;
    }
    
}
