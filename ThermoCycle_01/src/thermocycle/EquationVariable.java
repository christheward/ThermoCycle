/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import utilities.Units;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class EquationVariable implements Serializable {
    
    public final String name;
    public final String symbol;
    public final Units.UNITS_TYPE type;
    protected final double min;
    protected final double max;
    
    protected EquationVariable(String name, String symbol, Class node) {
        this.name = name;
        this.symbol = symbol;
        if (node.equals(FlowNode.class)) {
            this.type = UNITS_TYPE.FLOW_RATE;
        }
        else if(node.equals(HeatNode.class) | node.equals(WorkNode.class)) {
            this.type = UNITS_TYPE.POWER;
        }
        else {
            this.type = UNITS_TYPE.DIMENSIONLESS;
        }
        this.min = Double.NEGATIVE_INFINITY;
        this.max = Double.POSITIVE_INFINITY;
    }
    
    protected EquationVariable(String name, String symbol, Property property) {
        this.name = name;
        this.symbol = symbol;
        this.type = property.type;
        this.min = property.min;
        this.max = property.max;
    }
    
    protected EquationVariable(Attribute attribute) {
        this.name = attribute.name;
        this.symbol = attribute.symbol;
        this.type = attribute.type;
        this.min = attribute.min;
        this.max = attribute.max;
    }
    
    public final double getLowerGuess() {
        if (min == 0.0) {
            return 0.0;
        }
        else if (min == Double.NEGATIVE_INFINITY) {
            return -1e100;
        }
        return Double.NaN;
    }
    
    public final double getUpperGuess() {
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
