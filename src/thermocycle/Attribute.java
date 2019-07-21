/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.Objects;

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
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof Attribute) {
            Attribute a = (Attribute) object;
            return name.equals(a.name) && symbol.equals(a.symbol) && type.equals(a.type) && (min == a.min) && (max == a.max);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.symbol);
        hash = 53 * hash + Objects.hashCode(this.type);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        return hash;
    }
    
}
