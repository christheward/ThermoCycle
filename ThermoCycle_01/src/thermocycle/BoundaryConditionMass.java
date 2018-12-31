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
public class BoundaryConditionMass extends BoundaryCondition {

    protected final FlowNode node;
    protected final Double value;

    public BoundaryConditionMass(FlowNode node, Double value) {
        this.node = node;
        this.value = value;
    }

    @Override
    public void execute() {
        node.setMass(value);
    }

    @Override
    public boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionMass) {
            if (this.node == ((BoundaryConditionMass)cnd).node) {
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
