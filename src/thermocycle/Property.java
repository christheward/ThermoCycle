/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.Objects;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class Property implements Serializable {
    
    public final String name;
    public final String symbol;
    public final UnitsType type;
    public final double min;
    public final double max;
    
    protected Property(String name, String symbol, UnitsType type, double min, double max) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.min = min;
        this.max = max;
    }
    
    /**
     * Gets the lower bound for this variable for use with equations.
     * @return the lower bound.
     */
    public final double getLowerBound() {
        if (min == 0.0) {
            return Double.MIN_VALUE;
        }
        else if (min == Double.NEGATIVE_INFINITY) {
            return -1e100;
        }
        return Double.NaN;
    }
    
    /**
     * Gets the upper bound for this variable for use with equations.
     * @return 
     */
    public final double getUpperBound() {
        if (max == 1.0) {
            return 1.0;
        }
        else if (max == Double.POSITIVE_INFINITY) {
            return 1e100;
        }
        return Double.NaN;
    }
    
    @Override
    public String toString() {
        return symbol;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof Property) {
            Property p = (Property) object;
            return name.equals(p.name) && symbol.equals(p.symbol) && type.equals(p.type) && (min == p.min) && (max == p.max);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.symbol);
        hash = 83 * hash + Objects.hashCode(this.type);
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        return hash;
    }
    
}
