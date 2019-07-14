/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class Attribute implements Serializable {
    
    public final String name;
    public final String symbol;
    public final UnitsControl.UnitsType type;
    protected final double min;
    protected final double max;
    
    public Attribute(String name, String symbol, UnitsControl.UnitsType type, double min, double max) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public String toString() {
        return symbol;
    }
    
}
