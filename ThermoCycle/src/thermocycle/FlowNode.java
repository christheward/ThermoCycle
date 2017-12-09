/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.OptionalDouble;


/**
 *
 * @author Chris
 */
final public class FlowNode extends Node implements Properties {
    
    private final State state;
    private Fluid fluid;
    private OptionalDouble mass;
    
    /**
     * Constructor
     * @param let Specify whether this is an INLET or OUTLET
     * @param ambient The ambient conditions
     */
    FlowNode(Ports.Let let) {
        super(let);
        state = new State();
        mass = OptionalDouble.empty();
    }
    FlowNode(Ports.Let let, State amb, Fluid f) {
        super(let);
        state = new State();
        fluid = f;
    }
     
    /**
     * Gets the fluid for the flow node
     * @return Returns the fluid.
     */
    Fluid getFluid() {
        return fluid;
    }
    
    OptionalDouble getMass() {
        if (mass.isPresent()) {
            return OptionalDouble.of(mass.getAsDouble());
        }
        return OptionalDouble.empty();
    }
    
    OptionalDouble getState(Property p) {
        return state.get(p);
    }
    
    /**
     * Set the Node's fluid. Note that the reference is copied as we want all nodes to reference the original Fluid object.
     * @param fluid The fluid.
     */
    void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }
    
    /**
     * Set the Node's mass flow rate. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the mass flow rate to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    void setMass(OptionalDouble value) {
        if (!value.isPresent()) {
            throw new IllegalArgumentException("Value must be present.");
        }
        mass = OptionalDouble.of(value.getAsDouble());
    }
    
    /**
     * Sets a state property for the Node. Once the property's value has been set, other state properties are calculated where possible.
     * @param property The state property to set.
     * @param value The value to set the property to.
     */
    void setState(Property property, OptionalDouble value) {
        state.putIfAbsent(property, value);
        fluid.computeState(state);
    }

    /**
     * Sets the state properties for the Node. Once the property's value has been set, other state properties are calculated where possible.
     * @param s The state
     * @throws illegalStateException - Thrown if the fluid has not been set prior to setting a property.
     */
    void setState(State s) {
        state.putIfAbsent(s);
        if (fluid == null)
            throw new IllegalStateException("Error setting properties in Node " + id + ". Fluid must be set first.");
        fluid.computeState(state);
    }
    
    @Override
    boolean isComplete() {
        if (fluid == null) {
            return false;
        }
        return ((mass.isPresent()) && state.properties().containsAll(fluid.fluidState()));
    }
    
    @Override
    boolean update(Node n) {
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
                    throw new IllegalStateException("Node mass flow rates are incompatible - Node [" + id + "]: " + mass.getAsDouble() + " / Node [" + fn.id + "]: " + fn.mass.getAsDouble());
                }
            }
            
            // Update state properties
            fn.state.properties().forEach((p) -> {
                if (!state.contains(p)) {
                    state.putIfAbsent(p,fn.state.get(p));
                    fluid.computeState(state);
                }
                else if (!Equation.withinTolerance(state.get(p), fn.state.get(p))) {
                    throw new IllegalStateException("Node properties are incompatible (" + p.toString() + ") - Node [" + id + "]: " + state.get(p) + " / Node [" + fn.id + "]: " + fn.state.get(p));
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
    void computeState() {
        fluid.computeState(state);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(System.lineSeparator()).append("  fluid:   ");
        if (fluid == null) {
            sb.append("  null");
        }
        else {
            sb.append(fluid.getName());
        }
        sb.append(System.lineSeparator()).append("  mass:    ");
        if (!mass.isPresent()) {
            sb.append("  null");}
        else {
            sb.append(doubleFormat.format(mass.getAsDouble())).append(" kg/s");
        }
        sb.append(System.lineSeparator()).append("  state:   ").append(state.toString().replaceAll(System.lineSeparator(), System.lineSeparator() + "         "));
        return sb.toString();
    }
}