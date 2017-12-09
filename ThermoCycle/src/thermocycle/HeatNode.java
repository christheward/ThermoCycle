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
final public class HeatNode extends Node {
    
    // variables
    private OptionalDouble heat;
    
    /**
     * Constructor
     * @param let Specify whether this is an INLET or OUTLET
     */
    HeatNode(Ports.Let let) {
        super(let);
        heat = OptionalDouble.empty();
    }
    
    OptionalDouble getHeat() {
        if (heat.isPresent()) {
            return OptionalDouble.of(heat.getAsDouble());
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Set the Node's heat flux. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the heat flux to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    void setHeat(OptionalDouble value) {
        if (!value.isPresent()) {
            throw new IllegalArgumentException("Value must be present.");
        }
        heat = OptionalDouble.of(value.getAsDouble());
    }
    
    // methods
    @Override
    boolean isComplete() {
        return (heat.isPresent());
    }
    
    @Override
    boolean update(Node n) {
        if (n instanceof HeatNode) {
            HeatNode hn = (HeatNode) n;
            if (!heat.isPresent()) {
                if (hn.heat.isPresent()) {
                    heat = OptionalDouble.of(hn.heat.getAsDouble());
                    return true;
                }
            }
            else if (hn.heat.isPresent()) {
                if (!heat.equals(hn.heat)) {
                    throw new IllegalStateException("Node heat values are incompatible.");
                }
            }
        }
        else {
            throw new IllegalStateException("Incompatible node types.");
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(System.lineSeparator()).append("  heat: ").append(heat).append(" W").append(System.lineSeparator());
        return sb.toString();
    }
}
