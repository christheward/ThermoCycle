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
    
    private final FlowNode node;
    private final Property property;
    private final Double value;

    public BoundaryConditionProperty(FlowNode node, Property property, Double value) {
        this.node = node;
        this.property = property;
        this.value = value;
    }

    @Override
    public void execute() {
        node.setProperty(property, value);
    }

    @Override
    public boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionProperty) {
            if (this.node == ((BoundaryConditionProperty) cnd).node) {
                if (this.property == ((BoundaryConditionProperty) cnd).property)
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
