/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import thermocycle.BoundaryConditionMass;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class BoundaryMass {
    
    private final ReadOnlyObjectWrapper<BoundaryConditionMass> boundaryCondition;
    private final ReadOnlyStringWrapper mass;
    private final ReadOnlyBooleanWrapper present;
    private final ReadOnlyDoubleWrapper value;
    private final ReadOnlyStringWrapper units;
    
    public BoundaryMass() {
        this.boundaryCondition = new ReadOnlyObjectWrapper();
        this.mass = new ReadOnlyStringWrapper();
        this.present = new ReadOnlyBooleanWrapper();
        this.value = new ReadOnlyDoubleWrapper();
        this.units = new ReadOnlyStringWrapper();
        
        // Setup bindings
        this.present.bind(boundaryCondition.isNotNull());
        this.mass.setValue("Mass");
        this.value.bind(new DoubleBinding() {
            {
                bind(boundaryCondition);
            }
            @Override
            protected double computeValue() {
                System.out.println(present.getValue());
                return boundaryCondition.isNotNull().getValue() ? boundaryCondition.getValue().getValue() : 0.0;
            }
        });
        this.units.setValue("kg/s");
    }
    
    public void setBoundaryCondition(BoundaryConditionMass boundary) {
        boundaryCondition.setValue(boundary);
    }
    
    public void clearBoundaryCondition() {
        if (boundaryCondition.isNotNull().getValue()) {
            boundaryCondition.setValue(null);
        }
    }
    
    public ReadOnlyObjectProperty<BoundaryConditionMass> boundaryProperty() {
        return boundaryCondition.getReadOnlyProperty();
    }
    
    public ReadOnlyBooleanProperty presentProperty() {
        return present.getReadOnlyProperty();
    }
    
    public ReadOnlyStringProperty massProperty() {
        return mass.getReadOnlyProperty();
    }
    
    public ReadOnlyDoubleProperty valueProperty() {
        return value.getReadOnlyProperty();
    }
    
    public ReadOnlyStringProperty unitsProperty() {
        return units.getReadOnlyProperty();
    }
    
}
