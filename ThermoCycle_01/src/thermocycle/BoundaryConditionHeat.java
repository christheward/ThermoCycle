/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.OptionalDouble;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionHeat extends BoundaryCondition {

    protected final HeatNode node;
    protected final OptionalDouble value;

    public BoundaryConditionHeat(HeatNode node, OptionalDouble value) {
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
    public OptionalDouble value() {
        return value;
    }
    
}
