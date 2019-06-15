/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import report.Reportable;
import java.io.Serializable;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris
 */
public abstract class Fluid implements Serializable, Reportable {
    
    /**
     * Fluid properties.
     */
    public static final Property PRESSURE = new Property("Pressure", "P", UNITS_TYPE.PRESSURE, 0.0, Double.POSITIVE_INFINITY);
    public static final Property TEMPERATURE = new Property("Temperature", "T", UNITS_TYPE.TEMPERATURE, 0.0, Double.POSITIVE_INFINITY);
    public static final Property VOLUME = new Property("Specific volume", "v", UNITS_TYPE.SPECIFIC_VOLUME, 0.0, Double.POSITIVE_INFINITY);
    public static final Property DENSITY = new Property("Density", "rho", UNITS_TYPE.DENSITY, 0.0, Double.POSITIVE_INFINITY);
    public static final Property ENTROPY = new Property("Specific entropy", "s", UNITS_TYPE.ENTROPY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property ENERGY = new Property("Specific internal energy", "u", UNITS_TYPE.SPECIFIC_ENERGY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property ENTHALPY = new Property("Specific enthalpy", "h", UNITS_TYPE.SPECIFIC_ENERGY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property HELMHOLTZ = new Property("Specific helmholtz energy", "f", UNITS_TYPE.SPECIFIC_ENERGY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property GIBBS = new Property("Specific gibbs energy", "g", UNITS_TYPE.SPECIFIC_ENERGY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property MOLVOL = new Property("Molar volume" , "Vm", UNITS_TYPE.MOLAR_VOLUME, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property QUALITY = new Property("Quality", "X", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0);
    public static final Property MECHANICAL = new Property("Specific mechanical energy", "m", UNITS_TYPE.SPECIFIC_ENERGY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Property[] PROPERTIES = {PRESSURE, TEMPERATURE, VOLUME, DENSITY, ENTROPY, ENERGY, ENTHALPY, HELMHOLTZ, GIBBS, MOLVOL, QUALITY, MECHANICAL};
    
    /**
     * The universal gas constant.
     */
    public static double Ru = 8.3144598;
    
    /**
     * Advogadros number.
     */
    public static double Na = 60221408570000000000000.0;
    
    /**
     * The fluid name.
     */
    public final String name;
    
    /**
     * The fluid unique reference number
     */
    public final UUID id;
    
    /**
     * A list of all the equations relating to this component.
     */
    protected final List<FluidEquation> equations;
    
    /**
     * Constructor.
     * @param name the fluid name. 
     */
    protected Fluid(String name) {
        id = UUID.randomUUID();
        this.name = name;
        equations = new ArrayList();
        equations.add(new R_V());
        equations.add(new H_UPV());
        equations.add(new F_UTS());
        equations.add(new G_HTS());
    }
    
    /**
     * Gets the set of valid properties for this fluid.
     * @return the set of valid state properties.
     */
    public abstract Set<Property> getAllowableProperties();
    
    /**
     * Computes absent state properties from existing state properties for this fluid. This is done by continually trying to solve all the fluid equations until all equations are either solved or no more unknowns are being found,
     * @param state the state to compute.
     */
    protected final void computeState(State state) {
        // Keep solving the equations until no unknowns are being updated.
        while(!equations.stream().allMatch(e -> (e.solve(state) == false))) {}
    }
    
    @Override
    public String toString() {
        return (name + " (" + getClass().getSimpleName() + ")");
    }
    
    // R = 1 / V
    private class R_V extends FluidEquation {

        public R_V() {super(FluidEquation.equationString(DENSITY, VOLUME));}
        
        @Override
        protected  Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, DENSITY, VOLUME);
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(VOLUME).getAsDouble()*variables.get(DENSITY).getAsDouble() - 1;
        }
        
    }
    
    // H = U + P V
    private class H_UPV extends FluidEquation {
        
        public H_UPV() {super(FluidEquation.equationString(ENTHALPY, ENERGY, PRESSURE, VOLUME));}

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTHALPY, ENERGY, PRESSURE, VOLUME);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENTHALPY).getAsDouble() - variables.get(ENERGY).getAsDouble() - variables.get(PRESSURE).getAsDouble()*variables.get(VOLUME).getAsDouble();
        }
        
    }
            
    // F = U - T S
    private class F_UTS extends FluidEquation {

        public F_UTS() {super(FluidEquation.equationString(HELMHOLTZ, ENERGY, TEMPERATURE, ENTROPY));}

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, HELMHOLTZ, ENERGY, TEMPERATURE, ENTROPY);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(HELMHOLTZ).getAsDouble() - variables.get(ENERGY).getAsDouble() + variables.get(TEMPERATURE).getAsDouble()*variables.get(ENTROPY).getAsDouble();
        }
        
    }
    
    // G = H - T S
    private class G_HTS extends FluidEquation {

        public G_HTS() {super(FluidEquation.equationString(GIBBS, ENTHALPY, TEMPERATURE, ENTROPY));}

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, GIBBS, ENTHALPY, TEMPERATURE, ENTROPY);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(GIBBS).getAsDouble() - variables.get(ENTHALPY).getAsDouble() + variables.get(TEMPERATURE).getAsDouble()*variables.get(ENTROPY).getAsDouble();
        }
        
    }
        
}
