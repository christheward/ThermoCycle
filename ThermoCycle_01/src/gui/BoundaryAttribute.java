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
import thermocycle.Attributes.Attribute;
import thermocycle.BoundaryConditionAttribute;
import utilities.Units;
import utilities.Units.UNITS_SYSTEM;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class BoundaryAttribute {
    
    private final ReadOnlyObjectWrapper<BoundaryConditionAttribute> boundaryCondition;
    private final ReadOnlyBooleanWrapper present;
    private final ReadOnlyObjectWrapper<Attribute> attribute;
    private final ReadOnlyDoubleWrapper value;
    private final ReadOnlyStringWrapper units;
    
    public BoundaryAttribute(Attribute a) {
        this.boundaryCondition = new ReadOnlyObjectWrapper();
        this.present = new ReadOnlyBooleanWrapper();
        this.attribute = new ReadOnlyObjectWrapper(a);
        this.value = new ReadOnlyDoubleWrapper();
        this.units = new ReadOnlyStringWrapper();
        
        // Setup bindings
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
                bind(attribute);
            }
            @Override
            protected String computeValue() {
                return attribute.isNotNull().getValue() ? attribute.getValue().type.getUnits(UNITS_SYSTEM.SI).toString() : "";
            }
        });
        
    }
    
    public void setBoundaryCondition(BoundaryConditionAttribute boundary) {
        if (boundary.attribute == attribute.getValue()) {
            boundaryCondition.setValue(boundary);
        }
    }
    
    public void clearBoundaryCondition() {
        if (boundaryCondition.isNotNull().getValue()) {
            boundaryCondition.setValue(null);
        }
    }
    
    public ReadOnlyObjectProperty<BoundaryConditionAttribute> boundaryProperty() {
        return boundaryCondition.getReadOnlyProperty();
    }
    
    public ReadOnlyBooleanProperty presentProperty() {
        return present.getReadOnlyProperty();
    }
    
    public ReadOnlyObjectProperty<Attribute> attributeProperty() {
        return attribute.getReadOnlyProperty();
    }
    
    public ReadOnlyDoubleProperty valueProperty() {
        return value.getReadOnlyProperty();
    }
    
    public ReadOnlyStringProperty unitsProperty() {
        return units.getReadOnlyProperty();
    }
    
}
