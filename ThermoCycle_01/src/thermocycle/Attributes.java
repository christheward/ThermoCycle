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
public interface Attributes {
    
    /**
     * Attributes for components
     */
    enum Attribute {
        
        EFFICIENCY ("Efficiency", "eta", "-", 0.0, 1.0),
        PRATIO("Pressure Ratio", "PR", "-", 0.0, Double.POSITIVE_INFINITY),
        PLOSS("Pressure Loss", "PL", "-", 0.0, 1.0),
        EFFECTIVENESS ("Effectiveness", "epsilon", "-", 0.0, 1.0),
        AHEATTRANSFER("Actual Heat Transfer","Q_Actual","W",0.0,Double.POSITIVE_INFINITY),
        IHEATTRANSFER("Ideal Heat Transfer","Q_Ideal","W",0.0,Double.POSITIVE_INFINITY),
        
        SPLIT("Split","x","-",0.0,1.0);
        
        protected final String fullName;
        protected final String symbol;
        protected final String units;
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
        private Attribute(String name, String symbol, String units, double min, double max) {
            fullName = name;
            this.symbol = symbol;
            this.units = units;
            this.max = max;
            this.min = min;
        }
    }
}
