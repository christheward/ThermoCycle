/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.OptionalDouble;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris
 */
public class PropertyView {
    
    private final ObjectProperty property;
    private final BooleanProperty present;
    private final DoubleProperty value;
    private final StringProperty units;
    
    public PropertyView(Property property, OptionalDouble value) {
        this.property = new SimpleObjectProperty(property);
        this.present = new SimpleBooleanProperty(value.isPresent());
        this.value = new SimpleDoubleProperty(value.orElse(0.0));
        this.units = new SimpleStringProperty(property.units);
    }
    
    //public ObjectProperty attributeProperty() {
    //    return attribute;
    //}
    
    public Property getProperty() {
        return (Property)property.get();
    }
    
    public void setProperty(Property property) {
        this.property.set(property);
    }
    
    //public DoubleProperty valueProperty() {
    //    return value;
    //}
    
    public final Double getValue() {
        return value.get();
    }
    
    public void setValue(Double value) {
        this.value.set(value);
    }
    
    //public BooleanProperty presentProperty() {
    //    return present;
    //}
    
    public Boolean getPresent() {
        return present.get();
    }
    
    public void setPresent(Boolean present) {
        this.present.set(present);
    }
    
    //public StringProperty unitsProperty() {
    //    return units;
    //}
    
    public String getUnits() {
        return units.get();
    }
    
    public void setUnits(String units) {
        this.units.set(units);
    }
}
