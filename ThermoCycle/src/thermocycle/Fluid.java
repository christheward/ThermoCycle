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
    
    // static variables
    private static int idCount = 0;                             // fluid id counter
    static double R = 8.3144598;                      // ideal gas constant
    static double Na = 60221408570000000000000.0;     // advogadros constant
    
    // static methods
    static int idCount() {return Fluid.idCount;}
    
    /// variables
    private String name;            // fluid name
    private final int id;           // fluid id
    
    // constructors
    Fluid(String s) {
        Fluid.idCount++;            // increase id counter
        id = idCount;               // set id
        name = s;                   // set name
    }
    
    @Override
    public String toString() {
        return ("[" + id + "] " + name + " (" + getClass().getSimpleName() + ")");
    }
    
    // gettters
    final int getId() {return id;}
    final String getName() {return name;}
        
    // setters
    final void setName(String s) {name = s;}
    
    // abstract methods
    abstract Set<Property> fluidState();
    abstract void computeState(State state);
    
    // general relationships
    // H = U + P.V
    // F = U - T.S
    // G = H - T.S
    void calcH(State state) {
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
    void calcU(State state) {
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
    void calcG(State state) {
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
    void calcF(State state) {
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
    void calcT(State state) {
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
    void calcS(State state) {
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
    void calcP(State state) {
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
    void calcV(State state) {
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
    // reporting methods
    final void report() {
        System.out.println("[" + id + "] " + getName() + "(" + getClass().getSimpleName() + ")");
    }
}
