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
public class BoundaryConditionProperty extends BoundaryCondition {
    
    public final FlowNode node;
    public final Property property;
    
    public BoundaryConditionProperty(FlowNode node, Property property, List<Double> values) {
        super(values);
        this.node = node;
        this.property = property;
    }
    
    @Override
    protected void execute() {
        node.setProperty(property, getValue());
    }
    
    @Override
    public String getName() {
        return property.name;
    }
    
    @Override
    public UnitsType getUnitsType(){
        return property.type;
    }
    
    @Override
    protected boolean match(BoundaryCondition cnd) {
        if (cnd instanceof BoundaryConditionProperty) {
            if (this.node.equals(((BoundaryConditionProperty) cnd).node)) {
                if (this.property.equals(((BoundaryConditionProperty) cnd).property)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
