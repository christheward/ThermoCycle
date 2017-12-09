/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import static thermocycle.Ports.Let.*;

/**
 *
 * @author Chris
 */

public final class Connection<T extends Node> implements Serializable {
    
    // variables
    private final T n1;
    private final T n2;
    
    // constructor
    Connection(T node1, T node2) {
        if (node1 == null || node2 == null) {throw new IllegalStateException("Connnections cannnot be made between null nodes.");}                                                // check nodes are not null
        if (node1 == node2) {throw new IllegalStateException("Cannot connect a node to itself.");}                                                                                // check nodes are not the same
        if (node1.let == INLET && node2.let == INLET) {throw new IllegalStateException("Connection cannot be between an Inlet and an Inlet.");}     // check that connection is between inlet and outlet, inlet and internal, internal and internal, or internal and outlet
        if (node1.let == OUTLET && node2.let == OUTLET) {throw new IllegalStateException("Connection cannot be between an Outlet and an Outlet.");}
        n1 = node1;
        n2 = node2;
    }
    
    /**
     * Gets the sub-class type of the node
     * @return The node sub-class
     */
    Class nodeType() {
        return n1.getClass();
    }
    
    /**
     * Gets the nodes pairing in the connection
     * @param n The current node.
     * @return Returns the node connected to node n.
     */
    T getPair(T n) {
        if (!contains(n)) {return null;}       // check connection contains node, if it does return the other node
        else if (n == n1) {return n2;}
        else {return n1;}
    }
    
    /**
     * Checks to see if the connection contains a node.
     * @param n The node.
     * @return Returns true if the connection contained the node.
     */
    boolean contains(T n) {
        return (n == n1 || n == n2);
    }
    
    /**
     * Updates the connections to ensure equality of connected nodes.
     * @return Returns true when complete
     */
    void update() {
        n1.update(n2);
        n2.update(n1);
    }
    
    /**
     * A connection is complete if both nodes are complete.
     * @return Returns true if the connection is complete.
     */
    boolean isComplete() {
        return (n1.isComplete() && n2.isComplete());
    }
    
}
