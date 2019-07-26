/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import thermocycle.BoundaryCondition;
import thermocycle.Cycle;
import thermocycle.UnitsControl.Units;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public abstract class TableData {
    
    /**
     * The boundary condition object wrapper.
     */
    protected final ReadOnlyObjectWrapper<BoundaryCondition> boundaryCondition;
    
    /**
     * The boundary condition name wrapper.
     */
    protected final ReadOnlyStringWrapper name;
    
    /**
     * The boundary condition value wrapper.
     */
    protected final ReadOnlyObjectWrapper<Number> value;
    
    /**
     * The boundary condition units type wrapper.
     */
    protected final ReadOnlyObjectWrapper<UnitsType> unitsType;
    
    /**
     * The boudnary condition units wrapper.
     */
    protected final ReadOnlyObjectWrapper<Units> units;
    
    /**
     * Constructor
     */
    public TableData(){
        boundaryCondition = new ReadOnlyObjectWrapper();
        name = new ReadOnlyStringWrapper();
        value = new ReadOnlyObjectWrapper();
        unitsType = new ReadOnlyObjectWrapper();
        units = new ReadOnlyObjectWrapper();
        
        // Setup binding
        value.bind(new ObjectBinding<Number>() {
            {
                bind(boundaryCondition, units);
            }
            @Override
            protected Number computeValue() {
                if (boundaryCondition.isNotNull().getValue()){
                    if (units.isNotNull().getValue()) {
                        return units.getValue().fromSI(boundaryCondition.getValue().getValue());
                    }
                }
                return null;
            }
        });
        
    }
    
    /**
     * Clears the boundary condition.
     */
    public void clearBoundaryCondition() {
        boundaryCondition.setValue(null);
    }
    
    /**
     * Sets the boundary condition.
     * @param boundary the boundary condition.
     */
    public abstract void setBoundaryCondition(BoundaryCondition bc);
    
    /**
     * Creates a boundary condition in this model with this value.
     * @param model the model to create a boundary condition in.
     * @param value the value to assign to the boundary condition.
     */
    public abstract void createBoundaryCondition(Cycle model, double value);
    
    /**
     * Get a read only boundary condition property.
     * @return a read only boundary condition property.
     */
    public ReadOnlyObjectProperty<BoundaryCondition> boundaryConditionProperty() {
        return boundaryCondition.getReadOnlyProperty();
    }
    
    /**
     * Get a read only name property.
     * @return a read only name property.
     */
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }
    
    /**
     * Get a read only value property.
     * @return a read only value property.
     */
    public ReadOnlyObjectProperty<Number> valueProperty() {
        return value.getReadOnlyProperty();
    }
    
    /**
     * Get a read only units type property.
     * @return a read only units type property.
     */
    public ReadOnlyObjectProperty<UnitsType> unitsTypeProperty() {
        return unitsType.getReadOnlyProperty();
    }
    
    /**
     * Get a read only units property.
     * @return a read only units property.
     */
    public ReadOnlyObjectProperty<Units> unitsProperty() {
        return units.getReadOnlyProperty();
    }
    
}
