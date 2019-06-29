/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class Property implements Serializable {
    
    public final String name;
    public final String symbol;
    public final UNITS_TYPE type;
    public final double min;
    public final double max;
    
    protected Property(String name, String symbol, UNITS_TYPE type, double min, double max) {
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
    
}
