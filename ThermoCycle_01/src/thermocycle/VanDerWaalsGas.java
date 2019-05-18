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

/**
 *
 * @author Chris
 */
final class VanDerWaalsGas extends Fluid {
    
    // variables
    private double a;
    private double b;
    private double M;
    private double cv;
    
    // constructor
    VanDerWaalsGas(String name, double a, double b, double cv, double M) {
        super(name);
        this.a = a;     // Correction factor a
        this.b = b;     // Correction factor b
        this.M = M;     // Molar mass
    }
    
    // getters
    double getA() {return this.a;}
    double getB() {return this.b;}
    double getM() {return this.M;}
    double getCv() {return this.cv;}
    double getCp(State state) {return 0.0;}
    
    // setters
    void setAB(double a, double b) {}
    
    // abstract methods
    @Override
    public Set<Property> getAllowableProperties() {
        Set<Property> fluidState = new HashSet<>();
        fluidState.add(PRESSURE);
        fluidState.add(TEMPERATURE);
        fluidState.add(VOLUME);
        fluidState.add(ENTROPY);
        fluidState.add(ENERGY);
        fluidState.add(ENTHALPY);
        fluidState.add(GIBBS);
        fluidState.add(HELMHOLTZ);
        return fluidState;
    }
    
    @Override
    public ReportDataBlock getReportData() {
        ReportDataBlock rdb = new ReportDataBlock(name);
        rdb.addData("A", Double.toString(getA()));
        rdb.addData("B", Double.toString(getB()));
        rdb.addData("M", Double.toString(getM()));
        rdb.addData("Cv", Double.toString(getCv()));
        return rdb;
    }
    
    // private methods - state relationships
    // (P + a/(V/M)^2)(V/M - b) = R.T
    // U = Cv.T - a/(V.M);
    // H = Cv.T + R.T.Vm/(Vm-b) - 2.a.(V/Vm)/Vm
    // S = R ln((Vm-b)/(-b)) + cRln((u + a/Vm)/(a/Vm))
    
}