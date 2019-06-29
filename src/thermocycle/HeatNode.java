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
public final class HeatNode extends Node {
    
    /**
     * The node heat value.
     */
    private Double heat;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    public HeatNode(Port port) {
        super(port);
        heat = null;
    }
    
    @Override
    protected void clear() {
        heat = null;
    }
    
    /**
     * Gets the nodes heat value.
     * @return Returns the node's heat value.
     */
    public OptionalDouble getHeat() {
        if (isPresent()) {
            return OptionalDouble.of(heat);
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Set the Node's heat flux. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the heat flux to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    public void setHeat(Double value) {
        heat = value;
    }
    
    @Override
    protected boolean isPresent() {
        return (heat != null);
    }
    
    @Override
    protected boolean isComplete() {
        return (isPresent());
    }
    
    @Override
    protected boolean update(Node n) {
        if (n instanceof HeatNode) {
            HeatNode hn = (HeatNode) n;
            if (!isPresent()) {
                if (hn.isPresent()) {
                    heat = hn.heat;
                    return true;
                }
            }
            else if (hn.isPresent()) {
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
    public String getType() {
        return "heat";
    }
    
}
