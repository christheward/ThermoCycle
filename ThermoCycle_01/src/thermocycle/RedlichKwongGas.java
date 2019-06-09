/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.HashSet;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import report.ReportDataBlock;
import utilities.DimensionedDouble;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class RedlichKwongGas extends Fluid {
    
    /**
     * The critical pressure.
     */
    private final double Pc;
    
    /**
     * The critical temperature.
     */
    private final double Tc;
    
    /**
     * The critical volume.
     */
    private final double Vc;
    
    /**
     * The molar mass.
     */
    private double M;
    
    /**
     * Constant a
     */
    private double a;
    
    /**
     * Constant b
     */
    private double b;
    
    /**
     * Constnant c
     */
    private double c;
    
    /**
     * Constant C
     */
    private double C;
    
    public RedlichKwongGas(String name, double M, double Pc, double Tc) {
        super(name);
        this.M = M;
        this.Pc = Pc;
        this.Tc = Tc;
        this.Vc = (Ru*Tc)/(3*Pc);
        this.a = 0.42748*Math.pow(Ru, 2.0)*Math.pow(Tc, 2.5)/Pc;
        this.b = 0.08664*Ru*Tc/Pc;
        this.c = 0.259921;
        this.C = -7.07228 - 1.5*Math.log(M*Tc) * Math.log(Vc/1e6);
        equations.add(new P_VmT());
    }

    @Override
    public Set<Property> getAllowableProperties() {
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
        rdb.addData("Pc", DimensionedDouble.valueOfSI(Pc, UNITS_TYPE.PRESSURE));
        rdb.addData("Tc", DimensionedDouble.valueOfSI(Tc, UNITS_TYPE.TEMPERATURE));
        return rdb;
    }
    
    
    // P = f(Vm, T)
    private class P_VmT extends FluidEquation {

        public P_VmT() {
            super(FluidEquation.equationString(PRESSURE, MOLVOL, TEMPERATURE));
        }
        
        @Override
        protected Double function(Map<Property, OptionalDouble> variables) {
            return variables.get(PRESSURE).getAsDouble() - ((RedlichKwongGas.Ru*variables.get(TEMPERATURE).getAsDouble())/(variables.get(MOLVOL).getAsDouble()-b) - a/(Math.sqrt(variables.get(TEMPERATURE).getAsDouble())*variables.get(MOLVOL).getAsDouble()*(variables.get(MOLVOL).getAsDouble() + b)));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, PRESSURE, MOLVOL, TEMPERATURE);
        }
        
    }
    
}
