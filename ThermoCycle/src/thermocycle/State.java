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
final class State implements Properties, Serializable {
    
    /**
     * Property map.
     */
    private final EnumMap<Property, OptionalDouble> state;
    
    /**
     * Constructor.
     */
    State(){
        state = new EnumMap<>(Property.class);
    }
    
    /**
     * Checks to see if this state contains specified properties.
     * @param properties The specified properties.
     * @return Returns True if all specified properties are present in this state.
     */
    boolean contains(Property... properties) {
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
     * @return Returns True if all properties in the specified state are in this state.
     */
    boolean contains(State state) {
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
    OptionalDouble get(Property property) {
        if (state.containsKey(property)) {
            return OptionalDouble.of(state.get(property).getAsDouble());
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Gets a set of all the properties in this state.
     * @return Returns a set containing the properties in this state.
     */
    Set<Property> properties() {
        return state.keySet();
    }
    
    /**
     * Puts the property in this state and sets it's value. The value must be present and within the property's allowable limits. The property cannot already exist in the state.
     * @param property The property to be set.
     * @param value The values to set the property to. 
     */
    void putIfAbsent(Property property, OptionalDouble value) {
        
        // Check state does not already contain property
        if (state.containsKey(property)) {
            throw new IllegalArgumentException("Property already exists in the state.");
        }
        
        // put the value in the state
        put(property, value);
        
    }
    
    /**
     * Puts the property in this state and sets it's value. The value must be present and within the property's allowable limits.
     * @param property The property to be set.
     * @param value The values to set the property to. 
     */
    void put(Property property, OptionalDouble value) {
        
        // Check value is present
        if (!value.isPresent()) {
            throw new IllegalArgumentException("Value must be present!");
        }
        
        // Check value is within propoerty limits
        if (value.getAsDouble() > property.max) {
            throw new IllegalArgumentException("Value is greater than allowable maximum.");
        }
        if (value.getAsDouble() < property.min) {
            throw new IllegalArgumentException("Value is less than allowable minimum.");
        }
        
        // Put property in state
        state.put(property, OptionalDouble.of(value.getAsDouble()));
        
    }
    
    /**
     * Puts the properties and their values from a state to this state.
     * @param state The state properties to be set.
     */
    void putIfAbsent(State state) {
        state.state.keySet().stream().forEach((property) -> {this.putIfAbsent(property, state.state.get(property));});
    }
    
    /**
     * Removes all properties from the state.
     */
    void reset() {
        state.clear();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        state.keySet().stream().forEach(property -> {
            sb.append(String.format("%-13s",property.toString())).append(": ").append(get(property).getAsDouble()).append(" ").append(property.units).append(System.lineSeparator());
        });
        return sb.toString();
    }
}
