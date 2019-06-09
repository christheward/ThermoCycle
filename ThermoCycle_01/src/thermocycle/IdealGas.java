/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import utilities.DimensionedDouble;
import report.ReportDataBlock;
import java.util.*;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris
 */
public final class IdealGas extends Fluid {
    
    /**
     * The ratio of specific heats.
     */
    private double gamma;
    
    /**
     * The specific gas constant.
     */
    private double Rs;
    
    /**
     * Constructor
     * @param name The fluid name.
     * @param gamma The fluid ratio of specific heats.
     * @param Rs The fluid specific gas constant.
     */
    protected IdealGas(String name, double gamma, double Rs) {
        super(name);
        this.gamma = gamma;
        this.Rs = Rs;
        equations.add(new P_RT());
        equations.add(new H_T());
        equations.add(new U_T());
        equations.add(new S_TP());
        equations.add(new S_TV());
        equations.add(new S_PV());
    }
    
    /**
     * Gets the specific heat capacity at constant pressure.
     * @return Returns the heat capacity and constant pressure.
     */
    double getCp() {
        return Rs * gamma / (gamma - 1);
    }
    
    /**
     * Gets the specific heat capacity at constant volume.
     * @return Returns the heat capacity and constant volume.
     */
    double getCv() {
        return Rs / (gamma - 1);
    }
    
    /**
     * Gets the ratio of specific heats.
     * @return Returns the ratio of specific heats.
     */
    double getGa() {
        return gamma;
    }
    
    /**
     * Gets the specific gas constant.
     * @return Returns the specific gas constant.
     */
    double getRs() {
        return Rs;
    }
    
    /**
     * Sets the ratio of specific heats.
     * @param value The value to set.
     */
    void setGamma(double value) {
        gamma = value;
    }
    
    /**
     * Sets the specific gas constant.
     * @param value The value to set.
     */
    void setRs(double value) {
        Rs = value;
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
        rdb.addData("R", DimensionedDouble.valueOfSI(getRs(), UNITS_TYPE.SPECIFIC_ENERGY));
        rdb.addData("Gamma", DimensionedDouble.valueOfSI(getGa(), UNITS_TYPE.DIMENSIONLESS));
        rdb.addData("Cp", DimensionedDouble.valueOfSI(getCp(), UNITS_TYPE.ENTROPY));
        rdb.addData("Cv", DimensionedDouble.valueOfSI(getCv(), UNITS_TYPE.ENTROPY));
        return rdb;
    }
    
    // P = rho R T
    private class P_RT extends FluidEquation {

        public P_RT() {
            super(FluidEquation.equationString(PRESSURE, DENSITY, TEMPERATURE));
        }

        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(PRESSURE).getAsDouble() - variables.get(DENSITY).getAsDouble()*IdealGas.this.Rs*variables.get(TEMPERATURE).getAsDouble();
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
            return variables.get(ENERGY).getAsDouble() - IdealGas.this.getCv()*variables.get(TEMPERATURE).getAsDouble();
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
            return variables.get(ENTHALPY).getAsDouble() - IdealGas.this.getCp()*variables.get(TEMPERATURE).getAsDouble();
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
            return variables.get(ENTROPY).getAsDouble() - IdealGas.this.getCv()*Math.log(variables.get(TEMPERATURE).getAsDouble()) - IdealGas.this.Rs*Math.log(variables.get(VOLUME).getAsDouble()) + IdealGas.this.Rs*Math.log(IdealGas.this.Rs);
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
            return variables.get(ENTROPY).getAsDouble() - IdealGas.this.getCp()*Math.log(variables.get(TEMPERATURE).getAsDouble()) + IdealGas.this.Rs*Math.log(variables.get(PRESSURE).getAsDouble());
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
            return variables.get(ENTROPY).getAsDouble() - IdealGas.this.getCv()*Math.log(variables.get(PRESSURE).getAsDouble()) - IdealGas.this.getCp()*Math.log(variables.get(VOLUME).getAsDouble()) + IdealGas.this.getCp()*Math.log(IdealGas.this.Rs);
        }
    }
    
}