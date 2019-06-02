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
public interface Attributes {
    
    /**
     * Attributes for components
     */
    enum Attribute {
        
        EFFICIENCY ("Efficiency", "eta", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0),
        PRATIO("Pressure Ratio", "PR", UNITS_TYPE.DIMENSIONLESS, 0.0, Double.POSITIVE_INFINITY),
        PLOSS("Pressure Loss", "PL", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0),
        EFFECTIVENESS ("Effectiveness", "epsilon", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0),
        AHEATTRANSFER("Actual Heat Transfer","Q_Actual",UNITS_TYPE.POWER,0.0,Double.POSITIVE_INFINITY),
        IHEATTRANSFER("Ideal Heat Transfer","Q_Ideal",UNITS_TYPE.POWER,0.0,Double.POSITIVE_INFINITY),
        SPLIT("Split","x",UNITS_TYPE.DIMENSIONLESS,0.0,1.0);
        
        public final String fullName;
        public final String symbol;
        public final UNITS_TYPE type;
        protected final double min;
        protected final double max;
        
        /**
         * Constructor
         * @param name Attribute name
         * @param symbol Attribute symbol
         * @param units Attribute symbol
         * @param min Minimum attribute value
         * @param max Maximum attribute value
         */
        private Attribute(String fullname, String symbol, UNITS_TYPE units, double min, double max) {
            this.fullName = fullname;
            this.symbol = symbol;
            this.type = units;
            this.max = max;
            this.min = min;
        }
        
        @Override
        public String toString() {
            return fullName;
        }
    }
}
