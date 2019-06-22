/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import utilities.Units;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import utilities.Units.UNITS;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public final class DimensionedDouble {
    
    /**
     * The formatter used when printing.
     */
    private static NumberFormat df = new DecimalFormat("0.00E0");
    
    /**
     * The value of the number.
     */
    private final Double value;
    
    /**
     * The units of the number.
     */
    private final UNITS units;
    
    /**
     * Static constructor
     * @param value the value of the number.
     * @param units the dimensions of the number.
     * @return the dimensioned number.
     */
    public static DimensionedDouble valueOf(Double value, UNITS units) {
        return new DimensionedDouble(value, units);
    }
    
    /**
     * Static constructor for SI units
     * @param value the value of the number.
     * @param type the type of units.
     * @return the SI dimensioned number.
     */
    public static DimensionedDouble valueOfSI(Double value, UNITS_TYPE type) {
        return new DimensionedDouble(value, Units.getSiUnits(type));
    }
    
    /**
     * Constructor
     * @param value the value of the number.
     * @param units the dimensions of the number.
     */
    public DimensionedDouble(Double value, UNITS units) {
        this.value = value;
        this.units = units;
    }
    
    /**
     * Convert dimensioned number to new units.
     * @param newUnits the units to convert to.
     * @return the number in the new units.
     */
    public DimensionedDouble convertTo(UNITS newUnits) {
        return new DimensionedDouble(newUnits.fromSI(units.toSI(value)), newUnits);
    }
    
    @Override
    public String toString() {
        return df.format(value) + " " + units.toString();
    }
    
}
