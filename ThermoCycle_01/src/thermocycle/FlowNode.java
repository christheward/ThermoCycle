/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;


/**
 *
 * @author Chris
 */
public final class FlowNode extends Node implements Properties {
    
    /**
     * The fluid at this node.
     */
    private Fluid fluid;
    
    /**
     * The fluid state of this node.
     */
    private final State state;
    
    /**
     * The mass flow rate at this node.
     */
    private OptionalDouble mass;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    protected FlowNode(Port port) {
        super(port);
        state = new State();
        mass = OptionalDouble.empty();
    }
    
    /**
     * Clears the fluid node
     */
    @Override
    protected void clear() {
        clearMass();
        clearState();
    }
    
    /**
     * Clears the mass value of the fluid node
     */
    protected void clearMass() {
        mass = OptionalDouble.empty();
    }
    
    /**
     * Completely clears the state of the fluid node
     */
    protected void clearState() {
        state.clear();
    }
    
    /**
     * Clears the fluid node state of a property
     * @param property The property to clear
     */
    protected void clearState(Property property) {
        state.remove(property);
    }
    
    /**
     * Gets the fluid for the this node.
     * @return Returns the fluid.
     */
    protected Fluid getFluid() {
        return fluid;
    }
    
    /**
     * Determines if the fluids has been set
     * @return A boolean
     */
    protected Boolean isFluidSet() {
        return (fluid != null);
    }
    
    /**
     * Gets the list of allowable properties
     * @return Returns the list of allowable properties for the node.
     */
    protected Set<Property> allowableProperties() {
        return fluid == null ? new HashSet<>() : new HashSet<>(fluid.fluidState());
    }
    
    /**
     * Gets the mass flow rate of this node.
     * @return Returns the mass flow rate.
     */
    protected OptionalDouble getMass() {
        if (mass.isPresent()) {
            return OptionalDouble.of(mass.getAsDouble());
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Gets the state property of this node.
     * @param property The property to get the value of.
     * @return Returns the value of the state property.
     */
    protected OptionalDouble getState(Property property) {
        return state.get(property);
    }
    
    /**
     * Set the Node's fluid. Note that the reference is copied as we want all nodes to reference the original Fluid object.
     * @param fluid The fluid.
     */
    protected void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }
    
    /**
     * Set the Node's mass flow rate. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the mass flow rate to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    protected void setMass(OptionalDouble value) {
        if (value.isPresent()) {
            mass = OptionalDouble.of(value.getAsDouble());
        }
        else {
            throw new IllegalStateException("Cannot set mass to an empty OptionalDouble.");
        }
    }
    
    /**
     * Sets a state property for the Node. Once the property's value has been set, other state properties are calculated where possible.
     * @param property The state property to set.
     * @param value The value to set the property to.
     */
    protected void setState(Property property, OptionalDouble value) {
        if (value.isPresent()) {
            state.put(property, value);
            if (fluid == null) {
                throw new IllegalStateException("Error setting properties in Node. Fluid must be set first.");
            }
            else {
                fluid.computeState(state);
            }
        }
        else {
            throw new IllegalStateException("Cannot set property to an empty OptionalDouble.");
        }
    }

    /**
     * Sets the state properties for the Node. Once the property's value has been set, other state properties are calculated where possible.
     * @param s The state
     * @throws illegalStateException - Thrown if the fluid has not been set prior to setting a property.
     */
    protected void setState(State s) {
        state.put(s);
        if (fluid == null) {
            throw new IllegalStateException("Error setting properties in Node. Fluid must be set first.");
        }
        else {
            fluid.computeState(state);
        }
    }
    
    @Override
    protected boolean isComplete() {
        if (fluid == null) {
            return false;
        }
        return ((mass.isPresent()) && state.properties().containsAll(fluid.fluidState()));
    }
    
    @Override
    protected boolean update(Node n) {
        boolean updated = false;
        if (n instanceof FlowNode) {
            FlowNode fn = (FlowNode) n;
            
            // Check that both nodes reference the same fluid
            if (fluid != fn.fluid) {
                throw new IllegalStateException("Node fluids are incompatible.");
            }
            
            // Update the mass flow rate
            if (!mass.isPresent()) {
                if (fn.mass.isPresent()) {
                    mass = OptionalDouble.of(fn.mass.getAsDouble());
                    updated = true;
                }
            }
            else if (fn.mass.isPresent()) {
                if (!mass.equals(fn.mass)) {
                    throw new IllegalStateException("Node mass flow rates are incompatible - Node: " + mass.getAsDouble() + " / Node: " + fn.mass.getAsDouble());
                }
            }
            
            // Update state properties
            fn.state.properties().forEach((p) -> {
                if (!state.contains(p)) {
                    state.put(p,fn.state.get(p));
                    fluid.computeState(state);
                }
                else if (!Equation.withinTolerance(state.get(p), fn.state.get(p))) {
                    throw new IllegalStateException("Node properties are incompatible (" + p.toString() + ") - Node: " + state.get(p) + " / Node: " + fn.state.get(p));
                }
            });
        }
        else {
            throw new IllegalStateException("Incompatible node types.");
        }
        return updated;
    }
    
    /**
     * Compute missing state properties.
     */
    protected void computeState() {
        fluid.computeState(state);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(System.lineSeparator()).append("Mass: ").append(mass.isPresent() ? mass.getAsDouble() + "kg/s" : "Unknown");
        state.properties().forEach(p -> {
            sb.append(System.lineSeparator()).append(p.name()).append(": ").append(state.get(p).getAsDouble());
        });
        return sb.toString();
    }
}