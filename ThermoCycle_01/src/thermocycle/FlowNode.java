/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import thermocycle.Properties.Property;


/**
 *
 * @author Chris
 */
public final class FlowNode extends Node {
    
    /**
     * The fluid of this node.
     */
    private Fluid fluid;
    
    /**
     * The thermodynamic state of this node.
     */
    private final State state;
    
    /**
     * The mass flow rate at this node.
     */
    private Double mass;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    protected FlowNode(Port port) {
        super(port);
        state = new State();
        mass = null;
    }
    
    @Override
    protected boolean isPresent() {
        return (mass != null);
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
        mass = null;
    }
    
    /**
     * Completely clears the thermodynamics state of the fluid node
     */
    protected void clearState() {
        state.clearState();
    }
    
    /**
     * Clears the fluid node state of a property
     * @param property The property to clear
     */
    protected void clearProperty(Property property) {
        state.clearProperty(property);
    }
    
    /**
     * Gets the fluid for the this node.
     * @return Returns the fluid.
     */
    protected Optional<Fluid> getFluid() {
        return Optional.ofNullable(fluid);
    }
    
    /**
     * Gets the list of allowable properties
     * @return Returns the list of allowable properties for the node.
     */
    public Set<Property> getAllowableProperties() {
        return fluid == null ? new HashSet() : new HashSet(fluid.getAllowableProperties());
    }
    
    /**
     * Gets the mass flow rate of this node.
     * @return Returns the mass flow rate.
     */
    protected OptionalDouble getMass() {
        if (isPresent()) {
            return OptionalDouble.of(mass);
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Gets the state property of this node.
     * @param property The property to get the value of.
     * @return Returns the value of the state property.
     */
    protected OptionalDouble getState(Property property) {
        return state.getProperty(property);
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
    protected void setMass(Double value) {
        mass = value;
    }
    
    /**
     * Sets a state property for the Node. Once the property's value has been set, other state properties are calculated where possible.
     * @param property The state property to set.
     * @param value The value to set the property to.
     */
    protected void setProperty(Property property, Double value) {
        state.setProperty(property, value);
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
        return ((isPresent()) && state.properties().containsAll(fluid.getAllowableProperties()));
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
            if (!isPresent()) {
                if (fn.isPresent()) {
                    mass = fn.mass;
                    updated = true;
                }
            }
            else if (fn.isPresent()) {
                if (!mass.equals(fn.mass)) {
                    throw new IllegalStateException("Node mass flow rates are incompatible - Node: " + mass + " / Node: " + fn.mass);
                }
            }
            
            // Update state properties
            fn.state.properties().forEach((p) -> {
                if (!state.contains(p)) {
                    state.setProperty(p,fn.state.getProperty(p).getAsDouble());
                    fluid.computeState(state);
                }
                else if (!Equation.withinTolerance(state.getProperty(p).getAsDouble(), fn.state.getProperty(p).getAsDouble())) {
                    throw new IllegalStateException("Node properties are incompatible (" + p.toString() + ") - Node: " + state.getProperty(p) + " / Node: " + fn.state.getProperty(p));
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
    
}