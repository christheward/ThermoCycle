/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.fluids;

import utilities.DimensionedDouble;
import thermocycle.report.ReportDataBlock;
import java.util.*;
import thermocycle.Fluid;
import thermocycle.FluidEquation;
import thermocycle.Property;
import thermocycle.State;
import thermocycle.UnitsControl.UnitsType;

/**
 *
 * @author Chris
 */
public final class IdealGas extends Fluid {
    
    /**
     * The ratio of specific heats.
     */
    private final double gamma;
    
    /**
     * The specific gas constant.
     */
    private final double R;
    
    /**
     * The molar mass of the gas.
     */
    private final double M;
    
    /**
     * Specific heat capacity and constant volume.
     */
    private final double Cv;
    
    /**
     * Specific heat capacity at constant pressure.
     */
    private final double Cp;
    
    /**
     * Constructor
     * @param name The fluid name.
     * @param gamma The fluid ratio of specific heats.
     * @param Rs The fluid specific gas constant.
     */
    public IdealGas(String name, double gamma, double R) {
        super(name);
        this.gamma = gamma;
        this.R = R;
        this.M = Ru/R;
        this.Cv = R / (gamma - 1);
        this.Cp = R * gamma / (gamma - 1);
        equations.add(new P_RT());
        equations.add(new H_T());
        equations.add(new U_T());
        equations.add(new S_TP());
        equations.add(new S_TV());
        equations.add(new S_PV());
    }
    
    @Override
    public final Set<Property> getAllowableProperties() {
        Set<Property> fluidState = new HashSet();
        fluidState.add(Fluid.PRESSURE);
        fluidState.add(Fluid.TEMPERATURE);
        fluidState.add(Fluid.VOLUME);
        fluidState.add(Fluid.DENSITY);
        fluidState.add(Fluid.ENTROPY);
        fluidState.add(Fluid.ENERGY);
        fluidState.add(Fluid.ENTHALPY);
        fluidState.add(Fluid.GIBBS);
        fluidState.add(Fluid.HELMHOLTZ);
        return fluidState;
    }
    
    
    @Override
    public ReportDataBlock getReportData() {
        ReportDataBlock rdb = new ReportDataBlock(name);
        rdb.addData("R", DimensionedDouble.valueOfSI(R, UnitsType.SPECIFIC_ENERGY));
        rdb.addData("Gamma", DimensionedDouble.valueOfSI(gamma, UnitsType.DIMENSIONLESS));
        rdb.addData("Cp", DimensionedDouble.valueOfSI(Cp, UnitsType.ENTROPY));
        rdb.addData("Cv", DimensionedDouble.valueOfSI(Cv, UnitsType.ENTROPY));
        return rdb;
    }
    
    // P = rho R T
    private class P_RT extends FluidEquation {

        public P_RT() {
            super(FluidEquation.equationString(PRESSURE, DENSITY, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(PRESSURE).getAsDouble() - variables.get(DENSITY).getAsDouble()*IdealGas.this.R*variables.get(TEMPERATURE).getAsDouble();
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, PRESSURE, DENSITY, TEMPERATURE);
        }
        
    }
    
    // U = Cv T
    private class U_T extends FluidEquation {

        public U_T() {
            super(FluidEquation.equationString(ENERGY, TEMPERATURE));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENERGY, TEMPERATURE);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENERGY).getAsDouble() - IdealGas.this.Cv*variables.get(TEMPERATURE).getAsDouble();
        }
    }
    
    // H = Cp T
    private class H_T extends FluidEquation {

        public H_T() {
            super(FluidEquation.equationString(ENTHALPY, TEMPERATURE));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTHALPY, TEMPERATURE);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENTHALPY).getAsDouble() - IdealGas.this.Cp*variables.get(TEMPERATURE).getAsDouble();
        }
    }
    
    
    // S = Cv ln(T) + Rs ln(V) - Rs ln(Rs)
    private class S_TV extends FluidEquation {

        public S_TV() {
            super(FluidEquation.equationString(ENTROPY, TEMPERATURE, VOLUME));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTROPY, TEMPERATURE, VOLUME);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENTROPY).getAsDouble() - IdealGas.this.Cv*Math.log(variables.get(TEMPERATURE).getAsDouble()) - IdealGas.this.R*Math.log(variables.get(VOLUME).getAsDouble()) + IdealGas.this.R*Math.log(IdealGas.this.R);
        }
    }
    
    // S = Cp ln(T) - Rs ln(P)
    private class S_TP extends FluidEquation {

        public S_TP() {
            super(FluidEquation.equationString(ENTROPY, TEMPERATURE, PRESSURE));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTROPY, TEMPERATURE, PRESSURE);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENTROPY).getAsDouble() - IdealGas.this.Cp*Math.log(variables.get(TEMPERATURE).getAsDouble()) + IdealGas.this.R*Math.log(variables.get(PRESSURE).getAsDouble());
        }
    }
    
    // S = Cv log(P) + Cp log(V) - Cp log(Rs)
    private class S_PV extends FluidEquation {

        public S_PV() {
            super(FluidEquation.equationString(ENTROPY, PRESSURE, VOLUME));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, ENTROPY, PRESSURE, VOLUME);
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(ENTROPY).getAsDouble() - IdealGas.this.Cv*Math.log(variables.get(PRESSURE).getAsDouble()) - IdealGas.this.Cp*Math.log(variables.get(VOLUME).getAsDouble()) + IdealGas.this.Cp*Math.log(IdealGas.this.R);
        }
    }
    
}