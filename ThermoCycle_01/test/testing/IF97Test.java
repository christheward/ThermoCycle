/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import com.hummeling.if97.IF97;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static thermocycle.Properties.Property.ENTHALPY;
import static thermocycle.Properties.Property.ENTROPY;
import static thermocycle.Properties.Property.GIBBS;
import static thermocycle.Properties.Property.TEMPERATURE;

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
        
        System.out.println("Quality test");
        
        System.out.println(GIBBS.symbol + " = " + ENTHALPY.symbol + " - " + TEMPERATURE.symbol + ENTROPY.symbol);
        
        System.out.println(if97.vapourFractionHS(1000, 1));
        System.out.println(if97.vapourFractionHS(1000, 1e7));
        System.out.println(if97.vapourFractionTS(500, 7000));
        System.out.println(if97.vapourFractionTS(500, 7001));
        System.out.println(if97.vapourFractionPS(90e6, 5000));
        
        assertTrue(true);
    }
    
}
