/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

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
       //GT_Test();
       //GT_Test_InterCool();
       //GT_Test_Reheat();
       //GT_Test_ReheatInterCool();
       //GT_Test_WHRU();
       Refrigeration_Test();
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
        turb.getInlet().setMass(1.0);
        turb.getInlet().setProperty(PRESSURE, 500000.0);
        turb.getInlet().setProperty(TEMPERATURE, 900.0);
        turb.getOutlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        turb.setAttribute(EFFICIENCY, 0.95);
        
        // solve
        gasTurb.solve();
        
    }
    
    public static void Compressor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        comp.setAttribute(PRATIO, 5.0);
        comp.setAttribute(EFFICIENCY, 0.9);
        
        // set fluid
        gasTurb.setFluid(comp.getInlet(), air);
        
        //  set initial properties
        comp.getInlet().setMass(1.0);
        comp.getInlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        comp.getInlet().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        
        // solve
        gasTurb.solve();
        
    }
    
    public static void Combustor_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Combustor comb = gasTurb.createCombustor("Combustor");
        comb.setAttribute(PLOSS, 0.05);
        
        // set fluid
        gasTurb.setFluid(comb.getInlet(), air);
        
        //  set initial properties
        comb.getInlet().setMass(1.0);
        comb.getInlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        comb.getInlet().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        comb.getSupply().setHeat(2000.0);
        
        // solve
        gasTurb.solve();
        
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
        sink.getInlet().setMass(1.0);
        sink.getOutlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        sink.getOutlet().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        sink.getInlet().setProperty(TEMPERATURE, 500.0);
        sink.setAttribute(PLOSS,0.05);
        
        // solve
        gasTurb.solve();
     
    }
    
    public static void HeatExchanger_Test() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine");
        gasTurb.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        HeatExchanger whru = gasTurb.createHeatExchanger("WHRU");
        whru.setAttribute(EFFECTIVENESS, 0.95);
        
        // set fluid
        gasTurb.setFluid(whru.getInletHot(), air);
        gasTurb.setFluid(whru.getInletCold(), air);
        
        //  set initial properties
        whru.getInletHot().setMass(1.0);
        whru.getInletCold().setMass(1.0);
        whru.getInletCold().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        whru.getInletCold().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        whru.getInletHot().setProperty(TEMPERATURE, 500.0);
        whru.getInletHot().setProperty(PRESSURE, 3e5);
        
        // solve
        gasTurb.solve();
     
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
        comp.getInlet().setMass(1.0);
        comp.setAttribute(PRATIO, 5.0);
        comp.setAttribute(EFFICIENCY, 0.9);
        comp.getInlet().setProperty(PRESSURE, gasTurb.getAmbient(PRESSURE).getAsDouble());
        comp.getInlet().setProperty(TEMPERATURE, gasTurb.getAmbient(TEMPERATURE).getAsDouble());
        comb.getOutlet().setProperty(Property.TEMPERATURE, 1300.0);
        comb.setAttribute(PLOSS, 0.05);
        turb.setAttribute(EFFICIENCY, 0.95);
        sink.setAttribute(PLOSS, 0.05);
                
        // solve
        gasTurb.solve();
        
    }
    
    public static void GT_Test_InterCool() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurbCool = new Cycle("Gas Turbine with Intercooling");
        gasTurbCool.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurbCool.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor lp_comp = gasTurbCool.createCompressor("LP Compressor");
        lp_comp.setAttribute(PRATIO, 4.0);
        lp_comp.setAttribute(EFFICIENCY, 0.9);
        HeatSink cool = gasTurbCool.createHeatSink("Inter Cooler");
        Compressor hp_comp = gasTurbCool.createCompressor("HP Compressor");
        hp_comp.setAttribute(PRATIO, 5.0);
        hp_comp.setAttribute(EFFICIENCY, 0.9);
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
        lp_comp.getInlet().setMass(1.0);
        lp_comp.getInlet().setProperty(PRESSURE, gasTurbCool.getAmbient(PRESSURE).getAsDouble());
        lp_comp.getInlet().setProperty(TEMPERATURE, gasTurbCool.getAmbient(TEMPERATURE).getAsDouble());
        
        comb.getOutlet().setProperty(Property.TEMPERATURE, 1300.0);
        cool.getOutlet().setProperty(Property.TEMPERATURE, 350.0);
        comb.setAttribute(PLOSS, 0.05);
        turb.setAttribute(EFFICIENCY, 0.95);
        cool.setAttribute(PLOSS, 0.05);
        sink.setAttribute(PLOSS, 0.05);
        
        // solve
        gasTurbCool.solve();
        
    }
    
    public static void GT_Test_Reheat() throws InterruptedException, ExecutionException {
        
        // create cycle
        Cycle gasTurbHeat = new Cycle("Gas Turbine");
        gasTurbHeat.setAmbient(101325,300);
        
        // create fluids
        IdealGas air = gasTurbHeat.createIdealGas("Air", 1.4, 287.0);
        
        // create components
        Compressor comp = gasTurbHeat.createCompressor("Compressor");
        comp.setAttribute(PRATIO, 20.0);
        comp.setAttribute(EFFICIENCY, 0.9);
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
        
        comb.getOutlet().setProperty(Property.TEMPERATURE, 1300.0);
        reheat.getOutlet().setProperty(TEMPERATURE, 1300.0);
        hp_turb.getOutlet().setProperty(Property.PRESSURE, gasTurbHeat.getAmbient(Properties.Property.PRESSURE).getAsDouble()*5.0);
        comb.setAttribute(PLOSS, 0.05);
        reheat.setAttribute(PLOSS, 0.05);
        lp_turb.setAttribute(EFFICIENCY, 0.95);
        hp_turb.setAttribute(EFFICIENCY, 0.95);
        sink.setAttribute(PLOSS, 0.05);
                
        // solve
        gasTurbHeat.solve();
        
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
        gasTurb.solve();
        
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
        comb.getOutlet().setProperty(Property.TEMPERATURE, 1300.0);
        turb.getOutlet().setProperty(Property.PRESSURE, gasTurb.getAmbient(Properties.Property.PRESSURE).getAsDouble());
        turb.setAttribute(EFFICIENCY, 0.95);
        comp.setAttribute(PRATIO, 10.0);
        comp.setAttribute(EFFICIENCY, 0.9);
        comb.setAttribute(PLOSS, 0.05);
        sink.setAttribute(PLOSS, 0.0);
        
        // solve
        gasTurb.solve();
        
    }
    
    public static void Refrigeration_Test() throws InterruptedException, ExecutionException {
        
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
        refridge.solve();
        
    }    
}
