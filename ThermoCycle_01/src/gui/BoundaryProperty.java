/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.OptionalDouble;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class BoundaryProperty {
    
    private final ReadOnlyObjectWrapper<Property> property;
    private final ReadOnlyObjectWrapper<OptionalDouble> value;
    private final StringProperty units;
    private final ReadOnlyBooleanWrapper present;
    
    public BoundaryProperty(Property property, OptionalDouble value) {
        this.property = new ReadOnlyObjectWrapper(property);
        this.value = new ReadOnlyObjectWrapper(value);
        this.units = new SimpleStringProperty(property.units);
        this.present = new ReadOnlyBooleanWrapper();
        setUpBindings();
    }
    
    /**
     * Sets up the internal bindings for this object.
     */
    private void setUpBindings() {
        this.present.bind(new BooleanBinding() {
            {
                bind(value);
            }
            @Override
            protected boolean computeValue() {
                return value.getValue().isPresent();
            }
        });
    }
    
    public Property getProperty() {
        return property.getValue();
    }
    
    public String getUnits() {
        return units.getValue();
    }
    
    public OptionalDouble getValue() {
        return value.getValue();
    }
    
    public void setValue(Double newValue) {
        value.setValue(OptionalDouble.of(newValue));
        notifyAll();
    }
    
    public void clearValue() {
        this.value.setValue(OptionalDouble.empty());
    }
    
    public ReadOnlyBooleanProperty presentProperty() {
        return present.getReadOnlyProperty();
    }
    
}
