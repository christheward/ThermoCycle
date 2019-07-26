/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public interface UnitsControl {
    
    /**
     * Units Type ENUM
     */
    public enum UnitsType {
        DENSITY, DIMENSIONLESS, ENTROPY, FLOW_RATE, MOLAR_VOLUME, POWER, PRESSURE, SPECIFIC_ENERGY, TEMPERATURE, SPECIFIC_VOLUME;
        
        /**
         * Gets the units for this type.
         * @param system the system to get the units for.
         * @return the units of this type in the give system.
         */
        public Units getUnits(UnitsSystem system) {
            return system.getUnits(this);
        }
        
        /**
         * Gets a set of units of this type.
         * @return a set of units of this type.
         */
        public Set<Units> getUnits() {
            return Arrays.stream(Units.values()).filter(u -> u.type.equals(this)).collect(Collectors.toSet());
        }
        
    };
    
    /**
     * Units system ENUM
     */
    public enum UnitsSystem {
        SI("SI", Units.KGPM3, Units.FRACTION, Units.JPKGK, Units.KGPS, Units.M3PMOL, Units.W, Units.PA, Units.JPKG, Units.K, Units.M3PKG),
        IMPERIAL("Imperial", Units.LBPFT3, Units.FRACTION, Units.BTUPLBF, Units.LBPHR, Units.FT3PLB, Units.BTUPHR, Units.PSI, Units.BTUPLB, Units.F, Units.FT3PLB),
        ENGINEERING("Engineering", Units.KGPM3, Units.FRACTION, Units.KJPKGK, Units.KGPS, Units.M3PKMOL, Units.KW, Units.BAR, Units.KJPKG, Units.K, Units.M3PKG);
        
        /**
         * Name of the units system.
         */
        public final String name;
        
        /**
         * A map of the systems units.
         */
        private final EnumMap<UnitsType,Units> systemUnits;
        
        /**
         * Constructor.
         * @param name name of the the units system.
         * @param units the systems units.
         */
        private UnitsSystem(String name, Units... units) {
            this.name = name;
            this.systemUnits = new EnumMap(UnitsType.class);
            Arrays.asList(units).stream().forEach(u -> {
                systemUnits.put(u.type, u);
            });   
        }
        
        /**
         * Gets the units of given type from the system.
         * @param type the type of units to get.
         * @return the systems units.
         */
        public Units getUnits(UnitsType type) {
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
    public enum Units {
        
        KGPM3("kg/m^3",UnitsType.DENSITY,1.0,0.0),
        GPM3("g/m^3",UnitsType.DENSITY,0.001,0.0),
        GPCM3("g/cm^3",UnitsType.DENSITY,1000.0,0.0),
        LBPFT3("lb/ft^3",UnitsType.DENSITY,16.0185,0.0),
        
        FRACTION("-",UnitsType.DIMENSIONLESS,1.0, 0.0),
        PERCENTAGE("%",UnitsType.DIMENSIONLESS,0.01, 0.0),
        
        JPKGK("J/kg.K", UnitsType.ENTROPY,1.0,0.0),
        KJPKGK("kJ/kg.K", UnitsType.ENTROPY,1000.0,0.0),
        BTUPLBF("BTU/lb.F", UnitsType.ENTROPY,4183.99999999995,0.0),
        
        KGPS("kg/s",UnitsType.FLOW_RATE,1.0,0.0),
        KGPHR("kg/hr",UnitsType.FLOW_RATE,1/3600.0,0.0),
        GPS("g/s",UnitsType.FLOW_RATE,0.001,0.0),
        GPHR("g/hr",UnitsType.FLOW_RATE,0.001/3600.0,0.0),
        LBPS("lb/s",UnitsType.FLOW_RATE,0.453592,0.0),
        LBPHR("lb/hr",UnitsType.FLOW_RATE,7936.64,0.0),
        
        M3PMOL("m^3/mol",UnitsType.MOLAR_VOLUME,1.0,0.0),
        M3PKMOL("m^3/kmol",UnitsType.MOLAR_VOLUME,1000.0,0.0),
        CM3PMOL("cm^3/mol",UnitsType.MOLAR_VOLUME,0.0000001,0.0),
        FT3PMOL("ft^3/mol",UnitsType.MOLAR_VOLUME,0.0283168,0.0),
        
        W("W",UnitsType.POWER,1.0,0.0),
        KW("kW",UnitsType.POWER,1000.0,0.0),
        MW("MW",UnitsType.POWER,1000000.0,0.0),
        BTUPHR("BTU/hr",UnitsType.POWER,0.29307107,0.0),
        HP("HP",UnitsType.POWER,0.00134102,0.0),
        FTLBFPHR("ft.lbf/hr",UnitsType.POWER,0.00038,0.0),
        
        PA("Pa",UnitsType.PRESSURE,1.0,0.0),
        ATM("atm",UnitsType.PRESSURE,101325.0,0.0),
        MBAR("mbar",UnitsType.PRESSURE,100.0,0.0),
        BAR("bar",UnitsType.PRESSURE,100000.0,0.0),
        MMHG("mmHg",UnitsType.PRESSURE,133.32,0.0),
        MMH2O("mmH20",UnitsType.PRESSURE,9.80664857,0.0),
        LBFPIN2("lbf/in^2",UnitsType.PRESSURE,0.000145037737730,0.0),
        LBFPFT2("lbf/ft^2",UnitsType.PRESSURE,47.8802589804,0.0),
        PSI("psi",UnitsType.PRESSURE,6894.757293178,0.0),
        
        JPKG("J/kg", UnitsType.SPECIFIC_ENERGY,1.0,0.0),
        KJPKG("kJ/kg", UnitsType.SPECIFIC_ENERGY,1000.0,0.0),
        BTUPLB("BTU/lb", UnitsType.SPECIFIC_ENERGY,2326.0,0.0),
        CPG("calorie/g",UnitsType.SPECIFIC_ENERGY,4184.0,0.0),
        
        K("K",UnitsType.TEMPERATURE,1.0,0.0),
        C("C",UnitsType.TEMPERATURE,1.0,273.15),
        F("F",UnitsType.TEMPERATURE,5.0/9.0,459.67),
        R("R",UnitsType.TEMPERATURE,5.0/9.0,0.0),
        
        M3PKG("m^3/kg",UnitsType.SPECIFIC_VOLUME,1.0,0.0),
        M3PG("m^3/g",UnitsType.SPECIFIC_VOLUME,1000.0,0.0),
        CM3PG("cm^3/g",UnitsType.SPECIFIC_VOLUME,0.001,0.0),
        FT3PLB("ft^3/lb",UnitsType.SPECIFIC_VOLUME,0.0624278,0.0);
        
        private final String name;
        private final UnitsType type;
        private final Double factor;
        private final Double offset;
        
        /**
         * Constructor
         * @param name the name of the units.
         * @param factor the multiplication factor to convert the units into SI units.
         * @param offset the offset to convert the units into SI units.
         */
        private Units(String name, UnitsType type, Double factor, Double offset) {
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
        
        /**
         * Get the units type.
         * @return the units type.
         */
        public UnitsType getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
    }
    
}
