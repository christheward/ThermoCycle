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
    
    protected abstract void execute();
    protected abstract boolean match(BoundaryCondition cnd);
    public abstract Double getValue();
    
}
