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
import static thermocycle.Properties.Property.*;
import utilities.DimensionedDouble;
import utilities.Units;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class RedlichKwong extends Fluid {
    
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
    
    public RedlichKwong(String name, double M, double Pc, double Tc) {
        super(name);
        this.M = M;
        this.Pc = Pc;
        this.Tc = Tc;
        this.Vc = (R*Tc)/(3*Pc);
        this.a = 0.42748*Math.pow(R, 2.0)*Math.pow(Tc, 2.5)/Pc;
        this.b = 0.08664*R*Tc/Pc;
        this.c = 0.259921;
        this.C = -7.07228 - 1.5*Math.log(M*Tc) * Math.log(Vc/1e6);
        equations.add(new H_T());
        equations.add(new U_T());
        equations.add(new S_TP());
        equations.add(new S_TV());
        equations.add(new S_PV());
    }

    @Override
    public Set<Properties.Property> getAllowableProperties() {
        Set<Properties.Property> fluidState = new HashSet();
        fluidState.add(PRESSURE);
        fluidState.add(TEMPERATURE);
        fluidState.add(VOLUME);
        fluidState.add(DENSITY);
        fluidState.add(ENTROPY);
        fluidState.add(ENERGY);
        fluidState.add(ENTHALPY);
        fluidState.add(GIBBS);
        fluidState.add(HELMHOLTZ);
        return fluidState;
    }

    @Override
    protected Double initialGuess(Properties.Property property) {
        return 1000.0;
    }

    @Override
    public ReportDataBlock getReportData() {
        ReportDataBlock rdb = new ReportDataBlock(name);
        rdb.addData("Pc", DimensionedDouble.valueOfSI(Pc, Units.UNITS_TYPE.PRESSURE));
        rdb.addData("Tc", DimensionedDouble.valueOfSI(Tc, Units.UNITS_TYPE.TEMPERATURE));
        return rdb;
    }
    
    
    // P = f(Vm, T)
    private class P_VmT extends FluidEquation {

        public P_VmT() {
            super(RedlichKwong.this, FluidEquation.equationString(PRESSURE, MOLVOL, TEMPERATURE), PRESSURE.convergenceTolerance);
        }
        
        @Override
        protected OptionalDouble function(Map<Properties.Property, OptionalDouble> variables) {
            return OptionalDouble.of(variables.get(PRESSURE).getAsDouble() - ((RedlichKwong.R*variables.get(TEMPERATURE).getAsDouble())/(variables.get(MOLVOL).getAsDouble()-b) - a/(Math.sqrt(variables.get(TEMPERATURE).getAsDouble())*variables.get(MOLVOL).getAsDouble()*(variables.get(MOLVOL).getAsDouble() + b))));
        }

        @Override
        protected Map<Properties.Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, PRESSURE, MOLVOL, TEMPERATURE);
        }
        
    }
    
    private class G_ extends FluidEquation {

        public G_() {
            super(fluid, name, limit);
        }

        @Override
        protected OptionalDouble function(Map<Properties.Property, OptionalDouble> variables) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Map<Properties.Property, OptionalDouble> getVariables(State state) {
            return getVariables(state, GIBBS, MOLVOL, TEMPERATURE);
        }
        
    }
    
}
