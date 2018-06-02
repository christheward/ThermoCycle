/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

/**
 *
 * @author Chris
 */
public interface Properties {
    
    /**
     * Properties for states
     */
    public enum Property {
        PRESSURE ("Pressure", "P", "Pa",0.0, Double.POSITIVE_INFINITY),
        TEMPERATURE ("Temperature", "T", "K", 0.0, Double.POSITIVE_INFINITY),
        VOLUME ("Specific volume", "V", "m^3/kg", 0.0, Double.POSITIVE_INFINITY),
        ENTROPY ("Specific entropy", "s", "J/kg.K", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        ENERGY ("Specific internal energy", "u", "J/kg", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        ENTHALPY ("Specific enthalpy", "h", "J/kg", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        HELMHOLTZ ("Specific helmholtz energy", "f", "J/kg", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        GIBBS ("Specific gibbs energy", "g", "J/kg", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        MOLVOL ("Molar volume" , "Vm", "m^3/mol", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
        QUALITY ("Steam quality", "X", "-", 0.0, 1.0),
        MECHANICAL("Specific mechanical energy", "m", "J/kg", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        
        // variables
        protected final String fullName;
        protected final String symbol;
        protected final String units;
        protected final double max;
        protected final double min;
        
        /**
         * Constructor
         * @param name Property name
         * @param symbol Property symbol
         * @param units Property units
         * @param min Minimum property value
         * @param max Maximum property value
         */
        private Property(String name, String symbol, String units, double min, double max) {
            fullName = name;
            this.symbol = symbol;
            this.units = units;
            this.max = max;
            this.min = min;
        }   
    }
}
