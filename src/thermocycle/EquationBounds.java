/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class EquationBounds {
    
    /**
     * The upper bound.
     */
    public final double upper;

    /**
     * The lower bound.
     */
    public final double lower;

    /**
     * Constructor.
     * @param lower the lower bound.
     * @param upper the upper bound.
     */
    public EquationBounds(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
}
