/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * Relative solution accuracy.
     */
    public static double relativeAccuracy = 1e-6;
    
    /**
     * Absolute solution accuracy.
     */
    public static double absoluteAccuracy = 1e-6;
    
    /**
     * Maximum number of allowable iterations to try and reach convergence.
     */
    private static int maxIterations = 10000;
    
    /**
     * A description of the equation.
     */
    public final String writtenEquation;
    
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
    protected FluidEquation(String name) {
        writtenEquation = name;
    }
    
    /**
     * Calculates the current function value. Assumes that all the variables in the map are defined.
     * @param variables a map of function variables and their current values.
     * @return the current function value.
     */
    protected abstract Double function(Map<Property, OptionalDouble> variables);
    
    /**
     * Gets the upper and lower bounds for the equation.
     * @param variables a map of the variables.
     * @param unknownVariable the unknown variable.
     * @return the bounds for the equation solver.
     */
    protected EquationBounds getBounds(Map<Property, OptionalDouble> variables, Property unknownVariable) {
        return new EquationBounds(unknownVariable.getLowerBound(), unknownVariable.getUpperBound());
    }
    
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
            OptionalDouble value = solveVariable(state, variable);
            if (value.isPresent()) {
                state.setProperty(variable, value.getAsDouble());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Solves the equation for the unknown variable. The function assumes that there is only 1 unknown variable.
     * @param varaible the variable to solve for.
     * @return the variable value.
     */
    private final OptionalDouble solveVariable(State state, Property unknownVariable) {
        
        // Log
        logger.trace("Solving " + writtenEquation + " for " + unknownVariable.symbol);
        
        // Get the equation variables
        Map<Property, OptionalDouble> variables = getVariables(state);
        
        // Create univariate function
        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                variables.put(unknownVariable, OptionalDouble.of(x));
                return function(variables);
            }
        };
        
        // Create solver
        UnivariateSolver solver = new BrentSolver(relativeAccuracy,absoluteAccuracy,1e-4);
        
        // Get bounds
        EquationBounds bounds = this.getBounds(variables, unknownVariable);

        try {
            // UnivariateSolverUtils.isBracketing(f, bounds.lower, bounds.upper)
            double value = solver.solve(maxIterations, f, bounds.lower, bounds.upper);
            logger.trace("Solution found in " + solver.getEvaluations() + " iterations: " + value);
            return OptionalDouble.of(value);
        }
        catch(Exception e) {
            logger.error("No solution found.");
            logger.error(e.getMessage());
            variables.keySet().stream().forEach(k -> {
                logger.error(k + " = " + variables.get(k).getAsDouble());
            });
            return OptionalDouble.empty();
        }
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
