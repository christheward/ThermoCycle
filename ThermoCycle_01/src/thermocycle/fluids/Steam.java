/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.fluids;

import thermocycle.report.ReportDataBlock;
import java.util.*;

import com.hummeling.if97.IF97;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thermocycle.EquationBounds;
import thermocycle.Fluid;
import thermocycle.FluidEquation;
import thermocycle.Property;
import thermocycle.State;
import static thermocycle.Fluid.GIBBS;
import static thermocycle.Fluid.PRESSURE;
import static thermocycle.Fluid.QUALITY;
import static thermocycle.Fluid.TEMPERATURE;

/**
 *
 * @author Chris
 */
public final class Steam extends Fluid {
    
    /**
     * IF97 steam functions
     */
    private static IF97 if97 = new IF97(IF97.UnitSystem.SI);
    
    /**
     * Minimum pressure
     */
    private static double P_min = 1e-6;
    private static double P_mid = 50e6;
    private static double P_max = 100e6;
    private static double T_min = 273.15;
    private static double T_mid = 1073.15;
    private static double T_max = 2273.15;
    
        /**
     * Logger.
     */
    static private final Logger logger = LogManager.getLogger("DebugLog");

    
    /**
     * Constructor.
     */
    public Steam() {
        super("Steam");
        /**
         * There are inconsistencies when using these equations.  Need to find a way to just us HS or PT - and use a 2D secant solver?
         */
        //equations.add(new P_HS());
        //equations.add(new T_HS());
        //equations.add(new R_HS());
        //equations.add(new U_HS());
        equations.add(new R_PT());
        equations.add(new H_PT());
        equations.add(new S_PT());
        equations.add(new U_PT());
        equations.add(new G_PT());
        equations.add(new X_HS());
        equations.add(new X_PH());
        equations.add(new X_PS());
        equations.add(new X_TS());
        
    }
    
    @Override
    public final Set<Property> getAllowableProperties() {
        Set<Property> fluidState = new HashSet<>();
        fluidState.add(PRESSURE);
        fluidState.add(TEMPERATURE);
        fluidState.add(VOLUME);
        fluidState.add(DENSITY);
        fluidState.add(ENTROPY);
        fluidState.add(ENERGY);
        fluidState.add(ENTHALPY);
        fluidState.add(GIBBS);
        fluidState.add(HELMHOLTZ);
        fluidState.add(QUALITY);
        return fluidState;
    }
    
    /**
     * Gets the bounds for equations where two of the variables are Pressure and Temperature
     * @param variables the variables and their values.
     * @param unknownVariable the unknown variable.
     * @return the equation bounds.
     */
    protected EquationBounds getBoundsPT(Map<Property, OptionalDouble> variables, Property unknownVariable) {
        // Pressure is unknown
        if (unknownVariable.equals(PRESSURE)) {
            if (variables.get(TEMPERATURE).getAsDouble() < T_mid) {
                return new EquationBounds(P_min,P_max);
            }
            else if (variables.get(TEMPERATURE).getAsDouble() < T_max) {
                return new EquationBounds(P_min,P_mid);
            }
        }
        // Temperature is unknown
        else if (unknownVariable.equals(TEMPERATURE)) {
            if (variables.get(PRESSURE).getAsDouble() < P_mid) {
                return new EquationBounds(T_min,T_max);
            }
            else if (variables.get(PRESSURE).getAsDouble() < P_max) {
                return new EquationBounds(T_min,T_mid);
            }
        }
        // Third variable is unknown
        else {
            return new EquationBounds(unknownVariable.getLowerBound(), unknownVariable.getUpperBound());
        }
        return new EquationBounds(Double.NaN, Double.NaN);
    }
    
    /**
     * Calculates the bounds for any of the function used to calculated quality. 
     * @param variables the equation variables.
     * @param unknownVariable the unknown variable.
     * @return 
     */
    protected EquationBounds getBoundsX(Map<Property, OptionalDouble> variables, Property unknownVariable) {
        Set<Property> properties = new HashSet();
        properties.addAll(variables.keySet());
        properties.add(unknownVariable);
        
        // If unknown is quality
        if (unknownVariable.equals(QUALITY)) {
            return new EquationBounds(0.0, 1.0);
        }
        else if (unknownVariable.equals(PRESSURE)) {
            return new EquationBounds(P_min,IF97.pc*1e6);
        }
        else if (unknownVariable.equals(TEMPERATURE)) {
            return new EquationBounds(T_min,IF97.Tc);
        }
        else if (unknownVariable.equals(ENTHALPY)) {
            if (variables.containsKey(PRESSURE)) {
                return new EquationBounds(if97.specificEnthalpySaturatedLiquidP(variables.get(PRESSURE).getAsDouble()),if97.specificEnthalpySaturatedVapourP(variables.get(PRESSURE).getAsDouble()));
            }
            else if (variables.containsKey(TEMPERATURE)) {
                return new EquationBounds(if97.specificEnthalpySaturatedLiquidT(variables.get(TEMPERATURE).getAsDouble()),if97.specificEnthalpySaturatedVapourT(variables.get(TEMPERATURE).getAsDouble()));
            }
        }
        else if (unknownVariable.equals(ENTROPY)) {
            if (variables.containsKey(PRESSURE)) {
                return new EquationBounds(if97.specificEntropySaturatedLiquidP(variables.get(PRESSURE).getAsDouble()),if97.specificEnthalpySaturatedVapourP(variables.get(PRESSURE).getAsDouble()));
            }
            else if (variables.containsKey(TEMPERATURE)) {
                return new EquationBounds(if97.specificEntropySaturatedLiquidT(variables.get(TEMPERATURE).getAsDouble()),if97.specificEnthalpySaturatedVapourT(variables.get(TEMPERATURE).getAsDouble()));
            }
        }
        return new EquationBounds(Double.NaN,Double.NaN);
    }
    
    @Override
    public ReportDataBlock getReportData() {
        ReportDataBlock rdb = new ReportDataBlock(name);
        rdb.addData("Formulation", "IF97");
        return rdb;
    }
    
    protected boolean isSaturated(Map<Property, OptionalDouble> variables) {
        if (variables.get(TEMPERATURE).getAsDouble() < IF97.Tc) {
            return (if97.saturationPressureT(variables.get(TEMPERATURE).getAsDouble()) == variables.get(PRESSURE).getAsDouble());
        }
        return false;
    }
    
    /**
     * Calculates the thermodynamic quality 
     * @param h the mixture enthalpy.
     * @param hf the enthalpy of saturated liquid.
     * @param hg the enthalpy of saturated vapour.
     * @return the thermodynamic quality
     */
    private double quality(double h, double hf, double hg) {
        return (h-hf)/(hg-hf);
    }
    
    private class P_HS extends FluidEquation {
        
        public P_HS() {
            super(FluidEquation.equationString(PRESSURE, ENTHALPY, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(PRESSURE).getAsDouble() - if97.pressureHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble());
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, PRESSURE, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class T_HS extends FluidEquation {
        
        public T_HS() {
            super(FluidEquation.equationString(TEMPERATURE, ENTHALPY, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(TEMPERATURE).getAsDouble() - if97.temperatureHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble());
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, TEMPERATURE, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class R_HS extends FluidEquation {
        
        public R_HS() {
            super(FluidEquation.equationString(DENSITY, ENTHALPY, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(DENSITY).getAsDouble() - if97.densityHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble());
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, DENSITY, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class U_HS extends FluidEquation {
        
        public U_HS() {
            super(FluidEquation.equationString(ENERGY, ENTHALPY, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENERGY).getAsDouble() - if97.specificInternalEnergyHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble());
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENERGY, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class R_PT extends FluidEquation {
        
        public R_PT() {
            super(FluidEquation.equationString(DENSITY, PRESSURE, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if (isSaturated(variables)) {
                return Double.NaN;
            }
            return variables.get(DENSITY).getAsDouble() - if97.densityPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble());
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, DENSITY, PRESSURE, TEMPERATURE);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsPT(variables, unknownVariable);
        }
        
    }
    
    private class H_PT extends FluidEquation {
        
        public H_PT() {
            super(FluidEquation.equationString(ENTHALPY, PRESSURE, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if (isSaturated(variables)) {
                return Double.NaN;
            }
            return variables.get(ENTHALPY).getAsDouble() - if97.specificEnthalpyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble());
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTHALPY, PRESSURE, TEMPERATURE);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsPT(variables, unknownVariable);
        }
               
    }
    
    private class S_PT extends FluidEquation {
        
        public S_PT() {
            super(FluidEquation.equationString(ENTROPY, PRESSURE, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if (isSaturated(variables)) {
                return Double.NaN;
            }
            return variables.get(ENTROPY).getAsDouble() - if97.specificEntropyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble());
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTROPY, PRESSURE, TEMPERATURE);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsPT(variables, unknownVariable);
        }
        
    }
    
    private class U_PT extends FluidEquation {
        
        public U_PT() {
            super(FluidEquation.equationString(ENERGY, PRESSURE, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if (isSaturated(variables)) {
                return Double.NaN;
            }
            return variables.get(ENERGY).getAsDouble() - if97.specificInternalEnergyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble());
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENERGY, PRESSURE, TEMPERATURE);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsPT(variables, unknownVariable);
        }
        
    }
    
    private class G_PT extends FluidEquation {
        
        public G_PT() {
            super(FluidEquation.equationString(GIBBS, PRESSURE, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if (isSaturated(variables)) {
                return Double.NaN;
            }
            return variables.get(GIBBS).getAsDouble() - if97.specificGibbsFreeEnergyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble());
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, GIBBS, PRESSURE, TEMPERATURE);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsPT(variables, unknownVariable);
        }
        
    }
    
    private class X_HS extends FluidEquation {
        
        public X_HS() {
            super(FluidEquation.equationString(QUALITY, ENTHALPY, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if ((variables.get(QUALITY).getAsDouble() < 0.0) || (variables.get(QUALITY).getAsDouble() > 1.0)) {
                return Double.NaN;
            }
            double pressure = if97.pressureHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble());
            if (pressure > IF97.pc*1e6) {
                return Double.NaN;
            }
            double hLiquid = if97.specificEnthalpySaturatedLiquidP(pressure);
            double hVapour = if97.specificEnthalpySaturatedVapourP(pressure);
            return variables.get(QUALITY).getAsDouble() - quality(variables.get(ENTHALPY).getAsDouble(),hLiquid,hVapour);
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, ENTHALPY, ENTROPY);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsX(variables, unknownVariable);
        }
        
    }
    
    private class X_PH extends FluidEquation {

        public X_PH() {
            super(FluidEquation.equationString(QUALITY, PRESSURE, ENTHALPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if ((variables.get(QUALITY).getAsDouble() < 0.0) || (variables.get(QUALITY).getAsDouble() > 1.0)) {
                return Double.NaN;
            }
            if (variables.get(PRESSURE).getAsDouble() > IF97.pc*1e6) {
                return Double.NaN;
            }
            double hLiquid = if97.specificEnthalpySaturatedLiquidP(variables.get(PRESSURE).getAsDouble());
            double hVapour = if97.specificEnthalpySaturatedVapourP(variables.get(PRESSURE).getAsDouble());
            return variables.get(QUALITY).getAsDouble() - quality(variables.get(ENTHALPY).getAsDouble(),hLiquid,hVapour);
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, PRESSURE, ENTHALPY);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsX(variables, unknownVariable);
        }
        
    }
    
    private class X_PS extends FluidEquation {

        public X_PS() {
            super(FluidEquation.equationString(QUALITY, PRESSURE, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if ((variables.get(QUALITY).getAsDouble() < 0.0) || (variables.get(QUALITY).getAsDouble() > 1.0)) {
                return Double.NaN;
            }
            if (variables.get(PRESSURE).getAsDouble() > IF97.pc*1e6) {
                return Double.NaN;
            }
            double hMixture = if97.specificEnthalpyPS(variables.get(PRESSURE).getAsDouble(), variables.get(ENTROPY).getAsDouble());
            double hLiquid = if97.specificEnthalpySaturatedLiquidP(variables.get(PRESSURE).getAsDouble());
            double hVapour = if97.specificEnthalpySaturatedVapourP(variables.get(PRESSURE).getAsDouble());
            return variables.get(QUALITY).getAsDouble() - quality(hMixture,hLiquid,hVapour);
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, PRESSURE, ENTROPY);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsX(variables, unknownVariable);
        }
        
    }
    
    private class X_TS extends FluidEquation {

        public X_TS() {
            super(FluidEquation.equationString(QUALITY, TEMPERATURE, ENTROPY));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            if ((variables.get(QUALITY).getAsDouble() < 0.0) || (variables.get(QUALITY).getAsDouble() > 1.0)) {
                return Double.NaN;
            }
            if (variables.get(TEMPERATURE).getAsDouble() > IF97.Tc) {
                return Double.NaN;
            }
            double hMixture = if97.specificEnthalpyTX(variables.get(TEMPERATURE).getAsDouble(), variables.get(QUALITY).getAsDouble());
            double hLiquid = if97.specificEnthalpySaturatedLiquidT(variables.get(TEMPERATURE).getAsDouble());
            double hVapour = if97.specificEnthalpySaturatedVapourT(variables.get(TEMPERATURE).getAsDouble());
            return variables.get(QUALITY).getAsDouble() - quality(hMixture,hLiquid,hVapour);
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, PRESSURE, ENTROPY);
        }
        
        @Override
        protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
            return getBoundsX(variables, unknownVariable);
        }
        
    }
    
}