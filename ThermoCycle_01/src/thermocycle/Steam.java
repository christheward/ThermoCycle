/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import report.ReportDataBlock;
import java.util.*;
import thermocycle.Properties.Property;
import static thermocycle.Properties.Property.*;

import com.hummeling.if97.IF97;
import java.util.stream.Collectors;

/**
 *
 * @author Chris
 */
public final class Steam extends Fluid {
    
    private static IF97 if97 = new IF97(IF97.UnitSystem.SI);
    
    /**
     * Constructor.
     */
    protected Steam() {
        super("Steam");
        equations.add(new P_HS());
        equations.add(new T_HS());
        equations.add(new R_HS());
        equations.add(new X_HS());
        equations.add(new U_HS());
        //equations.add(new H_PX());
        equations.add(new G_PT());
        
    }
    
    @Override
    public final Set<Property> getAllowableProperties() {
        Set<Property> fluidState = new HashSet<>();
        fluidState.add(PRESSURE);
        fluidState.add(TEMPERATURE);
        fluidState.add(VOLUME);
        fluidState.add(ENTROPY);
        fluidState.add(ENERGY);
        fluidState.add(ENTHALPY);
        fluidState.add(GIBBS);
        fluidState.add(HELMHOLTZ);
        fluidState.add(QUALITY);
        return fluidState;
    }
    
    @Override
    public ReportDataBlock getReportData() {
        ReportDataBlock rdb = new ReportDataBlock(name);
        rdb.addData("Formulation", "IF97");
        return rdb;
    }
    
    private class P_HS extends FluidEquation {
        
        public P_HS() {
            super(FluidEquation.equationString(PRESSURE, ENTHALPY, ENTROPY), PRESSURE.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(PRESSURE).getAsDouble() - if97.pressureHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble()));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, PRESSURE, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class T_HS extends FluidEquation {
        
        public T_HS() {
            super(FluidEquation.equationString(TEMPERATURE, ENTHALPY, ENTROPY), TEMPERATURE.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(TEMPERATURE).getAsDouble() - if97.temperatureHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble()));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, TEMPERATURE, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class R_HS extends FluidEquation {
        
        public R_HS() {
            super(FluidEquation.equationString(DENSITY, ENTHALPY, ENTROPY), DENSITY.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(DENSITY).getAsDouble() - if97.densityHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble()));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, DENSITY, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class U_HS extends FluidEquation {
        
        public U_HS() {
            super(FluidEquation.equationString(ENERGY, ENTHALPY, ENTROPY), ENERGY.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(ENERGY).getAsDouble() - if97.specificInternalEnergyHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble()));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENERGY, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class R_PT extends FluidEquation {
        
        public R_PT() {
            super(FluidEquation.equationString(DENSITY, PRESSURE, TEMPERATURE), DENSITY.convergenceTolerance);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (if97.saturationPressureT(variables.get(TEMPERATURE).getAsDouble()) == variables.get(PRESSURE).getAsDouble()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(variables.get(DENSITY).getAsDouble() - if97.densityPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble()));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, DENSITY, PRESSURE, TEMPERATURE);
        }
        
    }
    
    private class H_PT extends FluidEquation {
        
        public H_PT() {
            super(FluidEquation.equationString(ENTHALPY, PRESSURE, TEMPERATURE), ENTHALPY.convergenceTolerance);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (if97.saturationPressureT(variables.get(TEMPERATURE).getAsDouble()) == variables.get(PRESSURE).getAsDouble()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(variables.get(ENTHALPY).getAsDouble() - if97.specificEnthalpyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble()));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTHALPY, PRESSURE, TEMPERATURE);
        }
        
    }
    
    private class S_PT extends FluidEquation {
        
        public S_PT() {
            super(FluidEquation.equationString(ENTROPY, PRESSURE, TEMPERATURE), ENTHALPY.convergenceTolerance);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (if97.saturationPressureT(variables.get(TEMPERATURE).getAsDouble()) == variables.get(PRESSURE).getAsDouble()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(variables.get(ENTROPY).getAsDouble() - if97.specificEntropyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble()));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTROPY, PRESSURE, TEMPERATURE);
        }
        
    }
    
    private class U_PT extends FluidEquation {
        
        public U_PT() {
            super(FluidEquation.equationString(ENERGY, PRESSURE, TEMPERATURE), ENERGY.convergenceTolerance);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (if97.saturationPressureT(variables.get(TEMPERATURE).getAsDouble()) == variables.get(PRESSURE).getAsDouble()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(variables.get(ENERGY).getAsDouble() - if97.specificInternalEnergyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble()));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENERGY, PRESSURE, TEMPERATURE);
        }
        
    }
    
    private class G_PT extends FluidEquation {
        
        public G_PT() {
            super(FluidEquation.equationString(GIBBS, PRESSURE, TEMPERATURE), GIBBS.convergenceTolerance);
        }

        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (if97.saturationPressureT(variables.get(TEMPERATURE).getAsDouble()) == variables.get(PRESSURE).getAsDouble()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(variables.get(GIBBS).getAsDouble() - if97.specificGibbsFreeEnergyPT(variables.get(PRESSURE).getAsDouble(), variables.get(TEMPERATURE).getAsDouble()));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, GIBBS, PRESSURE, TEMPERATURE);
        }
        
    }
    
    private class X_HS extends FluidEquation {
        
        public X_HS() {
            super(FluidEquation.equationString(QUALITY, ENTHALPY, ENTROPY), QUALITY.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            double pressure = if97.pressureHS(variables.get(ENTHALPY).getAsDouble(), variables.get(ENTROPY).getAsDouble());
            if (pressure > IF97.pc) {
                return OptionalDouble.empty();
            }
            double hLiquid = if97.specificEnthalpySaturatedLiquidP(pressure);
            double hVapour = if97.specificEnthalpySaturatedVapourP(pressure);
            return OptionalDouble.of(variables.get(QUALITY).getAsDouble() - quality(variables.get(ENTHALPY).getAsDouble(),hLiquid,hVapour));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class X_PH extends FluidEquation {

        public X_PH() {
            super(FluidEquation.equationString(QUALITY, PRESSURE, ENTHALPY), QUALITY.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (variables.get(PRESSURE).getAsDouble() > IF97.pc) {
                return OptionalDouble.empty();
            }
            double hLiquid = if97.specificEnthalpySaturatedLiquidP(variables.get(PRESSURE).getAsDouble());
            double hVapour = if97.specificEnthalpySaturatedVapourP(variables.get(PRESSURE).getAsDouble());
            return OptionalDouble.of(variables.get(QUALITY).getAsDouble() - quality(variables.get(ENTHALPY).getAsDouble(),hLiquid,hVapour));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, ENTHALPY, ENTROPY);
        }
        
    }
    
    private class X_PS extends FluidEquation {

        public X_PS() {
            super(FluidEquation.equationString(QUALITY, PRESSURE, ENTROPY), QUALITY.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Property, OptionalDouble> variables) {
            if (variables.get(PRESSURE).getAsDouble() > IF97.pc) {
                return OptionalDouble.empty();
            }
            double hMixture = if97.specificEnthalpyPS(variables.get(PRESSURE).getAsDouble(), variables.get(ENTROPY).getAsDouble());
            double hLiquid = if97.specificEnthalpySaturatedLiquidP(variables.get(PRESSURE).getAsDouble());
            double hVapour = if97.specificEnthalpySaturatedVapourP(variables.get(PRESSURE).getAsDouble());
            return OptionalDouble.of(variables.get(QUALITY).getAsDouble() - quality(hMixture,hLiquid,hVapour));
        }
        
        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, QUALITY, ENTHALPY, ENTROPY);
        }
        
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
    
}