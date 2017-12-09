/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Properties.Property.*;

/**
 *
 * @author Chris
 */
public final class IdealGas extends Fluid {
    
    // variables
    private double gamma;       // Ratio of specific heats
    private double Rs;           // Specific gas constant
    
    // constructor
    IdealGas(String name, double gamma, double Rs) {
        super(name);
        this.gamma = gamma;
        this.Rs = Rs;
    }
    
    // getters
    //OptionalValue getCp() {return Rs.multiply(gamma).divide(gamma.sub(1));}
    //OptionalValue getCv() {return Rs.divide(gamma.sub(1));}
    //OptionalValue getGa() {return gamma;}
    //OptionalValue getRs() {return Rs;}
    double getCp() {return Rs * gamma / (gamma - 1);}
    double getCv() {return Rs / (gamma - 1);}
    double getGa() {return gamma;}
    double getRs() {return Rs;}
    
    // setters
    void setGamma(double value) {gamma = value;}
    void setRs(double value) {Rs = value;}
    
    // methods
    @Override
    Set<Property> fluidState() {
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
    void computeState(State state) {
        Set<Property> unknowns = fluidState();
        unknowns.removeAll(state.properties());
        do {
            unknowns.forEach(p -> {
                switch (p) {
                    case TEMPERATURE: {calcT(state); break;}
                    case PRESSURE: {calcP(state); break;}
                    case VOLUME: {calcV(state); break;}
                    case ENTROPY: {calcS(state); break;}
                    case ENERGY: {calcU(state); break;}
                    case ENTHALPY: {calcH(state); break;}
                    case HELMHOLTZ: {calcF(state); break;}
                    case GIBBS: {calcG(state); break;}
                    }
            });
        } while (unknowns.removeAll(state.properties()));
    }
    
    // private methods - state relationships
    // P.V = Rs.T
    // U = Cv.T
    // H = Cp.T
    // S = Cp.ln(T) - Rs.ln(P)
    // S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs)
    // S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
    @Override
    void calcT(State state) {
        super.calcT(state);
        if (!state.contains(TEMPERATURE)) {
            // T = H / Cp
            if (state.contains(ENTHALPY)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of(state.get(ENTHALPY).getAsDouble() / getCp()));
            }
            // T = U / Cv
            else if (state.contains(ENERGY)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of(state.get(ENERGY).getAsDouble() / getCv()));
            }
            // T = P.V / Rs
            else if (state.contains(PRESSURE, VOLUME)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of(state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() / getRs()));
            }
            // T = e^((S - Rs.log(V) + Rs.log(Rs)) / Cv )
            else if (state.contains(ENTROPY, VOLUME)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of(Math.exp((state.get(ENTROPY).getAsDouble() - getRs() * Math.log(state.get(VOLUME).getAsDouble()) + getRs() * Math.log(getRs())) / getCv())));
            }
            // T = e^((S + Rs.log(P) ) / Cp )
            else if (state.contains(ENTROPY, PRESSURE)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of(Math.exp((state.get(ENTROPY).getAsDouble() + getRs() * Math.log(state.get(PRESSURE).getAsDouble())) / getCp())));
            }
        }
    }
    @Override
    void calcP(State state) {
        super.calcP(state);
        if (!state.contains(PRESSURE)) {
            // P = Rs * T / V
            if (state.contains(TEMPERATURE, VOLUME)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of(getRs() * state.get(TEMPERATURE).getAsDouble() / state.get(VOLUME).getAsDouble()));
            }
            // P = e^((-S + Cp.log (T)) / Rs)
            else if (state.contains(ENTROPY, TEMPERATURE)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of(Math.exp((-state.get(ENTROPY).getAsDouble() + getCp() * Math.log(state.get(TEMPERATURE).getAsDouble())) / getRs())));
            }
            // P = e^((S - Cp.log(V) + Cp.log(Rs)) / Cv)
            else if (state.contains(ENTROPY, VOLUME)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of(Math.exp((state.get(ENTROPY).getAsDouble() - getCp() * Math.log(state.get(VOLUME).getAsDouble()) + getCp() * Math.log(getRs())) / getCv())));
            }
        }
    }
    @Override
    void calcV(State state) {
        super.calcV(state);
        if (!state.contains(VOLUME)) {
            // V = Rs * T / P
            if (state.contains(TEMPERATURE, PRESSURE)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of(getRs() * state.get(TEMPERATURE).getAsDouble() / state.get(PRESSURE).getAsDouble()));
            }
            // V = e^((S - Cv.log(T) + Rs.log(Rs)) / Rs)
            else if (state.contains(ENTROPY, TEMPERATURE)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of(Math.exp((state.get(ENTROPY).getAsDouble() - getCv() * Math.log(state.get(TEMPERATURE).getAsDouble()) + getRs() * Math.log(getRs())) / getRs())));
            }
            // V = e^((S - Cv.log(P) + Cp.log(Rs)) / Cp)
            else if (state.contains(ENTROPY, PRESSURE)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of(Math.exp((state.get(ENTROPY).getAsDouble() - getCv() * Math.log(state.get(PRESSURE).getAsDouble()) + getCp() * Math.log(getRs())) / getCp())));
            }
        }
    }
    @Override
    void calcS(State state) {
        super.calcS(state);
        if (!state.contains(ENTROPY)) {
            // S = Cv.log(T) + Rs * log(V) - Rs.log(Rs)
            if (state.contains(TEMPERATURE, VOLUME)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of(getCv() * Math.log(state.get(TEMPERATURE).getAsDouble()) + getRs() * Math.log(state.get(VOLUME).getAsDouble()) - getRs() * Math.log(getRs())));
            }
            // S = Cp.log(T) - Rs * log(P)
            else if (state.contains(TEMPERATURE, PRESSURE)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of(getCp() * Math.log(state.get(TEMPERATURE).getAsDouble()) - getRs() * Math.log(state.get(PRESSURE).getAsDouble())));
            }
            // S = Cv.log(P) + Cp * log(V) - Cp.log(Rs)
            else if (state.contains(PRESSURE, VOLUME)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of(getCv() * Math.log(state.get(PRESSURE).getAsDouble()) + getCp() * Math.log(state.get(VOLUME).getAsDouble()) - getCp() * Math.log(getRs())));
            }
        }
    }
    @Override
    void calcU(State state) {
        super.calcU(state);
        if (!state.contains(ENERGY)) {
            // U = Cv.T
            if (state.contains(TEMPERATURE)) {
                state.putIfAbsent(ENERGY, OptionalDouble.of(getCv() * state.get(TEMPERATURE).getAsDouble()));
            }
        }
    }
    @Override
    void calcH(State state) {
        super.calcH(state);
        if (!state.contains(ENTHALPY)) {
            // H = Cp.T
            if (state.contains(TEMPERATURE)) {
                state.putIfAbsent(ENTHALPY, OptionalDouble.of(getCp() * state.get(TEMPERATURE).getAsDouble()));
            }
        }
    }
    @Override
    void calcF(State state) {
        super.calcF(state);
    }
    @Override
    void calcG(State state) {
        super.calcG(state);
    } 
}