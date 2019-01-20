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
public class BoundaryConditionWork extends BoundaryCondition {

    protected final WorkNode node;
    protected final Double value;

    public BoundaryConditionWork(WorkNode node, Double value) {
        this.node = node;
        this.value = value;
    }

    @Override
    protected void execute() {
        node.setWork(value);
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

    @Override
    public Double getValue() {
        return value;
    }
    
}
