/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;
import static thermocycle.Properties.Property.*;
import static thermocycle.Attributes.Attribute.*;

/**
 *
 * @author Chris
 */
public class ThermoCycle implements Properties {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
       //Turbine_Test();
       //Compressor_Test();
       //Combustor_Test();
       //HeatSink_Test();
       //HeatExchanger_Test();
       GT_Test();
       //GT_Test_InterCool();
       //GT_Test_Reheat();
       //GT_Test_ReheatInterCool();
       //GT_Test_WHRU();
    }
    
    public static void Turbine_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
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
        
    }
    
    public static void Compressor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        comp.setAttribute(PRATIO, OptionalDouble.of(5));
        comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
        
        // set fluid
        gasTurb.setFluid(comp.getInlet(), air);
        
        //  set initial properties
        comp.getInlet().setMass(OptionalDouble.of(1));
        comp.getInlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        comp.getInlet().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));
        
        // report pre-solve
        gasTurb.reportSetup();
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
        
    }
    
    public static void Combustor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Combustor comb = gasTurb.createCombustor("Combustor");
        comb.setAttribute(PLOSS, OptionalDouble.of(0.05));
        
        // set fluid
        gasTurb.setFluid(comb.getInlet(), air);
        
        //  set initial properties
        comb.getInlet().setMass(OptionalDouble.of(1));
        comb.getInlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        comb.getInlet().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));
        comb.getSupply().setHeat(OptionalDouble.of(2000));
        
        // report pre-solve
        gasTurb.reportSetup();
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
     
    }
    
    public static void HeatSink_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");
        
        // set fluid
        gasTurb.setFluid(sink.getInlet(), air);
        
        //  set initial properties
        sink.getInlet().setMass(OptionalDouble.of(1));
        sink.getOutlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        sink.getOutlet().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));
        sink.getInlet().setState(TEMPERATURE, OptionalDouble.of(500));
        sink.setAttribute(PLOSS,OptionalDouble.of(0.05));
        
        // report pre-solve
        gasTurb.reportSetup();
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
     
    }
    
    public static void HeatExchanger_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatExchanger whru = gasTurb.createHeatExchanger("WHRU");
        whru.setAttribute(EFFECTIVENESS, OptionalDouble.of(0.95));
        
        // set fluid
        gasTurb.setFluid(whru.getInletHot(), air);
        gasTurb.setFluid(whru.getInletCold(), air);
        
        //  set initial properties
        whru.getInletHot().setMass(OptionalDouble.of(1));
        whru.getInletCold().setMass(OptionalDouble.of(1));
        whru.getInletCold().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        whru.getInletCold().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));
        whru.getInletHot().setState(TEMPERATURE, OptionalDouble.of(500));
        whru.getInletHot().setState(PRESSURE, OptionalDouble.of(3e5));
        
        // report pre-solve
        gasTurb.reportSetup();
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
     
   }
    
    public static void GT_Test() throws InterruptedException, ExecutionException {
        
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
        
        //  set initial properties
        comp.getInlet().setMass(OptionalDouble.of(1));
        comp.setAttribute(PRATIO, OptionalDouble.of(5));
        comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
        comp.getInlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        comp.getInlet().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));        
        comb.getOutlet().setState(Property.TEMPERATURE, OptionalDouble.of(1300));
        comb.setAttribute(PLOSS, OptionalDouble.of(0.05));
        turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        sink.setAttribute(PLOSS, OptionalDouble.of(0.05));
        
        // report pre-solve
        gasTurb.reportSetup();
                
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
        
    }
    
    public static void GT_Test_InterCool() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurbCool = new Cycle("Gas Turbine with Intercooling");
        gasTurbCool.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurbCool.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor lp_comp = gasTurbCool.createCompressor("LP Compressor");
        lp_comp.setAttribute(PRATIO, OptionalDouble.of(4));
        lp_comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
        HeatSink cool = gasTurbCool.createHeatSink("Inter Cooler");
        Compressor hp_comp = gasTurbCool.createCompressor("HP Compressor");
        hp_comp.setAttribute(PRATIO, OptionalDouble.of(5));
        hp_comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
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
        
        //  set initial properties
        lp_comp.getInlet().setMass(OptionalDouble.of(1));
        lp_comp.getInlet().setState(PRESSURE, gasTurbCool.getAmbient(PRESSURE));
        lp_comp.getInlet().setState(TEMPERATURE, gasTurbCool.getAmbient(TEMPERATURE));
        
        comb.getOutlet().setState(Property.TEMPERATURE, OptionalDouble.of(1300));
        cool.getOutlet().setState(Property.TEMPERATURE, OptionalDouble.of(350));        
        comb.setAttribute(PLOSS, OptionalDouble.of(0.05));
        turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        cool.setAttribute(PLOSS, OptionalDouble.of(0.05));
        sink.setAttribute(PLOSS, OptionalDouble.of(0.05));
        
        // reports pre-solve
        gasTurbCool.reportSetup();
        
        // solve
        gasTurbCool.solve();
        
        // reports post-solve
        gasTurbCool.reportSolver();
        gasTurbCool.reportResults();
        
    }
    
    public static void GT_Test_Reheat() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurbHeat = new Cycle("Gas Turbine");
        gasTurbHeat.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurbHeat.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurbHeat.createCompressor("Compressor");
        comp.setAttribute(PRATIO, OptionalDouble.of(20));
        comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
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
        comp.getInlet().setMass(OptionalDouble.of(1));
        comp.getInlet().setState(PRESSURE, gasTurbHeat.getAmbient(PRESSURE));
        comp.getInlet().setState(TEMPERATURE, gasTurbHeat.getAmbient(TEMPERATURE));
        
        comb.getOutlet().setState(Property.TEMPERATURE, OptionalDouble.of(1300));
        reheat.getOutlet().setState(TEMPERATURE, OptionalDouble.of(1300));
        hp_turb.getOutlet().setState(Property.PRESSURE, OptionalDouble.of(gasTurbHeat.getAmbient(Properties.Property.PRESSURE).getAsDouble()*5));
        comb.setAttribute(PLOSS, OptionalDouble.of(0.05));
        reheat.setAttribute(PLOSS, OptionalDouble.of(0.05));
        lp_turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        hp_turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        sink.setAttribute(PLOSS, OptionalDouble.of(0.05));
        
        // Report pre-solve
        gasTurbHeat.reportSetup();
                
        // solve
        gasTurbHeat.solve();
        
        // reports post-solve
        gasTurbHeat.reportSolver();
        gasTurbHeat.reportResults();
        
    }
    
    public static void GT_Test_ReheatInterCool() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor lp_comp = gasTurb.createCompressor("LP Compressor");
        Compressor hp_comp = gasTurb.createCompressor("HP Compressor");
        lp_comp.setAttribute(PRATIO, OptionalDouble.of(5));
        lp_comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
        hp_comp.setAttribute(PRATIO, OptionalDouble.of(4));
        hp_comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
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
        lp_comp.getInlet().setMass(OptionalDouble.of(1));
        lp_comp.getInlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        lp_comp.getInlet().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));
        cool.getOutlet().setState(TEMPERATURE, OptionalDouble.of(350));
        comb.getOutlet().setState(TEMPERATURE, OptionalDouble.of(1300));
        hp_turb.getOutlet().setState(PRESSURE, OptionalDouble.of(gasTurb.getAmbient(PRESSURE).getAsDouble()*5));
        reheat.getOutlet().setState(TEMPERATURE, OptionalDouble.of(1300));
        comb.setAttribute(PLOSS, OptionalDouble.of(0.05));
        reheat.setAttribute(PLOSS, OptionalDouble.of(0.05));
        lp_turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        hp_turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        sink.setAttribute(PLOSS, OptionalDouble.of(0.05));
        cool.setAttribute(PLOSS, OptionalDouble.of(0.05));
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSetup();
        gasTurb.reportSolver();
        gasTurb.reportResults();
        
    }
    
    public static void GT_Test_WHRU() throws InterruptedException, ExecutionException {
        
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
        whru.setAttribute(EFFECTIVENESS, OptionalDouble.of(0.95));
        
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
        comp.getInlet().setMass(OptionalDouble.of(1));
        comp.getInlet().setState(PRESSURE, gasTurb.getAmbient(PRESSURE));
        comp.getInlet().setState(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE));
        comb.getOutlet().setState(Property.TEMPERATURE, OptionalDouble.of(1300));
        turb.getOutlet().setState(Property.PRESSURE, gasTurb.getAmbient(Properties.Property.PRESSURE));
        turb.setAttribute(EFFICIENCY, OptionalDouble.of(0.95));
        comp.setAttribute(PRATIO, OptionalDouble.of(10));
        comp.setAttribute(EFFICIENCY, OptionalDouble.of(0.9));
        comb.setAttribute(PLOSS, OptionalDouble.of(0.05));
        sink.setAttribute(PLOSS, OptionalDouble.of(0.0));
        
        // reports pre-solve
        gasTurb.reportSetup();
        
        // solve
        gasTurb.solve();
        
        // reports post solve
        gasTurb.reportSolver();
        gasTurb.reportResults();
        
    }    
}
