/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collector;
import org.jfree.chart.plot.XYPlot;
import org.junit.Test;
import static org.junit.Assert.*;
import static thermocycle.Attributes.Attribute.EFFICIENCY;
import static thermocycle.Properties.Property.PRESSURE;
import static thermocycle.Properties.Property.TEMPERATURE;

/**
 *
 * @author Chris
 */
public class CycleIT {
    
    public CycleIT() {
    }

    /**
     * Test of singletonCollector method, of Turbine component
     */
    @Test
    public void testTurbine() {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,3000);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Turbine turb = gasTurb.createTurbine("Turbine");
        
        // set fluid
        gasTurb.setFluid(turb.getInlet(), air);
        
        //  set initial properties
        turb.getInlet().setMass(OptionalDouble.of(1));
        turb.getInlet().setState(PRESSURE, OptionalDouble.of(500000));
        turb.getInlet().setState(TEMPERATURE, OptionalDouble.of(900));
        turb.getOutlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        
        // report pre-solve
        gasTurb.reportSetup();
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
        
        // checks
        assert turb.equations.stream().allMatch(eq -> eq.)
        
    }
    
}
