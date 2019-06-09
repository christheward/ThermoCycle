/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Chris
 */
final class State implements Serializable {
    
    /**
     * Property map.
     */
    private final HashMap<Property, Double> state;
    
    /**
     * Constructor.
     */
    public State(){
        state = new HashMap();
    }
    
    /**
     * Checks if this state contains specified properties.
     * @param properties The specified properties.
     * @return Returns true if all specified properties are present in this state.
     */
    protected boolean contains(Property... properties) {
        for (Property p : properties) {
            if (!state.containsKey(p)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks to see if this state contains all the properties in the specified state.
     * @param state The specified state.
     * @return Returns true if all properties in the specified state are in this state.
     */
    protected boolean contains(State state) {
        if (state.state.keySet().stream().anyMatch((p) -> !(this.state.containsKey(p)))) {
            return false;
        }
        return true;
    }
    
    /**
     * Gets the values of the specified property.
     * @param property The specified property.
     * @return If the property exists in the state returns the values of the specified property, else returns empty.
     */
    protected OptionalDouble getProperty(Property property) {
        if (state.containsKey(property)) {
            return OptionalDouble.of(state.get(property));
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Gets a set of all the properties in this state.
     * @return Returns a set containing the properties in this state.
     */
    protected Set<Property> properties() {
        return state.keySet();
    }
    
    /**
     * Puts the property in this state and sets it's value. The value must be present and within the property's allowable limits.
     * @param property The property to be set.
     * @param value The values to set the property to. 
     */
    protected void setProperty(Property property, Double value) {
        // Check value is within propoerty limits
        if (value > property.max) {
            throw new IllegalArgumentException("Value for " + property.name + " is greater than allowable maximum (" + property.max + ").");
        }
        if (value < property.min) {
            throw new IllegalArgumentException("Value for " + property.name + " is less than allowable minimum(" + property.min + ").");
        }
        // Put property in state
        state.put(property, value);
    }
    
    /**
     * Puts the properties and their values from a state to this state.
     * @param state The state properties to be set.
     */
    protected void setProperty(State state) {
        state.state.keySet().stream().forEach((property) -> {
            this.setProperty(property, state.state.get(property));
        });
    }
    
    protected void clearProperty(Property property) {
        state.remove(property);
    }
    
    /**
     * Removes all properties from the state.
     */
    protected void clearState() {
        state.clear();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        state.keySet().stream().forEach(property -> {
            sb.append(String.format("%-13s",property.toString())).append(": ").append(getProperty(property).getAsDouble()).append(" ").append(property.type).append(System.lineSeparator());
        });
        return sb.toString();
    }
    
}
