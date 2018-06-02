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
     * @param value The salue to set.
     */
    void setRs(double value) {
        Rs = value;
    }
    
    @Override
    protected final Set<Property> fluidState() {
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
    
    /**
     * Calculates the state temperature, T, based on state relationships. The ideal gas state relationships are: <br> - P.V = Rs.T <br> - U = Cv.T <br> - H = Cp.T <br> - S = Cp.ln(T) - Rs.ln(P) <br> - S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs) <br> - S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
     * @param state The state to calculate temperature for.
     */
    @Override
    protected void calcT(State state) {
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
    
    /**
     * Calculates the state pressure, P, based on state relationships. The ideal gas state relationships are: <br> - P.V = Rs.T <br> - U = Cv.T <br> - H = Cp.T <br> - S = Cp.ln(T) - Rs.ln(P) <br> - S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs) <br> - S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
     * @param state The state to calculate pressure for.
     */
    @Override
    protected void calcP(State state) {
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
    
    /**
     * Calculates the state specific volume, V, based on state relationships. The ideal gas state relationships are: <br> - P.V = Rs.T <br> - U = Cv.T <br> - H = Cp.T <br> - S = Cp.ln(T) - Rs.ln(P) <br> - S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs) <br> - S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
     * @param state The state to calculate specific volume for.
     */
    @Override
    protected void calcV(State state) {
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
    
    /**
     * Calculates the state entropy, S, based on state relationships. The ideal gas state relationships are: <br> - P.V = Rs.T <br> - U = Cv.T <br> - H = Cp.T <br> - S = Cp.ln(T) - Rs.ln(P) <br> - S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs) <br> - S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
     * @param state The state to calculate entropy for.
     */
    @Override
    protected void calcS(State state) {
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
    
    /**
     * Calculates the state internal energy, U, based on state relationships. The ideal gas state relationships are: <br> - P.V = Rs.T <br> - U = Cv.T <br> - H = Cp.T <br> - S = Cp.ln(T) - Rs.ln(P) <br> - S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs) <br> - S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
     * @param state The state to calculate internal energy for.
     */
    @Override
    protected void calcU(State state) {
        super.calcU(state);
        if (!state.contains(ENERGY)) {
            // U = Cv.T
            if (state.contains(TEMPERATURE)) {
                state.putIfAbsent(ENERGY, OptionalDouble.of(getCv() * state.get(TEMPERATURE).getAsDouble()));
            }
        }
    }
    
    /**
     * Calculates the state enthalpy, H, based on state relationships. The ideal gas state relationships are: <br> - P.V = Rs.T <br> - U = Cv.T <br> - H = Cp.T <br> - S = Cp.ln(T) - Rs.ln(P) <br> - S = Cv.ln(P) + Cp.ln(V) - Cp.ln(Rs) <br> - S = Cv.ln(T) + Rs.ln(V) - Rs.ln(Rs)
     * @param state The state to calculate enthalpy for.
     */
    @Override
    protected void calcH(State state) {
        super.calcH(state);
        if (!state.contains(ENTHALPY)) {
            // H = Cp.T
            if (state.contains(TEMPERATURE)) {
                state.putIfAbsent(ENTHALPY, OptionalDouble.of(getCp() * state.get(TEMPERATURE).getAsDouble()));
            }
        }
    }
    
    @Override
    protected void calcF(State state) {
        super.calcF(state);
    }
    
    @Override
    protected void calcG(State state) {
        super.calcG(state);
    } 
}