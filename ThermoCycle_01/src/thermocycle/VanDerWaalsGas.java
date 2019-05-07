/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

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
    protected void computeState(State state) {
        Set<Property> unknowns = this.getAllowableProperties();
        unknowns.removeAll(state.properties());
        do {
            unknowns.forEach(p -> {
                switch (p) {
                    case TEMPERATURE: {this.calcT(state); break;}
                    case PRESSURE: {this.calcP(state); break;}
                    case VOLUME: {this.calcV(state); break;}
                    case ENTROPY: {this.calcS(state); break;}
                    case ENERGY: {this.calcU(state); break;}
                    case ENTHALPY: {this.calcH(state); break;}
                    case HELMHOLTZ: {this.calcF(state); break;}
                    case GIBBS: {this.calcG(state); break;}
                }});
        } while (unknowns.removeAll(state.properties()));
    }
    
    // private methods - state relationships
    // (P + a/(V/M)^2)(V/M - b) = R.T
    // U = Cv.T - a/(V.M);
    // H = Cv.T + R.T.Vm/(Vm-b) - 2.a.(V/Vm)/Vm
    // S = R ln((Vm-b)/(-b)) + cRln((u + a/Vm)/(a/Vm))
    @Override
    protected void calcT(State state) {
        super.calcT(state);
    }
    @Override
    protected void calcP(State state) {
        super.calcP(state);
    }
    @Override
    protected void calcV(State state) {
        super.calcV(state);
    }
    @Override
    protected void calcS(State state) {
        super.calcS(state);
    }
    @Override
    protected void calcU(State state) {
        super.calcU(state);
    }
    @Override
    protected void calcH(State state) {
    }
    @Override
    protected void calcF(State state) {
    }
    @Override
    protected void calcG(State state) {
    }
}