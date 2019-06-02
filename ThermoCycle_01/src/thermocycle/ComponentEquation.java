/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Chris
 */
abstract class ComponentEquation implements Serializable {
    
    /**
     * Logger.
     */
    static private final Logger logger = LogManager.getLogger("DebugLog");
    
    /**
     * Function symbol.
     */
    protected static final String func = "\u0192";

    
    /**
     * Eligible equation states.
     */
    private static enum STATE {UNSOLVED, INCOMPATIBLE, COMPATIBLE};
    
    /**
     * The maximum number of iteration allowed to find a converged solution.
     */
    private static int iterationLimit = 100;
    
    /**
     * Current equation state.
     */
    private STATE state;
    
    /**
     * A description of the equation.
     */
    public final String writtenEquation;
    
    /**
     * Convergence limit for the equation.
     */
    private final double convergenceTolerance;
    
    /**
     * Constructor
     * @param name the name of the equations
     * @param tolerance the convergence tolerance of the equations.
     */
    protected ComponentEquation(String name, double tolerance) {
        writtenEquation = name;
        convergenceTolerance = tolerance;
        state = STATE.UNSOLVED;
    }
    
    /**
     * Checks to see if the equation is compatible. This is required because equation variables may be set by other equations.
     * @return true if the equation is within the convergence tolerance.
     */
    protected final boolean compatible() {
        return (Math.abs(function(getVariables())) < convergenceTolerance) ? true : false;
    }
    
    /**
     * Calculates the current function value.
     * @param variable a map of function variables and their current values.
     * @return the current function value.
     */
    protected abstract Double function(Map<String, OptionalDouble> variables);
    
    /**
     * Gets the equation variables and their current values.
     * @return a map of the equation variables, which uses variable names as the keys.
     */
    protected abstract Map<String, OptionalDouble> getVariables();
    
    /**
     * Determine if the equation has been solved.
     * @return true if the equation is solved.
     */
    protected final boolean isSolved() {
        return (state.equals(state.UNSOLVED)) ? false : true;
    }
    
    /**
     * Saves the value to the equation variable.
     * @param variable the equation variable to save.
     * @param value the value to save.
     * @return any nodes that have been updated during the save. 
     */
    protected abstract Node saveVariable(String variable, Double value);
    
    /**
     * Resets the state of the equation to unsolved.
     */
    protected final void reset() {
        state = STATE.UNSOLVED;
    }
    
    /**
     * Checks to see how many unknown variables there are. If there is only one unknown then solves the equation for the unknown variable. If there are no unknowns then checks for compatability.
     * @return the updated node else returns null.
     */
    protected final Node solve() {
        switch (unknowns().size()) {
            // if 0 unknowns check compatability
            case 0: {
                if (compatible()) {
                    state = STATE.COMPATIBLE;
                    logger.info(this + " equation is compatible");
                }
                else {
                    state = STATE.INCOMPATIBLE;
                    logger.error(this + " equation is not compatible");
                }
                return null;}
            // if 1 unknown solve for single unknown
            case 1: {
                logger.trace("Solving " + writtenEquation + " for " + unknowns().get(0));
                return saveVariable(unknowns().get(0), solveVariable(unknowns().get(0), 1000.0).getAsDouble());
            }
            // if more than one unknown do nothing.
            default: {
                return null;
            }
        }
    }
    
    /**
     * Solves the equation for the unknown variable. The function assumes that there is only 1 unknown variable.
     * @param varaible the variable to solve for.
     * @return the variable value.
     */
    private final OptionalDouble solveVariable(String unknownVariable, double initialGuess) {
        
        // Get the equation variables
        Map<String, OptionalDouble> variables = getVariables();
        
        // Set up the convergence queues
        Deque<Double> xVariables = new LinkedList();
        Deque<Double> fVariables = new LinkedList();
        
        // Populate the convergence queues
        xVariables.add(initialGuess*0.95);
        variables.put(unknownVariable, OptionalDouble.of(xVariables.getLast()));
        fVariables.add(function(variables));
        xVariables.add(initialGuess);
        variables.put(unknownVariable, OptionalDouble.of(xVariables.getLast()));
        fVariables.add(function(variables));
        
        // Initialise the iteration counter
        int iteration = 1;
        
        // Iterate until converged
        while(Math.abs(fVariables.getLast()) > convergenceTolerance) {
            
            // Update solution estimate in the queues
            if (xVariables.getLast().equals(xVariables.getFirst())) {
                xVariables.add(xVariables.getLast());
            }
            else {
                xVariables.add(xVariables.getLast() - fVariables.getLast()*(xVariables.getLast() - xVariables.getFirst())/(fVariables.getLast() - fVariables.getFirst()));
            }
            variables.put(unknownVariable, OptionalDouble.of(xVariables.getLast()));
            fVariables.add(function(variables));
            
            // Remove the oldest queue values
            xVariables.remove();
            fVariables.remove();
            
            // Update log
            logger.trace(unknownVariable + " = " + xVariables.getLast() + " (Iteration " + iteration + ")");
            
            // Check iteration limit
            if (iteration > iterationLimit) {
                logger.debug("Maximum number of iterations reached for equation convergence.");
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
        return this.getClass().getSimpleName().replace("_", " ");
    }
    
    /**
     * Determines the unknown variables.
     * @return a list of the unknown equation variables.
     */
    private final List<String> unknowns() {
        Map<String,OptionalDouble> variables = getVariables();
        return variables.keySet().stream().filter(name -> !variables.get(name).isPresent()).collect(Collectors.toList());
    }
        
}
