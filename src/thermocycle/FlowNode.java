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
    public FlowNode(Port port) {
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
    public Optional<Fluid> getFluid() {
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
    public OptionalDouble getMass() {
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
    public OptionalDouble getState(Property property) {
        return state.getProperty(property);
    }
    
    /**
     * Set the Node's fluid. Note that the reference is copied as we want all nodes to reference the original Fluid object.
     * @param fluid The fluid.
     */
    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }
    
    /**
     * Set the Node's mass flow rate. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the mass flow rate to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    public void setMass(Double value) {
        mass = value;
    }
    
    /**
     * Sets a state property for the Node. Once the property's value has been set, other state properties are calculated where possible.
     * @param property The state property to set.
     * @param value The value to set the property to.
     */
    public void setProperty(Property property, Double value) {
        if (fluid == null) {
            throw new IllegalStateException("Error setting properties in Node. Fluid must be set first.");
        }
        state.setProperty(property, value);
        // Do not compute state here - this can lead to inconsitencies for Steam where properties in connected nodes are calcualted using different equations and are therefore slightly different.
        //fluid.computeState(state);
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
                }
                else if (FluidEquation.relativeAccuracy < Math.abs(state.getProperty(p).getAsDouble() - fn.state.getProperty(p).getAsDouble())) {
                    throw new IllegalStateException("Node properties are incompatible (" + p.toString() + ") - Node: " + state.getProperty(p) + " / Node: " + fn.state.getProperty(p));
                }
            });
            
            // Compute state after all properties have been updated
            fluid.computeState(state);
        }
        else {
            throw new IllegalStateException("Incompatible node types.");
        }
        return updated;
    }
    
    /**
     * Compute missing state properties.
     */
    public void computeState() {
        fluid.computeState(state);
    }
    
    @Override
    public String getType() {
        return "Flow";
    }
    
}