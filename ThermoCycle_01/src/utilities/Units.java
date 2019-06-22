/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public interface Units {
    
    /**
     * Units Type ENUM
     */
    public enum UNITS_TYPE {
        DENSITY, DIMENSIONLESS, ENTROPY, FLOW_RATE, MOLAR_VOLUME, POWER, PRESSURE, SPECIFIC_ENERGY, TEMPERATURE, SPECIFIC_VOLUME;
        
        /**
         * Gets the units for this type.
         * @param system the system to get the units for.
         * @return the units of this type in the give system.
         */
        public UNITS getUnits(UNITS_SYSTEM system) {
            return system.getUnits(this);
        }
        
    };
    
    /**
     * Units system ENUM
     */
    public enum UNITS_SYSTEM {
        SI("SI", UNITS.KGPM3, UNITS.FRACTION, UNITS.JPKGK, UNITS.KGPS, UNITS.M3PMOL, UNITS.W, UNITS.PA, UNITS.JPKG, UNITS.K, UNITS.M3PKG),
        IMPERIAL("Imperial", UNITS.LBPFT3, UNITS.FRACTION, UNITS.BTUPLBF, UNITS.LBPHR, UNITS.FT3PLB, UNITS.BTUPHR, UNITS.PSI, UNITS.BTUPLB, UNITS.F, UNITS.FT3PLB);
        
        /**
         * Name of the units system.
         */
        public final String name;
        
        /**
         * A map of the systems units.
         */
        private final EnumMap<UNITS_TYPE,UNITS> systemUnits;
        
        /**
         * Constructor.
         * @param name name of the the units system.
         * @param units the systems units.
         */
        private UNITS_SYSTEM(String name, UNITS... units) {
            this.name = name;
            this.systemUnits = new EnumMap(UNITS_TYPE.class);
            Arrays.asList(units).stream().forEach(u -> {
                systemUnits.put(u.type, u);
            });   
        }
        
        /**
         * Gets the units of given type from the system.
         * @param type the type of units to get.
         * @return the systems units.
         */
        public UNITS getUnits(UNITS_TYPE type) {
            return systemUnits.get(type);
        }
        
        @Override
        public String toString() {
            return name;
        }

    }

    /**
     * Units ENUM
     */
    public enum UNITS {
        
        KGPM3("kg/m^3",UNITS_TYPE.DENSITY,1.0,0.0),
        GPM3("g/m^3",UNITS_TYPE.DENSITY,0.001,0.0),
        GPCM3("g/cm^3",UNITS_TYPE.DENSITY,1000.0,0.0),
        LBPFT3("lb/ft^3",UNITS_TYPE.DENSITY,16.0185,0.0),
        
        FRACTION("",UNITS_TYPE.DIMENSIONLESS,1.0, 0.0),
        PERCENTAGE("%",UNITS_TYPE.DIMENSIONLESS,100.0, 0.0),
        
        JPKGK("J/Kg.K", UNITS_TYPE.ENTROPY,1.0,0.0),
        KJPKGK("kJ/Kg.K", UNITS_TYPE.ENTROPY,1000.0,0.0),
        BTUPLBF("BTU/lb.F", UNITS_TYPE.ENTROPY,4183.99999999995,0.0),
        LBFFTPLBR("lbf.ft/lb.R",UNITS_TYPE.ENTROPY,5.380320456,0.0),
        
        KGPS("kg/s",UNITS_TYPE.FLOW_RATE,1.0,0.0),
        KGPHR("kg/hr",UNITS_TYPE.FLOW_RATE,3600.0,0.0),
        GPS("g/s",UNITS_TYPE.FLOW_RATE,1000.0,0.0),
        GPHR("g/hr",UNITS_TYPE.FLOW_RATE,3600000.0,0.0),
        LBPS("lb/s",UNITS_TYPE.FLOW_RATE,0.453592,0.0),
        LBPHR("lb/hr",UNITS_TYPE.FLOW_RATE,0.000125998,0.0),
        
        M3PMOL("m^3/mol",UNITS_TYPE.MOLAR_VOLUME,1.0,0.0),
        CM3PMOL("cm^3/mol",UNITS_TYPE.MOLAR_VOLUME,0.0000001,0.0),
        FT3PMOL("ft^3/mol",UNITS_TYPE.MOLAR_VOLUME,0.0283168,0.0),
        
        W("W",UNITS_TYPE.POWER,1.0,0.0),
        KW("kW",UNITS_TYPE.POWER,1000.0,0.0),
        MW("MW",UNITS_TYPE.POWER,1000000.0,0.0),
        BTUPHR("BTU/hr",UNITS_TYPE.POWER,0.2931,0.0),
        HP("HP",UNITS_TYPE.POWER,0.00134102,0.0),
        FTLBFPHR("ft.lbf/hr",UNITS_TYPE.POWER,0.00038,0.0),
        
        PA("Pa",UNITS_TYPE.PRESSURE,1.0,0.0),
        ATM("atm",UNITS_TYPE.PRESSURE,101325.0,0.0),
        MBAR("mbar",UNITS_TYPE.PRESSURE,100.0,0.0),
        BAR("bar",UNITS_TYPE.PRESSURE,100000.0,0.0),
        MMHG("mmHg",UNITS_TYPE.PRESSURE,133.32,0.0),
        MMH2O("mmH20",UNITS_TYPE.PRESSURE,9.80664857,0.0),
        LBFPIN2("lbf/in^2",UNITS_TYPE.PRESSURE,0.000145037737730,0.0),
        LBFPFT2("lbf/ft^2",UNITS_TYPE.PRESSURE,47.8802589804,0.0),
        PSI("psi",UNITS_TYPE.PRESSURE,6894.757293178,0.0),
        
        JPKG("J/Kg", UNITS_TYPE.SPECIFIC_ENERGY,1.0,0.0),
        KJPKG("J/Kg", UNITS_TYPE.SPECIFIC_ENERGY,0.001,0.0),
        BTUPLB("BTU/lb", UNITS_TYPE.SPECIFIC_ENERGY,2324.444444445,0.0),
        CPG("calorie/g",UNITS_TYPE.SPECIFIC_ENERGY,4184.000000005,0.0),
        
        K("K",UNITS_TYPE.TEMPERATURE,1.0,0.0),
        C("C",UNITS_TYPE.TEMPERATURE,1.0,273.15),
        F("F",UNITS_TYPE.TEMPERATURE,5.0/9.0,459.67),
        R("R",UNITS_TYPE.TEMPERATURE,5.0/9.0,0.0),
        
        M3PKG("m^3/kg",UNITS_TYPE.SPECIFIC_VOLUME,1.0,0.0),
        M3PG("m^3/g",UNITS_TYPE.SPECIFIC_VOLUME,1000.0,0.0),
        CM3PG("cm^3/g",UNITS_TYPE.SPECIFIC_VOLUME,0.001,0.0),
        FT3PLB("ft^3/lb",UNITS_TYPE.SPECIFIC_VOLUME,0.0624278,0.0);
        
        private final String name;
        private final UNITS_TYPE type;
        private final Double factor;
        private final Double offset;
        
        /**
         * Constructor
         * @param name the name of the units.
         * @param factor the factor to convert the units into SI units.
         * @param offset the offset to convert the units into SI units.
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
