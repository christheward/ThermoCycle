/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.text.DecimalFormat;
import java.io.Serializable;

/**
 *
 * @author Chris
 */
public abstract class Node implements Ports, Serializable {
    
    // static variables
    private static int idCount = 0;
    static DecimalFormat doubleFormat = new DecimalFormat("0.0000E0");
    
    // static methods
    static int idCount() {return Node.idCount;}
    
    // variables
    final int id;
    final Ports.Let let;
    
    // constructor
    Node(Ports.Let let) {
        Node.idCount++;
        id = idCount;
        this.let = let;
    }
    
    // getters
    abstract boolean isComplete();
    abstract boolean update(Node n);
    
    // reporting methods
    final void report() {
        System.out.println("[" + id + "] " + Node.this.getClass().getSimpleName());
    }
    public String csvOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(getClass().getSimpleName()).append(",").append(let);
        return sb.toString();
    };
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(id).append("] ").append(Node.this.getClass().getSimpleName());
        return sb.toString();
    }
}

