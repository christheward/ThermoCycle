/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.util.Arrays;
import thermocycle.*;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Properties.Property.*;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class EndToEndTest {
    
    public EndToEndTest() {
        // specify log4j2 configuration file location
        System.setProperty("log4j.configurationFile", "./src/resources/log4j2.xml");
    }
    
    @Test
    public void Turbine_Test() throws InterruptedException, ExecutionException {
                
        // create cycle
        Cycle gasTurb = new Cycle("Turbine Test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Turbine turb = gasTurb.createTurbine("Turbine");
        
        // set fluid
        gasTurb.setFluid(turb.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(turb.getInlet(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionProperty(turb.getInlet(), PRESSURE, Arrays.asList(new Double[] {1000000.0}));
        gasTurb.setBoundaryConditionProperty(turb.getInlet(), TEMPERATURE, Arrays.asList(new Double[] {900.0}));
        gasTurb.setBoundaryConditionProperty(turb.getOutlet(), PRESSURE, Arrays.asList(new Double[] {101325.0}));
        gasTurb.setBoundaryConditionAttribute(turb, EFFICIENCY, Arrays.asList(new Double[] {0.95}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void Compressor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Compressor Test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        
        // set fluid
        gasTurb.setFluid(comp.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comp.getInlet(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), TEMPERATURE, Arrays.asList(new Double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionAttribute(comp, PRATIO, Arrays.asList(new Double[] {5.0}));
        gasTurb.setBoundaryConditionAttribute(comp, EFFICIENCY, Arrays.asList(new Double[] {0.9}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void Combustor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Combustor Test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Combustor comb = gasTurb.createCombustor("Combustor");
        
        // set fluid
        gasTurb.setFluid(comb.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comb.getInlet(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionProperty(comb.getInlet(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comb.getInlet(), TEMPERATURE, Arrays.asList(new Double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionAttribute(comb, PLOSS, Arrays.asList(new Double[] {0.05}));
        gasTurb.setBoundaryConditionHeat(comb.getSupply(), Arrays.asList(new Double[] {2000.0}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void HeatSink_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Heat Sink Test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");
        
        // set fluid
        gasTurb.setFluid(sink.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(sink.getInlet(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionProperty(sink.getOutlet(), TEMPERATURE, Arrays.asList(new Double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(sink.getOutlet(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(sink.getInlet(), TEMPERATURE, Arrays.asList(new Double[] {500.0}));
        gasTurb.setBoundaryConditionAttribute(sink, PLOSS, Arrays.asList(new Double[] {0.05}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void HeatExchanger_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Heat exchanger test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatExchanger whru = gasTurb.createHeatExchanger("WHRU");
        
        // set fluid
        gasTurb.setFluid(whru.getInletHot(), air);
        gasTurb.setFluid(whru.getInletCold(), air);
        
        // Set boundary conditions
        gasTurb.setBoundaryConditionMass(whru.getInletCold(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionMass(whru.getInletHot(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionAttribute(whru, EFFECTIVENESS, Arrays.asList(new Double[] {0.95}));
        gasTurb.setBoundaryConditionProperty(whru.getInletCold(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(whru.getInletCold(), TEMPERATURE, Arrays.asList(new Double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(whru.getInletHot(), PRESSURE, Arrays.asList(new Double[] {3e5}));
        gasTurb.setBoundaryConditionProperty(whru.getInletHot(), TEMPERATURE, Arrays.asList(new Double[] {500.0}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void GT_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine Test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        Combustor comb = gasTurb.createCombustor("Combustor");
        Turbine turb = gasTurb.createTurbine("Turbine");
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");
        
        // create connections
        gasTurb.createConnection(comp.getOutlet(), comb.getInlet());
        gasTurb.createConnection(comb.getOutlet(), turb.getInlet());
        gasTurb.createConnection(turb.getOutlet(), sink.getInlet());
        gasTurb.createConnection(sink.getOutlet(), comp.getInlet());
        
        // set fluid
        gasTurb.setFluid(comp.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comp.getInlet(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), TEMPERATURE, Arrays.asList(new Double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comb.getOutlet(), TEMPERATURE, Arrays.asList(new Double[] {1300.0}));
        gasTurb.setBoundaryConditionAttribute(comp, PRATIO, Arrays.asList(new Double[] {5.0}));
        gasTurb.setBoundaryConditionAttribute(comp, EFFICIENCY, Arrays.asList(new Double[] {0.9}));
        gasTurb.setBoundaryConditionAttribute(comb, PLOSS, Arrays.asList(new Double[] {0.05}));
        gasTurb.setBoundaryConditionAttribute(turb, EFFICIENCY, Arrays.asList(new Double[] {0.95}));
        gasTurb.setBoundaryConditionAttribute(sink, PLOSS, Arrays.asList(new Double[] {0.05}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void GT_Test_ReheatInterCool() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine with Reheat and Intercooler Test");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor lp_comp = gasTurb.createCompressor("LP Compressor");
        Compressor hp_comp = gasTurb.createCompressor("HP Compressor");
        HeatSink cool = gasTurb.createHeatSink("Intercooler");
        Combustor comb = gasTurb.createCombustor("Combustor");
        Combustor reheat = gasTurb.createCombustor("Reheat");
        Turbine hp_turb = gasTurb.createTurbine("HP Turbine");
        Turbine lp_turb = gasTurb.createTurbine("LP Turbine");
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");
        
        // create connections
        gasTurb.createConnection(lp_comp.getOutlet(), cool.getInlet());
        gasTurb.createConnection(cool.getOutlet(), hp_comp.getInlet());
        gasTurb.createConnection(hp_comp.getOutlet(), comb.getInlet());
        gasTurb.createConnection(comb.getOutlet(), hp_turb.getInlet());
        gasTurb.createConnection(hp_turb.getOutlet(), reheat.getInlet());
        gasTurb.createConnection(reheat.getOutlet(), lp_turb.getInlet());
        gasTurb.createConnection(lp_turb.getOutlet(), sink.getInlet());
        gasTurb.createConnection(sink.getOutlet(), lp_comp.getInlet());
        
        // set fluid
        gasTurb.setFluid(lp_comp.getInlet(), air);
        
        //  set initial properties
        gasTurb.setBoundaryConditionAttribute(lp_comp, PRATIO, Arrays.asList(new Double[] {5.0}));
        gasTurb.setBoundaryConditionAttribute(lp_comp, EFFICIENCY, Arrays.asList(new Double[] {0.9}));
        gasTurb.setBoundaryConditionAttribute(hp_comp, PRATIO, Arrays.asList(new Double[] {4.0}));
        gasTurb.setBoundaryConditionAttribute(hp_comp, EFFICIENCY, Arrays.asList(new Double[] {0.9}));
        gasTurb.setBoundaryConditionMass(lp_comp.getInlet(), Arrays.asList(new Double[] {1.0}));
        gasTurb.setBoundaryConditionProperty(lp_comp.getInlet(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(lp_comp.getInlet(), TEMPERATURE, Arrays.asList(new Double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(cool.getOutlet(), TEMPERATURE, Arrays.asList(new Double[] {300.0}));
        gasTurb.setBoundaryConditionProperty(comb.getOutlet(), TEMPERATURE, Arrays.asList(new Double[] {1300.0}));
        gasTurb.setBoundaryConditionProperty(hp_turb.getOutlet(), PRESSURE, Arrays.asList(new Double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()*5.0}));
        gasTurb.setBoundaryConditionProperty(reheat.getOutlet(), TEMPERATURE, Arrays.asList(new Double[] {1300.0}));
        gasTurb.setBoundaryConditionAttribute(comb, PLOSS, Arrays.asList(new Double[] {0.05}));
        gasTurb.setBoundaryConditionAttribute(reheat, PLOSS, Arrays.asList(new Double[] {0.05}));
        gasTurb.setBoundaryConditionAttribute(lp_turb, EFFICIENCY, Arrays.asList(new Double[] {0.95}));
        gasTurb.setBoundaryConditionAttribute(hp_turb, EFFICIENCY, Arrays.asList(new Double[] {0.95}));
        gasTurb.setBoundaryConditionAttribute(sink, PLOSS, Arrays.asList(new Double[] {0.05}));
        gasTurb.setBoundaryConditionAttribute(cool, PLOSS, Arrays.asList(new Double[] {0.05}));
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
}
