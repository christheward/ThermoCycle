/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

/**
 *
 * @author Chris
 */
public final class HeatNode extends Node {
    
    /**
     * The node heat value.
     */
    private ParametricDouble heat;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    protected HeatNode(Port port) {
        super(port);
        heat = ParametricDouble.empty();
    }
    
    @Override
    protected void clear() {
        setHeat(ParametricDouble.empty());
    }
    
    /**
     * Gets the nodes heat value.
     * @return Returns the node's heat value.
     */
    protected ParametricDouble getHeat() {
        if (heat.isPresent()) {
            return ParametricDouble.of(heat.getAsDouble());
        }
        return ParametricDouble.empty();
    }
    
    /**
     * Set the Node's heat flux. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the heat flux to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    protected void setHeat(ParametricDouble value) {
        heat = ParametricDouble.of(value.getAsDouble());
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
                    heat = ParametricDouble.of(hn.heat.getAsDouble());
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
