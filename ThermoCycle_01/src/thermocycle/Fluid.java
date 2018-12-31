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
     * The component unique reference number
     */
    public final UUID id;
    
    /**
     * Constructor.
     * @param name The fluid name. 
     */
    protected Fluid(String name) {
        id = UUID.randomUUID();
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
    protected abstract Set<Property> getAllowableProperties();
    
    /**
     * Computes absent state properties from existing state properties for this fluid.
     * @param state The state to compute.
     */
    protected abstract void computeState(State state);
    
    /**
     * Calculates the fluid density, R, from the specific volume.
     * @param state The state to compute density for.
     */
    protected void calcR(State state) {
        // R = 1/V
        if (state.contains(VOLUME)) {
            state.setProperty(DENSITY, 1.0/state.getProperty(VOLUME).getAsDouble());
        }
    }
    
    /**
     * Calculates the fluid enthalpy, H, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute enthalpy for.
     */
    protected void calcH(State state) {
        // H = U + P.V
        if (state.contains(ENERGY, PRESSURE, VOLUME)) {
            state.setProperty(ENTHALPY, state.getProperty(ENERGY).getAsDouble() + state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble());
        }
        // H = G + T.S
        else if (state.contains(GIBBS, TEMPERATURE, ENTROPY)) {
            state.setProperty(ENTHALPY, state.getProperty(GIBBS).getAsDouble() + state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble());
        }
        // H = G + U - F
        else if (state.contains(GIBBS, ENERGY, HELMHOLTZ)) {
            state.setProperty(ENTHALPY, state.getProperty(GIBBS).getAsDouble() + state.getProperty(ENERGY).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble());
        }
        // H = F + T.S + P.V
        else if (state.contains(HELMHOLTZ, TEMPERATURE, ENTROPY, PRESSURE, VOLUME)) {
            state.setProperty(ENTHALPY, state.getProperty(HELMHOLTZ).getAsDouble() + state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble() + state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble());
        }
    }

    /**
     * Calculates the fluid internal energy, U, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute internal energy for.
     */
    protected void calcU(State state) {
        // U = H - P.V
        if (state.contains(ENTHALPY, PRESSURE, VOLUME)) {
            state.setProperty(ENERGY, state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble());
        }
        // U = F + T.sS
        else if (state.contains(HELMHOLTZ, TEMPERATURE, ENTROPY)) {
            state.setProperty(ENERGY, state.getProperty(HELMHOLTZ).getAsDouble() + state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble());
        }
        // U = F + H - G
        else if (state.contains(HELMHOLTZ, ENTHALPY, GIBBS)) {
            state.setProperty(ENERGY, state.getProperty(HELMHOLTZ).getAsDouble() + state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(GIBBS).getAsDouble());
        }
        // U = G + T.S - P.V
        else if (state.contains(GIBBS, TEMPERATURE, ENTROPY, PRESSURE, VOLUME)) {state.setProperty(ENERGY, state.getProperty(GIBBS).getAsDouble() + state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble() - state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble());}
    }
    
    /**
     * Calculates the fluid Gibbs free energy, G, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute Gibbs free energy for.
     */
    protected void calcG(State state) {
        // G = H - T.S
        if (state.contains(ENTHALPY, TEMPERATURE, ENTROPY)) {
            state.setProperty(GIBBS, state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble());
        }
        // G = U + PV - T.S
        else if (state.contains(ENERGY, PRESSURE, VOLUME, TEMPERATURE, ENTROPY)) {
            state.setProperty(GIBBS, state.getProperty(ENERGY).getAsDouble() + state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble()*state.getProperty(ENTROPY).getAsDouble());
        }
        // G = F + P.V
        else if (state.contains(HELMHOLTZ, PRESSURE, VOLUME)) {
            state.setProperty(GIBBS, state.getProperty(HELMHOLTZ).getAsDouble() + state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble());
        }
        // G = H - U + F
        else if (state.contains(ENTHALPY, ENERGY, HELMHOLTZ)) {
            state.setProperty(GIBBS, state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(ENERGY).getAsDouble() + state.getProperty(HELMHOLTZ).getAsDouble());
        }
    }
    
    /**
     * Calculates the fluid Helmholtz free energy, F, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute Helmholtz free energy for.
     */
   protected void calcF(State state) {
        // F = U - T.S
        if (state.contains(ENERGY, TEMPERATURE, ENTROPY)) {
            state.setProperty(HELMHOLTZ, state.getProperty(ENERGY).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble());
        }
        // F = H - P.V - T.S
        else if (state.contains(ENTHALPY, PRESSURE, VOLUME, TEMPERATURE, ENTROPY)) {
            state.setProperty(HELMHOLTZ, state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble());
        }
        // F = G - P.V
        else if (state.contains(GIBBS, PRESSURE, VOLUME)) {
            state.setProperty(HELMHOLTZ, state.getProperty(GIBBS).getAsDouble() - state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble());
        }
        // F = U - H + G
        else if (state.contains(ENERGY, ENTHALPY, GIBBS)) {
            state.setProperty(HELMHOLTZ, state.getProperty(ENERGY).getAsDouble() - state.getProperty(ENTHALPY).getAsDouble() + state.getProperty(GIBBS).getAsDouble());
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
                state.setProperty(TEMPERATURE, (state.getProperty(ENERGY).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble()) / state.getProperty(ENTROPY).getAsDouble());
            }
            // T = (H - G)/S
            else if (state.contains(ENTHALPY, GIBBS)) {
                state.setProperty(TEMPERATURE, (state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(GIBBS).getAsDouble()) / state.getProperty(ENTROPY).getAsDouble());
            }
            // T = (U + P.V - G)/S
            else if (state.contains(ENERGY, PRESSURE, VOLUME, GIBBS)) {
                state.setProperty(TEMPERATURE, (state.getProperty(ENERGY).getAsDouble() + state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble() - state.getProperty(GIBBS).getAsDouble()) / state.getProperty(ENTROPY).getAsDouble());
            }
            // T = (H - P.V - F)/S
            else if (state.contains(ENTHALPY, PRESSURE, VOLUME, HELMHOLTZ)) {
                state.setProperty(TEMPERATURE, (state.getProperty(ENERGY).getAsDouble() - state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble()) / state.getProperty(ENTROPY).getAsDouble());
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
                state.setProperty(ENTROPY, (state.getProperty(ENERGY).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble()) / state.getProperty(TEMPERATURE).getAsDouble());
            }
            // T = (H - G)/S
            else if (state.contains(ENTHALPY, GIBBS)) {
                state.setProperty(ENTROPY, (state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(GIBBS).getAsDouble()) / state.getProperty(TEMPERATURE).getAsDouble());
            }
            // T = (U + P.V - G)/S
            else if (state.contains(ENERGY, PRESSURE, VOLUME, GIBBS)) {
                state.setProperty(ENTROPY, (state.getProperty(ENERGY).getAsDouble() + state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble() - state.getProperty(GIBBS).getAsDouble()) / state.getProperty(TEMPERATURE).getAsDouble());
            }
            // T = (H - P.V - F)/S
            else if (state.contains(ENTHALPY, PRESSURE, VOLUME, HELMHOLTZ)) {
                state.setProperty(ENTROPY, (state.getProperty(ENERGY).getAsDouble() - state.getProperty(PRESSURE).getAsDouble() * state.getProperty(VOLUME).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble()) / state.getProperty(TEMPERATURE).getAsDouble());
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
                state.setProperty(PRESSURE, (state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(ENERGY).getAsDouble()) / state.getProperty(VOLUME).getAsDouble());
            }
            // P = (G - F)/V
            else if (state.contains(GIBBS, HELMHOLTZ)) {
                state.setProperty(PRESSURE, (state.getProperty(GIBBS).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble()) / state.getProperty(VOLUME).getAsDouble());
            }
            // P = (G + T.S - U)/V
            else if (state.contains(GIBBS, TEMPERATURE, ENTROPY, ENERGY)) {
                state.setProperty(PRESSURE, (state.getProperty(GIBBS).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble() - state.getProperty(ENERGY).getAsDouble()) / state.getProperty(VOLUME).getAsDouble());
            }
            // P = (H - F - TS)/V
            else if (state.contains(ENTHALPY, HELMHOLTZ, TEMPERATURE, ENTROPY)) {
                state.setProperty(PRESSURE, (state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble()) / state.getProperty(VOLUME).getAsDouble());
            }
        }
    }
    
    /**
     * Calculates the fluid specific volume, V, based on general state relationships. The general relationship considered are, <br> - H = U + P.V <br> - F = U - T.S <br> - G = H - T.S
     * @param state The state to compute specific volume for.
     */
    protected void calcV(State state) {
        if (state.contains(DENSITY)) {
            // V = 1/R;
            state.setProperty(VOLUME, 1.0/state.getProperty(DENSITY).getAsDouble());
        }
        if (state.contains(PRESSURE)) {
            // V = (H - U)/P
            if (state.contains(ENTHALPY, ENERGY)) {
                state.setProperty(VOLUME, (state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(ENERGY).getAsDouble()) / state.getProperty(PRESSURE).getAsDouble());
            }
            // V = (G - F)/P
            else if (state.contains(GIBBS, HELMHOLTZ)) {
                state.setProperty(VOLUME, (state.getProperty(GIBBS).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble()) / state.getProperty(PRESSURE).getAsDouble());
            }
            // V = (G + T.S - U)/P
            else if (state.contains(GIBBS, TEMPERATURE, ENTROPY, ENERGY)) {
                state.setProperty(VOLUME, (state.getProperty(GIBBS).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble() - state.getProperty(ENERGY).getAsDouble()) / state.getProperty(PRESSURE).getAsDouble());
            }
            // V = (H - F - T.S)/P
            else if (state.contains(ENTHALPY, HELMHOLTZ, TEMPERATURE, ENTROPY)) {
                state.setProperty(VOLUME, (state.getProperty(ENTHALPY).getAsDouble() - state.getProperty(HELMHOLTZ).getAsDouble() - state.getProperty(TEMPERATURE).getAsDouble() * state.getProperty(ENTROPY).getAsDouble())/state.getProperty(PRESSURE).getAsDouble());
            }
        }
    }
        
    @Override
    public String toString() {
        return (name + " (" + getClass().getSimpleName() + ")");
    }
    
}
