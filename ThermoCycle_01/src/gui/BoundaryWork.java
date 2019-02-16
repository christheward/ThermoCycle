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
import thermocycle.BoundaryConditionWork;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class BoundaryWork {
    
    private final ReadOnlyObjectWrapper<BoundaryConditionWork> boundaryCondition;
    private final ReadOnlyStringWrapper work;
    private final ReadOnlyBooleanWrapper present;
    private final ReadOnlyDoubleWrapper value;
    private final ReadOnlyStringWrapper units;
    
    public BoundaryWork() {
        this.boundaryCondition = new ReadOnlyObjectWrapper();
        this.work = new ReadOnlyStringWrapper();
        this.present = new ReadOnlyBooleanWrapper();
        this.value = new ReadOnlyDoubleWrapper();
        this.units = new ReadOnlyStringWrapper();
        
        // Setup bindings
        this.present.bind(boundaryCondition.isNotNull());
        this.work.setValue("Work");
        this.value.bind(new DoubleBinding() {
            {
                bind(boundaryCondition);
            }
            @Override
            protected double computeValue() {
                return boundaryCondition.isNotNull().getValue() ? boundaryCondition.getValue().getValue() : 0.0;
            }
        });
        this.units.setValue("W");
    }
    
    public void setBoundaryCondition(BoundaryConditionWork boundary) {
        boundaryCondition.setValue(boundary);
    }
    
    public void clearBoundaryCondition() {
        if (boundaryCondition.isNotNull().getValue()) {
            boundaryCondition.setValue(null);
        }
    }
    
    public ReadOnlyObjectProperty<BoundaryConditionWork> boundaryProperty() {
        return boundaryCondition.getReadOnlyProperty();
    }
    
    public ReadOnlyBooleanProperty presentProperty() {
        return present.getReadOnlyProperty();
    }
    
    public ReadOnlyStringProperty workProperty() {
        return work.getReadOnlyProperty();
    }
    
    public ReadOnlyDoubleProperty valueProperty() {
        return value.getReadOnlyProperty();
    }
    
    public ReadOnlyStringProperty unitsProperty() {
        return units.getReadOnlyProperty();
    }
    
}
