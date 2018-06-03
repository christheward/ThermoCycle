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
public final class WorkNode extends Node {
    
    /**
     * The node work value.
     */
    private ParametricDouble work;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    protected WorkNode(Port port) {
        super(port);
        work = ParametricDouble.empty();
    }
    
    @Override
    protected void clear() {
        setWork(ParametricDouble.empty());
    }
    
    /**
     * Gets the node work values
     * @return Returns the node work value.
     */
    protected ParametricDouble getWork() {
        if (work.isPresent()) {
            return ParametricDouble.of(work.getAsDouble());
        }
        return ParametricDouble.empty();
    }
    
    /**
     * Set the node work value. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the heat flux to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    void setWork(ParametricDouble value) {
        work = ParametricDouble.of(value.getAsDouble());
    }
    
    @Override
    protected boolean isComplete() {
        return (work.isPresent());
    }
    
    @Override
    protected boolean update(Node n) {
        if (n instanceof WorkNode) {
            WorkNode wn = (WorkNode) n;
            if (!work.isPresent()) {
                if (wn.work.isPresent()) {
                    work = ParametricDouble.of(wn.work.getAsDouble());
                    return true;
                }
            }
            else if (wn.work.isPresent()) {
                if (!work.equals(wn.work)) {
                    throw new IllegalStateException("Node work values are incompatible.");
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
        sb.append(System.lineSeparator()).append("  work: ").append(work).append(" W").append(System.lineSeparator());
        return sb.toString();
    }
}
