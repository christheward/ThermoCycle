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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Chris
 */
abstract class Equation implements Serializable {
    
    static private final Logger logger = LogManager.getLogger("DebugLog");
    
    /**
     * Compatibility flag. True if all equations are compatible.
     */
    private enum STATE {UNSOLVED, INCOMPATIBLE, COMPATIBLE};
    private STATE state;
    
    /**
     * Tolerance required for matching OptionalDoubles.
     */
    private static double tolerance = 1e-5;
    
    /**
     * Checks if two the calculated value is equal to the known value to within the tolerance.
     * @param knownValue The known value.
     * @param calculatedValue The calculated value.
     * @return true if the two values are equal to within the tolerance.
     */
    protected static final boolean withinTolerance(Double knownValue, Double calculatedValue) {
            if (knownValue == 0.0) {
                return (calculatedValue == 0);
            }
            return (Math.abs((knownValue - calculatedValue)/knownValue) < tolerance);
    }
    
    /**
     * Constructor
     */
    protected Equation() {
        state = STATE.UNSOLVED;
    }
    
    /**
     * Gets a map of all the equation variables and their values.
     * @return A map of the equation variables, which uses variable names as the keys.
     */
    protected abstract Map<String, OptionalDouble> getVariables();
    
    /**
     * Tries to solve the equation for a particular variable.
     * @param varaible The variable to try and solve for.
     * @return The variable value.
     */
    protected abstract OptionalDouble solveVariable(String varaible);
    
    /**
     * Saves the equation variable
     * @param variable
     * @param value
     * @return 
     */
    protected abstract Node saveVariable(String variable, Double value);
    
    /**
     * Gets the equation compatibility.
     * @return Returns true if the equation is compatible.
     */
    protected boolean isCompatible() {
        return (state.equals(STATE.COMPATIBLE)) ? true : false;
    }
    
    /**
     * Gets whether the equation is solved.
     * @return Returns true if the equation is solved.
     */
    protected boolean isSolved() {
        return (state.equals(state.UNSOLVED)) ? false : true;
    }
    
    /**
    * Checks whether all equation variables are compatible with each other. If equations can't be used to calculate a variable definitely then these are automatically compatible.
    * @return whether equation variable are all compatible.
    */
    private boolean compatible() {
        if (unknowns().size() == 0) {
            Map<String,OptionalDouble> variables = getVariables();
            return variables.keySet().stream().filter(name -> solveVariable(name).isPresent()).allMatch(name -> Equation.withinTolerance(variables.get(name).getAsDouble(), solveVariable(name).getAsDouble()));
        }
        return false;
    }
    
    /**
     * Checks to see how many unknown variables there are. If there is only one, solves the equation for the unknown variable. If there are no unknowns then checks for compatability
     * @return Return the updated node else returns null.
     */
    protected Node solve() {
        switch (unknowns().size()) {
            // if 0 unknows check compatability
            case 0: {
                if (compatible()) {
                    state = STATE.COMPATIBLE;
                    logger.info(this.getClass().getSimpleName() + " equation is compatible");
                }
                else {
                    state = state.INCOMPATIBLE;
                    logger.error(this.getClass().getSimpleName() + " equation is not compatible");
                }
                return null;}
            // if 1 unknown solve for single unknown
            case 1: {
                logger.info("Solving " + this.getClass().getSimpleName() + " for " + unknowns().get(0));
                return saveVariable(unknowns().get(0), solveVariable(unknowns().get(0)).getAsDouble());
            }
            // if more than one unknown do nothing.
            default: {
                return null;
            }
        }
    }
    
    /**
     * Determines the unknown variables
     * @return Returns a list of the unknown variables in the equation.
     */
    private List<String> unknowns() {
        Map<String,OptionalDouble> variables = getVariables();
        return variables.keySet().stream().filter(name -> !variables.get(name).isPresent()).collect(Collectors.toList());
    }
    
    /**
     * Resets the equation's state to unsolved, for use with parametric studies.
     */
    protected void reset() {
        state = STATE.UNSOLVED;
    }
    
    @Override
    public String toString() {
        return (getClass().getSimpleName() + " is " + state);
    }
}
