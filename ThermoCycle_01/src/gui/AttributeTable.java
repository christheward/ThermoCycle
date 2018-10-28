/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.OptionalDouble;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import thermocycle.Attributes.Attribute;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris
 */
public class AttributeTable {
    
    private final ObjectProperty attribute;
    private final ObjectProperty value;
    
    public AttributeTable(Attribute attribute, OptionalDouble value) {
        this.attribute = new SimpleObjectProperty(attribute);
        this.value = new SimpleObjectProperty(value);
    }
    
    public final Attribute getAttribute() {
        return (Attribute)attribute.getValue();
    }
    
    public final void setAttribute(Attribute attribute) {
        this.attribute.setValue(attribute);
    }
    
    public final OptionalDouble getValue() {
        return ((OptionalDouble)value.getValue());
    }
    
    public final void setValue(OptionalDouble value) {
        this.value.setValue(value);
    }
    
    public final String getUnits() {
        return (getAttribute().units);
    }
    
}
