/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.OptionalDouble;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris
 */
public class StateTable {
    
    private final ObjectProperty property;
    private final ObjectProperty value;
    
    public StateTable(Property property, OptionalDouble value) {
        this.property = new SimpleObjectProperty(property);
        this.value = new SimpleObjectProperty(value);
    }
    
    public final Property getProperty() {
        return (Property)property.getValue();
    }
    
    public final void setProperty(Property property) {
        this.property.setValue(property);
    }
    
    public final OptionalDouble getValue() {
        return ((OptionalDouble)value.getValue());
    }
    
    public final void setValue(OptionalDouble value) {
        this.value.setValue(value);
    }
    
}
