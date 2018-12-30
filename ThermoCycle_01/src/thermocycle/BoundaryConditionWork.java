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
public class BoundaryConditionWork extends BoundaryCondition {

    protected final WorkNode node;
    protected final OptionalDouble value;

    public BoundaryConditionWork(WorkNode node, OptionalDouble value) {
        this.node = node;
        this.value = value;
    }

    @Override
    public void execute() {
        node.setWork(value);
    }

    @Override
    public boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionWork) {
            if (this.node == ((BoundaryConditionWork)cnd).node) {
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
