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
public class BoundaryConditionFlow extends BoundaryCondition {
    
    protected final FlowNode node;
    protected final Double value;
    
    public BoundaryConditionFlow(FlowNode node, Double value) {
        this.node = node;
        this.value = value;
    }
    
    @Override
    protected void execute() {
        node.setMass(value);
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionFlow) {
            if (this.node == ((BoundaryConditionFlow)cnd).node) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Double getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BoundaryConditionFlow)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return ((BoundaryConditionFlow)obj).node.equals(this.node);
    }
    
}
