/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import thermocycle.UnitsControl.Units;
import thermocycle.UnitsControl.UnitsType;
import thermocycle.UnitsControl;
import thermocycle.UnitsControl.UnitsSystem;

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
    private final Units units;
    
    /**
     * Static constructor
     * @param value the value of the number.
     * @param units the dimensions of the number.
     * @return the dimensioned number.
     */
    public static DimensionedDouble valueOf(Double value, Units units) {
        return new DimensionedDouble(value, units);
    }
    
    /**
     * Static constructor
     * @param value the value of the number.
     * @param unitsType the type of the units.
     * @return the dimensioned number in SI units.
     */
    public static DimensionedDouble valueOfSI(Double value, UnitsType unitsType) {
        return new DimensionedDouble(value, unitsType.getUnits(UnitsSystem.SI));
    }
    
    
    /**
     * Constructor
     * @param value the value of the number.
     * @param units the dimensions of the number.
     */
    public DimensionedDouble(Double value, Units units) {
        this.value = value;
        this.units = units;
    }
    
    /**
     * Convert dimensioned number to new units.
     * @param newUnits the units to convert to.
     * @return the number in the new units.
     */
    public DimensionedDouble convertTo(Units newUnits) {
        return new DimensionedDouble(newUnits.fromSI(units.toSI(value)), newUnits);
    }
    
    @Override
    public String toString() {
        return df.format(value) + " " + units.toString();
    }
    
}
