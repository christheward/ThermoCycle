/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import static thermocycle.Node.Port.*;

/**
 *
 * @author Chris
 */

public final class Connection<T extends Node> implements Serializable {
    
    /**
     * The first node in the connection.
     */
    private final T n1;
    
    /**
     * The second node in the connection.
     */
    private final T n2;
    
    /**
     * Constructor
     * @param n1 The first node in the connection.
     * @param n2 The second node in the connection.
     */
    public Connection(T n1, T n2) {
        if (n1 == null || n2 == null) {throw new IllegalStateException("Connnections cannnot be made between null nodes.");}                                                // check nodes are not null
        if (n1 == n2) {throw new IllegalStateException("Cannot connect a node to itself.");}                                                                                // check nodes are not the same
        if (n1.port == INLET && n2.port == INLET) {throw new IllegalStateException("Connection cannot be between an Inlet and an Inlet.");}     // check that connection is between inlet and outlet, inlet and internal, internal and internal, or internal and outlet
        if (n1.port == OUTLET && n2.port == OUTLET) {throw new IllegalStateException("Connection cannot be between an Outlet and an Outlet.");}
        this.n1 = n1;
        this.n2 = n2;
    }
    
    /**
     * Gets the sub-class type of the node.
     * @return The node sub-class.
     */
    protected Class nodeType() {
        return n1.getClass();
    }
    
    /**
     * Gets the nodes pairing in the connection
     * @param node The current node.
     * @return Returns the node connected to node n.
     */
    protected T getPair(T node) {
        if (!contains(node)) {
            return null;
        }
        // check connection contains node, if it does return the other node
        else if (node == n1) {
            return n2;
        }
        else {
            return n1;
        }
    }
    
    /**
     * Checks to see if the connection contains a node.
     * @param n The node.
     * @return Returns true if the connection contained the node.
     */
    protected boolean contains(T n) {
        return (n == n1 || n == n2);
    }
    
    /**
     * Updates the connections to ensure equality of connected nodes.
     * @return true when complete
     */
    protected void update() {
        n1.update(n2);
        n2.update(n1);
    }
    
    /**
     * A connection is complete if both nodes are complete.
     * @return true if the connection is complete.
     */
    protected boolean isComplete() {
        return (n1.isComplete() && n2.isComplete());
    }
    
    /**
     * Gets a set containing the two nodes in the connection.
     * @return a set containing the two nodes in the connection.
     */
    protected Set<Node> getNodes() {
        Set nodes = new HashSet();
        nodes.add(n1);
        nodes.add(n2);
        return nodes;
    }
    
}
