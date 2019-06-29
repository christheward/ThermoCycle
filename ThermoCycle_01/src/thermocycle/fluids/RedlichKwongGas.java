/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.fluids;

import java.util.HashSet;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import thermocycle.Fluid;
import thermocycle.FluidEquation;
import thermocycle.Property;
import thermocycle.State;
import thermocycle.report.ReportDataBlock;
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
     * Constant alpah0.
     */
    private final double alpha0;
    
    /**
     * Specific gas constant.
     */
    private final double R;
    
    /**
     * The molar mass.
     */
    private final double M;
    
    /**
     * Constant b
     */
    private final double b;
    
    /**
     * Constant c
     */
    private final double c;
    
    /**
     * Acentric factor;
     */
    private final double omega;
    
    /**
     * Constant n.
     */
    private final double n;
    
    /**
     * Constant C
     */
    private double C;
    
    public RedlichKwongGas(String name, double M, double Pc, double Tc, double Vc, double omega) {
        super(name);
        this.M = M;
        this.R = Ru/M;
        this.Pc = Pc;
        this.Tc = Tc;
        this.Vc = Vc;
        this.omega = omega;
        this.alpha0 = 0.42747*Math.pow(R*Tc,2)/Pc;
        this.b = 0.08664*Ru*Tc/Pc;
        this.n = 0.4986+1.1735*omega+0.4754*Math.pow(omega,2);
        this.c = R*Tc/(Pc + alpha0/(Vc*(Vc+b))) + b - Vc;
        equations.add(new P_VmT());
    }
    
    
    private double alpha(double temperature) {
        return alpha0*Math.pow(temperature/Tc,-n);
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
            return variables.get(PRESSURE).getAsDouble() - ((RedlichKwongGas.Ru*variables.get(TEMPERATURE).getAsDouble())/(variables.get(MOLVOL).getAsDouble()-b) - alpha0/(Math.sqrt(variables.get(TEMPERATURE).getAsDouble())*variables.get(MOLVOL).getAsDouble()*(variables.get(MOLVOL).getAsDouble() + b)));
        }

        @Override
        protected Map<Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, PRESSURE, MOLVOL, TEMPERATURE);
        }
        
    }
    
}
