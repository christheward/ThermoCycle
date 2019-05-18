/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import utilities.SingletonCollector;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public interface Units {
    
    /**
     * List of possible unit types
     */
    public enum UNITS_TYPE {
        DENSITY, DIMENSIONLESS, ENTROPY, FLOW_RATE, MOLAR_VOLUME, POWER, PRESSURE, SPECIFIC_ENERGY, TEMPERATURE, VOLUME;
        
        public UNITS getSI() {
            return Stream.of(UNITS.values()).filter(u -> u.type.equals(this)).filter(u -> u.isSI()).collect(SingletonCollector.singletonCollector());
        }
    
    };

    /**
     * Units ENUM
     */
    public enum UNITS {
        
        kgpm3("kg/m^3",UNITS_TYPE.DENSITY,1.0,0.0),
        gpm3("g/m^3",UNITS_TYPE.DENSITY,0.001,0.0),
        gpcm3("g/cm^3",UNITS_TYPE.DENSITY,1000.0,0.0),
        lbpft3("lb/ft^3",UNITS_TYPE.DENSITY,16.0185,0.0),
        
        fraction("",UNITS_TYPE.DIMENSIONLESS,1.0, 0.0),
        percentage("%",UNITS_TYPE.DIMENSIONLESS,100.0, 0.0),
        
        JpKgK("J/Kg.K", UNITS_TYPE.ENTROPY,1.0,0.0),
        
        kgps("kg/s",UNITS_TYPE.FLOW_RATE,1.0,0.0),
        kgphr("kg/hr",UNITS_TYPE.FLOW_RATE,3600.0,0.0),
        gps("g/s",UNITS_TYPE.FLOW_RATE,1000.0,0.0),
        gphr("g/hr",UNITS_TYPE.FLOW_RATE,3600000.0,0.0),
        lbps("lb/s",UNITS_TYPE.FLOW_RATE,0.453592,0.0),
        lbphr("lb/hr",UNITS_TYPE.FLOW_RATE,0.000125998,0.0),
        
        m3pmol("m^3/mol",UNITS_TYPE.MOLAR_VOLUME,1.0,0.0),
        cm3pmol("cm^3/mol",UNITS_TYPE.MOLAR_VOLUME,0.0000001,0.0),
        ft3pmol("ft^3/mol",UNITS_TYPE.MOLAR_VOLUME,0.0283168,0.0),
        
        mW("mW",UNITS_TYPE.POWER,0.001,0.0),
        W("W",UNITS_TYPE.POWER,1.0,0.0),
        kW("kW",UNITS_TYPE.POWER,1000.0,0.0),
        MW("MW",UNITS_TYPE.POWER,1000000.0,0.0),
        
        Pa("Pa",UNITS_TYPE.PRESSURE,1.0,0.0),
        atm("atm",UNITS_TYPE.PRESSURE,101325.0,0.0),
        mbar("mbar",UNITS_TYPE.PRESSURE,100.0,0.0),
        bar("bar",UNITS_TYPE.PRESSURE,100000.0,0.0),
        mmHg("mmHg",UNITS_TYPE.PRESSURE,133.32,0.0),
        mmH2O("mmH20",UNITS_TYPE.PRESSURE,9.80664857,0.0),
        lbfpin2("lbf/in^2",UNITS_TYPE.PRESSURE,0.000145037737730,0.0),
        
        JpKg("J/Kg", UNITS_TYPE.SPECIFIC_ENERGY,1.0,0.0),
        kJpKg("J/Kg", UNITS_TYPE.SPECIFIC_ENERGY,0.001,0.0),
        
        K("K",UNITS_TYPE.TEMPERATURE,1.0,0.0),
        C("C",UNITS_TYPE.TEMPERATURE,1.0,273.15),
        F("F",UNITS_TYPE.TEMPERATURE,5.0/9.0,459.67),
        R("R",UNITS_TYPE.TEMPERATURE,5.0/9.0,0.0),
        
        m3pkg("m^3/kg",UNITS_TYPE.VOLUME,1.0,0.0),
        m3pg("m^3/g",UNITS_TYPE.VOLUME,1000.0,0.0),
        cm3pg("cm^3/g",UNITS_TYPE.VOLUME,0.001,0.0),
        ft3plb("ft^3/lb",UNITS_TYPE.VOLUME,0.0624278,0.0);
        
        private final String name;
        private final UNITS_TYPE type;
        private final Double factor;
        private final Double offset;
        
        /**
         * Constructor
         * @param name
         * @param factor
         * @param offset 
         */
        private UNITS(String name, UNITS_TYPE type, Double factor, Double offset) {
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
        public final Double toSI(Double value) {
            return (value + offset)*factor;
        }
        
        /**
         * Converts SI units to non SI units.
         * @param value the value in SI units.
         * @return the values in this unit system.
         */
        public final Double fromSI(Double value) {
            return (value/factor  - offset);
        }
        
        /**
         * Determines if the unit is SI.
         * @return true if an SI unit.
         */
        public boolean isSI() {
            return ((factor == 1.0) & (offset == 0.0));
        }
        
        @Override
        public String toString() {
            return name;
        }
        
    }
    
    /**
     * Gets the units object given a name.
     * @param name the name of the units to get.
     * @return the units object.
     */
    public static Optional<UNITS> getUnits(String name) {
        return Arrays.stream(UNITS.values()).filter(u -> u .name.equals(name)).findFirst();
    }
    
    /**
     * Gets the SI units for the units type.
     * @param type the type of units
     * @return the SI units.
     */
    public static UNITS getSiUnits(UNITS_TYPE type) {
        return Stream.of(UNITS.values()).filter(u -> u.type.equals(type)).filter(u -> u.isSI()).collect(SingletonCollector.singletonCollector());
    }
    
    /**
     * Gets all units of a given type.
     * @param type the type of units to get.
     * @return a set of units of the given type.
     */
    public static Set<UNITS> getUnits(UNITS_TYPE type) {
        return Arrays.stream(UNITS.values()).filter(u -> u.type.equals(type)).collect(Collectors.toSet());
    }
    
}
