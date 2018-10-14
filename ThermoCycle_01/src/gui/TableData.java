/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.OptionalDouble;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Chris
 */
public class TableData {
    
    private final StringProperty property;
    private final DoubleProperty value;
    private final StringProperty units;
    
    public TableData(String property, OptionalDouble value, String units) {
        this.property = new SimpleStringProperty(property);
        this.value = new SimpleDoubleProperty(value.orElse(0));
        this.units = new SimpleStringProperty(units);
    }
    
    public final String getProperty() {
        return property.getValue();
    }
    
    public final void setProperty(String name) {
        property.setValue(name);
    }
}
