/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.List;
import java.io.Serializable;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.SecantSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
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
     * Maximum number of allowable iterations to try and reach convergence.
     */
    private static int maxIterations = 10000;
    
    /**
     * Eligible equation states.
     */
    private static enum STATE {UNSOLVED, INCOMPATIBLE, COMPATIBLE};
    
    /**
     * Current equation state.
     */
    private STATE state;
    
    /**
     * Convergence limit for the equation.
     */
    private final double tolerance;
    
    /**
     * Constructor
     * @param tolerance the convergence tolerance of the equations.
     */
    protected ComponentEquation(double tolerance) {
        this.tolerance = tolerance;
        state = STATE.UNSOLVED;
    }
    
    /**
     * Checks to see if the equation is compatible. This is required because equation variables may be set by other equations.
     * @return true if the equation is within the convergence tolerance.
     */
    protected final boolean compatible() {
        return (Math.abs(function(getVariables())) < tolerance) ? true : false;
    }
    
    /**
     * Gets a string describing the equation.
     * @return a string describing the equation.
     */
    public abstract String equation();
    
    /**
     * Calculates the current function value.
     * @param variable a map of function variables and their current values.
     * @return the current function value.
     */
    protected abstract Double function(Map<EquationVariable, OptionalDouble> variables);
    
    /**
     * Gets the equation variables and their current values.
     * @return a map of the equation variables, which uses variable names as the keys.
     */
    protected abstract Map<EquationVariable, OptionalDouble> getVariables();
    
    /**
     * Determine if the equation has been solved.
     * @return true if the equation is solved.
     */
    protected final boolean isSolved() {
        return (state.equals(state.UNSOLVED)) ? false : true;
    }
    
    /**
     * Resets the state of the equation to unsolved.
     */
    protected final void reset() {
        state = STATE.UNSOLVED;
    }
    
    /**
     * Saves the value to the equation variable.
     * @param variable the equation variable to save.
     * @param value the value to save.
     * @return any nodes that have been updated during the save. 
     */
    protected abstract Node saveVariable(EquationVariable variable, Double value);
    
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
                return saveVariable(unknowns().get(0), solveVariable(unknowns().get(0)).getAsDouble());
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
    private final OptionalDouble solveVariable(EquationVariable unknownVariable) {
        
        // Log
        logger.trace("Solving " + equation() + " for " + unknownVariable);
        
        // Get the equation variables
        Map<EquationVariable, OptionalDouble> variables = getVariables();
        
        // Create univariate function
        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                variables.put(unknownVariable, OptionalDouble.of(x));
                return function(variables);
            }
        };
        
        // Create solver
        //UnivariateSolver solver = new SecantSolver(1e-6);
        UnivariateSolver solver = new BrentSolver(tolerance);
        try {
            double value = solver.solve(maxIterations, f, unknownVariable.getLowerGuess(), unknownVariable.getUpperGuess());
            logger.trace("Solution found in " + solver.getEvaluations() + " iterations: " + value);
            return OptionalDouble.of(value);
        }
        catch(Exception e) {
            logger.error("No solution found.");
            logger.error(e.getMessage());
            variables.keySet().stream().forEach(k -> {
                logger.error(k + " = " + variables.get(k).getAsDouble());
            });
            logger.error("Lower = " + unknownVariable.getLowerGuess() + " >> f = " + f.value(unknownVariable.getLowerGuess()));
            logger.error("Upper = " + unknownVariable.getUpperGuess() + " >> f = " + f.value(unknownVariable.getUpperGuess()));
            return OptionalDouble.empty();
        }
    };
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName().replace("_", " ");
    }
    
    /**
     * Determines the unknown variables.
     * @return a list of the unknown equation variables.
     */
    private final List<EquationVariable> unknowns() {
        Map<EquationVariable,OptionalDouble> variables = getVariables();
        return variables.keySet().stream().filter(name -> !variables.get(name).isPresent()).collect(Collectors.toList());
    }
        
}
