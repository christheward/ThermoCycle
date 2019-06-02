/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris
 */
public interface Properties {
    
    /**
     * Tolerance
     */
    public final static double TOLERANCE = 1e-5;
    
    /**
     * Properties for states
     */
    public enum Property {
        PRESSURE ("Pressure", "P", UNITS_TYPE.PRESSURE, 5000, 0.0, Double.POSITIVE_INFINITY),
        TEMPERATURE ("Temperature", "T", UNITS_TYPE.TEMPERATURE, 1e-6, 0.0, Double.POSITIVE_INFINITY),
        VOLUME ("Specific volume", "v", UNITS_TYPE.SPECIFIC_VOLUME, 1e-6, 0.0, Double.POSITIVE_INFINITY),
        DENSITY("Density", "\u03C1", UNITS_TYPE.DENSITY, 1e-6, 0.0, Double.POSITIVE_INFINITY),
        ENTROPY ("Specific entropy", "s", UNITS_TYPE.ENTROPY, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        ENERGY ("Specific internal energy", "u", UNITS_TYPE.SPECIFIC_ENERGY, 1e-6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        ENTHALPY ("Specific enthalpy", "h", UNITS_TYPE.SPECIFIC_ENERGY, 1e-6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        HELMHOLTZ ("Specific helmholtz energy", "f", UNITS_TYPE.SPECIFIC_ENERGY, 1e-6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        GIBBS ("Specific gibbs energy", "g", UNITS_TYPE.SPECIFIC_ENERGY, 1e-6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        MOLVOL ("Molar volume" , "Vm", UNITS_TYPE.MOLAR_VOLUME, 1e-6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        QUALITY ("Quality", "X", UNITS_TYPE.DIMENSIONLESS, 1e-6, 0.0, 1.0),
        MECHANICAL("Specific mechanical energy", "m", UNITS_TYPE.SPECIFIC_ENERGY, 1e-6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        
        // variables
        public final String fullName;
        public final String symbol;
        public final UNITS_TYPE type;
        public final double convergenceTolerance;
        protected final double max;
        protected final double min;
        
        /**
         * Constructor
         * @param name Property name
         * @param symbol Property symbol
         * @param units Property units
         * @param convergenceTolerance Convergence tolerance
         * @param min Minimum property value
         * @param max Maximum property value
         */
        private Property(String fullname, String symbol, UNITS_TYPE units, double convergenceTolerance, double min, double max) {
            this.fullName = fullname;
            this.symbol = symbol;
            this.type = units;
            this.convergenceTolerance = convergenceTolerance;
            this.max = max;
            this.min = min;
        }
        
        public String toString() {
            return fullName;
        }
    }
}
