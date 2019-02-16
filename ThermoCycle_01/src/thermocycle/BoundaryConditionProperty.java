/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import thermocycle.Properties.Property;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionProperty extends BoundaryCondition {
    
    public final FlowNode node;
    public final Property property;
    
    public BoundaryConditionProperty(FlowNode node, Property property, double[] values) {
        super(values);
        this.node = node;
        this.property = property;
    }
    
    @Override
    protected void execute() {
        node.setProperty(property, values[idx]);
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionProperty) {
            if (this.node == ((BoundaryConditionProperty) cnd).node) {
                if (this.property == ((BoundaryConditionProperty) cnd).property)
                    return true;
            }
        }
        return false;
    }
    
}
