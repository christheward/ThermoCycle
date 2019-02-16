/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import thermocycle.Attributes.Attribute;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionAttribute extends BoundaryCondition {
    
    public final Component component;
    public final Attribute attribute;
    
    public BoundaryConditionAttribute(Component component, Attribute attribute, double[] values) {
        super(values);
        this.component = component;
        this.attribute = attribute;
    }
    
    @Override
    protected void execute() {
        component.setAttribute(attribute, values[idx]);
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionAttribute) {
            if (this.component == (((BoundaryConditionAttribute) cnd).component)) {
                if (this.attribute == (((BoundaryConditionAttribute) cnd).attribute)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
