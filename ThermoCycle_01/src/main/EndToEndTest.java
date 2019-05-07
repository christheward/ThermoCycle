/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import thermocycle.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import thermocycle.Combustor;
import thermocycle.Compressor;
import thermocycle.Cycle;
import thermocycle.HeatExchanger;
import thermocycle.HeatSink;
import thermocycle.IdealGas;
import thermocycle.Turbine;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Properties.Property.*;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class EndToEndTest {
    
    public static void main(String[] args) {
        
    }
    
    
    public EndToEndTest() {
        try {
            Turbine_Test();
        } catch (InterruptedException ex) {
            Logger.getLogger(EndToEndTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(EndToEndTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Turbine_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Turbine turb = gasTurb.createTurbine("Turbine");
        
        // set fluid
        gasTurb.setFluid(turb.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(turb.getInlet(), new double[] {1.0});
        gasTurb.setBoundaryConditionProperty(turb.getInlet(), PRESSURE, new double[] {1000000});
        gasTurb.setBoundaryConditionProperty(turb.getInlet(), TEMPERATURE, new double[] {900});
        gasTurb.setBoundaryConditionProperty(turb.getOutlet(), PRESSURE, new double[] {101325});
        gasTurb.setBoundaryConditionAttribute(turb, EFFICIENCY, new double[] {0.95});
        
        // solve
        boolean solve = gasTurb.solveParametric();
                
    }
    
    public void Compressor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        
        // set fluid
        gasTurb.setFluid(comp.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comp.getInlet(), new double[] {1.0});
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), PRESSURE, new double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), TEMPERATURE, new double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()});
        gasTurb.setBoundaryConditionAttribute(comp, PRATIO, new double[] {5.0});
        gasTurb.setBoundaryConditionAttribute(comp, EFFICIENCY, new double[] {0.9});
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
    }
    
    public void Combustor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Combustor comb = gasTurb.createCombustor("Combustor");
        
        // set fluid
        gasTurb.setFluid(comb.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comb.getInlet(), new double[] {1.0});
        gasTurb.setBoundaryConditionProperty(comb.getInlet(), PRESSURE, new double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(comb.getInlet(), TEMPERATURE, new double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()});
        gasTurb.setBoundaryConditionAttribute(comb, PLOSS, new double[] {0.05});
        gasTurb.setBoundaryConditionHeat(comb.getSupply(), new double[] {2000.0});
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
        System.out.println(gasTurb);
        
    }
    
    public void HeatSink_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");
        
        // set fluid
        gasTurb.setFluid(sink.getInlet(), air);
        
        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(sink.getInlet(), new double[] {1.0});
        gasTurb.setBoundaryConditionProperty(sink.getOutlet(), TEMPERATURE, new double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(sink.getOutlet(), PRESSURE, new double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(sink.getInlet(), TEMPERATURE, new double[] {500});
        gasTurb.setBoundaryConditionAttribute(sink, PLOSS, new double[] {5.0});
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
    }
    
    public void HeatExchanger_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatExchanger whru = gasTurb.createHeatExchanger("WHRU");
        
        // set fluid
        gasTurb.setFluid(whru.getInletHot(), air);
        gasTurb.setFluid(whru.getInletCold(), air);
        
        // Set boundary conditions
        gasTurb.setBoundaryConditionMass(whru.getInletCold(), new double[] {1.0});
        gasTurb.setBoundaryConditionMass(whru.getInletHot(), new double[] {1.0});
        gasTurb.setBoundaryConditionAttribute(whru, EFFECTIVENESS, new double[] {0.95});
        gasTurb.setBoundaryConditionProperty(whru.getInletCold(), PRESSURE, new double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(whru.getInletCold(), TEMPERATURE, new double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(whru.getInletHot(), PRESSURE, new double[] {3e5});
        gasTurb.setBoundaryConditionProperty(whru.getInletHot(), TEMPERATURE, new double[] {500});
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
    }
    
    public void GT_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
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
        gasTurb.setBoundaryConditionMass(comp.getInlet(), new double[] {1.0});
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), PRESSURE, new double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(comp.getInlet(), TEMPERATURE, new double[] {gasTurb.getAmbient(TEMPERATURE).getAsDouble()});
        gasTurb.setBoundaryConditionProperty(comb.getOutlet(), PRESSURE, new double[] {gasTurb.getAmbient(PRESSURE).getAsDouble()});
        gasTurb.setBoundaryConditionAttribute(comp, PRATIO, new double[] {5.0});
        gasTurb.setBoundaryConditionAttribute(comp, EFFICIENCY, new double[] {0.9});
        gasTurb.setBoundaryConditionAttribute(comb, PLOSS, new double[] {0.05});
        gasTurb.setBoundaryConditionAttribute(turb, EFFICIENCY, new double[] {0.95});
        gasTurb.setBoundaryConditionAttribute(sink, PLOSS, new double[] {0.05});
        
        // solve
        boolean solve = gasTurb.solveParametric();
        
    }
    
    public void GT_Test_InterCool() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurbCool = new Cycle("Gas Turbine with Intercooling");
        gasTurbCool.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurbCool.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor lp_comp = gasTurbCool.createCompressor("LP Compressor");
        HeatSink cool = gasTurbCool.createHeatSink("Inter Cooler");
        Compressor hp_comp = gasTurbCool.createCompressor("HP Compressor");
        Combustor comb = gasTurbCool.createCombustor("Combustor");
        Turbine turb = gasTurbCool.createTurbine("Turbine");
        HeatSink sink = gasTurbCool.createHeatSink("Heat Sink");
        
        // create connections
        gasTurbCool.createConnection(lp_comp.getOutlet(), cool.getInlet());
        gasTurbCool.createConnection(cool.getOutlet(), hp_comp.getInlet());
        gasTurbCool.createConnection(hp_comp.getOutlet(), comb.getInlet());
        gasTurbCool.createConnection(comb.getOutlet(), turb.getInlet());
        gasTurbCool.createConnection(turb.getOutlet(), sink.getInlet());
        gasTurbCool.createConnection(sink.getOutlet(), lp_comp.getInlet());
        
        // set fluid
        gasTurbCool.setFluid(hp_comp.getInlet(), air);
        
        //  set boundary conditions
        gasTurbCool.setBoundaryConditionMass(lp_comp.getInlet(), new double[] {1.0});
        gasTurbCool.setBoundaryConditionProperty(lp_comp.getInlet(), PRESSURE, new double[] {gasTurbCool.getAmbient(PRESSURE).getAsDouble()});
        gasTurbCool.setBoundaryConditionProperty(lp_comp.getInlet(), TEMPERATURE, new double[] {gasTurbCool.getAmbient(TEMPERATURE).getAsDouble()});
        gasTurbCool.setBoundaryConditionProperty(comb.getOutlet(), TEMPERATURE, new double[] {1300.0});
        gasTurbCool.setBoundaryConditionProperty(cool.getOutlet(), TEMPERATURE, new double[] {350.0});
        gasTurbCool.setBoundaryConditionAttribute(lp_comp, PRATIO, new double[] {4.0});
        gasTurbCool.setBoundaryConditionAttribute(lp_comp, EFFICIENCY, new double[] {0.9});
        gasTurbCool.setBoundaryConditionAttribute(hp_comp, PRATIO, new double[] {5.0});
        gasTurbCool.setBoundaryConditionAttribute(hp_comp, EFFICIENCY, new double[] {0.9});
        gasTurbCool.setBoundaryConditionAttribute(comb, PLOSS, new double[] {0.05});
        gasTurbCool.setBoundaryConditionAttribute(turb, EFFICIENCY, new double[] {0.95});
        gasTurbCool.setBoundaryConditionAttribute(cool, PLOSS, new double[] {0.05});
        gasTurbCool.setBoundaryConditionAttribute(sink, PLOSS, new double[] {0.05});
        
        // solve
        boolean solve = gasTurbCool.solveParametric();
        
    }
    /**
    @Test
    public void GT_Test_Reheat() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurbHeat = new Cycle("Gas Turbine");
        gasTurbHeat.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurbHeat.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurbHeat.createCompressor("Compressor");
        Combustor comb = gasTurbHeat.createCombustor("Combustor");
        Combustor reheat = gasTurbHeat.createCombustor("Reheat");
        Turbine hp_turb = gasTurbHeat.createTurbine("HP Turbine");
        Turbine lp_turb = gasTurbHeat.createTurbine("LP Turbine");
        HeatSink sink = gasTurbHeat.createHeatSink("Heat Sink");
        
        // create connections
        gasTurbHeat.createConnection(comp.getOutlet(), comb.getInlet());
        gasTurbHeat.createConnection(comb.getOutlet(), hp_turb.getInlet());
        gasTurbHeat.createConnection(hp_turb.getOutlet(), reheat.getInlet());
        gasTurbHeat.createConnection(reheat.getOutlet(), lp_turb.getInlet());
        gasTurbHeat.createConnection(lp_turb.getOutlet(), sink.getInlet());
        gasTurbHeat.createConnection(sink.getOutlet(), comp.getInlet());
        
        // set fluid
        gasTurbHeat.setFluid(comp.getInlet(), air);
        
        //  set initial properties
        comp.getInlet().setMass(1.0);
        comp.getInlet().setProperty(PRESSURE, gasTurbHeat.getAmbient(PRESSURE).getAsDouble());
        comp.getInlet().setProperty(TEMPERATURE, gasTurbHeat.getAmbient(TEMPERATURE).getAsDouble());
        comp.setAttribute(PRATIO, 20.0);
        comp.setAttribute(EFFICIENCY, 0.9);
        
        comb.getOutlet().setProperty(Properties.Property.TEMPERATURE, 1300.0);
        reheat.getOutlet().setProperty(TEMPERATURE, 1300.0);
        hp_turb.getOutlet().setProperty(Properties.Property.PRESSURE, gasTurbHeat.getAmbient(Properties.Property.PRESSURE).getAsDouble()*5.0);
        comb.setAttribute(PLOSS, 0.05);
        reheat.setAttribute(PLOSS, 0.05);
        lp_turb.setAttribute(EFFICIENCY, 0.95);
        hp_turb.setAttribute(EFFICIENCY, 0.95);
        sink.setAttribute(PLOSS, 0.05);
        
        // solve
        boolean solve = gasTurbHeat.solve();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void GT_Test_ReheatInterCool() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor lp_comp = gasTurb.createCompressor("LP Compressor");
        Compressor hp_comp = gasTurb.createCompressor("HP Compressor");
        lp_comp.setAttribute(PRATIO, 5.0);
        lp_comp.setAttribute(EFFICIENCY, 0.9);
        hp_comp.setAttribute(PRATIO, 4.0);
        hp_comp.setAttribute(EFFICIENCY, 0.9);
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
        lp_comp.getInlet().setMass(1.0);
        lp_comp.getInlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        lp_comp.getInlet().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        cool.getOutlet().setProperty(TEMPERATURE, 350.0);
        comb.getOutlet().setProperty(TEMPERATURE, 1300.0);
        hp_turb.getOutlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble()*5.0);
        reheat.getOutlet().setProperty(TEMPERATURE, 1300.0);
        comb.setAttribute(PLOSS, 0.05);
        reheat.setAttribute(PLOSS, 0.05);
        lp_turb.setAttribute(EFFICIENCY, 0.95);
        hp_turb.setAttribute(EFFICIENCY, 0.95);
        sink.setAttribute(PLOSS, 0.05);
        cool.setAttribute(PLOSS, 0.05);
        
        // solve
        boolean solve = gasTurb.solve();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void GT_Test_WHRU() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        Combustor comb = gasTurb.createCombustor("Combustor");
        Turbine turb = gasTurb.createTurbine("Turbine");
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");
        HeatExchanger whru = gasTurb.createHeatExchanger("WHRU");
        whru.setAttribute(EFFECTIVENESS, 0.95);
        
        // create connections
        gasTurb.createConnection(comp.getOutlet(), whru.getInletCold());
        gasTurb.createConnection(whru.getOutletCold(), comb.getInlet());
        gasTurb.createConnection(comb.getOutlet(), turb.getInlet());
        gasTurb.createConnection(turb.getOutlet(), whru.getInletHot());
        gasTurb.createConnection(whru.getOutletHot(), sink.getInlet());
        gasTurb.createConnection(sink.getOutlet(), comp.getInlet());
        
        // set fluid
        gasTurb.setFluid(comp.getInlet(), air);
        
        //  set initial properties
        comp.getInlet().setMass(1.0);
        comp.getInlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        comp.getInlet().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        comb.getOutlet().setProperty(Properties.Property.TEMPERATURE, 1300.0);
        turb.getOutlet().setProperty(Properties.Property.PRESSURE, gasTurb.getAmbient(Properties.Property.PRESSURE).getAsDouble());
        turb.setAttribute(EFFICIENCY, 0.95);
        comp.setAttribute(PRATIO, 10.0);
        comp.setAttribute(EFFICIENCY, 0.9);
        comb.setAttribute(PLOSS, 0.05);
        sink.setAttribute(PLOSS, 0.0);
        
        // solve
        boolean solve = gasTurb.solve();
        
        // assertions
        assertEquals(solve, true);
        
    }
    
    @Test
    public void Refrigeration_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle refridge = new Cycle("Refrigertion cycle");
        refridge.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = refridge.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = refridge.createCompressor("Compressor");
        Combustor comb = refridge.createCombustor("Evapourator");
        Turbine turb = refridge.createTurbine("Turbine");
        HeatSink sink = refridge.createHeatSink("Condensor");
        
        // create connections
        refridge.createConnection(comp.getOutlet(), sink.getInlet());
        refridge.createConnection(sink.getOutlet(), turb.getInlet());
        refridge.createConnection(turb.getOutlet(), comb.getInlet());
        refridge.createConnection(comb.getOutlet(), comp.getInlet());

        // set fluid
        refridge.setFluid(comp.getInlet(), air);
        
        //  set initial properties
        comp.getInlet().setMass(1.0);
        comp.getInlet().setProperty(PRESSURE, refridge.getAmbient(PRESSURE).getAsDouble());
        comp.getInlet().setProperty(TEMPERATURE, 253.0);
        
        sink.getOutlet().setProperty(TEMPERATURE, 257.0);
        
        turb.setAttribute(EFFICIENCY, 0.95);
        comp.setAttribute(PRATIO, 10.0);
        comp.setAttribute(EFFICIENCY, 0.9);
        comb.setAttribute(PLOSS, 0.0);
        sink.setAttribute(PLOSS, 0.0);
        
        // solve
        boolean solve = refridge.solve();
        
        // assertions
        assertEquals(solve, true);
        
    }
    */
}
