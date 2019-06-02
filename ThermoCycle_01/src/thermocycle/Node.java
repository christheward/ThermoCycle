/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.Observable;
import report.Reportable;
import utilities.StringFormatter;

/**
 *
 * @author Chris
 */
public abstract class Node extends Observable implements Serializable {
    
    /**
     * The type of node port.
     */
    public enum Port {INLET, OUTLET, INTERNAL;
    
    @Override
    public String toString() {return StringFormatter.toCapitalFirst(this.name());};
    
    };
    
    /**
     * The node port type. This is final and cannot be changed.
     */
    public final Port port;
    
    /**
     * Constructor.
     * @param let Defines where the node is an inlet or outlet.
     */
    protected Node(Port port) {
        this.port = port;
    }
    
    /**
     * Clears the node's numerical values.
     */
    protected abstract void clear();
    
    /**
     * Checks if the node is complete.
     * @return true if the node is complete, false otherwise.
     */
    protected abstract boolean isComplete();
    
    /**
     * Updates this node's properties to match those of the source node.
     * @param sourceNode The source node.
     * @return true if this node is updated.
     */
    protected abstract boolean update(Node sourceNode);
    
    /**
     * Checks if the nodes mass, heat or work value is present.
     * @return true if the value is present, false otherwise.
     */
    protected abstract boolean isPresent();
    
    /**
     * Gets the node type as a string
     * @return a string describing the node type.
     */
    public abstract String getType();
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType());
        sb.append(" ");
        sb.append(this.port);
        return sb.toString();
    }
}

