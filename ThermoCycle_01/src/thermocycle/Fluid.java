/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.Serializable;
import java.util.*;
import static thermocycle.Properties.Property.*;

/**
 *
 * @author Chris
 */
public abstract class Fluid implements Properties, Serializable {
    
    /**
     * The universal gas constant.
     */
    static double R = 8.3144598;
    
    /**
     * Advogadros number.
     */
    static double Na = 60221408570000000000000.0;
    
    /**
     * The fluid name.
     */
    private String name;
    
    /**
     * Constructor.
     * @param name The fluid name. 
     */
    protected Fluid(String name) {
        this.name = name;
    }
        
    /**
     * Gets the fluid name.
     * @return Returns the fluid name.
     */
    protected final String getName() {
        return name;
    }
        
    /**
     * Sets the fluid name.
     * @param name The fluid name.
     */
    protected final void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the set of valid properties for this fluid.
     * @return Returns the set of valid state properties.
     */
    protected abstract Set<Property> fluidState();
    
    /**
     * Computes absent state properties from existing state properties for this fluid.
     * @param state The state to computre.
     */
    protected abstract void computeState(State state);
    
    /**
     * Calculates the fluid enthalpy, H, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute enthalpy for.
     */
    protected void calcH(State state) {
        // H = U + P.V
        if (state.contains(ENERGY, PRESSURE, VOLUME)) {
            state.putIfAbsent(ENTHALPY, OptionalDouble.of(state.get(ENERGY).getAsDouble() + state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble()));
        }
        // H = G + T.S
        else if (state.contains(GIBBS, TEMPERATURE, ENTROPY)) {
            state.putIfAbsent(ENTHALPY, OptionalDouble.of(state.get(GIBBS).getAsDouble() + state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble()));
        }
        // H = G + U - F
        else if (state.contains(GIBBS, ENERGY, HELMHOLTZ)) {
            state.putIfAbsent(ENTHALPY, OptionalDouble.of(state.get(GIBBS).getAsDouble() + state.get(ENERGY).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()));
        }
        // H = F + T.S + P.V
        else if (state.contains(HELMHOLTZ, TEMPERATURE, ENTROPY, PRESSURE, VOLUME)) {
            state.putIfAbsent(ENTHALPY, OptionalDouble.of(state.get(HELMHOLTZ).getAsDouble() + state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble() + state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble()));
        }
    }

    /**
     * Calculates the fluid internal energy, U, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute internal energy for.
     */
    protected void calcU(State state) {
        // U = H - P.V
        if (state.contains(ENTHALPY, PRESSURE, VOLUME)) {
            state.putIfAbsent(ENERGY, OptionalDouble.of(state.get(ENTHALPY).getAsDouble() - state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble()));
        }
        // U = F + T.sS
        else if (state.contains(HELMHOLTZ, TEMPERATURE, ENTROPY)) {
            state.putIfAbsent(ENERGY, OptionalDouble.of(state.get(HELMHOLTZ).getAsDouble() + state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble()));
        }
        // U = F + H - G
        else if (state.contains(HELMHOLTZ, ENTHALPY, GIBBS)) {
            state.putIfAbsent(ENERGY, OptionalDouble.of(state.get(HELMHOLTZ).getAsDouble() + state.get(ENTHALPY).getAsDouble() - state.get(GIBBS).getAsDouble()));
        }
        // U = G + T.S - P.V
        else if (state.contains(GIBBS, TEMPERATURE, ENTROPY, PRESSURE, VOLUME)) {state.putIfAbsent(ENERGY, OptionalDouble.of(state.get(GIBBS).getAsDouble() + state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble() - state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble()));}
    }
    
    /**
     * Calculates the fluid Gibbs free energy, G, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute Gibbs free energy for.
     */
    protected void calcG(State state) {
        // G = H - T.S
        if (state.contains(ENTHALPY, TEMPERATURE, ENTROPY)) {
            state.putIfAbsent(GIBBS, OptionalDouble.of(state.get(ENTHALPY).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble()));
        }
        // G = U + PV - T.S
        else if (state.contains(ENERGY, PRESSURE, VOLUME, TEMPERATURE, ENTROPY)) {
            state.putIfAbsent(GIBBS, OptionalDouble.of(state.get(ENERGY).getAsDouble() + state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() - state.get(TEMPERATURE).getAsDouble()*state.get(ENTROPY).getAsDouble()));
        }
        // G = F + P.V
        else if (state.contains(HELMHOLTZ, PRESSURE, VOLUME)) {
            state.putIfAbsent(GIBBS, OptionalDouble.of(state.get(HELMHOLTZ).getAsDouble() + state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble()));
        }
        // G = H - U + F
        else if (state.contains(ENTHALPY, ENERGY, HELMHOLTZ)) {
            state.putIfAbsent(GIBBS, OptionalDouble.of(state.get(ENTHALPY).getAsDouble() - state.get(ENERGY).getAsDouble() + state.get(HELMHOLTZ).getAsDouble()));
        }
    }
    
    /**
     * Calculates the fluid Helmholtz free energy, F, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute Helmholtz free energy for.
     */
   protected void calcF(State state) {
        // F = U - T.S
        if (state.contains(ENERGY, TEMPERATURE, ENTROPY)) {
            state.putIfAbsent(HELMHOLTZ, OptionalDouble.of(state.get(ENERGY).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble()));
        }
        // F = H - P.V - T.S
        else if (state.contains(ENTHALPY, PRESSURE, VOLUME, TEMPERATURE, ENTROPY)) {
            state.putIfAbsent(HELMHOLTZ, OptionalDouble.of(state.get(ENTHALPY).getAsDouble() - state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble()));
        }
        // F = G - P.V
        else if (state.contains(GIBBS, PRESSURE, VOLUME)) {
            state.putIfAbsent(HELMHOLTZ, OptionalDouble.of(state.get(GIBBS).getAsDouble() - state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble()));
        }
        // F = U - H + G
        else if (state.contains(ENERGY, ENTHALPY, GIBBS)) {
            state.putIfAbsent(HELMHOLTZ, OptionalDouble.of(state.get(ENERGY).getAsDouble() - state.get(ENTHALPY).getAsDouble() + state.get(GIBBS).getAsDouble()));
        }
    }
    
    /**
     * Calculates the fluid temperature, T, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute temperature for.
     */
    protected void calcT(State state) {
        if (state.contains(ENTROPY)) {
            // T = (U - F)/S
            if (state.contains(ENERGY, HELMHOLTZ)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of((state.get(ENERGY).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()) / state.get(ENTROPY).getAsDouble()));
            }
            // T = (H - G)/S
            else if (state.contains(ENTHALPY, GIBBS)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of((state.get(ENTHALPY).getAsDouble() - state.get(GIBBS).getAsDouble()) / state.get(ENTROPY).getAsDouble()));
            }
            // T = (U + P.V - G)/S
            else if (state.contains(ENERGY, PRESSURE, VOLUME, GIBBS)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of((state.get(ENERGY).getAsDouble() + state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() - state.get(GIBBS).getAsDouble()) / state.get(ENTROPY).getAsDouble()));
            }
            // T = (H - P.V - F)/S
            else if (state.contains(ENTHALPY, PRESSURE, VOLUME, HELMHOLTZ)) {
                state.putIfAbsent(TEMPERATURE, OptionalDouble.of((state.get(ENERGY).getAsDouble() - state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()) / state.get(ENTROPY).getAsDouble()));
            }
        }
    }
    
    /**
     * Calculates the fluid entropy, S, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute entropy for.
     */
    protected void calcS(State state) {
        if (state.contains(TEMPERATURE)) {
            // S = (U - F)/T
            if (state.contains(ENERGY, HELMHOLTZ)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of((state.get(ENERGY).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()) / state.get(TEMPERATURE).getAsDouble()));
            }
            // T = (H - G)/S
            else if (state.contains(ENTHALPY, GIBBS)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of((state.get(ENTHALPY).getAsDouble() - state.get(GIBBS).getAsDouble()) / state.get(TEMPERATURE).getAsDouble()));
            }
            // T = (U + P.V - G)/S
            else if (state.contains(ENERGY, PRESSURE, VOLUME, GIBBS)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of((state.get(ENERGY).getAsDouble() + state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() - state.get(GIBBS).getAsDouble()) / state.get(TEMPERATURE).getAsDouble()));
            }
            // T = (H - P.V - F)/S
            else if (state.contains(ENTHALPY, PRESSURE, VOLUME, HELMHOLTZ)) {
                state.putIfAbsent(ENTROPY, OptionalDouble.of((state.get(ENERGY).getAsDouble() - state.get(PRESSURE).getAsDouble() * state.get(VOLUME).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()) / state.get(TEMPERATURE).getAsDouble()));
            }
        }
    }
    
    /**
     * Calculates the fluid pressure from existing state properties if possible. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute pressure for.
     */
    protected void calcP(State state) {
        if (state.contains(VOLUME)) {
            // P = (H - U)/V
            if (state.contains(ENTHALPY, ENERGY)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of((state.get(ENTHALPY).getAsDouble() - state.get(ENERGY).getAsDouble()) / state.get(VOLUME).getAsDouble()));
            }
            // P = (G - F)/V
            else if (state.contains(GIBBS, HELMHOLTZ)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of((state.get(GIBBS).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()) / state.get(VOLUME).getAsDouble()));
            }
            // P = (G + T.S - U)/V
            else if (state.contains(GIBBS, TEMPERATURE, ENTROPY, ENERGY)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of((state.get(GIBBS).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble() - state.get(ENERGY).getAsDouble()) / state.get(VOLUME).getAsDouble()));
            }
            // P = (H - F - TS)/V
            else if (state.contains(ENTHALPY, HELMHOLTZ, TEMPERATURE, ENTROPY)) {
                state.putIfAbsent(PRESSURE, OptionalDouble.of((state.get(ENTHALPY).getAsDouble() - state.get(HELMHOLTZ).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble()) / state.get(VOLUME).getAsDouble()));
            }
        }
    }
    
    /**
     * Calculates the fluid specific volume, V, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute specific volume for.
     */
    protected void calcV(State state) {
        if (state.contains(PRESSURE)) {
            // V = (H - U)/P
            if (state.contains(ENTHALPY, ENERGY)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of((state.get(ENTHALPY).getAsDouble() - state.get(ENERGY).getAsDouble()) / state.get(PRESSURE).getAsDouble()));
            }
            // V = (G - F)/P
            else if (state.contains(GIBBS, HELMHOLTZ)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of((state.get(GIBBS).getAsDouble() - state.get(HELMHOLTZ).getAsDouble()) / state.get(PRESSURE).getAsDouble()));
            }
            // V = (G + T.S - U)/P
            else if (state.contains(GIBBS, TEMPERATURE, ENTROPY, ENERGY)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of((state.get(GIBBS).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble() - state.get(ENERGY).getAsDouble()) / state.get(PRESSURE).getAsDouble()));
            }
            // V = (H - F - T.S)/P
            else if (state.contains(ENTHALPY, HELMHOLTZ, TEMPERATURE, ENTROPY)) {
                state.putIfAbsent(VOLUME, OptionalDouble.of((state.get(ENTHALPY).getAsDouble() - state.get(HELMHOLTZ).getAsDouble() - state.get(TEMPERATURE).getAsDouble() * state.get(ENTROPY).getAsDouble())/state.get(PRESSURE).getAsDouble()));
            }
        }
    }
        
    @Override
    public String toString() {
        return (name + " (" + getClass().getSimpleName() + ")");
    }
    
}
