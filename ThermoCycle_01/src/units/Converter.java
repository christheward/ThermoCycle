/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package units;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public final class Converter implements Serializable {
    
    /**
     * Converts a value from non SI units to SI units.
     * @param value the value in the non SI units system.
     * @param units the units to convert from.
     * @return the value in SI units.
     */
    public static double toSI(double value, UNITS units) {
        return units.toSI(value);
    }
    
    /**
     * Converts a value from SI units to non SI units.
     * @param value the value in SI units.
     * @param units the units to convert to.
     * @return the value in SI units.
     */
    public static double fromSI(double value, UNITS units) {
        return units.fromSI(value);
    }
    
    /**
     * Gets the units object given a name.
     * @param name the name of the units to get.
     * @return the units object.
     */
    public static Optional<UNITS> getUnit(String name) {
        return Arrays.stream(UNITS.values()).filter(u -> u .name.equals(name)).findFirst();
    }
    
    /**
     * Gets all units of a given type.
     * @param type the type of units to get.
     * @return a set of units of the given type.
     */
    public static Set<UNITS> getType(UNIITS_TYPE type) {
        return Arrays.stream(UNITS.values()).filter(u -> u.type.equals(type)).collect(Collectors.toSet());
    }
    
    /**
     * Covert a value from one set of units to another.
     * @param value the value to convert.
     * @param from the units to convert from.
     * @param to the units to convert to.
     * @return the value in the new unit system.
     */
    public static double convert(double value, UNITS from, UNITS to) {
        return to.fromSI(from.toSI(value));
    }
    
    /**
     * List of possible unit types
     */
    private enum UNIITS_TYPE {MASS, TEMPERATURE, ENERGY, PRESSURE, DIMENSIONLESS};

    /**
     * Units ENUM
     */
    public enum UNITS {
        kgps("kg/s",UNIITS_TYPE.MASS,1.0,0.0),
        kgphr("kg/hr",UNIITS_TYPE.MASS,3600.0,0.0),
        gps("g/s",UNIITS_TYPE.MASS,1000.0,0.0),
        gphr("g/hr",UNIITS_TYPE.MASS,3600000.0,0.0),
        lbps("lb/s",UNIITS_TYPE.MASS,0.453592,0.0),
        lbphr("lb/hr",UNIITS_TYPE.MASS,0.000125998,0.0),
        
        K("K",UNIITS_TYPE.TEMPERATURE,1.0,0.0),
        C("C",UNIITS_TYPE.TEMPERATURE,1.0,273.15),
        F("F",UNIITS_TYPE.TEMPERATURE,5.0/9.0,459.67),
        R("R",UNIITS_TYPE.TEMPERATURE,5.0/9.0,0.0),
        
        mW("mW",UNIITS_TYPE.ENERGY,0.001,0.0),
        W("W",UNIITS_TYPE.ENERGY,1.0,0.0),
        kW("kW",UNIITS_TYPE.ENERGY,1000.0,0.0),
        MW("MW",UNIITS_TYPE.ENERGY,1000000.0,0.0),
        
        Pa("Pa",UNIITS_TYPE.PRESSURE,1.0,0.0),
        atm("atm",UNIITS_TYPE.PRESSURE,101325.0,0.0),
        mbar("mbar",UNIITS_TYPE.PRESSURE,100.0,0.0),
        bar("bar",UNIITS_TYPE.PRESSURE,100000.0,0.0),
        mmHg("mmHg",UNIITS_TYPE.PRESSURE,133.32,0.0),
        mmH2O("mmH20",UNIITS_TYPE.PRESSURE,9.80664857,0.0),
        lbfpin2("lbf/in^2",UNIITS_TYPE.PRESSURE,0.000145037737730,0.0),
        
        fraction("-",UNIITS_TYPE.DIMENSIONLESS,1.0, 0.0),
        percentage("%",UNIITS_TYPE.DIMENSIONLESS,100.0, 0.0);
        
        private final String name;
        private final UNIITS_TYPE type;
        private final double factor;
        private final double offset;
        
        /**
         * Constructor
         * @param name
         * @param factor
         * @param offset 
         */
        private UNITS(String name, UNIITS_TYPE type, double factor, double offset) {
            this.name = name;
            this.type = type;
            this.factor = factor;
            this.offset = offset;
        }
        
        /**
         * Converts non SI units to SI units.
         * @param value the value in this units system.
         * @return the values in SI units.
         */
        public final double toSI(double value) {
            return (value + offset)*factor;
        }
        
        /**
         * Converts SI units to non SI units.
         * @param value the value in SI units.
         * @return the values in this unit system.
         */
        public final double fromSI(double value) {
            return (value/factor  - offset);
        }
        
        /**
         * Determines if the unit is SI.
         * @return true if an SI unit.
         */
        public boolean isSI() {
            return ((factor == 1.0) & (offset == 0.0));
        }
        
        
    }
    
}
