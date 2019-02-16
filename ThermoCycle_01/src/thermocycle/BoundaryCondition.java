/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;

/**
 *
 * @author Chris
 */
public abstract class BoundaryCondition implements Serializable {
    
    public final double[] values;
    protected int idx;
    
    public BoundaryCondition(double[] values) {
        this.values = values;
        this.idx = 0;
    }
    
    // Gets the current boundary condition value
    public final double getValue() {
        return values[idx];
    }

    protected abstract void execute();
    protected abstract boolean match(BoundaryCondition cnd);
    
}
