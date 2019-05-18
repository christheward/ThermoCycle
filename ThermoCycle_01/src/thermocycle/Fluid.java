/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import report.Reportable;
import java.io.Serializable;
import java.util.*;
import thermocycle.Properties.Property;
import static thermocycle.Properties.Property.*;

/**
 *
 * @author Chris
 */
public abstract class Fluid implements Serializable, Reportable {
    
    /**
     * The universal gas constant.
     */
    static double R = 8.3144598;
    
    /**
     * Advogadros number.
     */
    static double Na = 60221408570000000000000.0;
    
    /**
     * The fluid name.
     */
    public final String name;
    
    /**
     * The component unique reference number
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
     * Computes absent state properties from existing state properties for this fluid.
     * @param state the state to compute.
     */
    protected final void computeState(State state) {
        // Keep solving the equations untill no unknowns are being updated.
        while(!equations.stream().allMatch(e -> (e.solve(state) == false))) {}
    }
    
    @Override
    public String toString() {
        return (name + " (" + getClass().getSimpleName() + ")");
    }
    
    // R = 1 / V
    private class R_V extends FluidEquation {

        public R_V() {super(FluidEquation.equationString(DENSITY, VOLUME), DENSITY.convergenceTolerance);}
        
        @Override
        protected  Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, DENSITY, VOLUME);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(VOLUME).getAsDouble()*variables.get(DENSITY).getAsDouble() - 1);
        }
        
    }
    
    // H = U + P V
    private class H_UPV extends FluidEquation {
        
        public H_UPV() {super(FluidEquation.equationString(ENTHALPY, ENERGY, PRESSURE, VOLUME), ENTHALPY.convergenceTolerance);}

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTHALPY, ENERGY, PRESSURE, VOLUME);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(ENTHALPY).getAsDouble() - variables.get(ENERGY).getAsDouble() - variables.get(PRESSURE).getAsDouble()*variables.get(VOLUME).getAsDouble());
        }
        
    }
            
    // F = U - T S
    private class F_UTS extends FluidEquation {

        public F_UTS() {super(FluidEquation.equationString(HELMHOLTZ, ENERGY, TEMPERATURE, ENTROPY), HELMHOLTZ.convergenceTolerance);}

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, HELMHOLTZ, ENERGY, TEMPERATURE, ENTROPY);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(HELMHOLTZ).getAsDouble() - variables.get(ENERGY).getAsDouble() + variables.get(TEMPERATURE).getAsDouble()*variables.get(ENTROPY).getAsDouble());
        }
        
    }
    
    // G = H - T S
    private class G_HTS extends FluidEquation {

        public G_HTS() {super(FluidEquation.equationString(GIBBS, ENTHALPY, TEMPERATURE, ENTROPY),1e-5);}

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, GIBBS, ENTHALPY, TEMPERATURE, ENTROPY);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(GIBBS).getAsDouble() - variables.get(ENTHALPY).getAsDouble() + variables.get(TEMPERATURE).getAsDouble()*variables.get(ENTROPY).getAsDouble());
        }
        
    }
    
}
