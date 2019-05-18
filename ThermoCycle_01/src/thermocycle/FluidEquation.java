/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thermocycle.Properties.Property;

/**
 *
 * @author Chris
 */
abstract class FluidEquation implements Serializable {
    
    /**
     * Logger.
     */
    private static final Logger logger = LogManager.getLogger("DebugLog");
    
    /**
     * Function symbol.
     */
    private static final String func = "\u0192";
    
    /**
     * The maximum number of iteration allowed to find a converged solution.
     */
    private static int iterationLimit = 100;
    
    /**
     * A description of the equation.
     */
    public final String writtenEquation;
    
    /**
     * Convergence limit for the equation.
     */
    private final double convergenceLimit;
    
    /**
     * Generates a string describing the equation.
     * @param properties the properties in the equation. The first entry is the subject of the equation.
     * @return a string describing the equation.
     */
    public static final String equationString(Property... properties) {
        if (properties.length < 2) {
            return "";
        }
        StringBuilder sb = new StringBuilder(properties[0].symbol + " = " + func + "(" + properties[1].symbol);
        for (int i=2;i<properties.length;i++) {
            sb.append(",").append(properties[i].symbol);
        }
        sb.append(")");
        return (sb.toString());
    }
    
    /**
     * Constructor
     */
    protected FluidEquation(String name, double limit) {
        writtenEquation = name;
        convergenceLimit = limit;
    }
    
    /**
     * Calculates the current function value. Assumes that all the variables in the map are defined.
     * @param variables a map of function variables and their current values.
     * @return the current function value.
     */
    protected abstract OptionalDouble function(Map<Property, OptionalDouble> variables);
    
    /**
     * Gets a map of the state properties and their current values.
     * @param state the sate to get the properties from
     * @return a map of properties and current values.
     */
    protected abstract Map<Property, OptionalDouble> getVariables(State state);
    
    /**
     * Gets a map of the state properties and their current values.
     * @param state the state to get the properties from.
     * @param properties the properties to get.
     * @return a map of the properties and their current values
     */
    protected final Map<Property, OptionalDouble> getVariables(State state, Property... properties) {
        Map<Property, OptionalDouble> variables = new HashMap();
        Arrays.asList(properties).stream().forEach(p -> {
            variables.put(p, state.getProperty(p));
        });
        return variables;
    }
    
    /**
     * Saves the value to the equation variable.
     * @param variable the equation variable to save.
     * @param value the value to save.
     */
    protected final void saveVariable(State state, Property variable, Double value) {
        state.setProperty(variable, value);
    }
    
    /**
     * Checks to see how many unknown variables there are. If there is only one unknown then solves the equation for the unknown variable.
     * @return the true if an unknown was found.
     */
    protected final boolean solve(State state) {
        if (unknowns(state).size() == 1) {
            Property variable = unknowns(state).get(0);
            logger.trace("Solving " + writtenEquation + " for " + variable.symbol);
            state.setProperty(variable, solveVariable(state, variable, 1000.0).getAsDouble());
            return true;
        }
        return false;
    }
    
    /**
     * Solves the equation for the unknown variable. The function assumes that there is only 1 unknown variable.
     * @param varaible the variable to solve for.
     * @return the variable value.
     */
    private final OptionalDouble solveVariable(State state, Property unknownVariable, double initialGuess) {
        
        // Get the equation variables
        Map<Property, OptionalDouble> variables = getVariables(state);
        
        // Set up the convergence queues
        Deque<Double> xVariables = new LinkedList();
        Deque<Double> fVariables = new LinkedList();
        
        // Populate the convergence queues
        xVariables.add(initialGuess*0.95);
        variables.put(unknownVariable, OptionalDouble.of(xVariables.getLast()));
        fVariables.add(function(variables).getAsDouble());
        xVariables.add(initialGuess);
        variables.put(unknownVariable, OptionalDouble.of(xVariables.getLast()));
        fVariables.add(function(variables).getAsDouble());
        
        // Initialise the iteration counter
        int iteration = 1;
        
        // Iterate until converged
        while(Math.abs(fVariables.getLast()) > convergenceLimit) {
            
            // Update solution estimate in the queues
            if (xVariables.getLast().equals(xVariables.getFirst())) {
                xVariables.add(xVariables.getLast());
            }
            else {
                xVariables.add(xVariables.getLast() - fVariables.getLast()*(xVariables.getLast() - xVariables.getFirst())/(fVariables.getLast() - fVariables.getFirst()));
            }
            variables.put(unknownVariable, OptionalDouble.of(xVariables.getLast()));
            OptionalDouble f = function(variables);
            if (f.isPresent()) {
                fVariables.add(f.getAsDouble());
            }
            
            // Remove the oldest queue values
            xVariables.remove();
            fVariables.remove();
            
            // Update log
            logger.trace(unknownVariable.symbol + " = " + xVariables.getLast() + " (Iteration " + iteration + ")");
            
            // Check iteration limit
            if (iteration > iterationLimit) {
                logger.trace("Maximum number of iterations reached for equation convergence.");
                return OptionalDouble.empty();
            }
            else {
                iteration = iteration + 1;
            }
        }
        
        return OptionalDouble.of(xVariables.getLast());
    };
    
    @Override
    public String toString() {
        return writtenEquation;
    }
    
    /**
     * Determines the unknown variables.
     * @return a list of the unknown equation variables.
     */
    private final List<Property> unknowns(State state) {
        Map<Property, OptionalDouble> variables = getVariables(state);
        return variables.keySet().stream().filter(name -> !variables.get(name).isPresent()).collect(Collectors.toList());
    }
    
}
