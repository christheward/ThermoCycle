/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris Ward
 */
public class BoundaryConditionAttribute extends BoundaryCondition {
    
    public final Component component;
    public final Attribute attribute;
    
    public BoundaryConditionAttribute(Component component, Attribute attribute, List<Double> values) {
        super(values);
        this.component = component;
        this.attribute = attribute;
    }
    
    @Override
    protected void execute() {
        component.setAttribute(attribute, getValue());
    }
    
    @Override
    public String getName() {
        return attribute.name;
    }
    
    @Override
    public UnitsType getUnitsType(){
        return attribute.type;
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionAttribute) {
            if (this.component.equals(((BoundaryConditionAttribute) cnd).component)) {
                if (this.attribute.equals(((BoundaryConditionAttribute) cnd).attribute)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
