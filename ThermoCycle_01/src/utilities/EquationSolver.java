/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.SecantSolver;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class EquationSolver{
    
    private SecantSolver ss;
    
    public EquationSolver() {
        
        ss = new SecantSolver();
        
        
    }
    
    
    private class test implements UnivariateFunction {
        
        @Override
        public double value(double x) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        
        
    }
    
    
}
