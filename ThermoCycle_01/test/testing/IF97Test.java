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
        
        // Calc h(p,x) and s(p,x)
        double h = if97.specificEnthalpyPX(p, x);
        double s = if97.specificEntropyPX(p,x);
        
        // Calc x(h,s) and p(h,s)
        double xCheck1 = if97.vapourFractionHS(p, s);
        double pCheck1 = if97.pressureHS(h, s);
        
        // Calc x2(h,s) and p2(h,s)
        double xCheck2 = if97.vapourFractionHS(p, 27443.945269272404);
        double pCheck2 = if97.pressureHS(h, 27443.945269272404);
        
        // Calc s(p2,x)
        double sCheck1 = if97.specificEntropyPX(pCheck1, x);
        double sCheck2 = if97.specificEntropyPX(pCheck1, xCheck1);
        
        System.out.println("x = " + x);
        System.out.println("p = " + p);
        System.out.println("h(p,x) = " + h);
        System.out.println("s(p,x) = " + s);
        
        System.out.println("x(h,s) = " + xCheck1);
        System.out.println("p(h,s) = " + pCheck1);
        
        System.out.println("x2(h,s) = " + xCheck2);
        System.out.println("p2(h,s) = " + pCheck2);
        
        System.out.println("s1(p,x) = " + sCheck1);
        System.out.println("s2(p,x) = " + sCheck2);
        
        /**
        System.out.println("Quality test");
        System.out.println(GIBBS.symbol + " = " + ENTHALPY.symbol + " - " + TEMPERATURE.symbol + ENTROPY.symbol);
        System.out.println(if97.vapourFractionHS(1000, 1));
        System.out.println(if97.vapourFractionHS(1000, 1e7));
        System.out.println(if97.vapourFractionTS(500, 7000));
        System.out.println(if97.vapourFractionTS(500, 7001));
        System.out.println(if97.vapourFractionPS(90e6, 5000));
        */
        
        assertTrue(true);
    }
    
    
}
