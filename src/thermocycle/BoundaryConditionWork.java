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
public class BoundaryConditionWork extends BoundaryCondition {

    public final WorkNode node;
    
    public BoundaryConditionWork(WorkNode node, List<Double> values) {
        super(values);
        this.node = node;
    }

    @Override
    protected void execute() {
        node.setWork(getValue());
    }
    
    @Override
    public String getName() {
        return "Work";
    }
    
    @Override
    public UnitsType getUnitsType(){
        return UnitsType.POWER;
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionWork) {
            if (this.node == ((BoundaryConditionWork)cnd).node) {
                return true;
            }
        }
        return false;
    }
    
}
