/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 *
 * @author Chris
 */
public final class HeatNode extends Node {
    
    /**
     * The node heat value.
     */
    private OptionalDouble heat;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    protected HeatNode(Port port) {
        super(port);
        heat = OptionalDouble.empty();
    }
    
    @Override
    protected void clear() {
        heat = OptionalDouble.empty();
    }
    
    /**
     * Gets the nodes heat value.
     * @return Returns the node's heat value.
     */
    public OptionalDouble getHeat() {
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
    protected void setHeat(OptionalDouble value) {
        if (value.isPresent()) {
            heat = OptionalDouble.of(value.getAsDouble());
        }
        else {
            throw new IllegalStateException("Cannot set heat to an empty OptionalDouble.");
        }
    }
    
    @Override
    protected boolean isComplete() {
        return (heat.isPresent());
    }
    
    @Override
    protected boolean update(Node n) {
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
        //StringBuilder sb = new StringBuilder(super.toString());
        //sb.append(System.lineSeparator()).append("  heat: ").append(heat).append(" W").append(System.lineSeparator());
        //return sb.toString();
        return (heat.isPresent() ? String.valueOf(heat.getAsDouble()) : "-Unknown-");
    }
}
