/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import com.hummeling.if97.IF97;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class IF97Test {
    
    public IF97 if97;
    
    public IF97Test() {
        if97 = new IF97(IF97.UnitSystem.SI);
    }
    
    @Test
    public void QualityTest() {
        
        
        System.out.println("Value test");
        
        // x and p are known
        double x = 0;
        double p = 101325;
        
        // calculate h
        double h = if97.specificEnthalpyPX(p, x);
        
        // calcualte T
        double t = 1273.15;
        double h2 = if97.specificEnthalpyPT(p,t);
        
        System.out.println(p);
        System.out.println(t);
        System.out.println(h2);
        
        
        assertTrue(true);
    }
    
    
}
