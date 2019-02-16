/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionHeat extends BoundaryCondition {
    
    public final HeatNode node;
    
    public BoundaryConditionHeat(HeatNode node, double[] values) {
        super(values);
        this.node = node;
    }
    
    @Override
    protected void execute() {
        node.setHeat(values[idx]);
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionHeat) {
            if (this.node == ((BoundaryConditionHeat)cnd).node) {
                return true;
            }
        }
        return false;
    }
        
}
