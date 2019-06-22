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
public final class WorkNode extends Node {
    
    /**
     * The node work value.
     */
    private Double work;
    
    /**
     * Constructor.
     * @param port The node port type.
     */
    public WorkNode(Port port) {
        super(port);
        work = null;
    }
    
    @Override
    protected void clear() {
        work = null;
    }
    
    @Override
    protected boolean isPresent() {
        return (work != null);
    }
    /**
     * Gets the node work values
     * @return Returns the node work value.
     */
    public OptionalDouble getWork() {
        if (isPresent()) {
            return OptionalDouble.of(work);
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Set the node work value. Note that a new object is created as we do not want to reference the original object.
     * @param value The value to set the heat flux to.
     * @throws IllegalArgumentException Thrown if the value is not present.
     */
    public void setWork(Double value) {
        work = value;
    }
    
    @Override
    protected boolean isComplete() {
        return (isPresent());
    }
    
    @Override
    protected boolean update(Node n) {
        if (n instanceof WorkNode) {
            WorkNode wn = (WorkNode) n;
            if (!isPresent()) {
                if (wn.isPresent()) {
                    work = wn.work;
                    return true;
                }
            }
            else if (wn.isPresent()) {
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
    public String getType() {
        return "work";
    }
    
}
