/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.OptionalDouble;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import thermocycle.Attributes.Attribute;

/**
 *
 * @author Chris
 */
public class AttributeView {
    
    private final ObjectProperty attribute;
    private final StringProperty units;
    private final StringProperty output;
    
    public AttributeView(Attribute attribute, OptionalDouble value) {
        this.attribute = new SimpleObjectProperty(attribute);
        this.units = new SimpleStringProperty(attribute.units);
        this.output = new SimpleStringProperty(MasterSceneController.displayOptionalDouble(value));
    }
        
    public Attribute getAttribute() {
        return (Attribute)attribute.get();
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute.set(attribute);
    }
        
    public String getUnits() {
        return units.get();
    }
    
    public void setUnits(String units) {
        this.units.set(units);
    }

    public String getOutput() {
        return output.get();
    }
    
    public void setOutput(String output) {
        this.output.set(output);
    }
    
}
