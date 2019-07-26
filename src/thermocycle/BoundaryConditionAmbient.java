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
public class BoundaryConditionAmbient extends BoundaryCondition {
    
    public final State ambient;
    public final Property property;
    
    public BoundaryConditionAmbient(State ambient, Property property, List<Double> values) {
        super(values);
        this.ambient = ambient;
        this.property = property;
    }
    
    @Override
    protected void execute() {
        ambient.setProperty(property, getValue());
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
        if (cnd instanceof BoundaryConditionAmbient) {
            if (this.ambient.equals(((BoundaryConditionAmbient) cnd).ambient)) {
                if (this.property.equals(((BoundaryConditionAmbient) cnd).property))
                    return true;
            }
        }
        return false;
    }
    
}
