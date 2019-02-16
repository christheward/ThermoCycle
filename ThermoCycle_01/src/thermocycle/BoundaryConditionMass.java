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
    
    public final FlowNode node;
    
    public BoundaryConditionMass(FlowNode node, double[] values) {
        super(values);
        this.node = node;
    }
    
    @Override
    protected void execute() {
        node.setMass(values[idx]);
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionMass) {
            if (this.node == ((BoundaryConditionMass)cnd).node) {
                return true;
            }
        }
        return false;
    }
    
    /**
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BoundaryConditionMass)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return ((BoundaryConditionMass)obj).node.equals(this.node);
    }
    */
}
