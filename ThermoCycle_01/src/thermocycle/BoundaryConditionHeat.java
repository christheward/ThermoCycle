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

    protected final HeatNode node;
    protected final Double value;

    public BoundaryConditionHeat(HeatNode node, Double value) {
        this.node = node;
        this.value = value;
    }

    @Override
    public void execute() {
        node.setHeat(value);
    }

    @Override
    public boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionHeat) {
            if (this.node == ((BoundaryConditionHeat)cnd).node) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Double value() {
        return value;
    }
    
}
