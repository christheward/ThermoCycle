/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import thermocycle.BoundaryConditionProperty;
import thermocycle.Properties.Property;
import utilities.Units;
import utilities.Units.UNITS_SYSTEM;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class BoundaryProperty {
    
    private final ReadOnlyObjectWrapper<BoundaryConditionProperty> boundaryCondition;
    private final ReadOnlyBooleanWrapper present;
    private final ReadOnlyObjectWrapper<Property> property;
    private final ReadOnlyDoubleWrapper value;
    private final ReadOnlyStringWrapper units;
    
    public BoundaryProperty(Property p) {
        this.boundaryCondition = new ReadOnlyObjectWrapper();
        this.present = new ReadOnlyBooleanWrapper();
        this.property = new ReadOnlyObjectWrapper(p);
        this.value = new ReadOnlyDoubleWrapper();
        this.units = new ReadOnlyStringWrapper();
        
        // Setup binding
        this.present.bind(boundaryCondition.isNotNull());
        this.value.bind(new DoubleBinding() {
            {
                bind(boundaryCondition);
            }
            @Override
            protected double computeValue() {
                return boundaryCondition.isNotNull().getValue() ? boundaryCondition.getValue().getValue() : 0.0;
            }
        });
        this.units.bind(new StringBinding() {
            {
                bind(property);
            }
            @Override
            protected String computeValue() {
                return property.isNotNull().getValue() ? property.getValue().type.getUnits(UNITS_SYSTEM.SI).toString() : "";
            }
        });

    }
    
    public void setBoundaryCondition(BoundaryConditionProperty boundary) {
        if (boundary.property == property.getValue()) {
            boundaryCondition.setValue(boundary);
        }
    }
    
    public void clearBoundaryCondition() {
        if (boundaryCondition.isNotNull().getValue()) {
            boundaryCondition.setValue(null);
        }
    }
    
    public ReadOnlyObjectProperty<BoundaryConditionProperty> boundaryProperty() {
        return boundaryCondition.getReadOnlyProperty();
    }
    
    public ReadOnlyBooleanProperty presentProperty() {
        return present.getReadOnlyProperty();
    }
    
    public ReadOnlyObjectProperty<Property> propertyProperty() {
        return property.getReadOnlyProperty();
    }
    
    public ReadOnlyDoubleProperty valueProperty() {
        return value.getReadOnlyProperty();
    }
    
    public ReadOnlyStringProperty unitsProperty() {
        return units.getReadOnlyProperty();
    }
    
}
