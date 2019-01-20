/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public abstract class Units {
    
    public Units() {
    }
    
    protected abstract double toSI(double nonSI);
    protected abstract double fromSI(double nonSI);
    
}
