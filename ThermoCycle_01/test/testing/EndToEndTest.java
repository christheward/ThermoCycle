/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import thermocycle.*;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
    public void Encodinf() throws UnsupportedEncodingException {
        String test;

        //PrintStream ps = new PrintStream(System.out, true, "UTF-8");
        PrintStream ps = new PrintStream(System.out, true, "UTF-16");

        test = "\u03C1";
        ps.print(test);
    }

    @Test
    public void Turbine_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Turbine Test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);

        // create components
        Turbine turb = gasTurb.createTurbine("Turbine");

        // set fluid
        gasTurb.setFluid(turb.getFlowNode("Inlet"), air);

        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(turb.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(turb.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{1000000.0}));
        gasTurb.setBoundaryConditionProperty(turb.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{900.0}));
        gasTurb.setBoundaryConditionProperty(turb.getFlowNode("Outlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{101325.0}));
        gasTurb.setBoundaryConditionAttribute(turb, turb.EFFICIENCY, Arrays.asList(new Double[]{0.95}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

    @Test
    public void Compressor_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Compressor Test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);

        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");

        // set fluid
        gasTurb.setFluid(comp.getFlowNode("Inlet"), air);

        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comp.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(comp.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comp.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionAttribute(comp, comp.P_RATIO, Arrays.asList(new Double[]{5.0}));
        gasTurb.setBoundaryConditionAttribute(comp, comp.EFFICIENCY, Arrays.asList(new Double[]{0.9}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

    @Test
    public void Combustor_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Combustor Test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);

        // create components
        Combustor comb = gasTurb.createCombustor("Combustor");

        // set fluid
        gasTurb.setFluid(comb.getFlowNode("Inlet"), air);

        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comb.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(comb.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comb.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionAttribute(comb, comb.P_LOSS, Arrays.asList(new Double[]{0.05}));
        gasTurb.setBoundaryConditionHeat(comb.getHeatNode("Supply"), Arrays.asList(new Double[]{2000.0}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

    @Test
    public void HeatSink_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Heat Sink Test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);

        // create components
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");

        // set fluid
        gasTurb.setFluid(sink.getFlowNode("Inlet"), air);

        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(sink.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(sink.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(sink.getFlowNode("Outlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(sink.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{500.0}));
        gasTurb.setBoundaryConditionAttribute(sink, sink.P_LOSS, Arrays.asList(new Double[]{0.05}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

    @Test
    public void HeatExchanger_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Heat exchanger test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);

        // create components
        HeatExchanger whru = gasTurb.createHeatExchanger("WHRU");

        // set fluid
        gasTurb.setFluid(whru.getFlowNode("Hot Side Inlet"), air);
        gasTurb.setFluid(whru.getFlowNode("Cold Side Inlet"), air);

        // Set boundary conditions
        gasTurb.setBoundaryConditionMass(whru.getFlowNode("Cold Side Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionMass(whru.getFlowNode("Hot Side Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionAttribute(whru, whru.EFFECTIVENESS, Arrays.asList(new Double[]{0.95}));
        gasTurb.setBoundaryConditionProperty(whru.getFlowNode("Cold Side Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(whru.getFlowNode("Cold Side Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(whru.getFlowNode("Hot Side Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{3e5}));
        gasTurb.setBoundaryConditionProperty(whru.getFlowNode("Hot Side Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{500.0}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

    @Test
    public void GT_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine Test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);

        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        Combustor comb = gasTurb.createCombustor("Combustor");
        Turbine turb = gasTurb.createTurbine("Turbine");
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");

        // create connections
        gasTurb.createConnection(comp.getFlowNode("Outlet"), comb.getFlowNode("Inlet"));
        gasTurb.createConnection(comb.getFlowNode("Outlet"), turb.getFlowNode("Inlet"));
        gasTurb.createConnection(turb.getFlowNode("Outlet"), sink.getFlowNode("Inlet"));
        gasTurb.createConnection(sink.getFlowNode("Outlet"), comp.getFlowNode("Inlet"));

        // set fluid
        gasTurb.setFluid(comp.getFlowNode("Inlet"), air);

        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comp.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(comp.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comp.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comb.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{1300.0}));
        gasTurb.setBoundaryConditionAttribute(comp, comp.P_RATIO, Arrays.asList(new Double[]{5.0}));
        gasTurb.setBoundaryConditionAttribute(comp, comp.EFFICIENCY, Arrays.asList(new Double[]{0.9}));
        gasTurb.setBoundaryConditionAttribute(comb, comb.P_LOSS, Arrays.asList(new Double[]{0.05}));
        gasTurb.setBoundaryConditionAttribute(turb, turb.EFFICIENCY, Arrays.asList(new Double[]{0.95}));
        gasTurb.setBoundaryConditionAttribute(sink, sink.P_LOSS, Arrays.asList(new Double[]{0.05}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

    @Test
    public void GT_Test_ReheatInterCool() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine with Reheat and Intercooler Test");
        gasTurb.setAmbient(101325, 300);

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
        gasTurb.createConnection(lp_comp.getFlowNode("Outlet"), cool.getFlowNode("Inlet"));
        gasTurb.createConnection(cool.getFlowNode("Outlet"), hp_comp.getFlowNode("Inlet"));
        gasTurb.createConnection(hp_comp.getFlowNode("Outlet"), comb.getFlowNode("Inlet"));
        gasTurb.createConnection(comb.getFlowNode("Outlet"), hp_turb.getFlowNode("Inlet"));
        gasTurb.createConnection(hp_turb.getFlowNode("Outlet"), reheat.getFlowNode("Inlet"));
        gasTurb.createConnection(reheat.getFlowNode("Outlet"), lp_turb.getFlowNode("Inlet"));
        gasTurb.createConnection(lp_turb.getFlowNode("Outlet"), sink.getFlowNode("Inlet"));
        gasTurb.createConnection(sink.getFlowNode("Outlet"), lp_comp.getFlowNode("Inlet"));

        // set fluid
        gasTurb.setFluid(lp_comp.getFlowNode("Inlet"), air);

        //  set initial properties
        gasTurb.setBoundaryConditionAttribute(lp_comp, lp_comp.P_RATIO, Arrays.asList(new Double[]{5.0}));
        gasTurb.setBoundaryConditionAttribute(lp_comp, lp_comp.EFFICIENCY, Arrays.asList(new Double[]{0.9}));
        gasTurb.setBoundaryConditionAttribute(hp_comp, hp_comp.P_RATIO, Arrays.asList(new Double[]{4.0}));
        gasTurb.setBoundaryConditionAttribute(hp_comp, hp_comp.EFFICIENCY, Arrays.asList(new Double[]{0.9}));
        gasTurb.setBoundaryConditionMass(lp_comp.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(lp_comp.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(lp_comp.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(cool.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{300.0}));
        gasTurb.setBoundaryConditionProperty(comb.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{1300.0}));
        gasTurb.setBoundaryConditionProperty(hp_turb.getFlowNode("Outlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble() * 5.0}));
        gasTurb.setBoundaryConditionProperty(reheat.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{1300.0}));
        gasTurb.setBoundaryConditionAttribute(comb, comb.P_LOSS, Arrays.asList(new Double[]{0.05}));
        gasTurb.setBoundaryConditionAttribute(reheat, reheat.P_LOSS, Arrays.asList(new Double[]{0.05}));
        gasTurb.setBoundaryConditionAttribute(lp_turb, lp_turb.EFFICIENCY, Arrays.asList(new Double[]{0.95}));
        gasTurb.setBoundaryConditionAttribute(hp_turb, lp_turb.EFFICIENCY, Arrays.asList(new Double[]{0.95}));
        gasTurb.setBoundaryConditionAttribute(sink, sink.P_LOSS, Arrays.asList(new Double[]{0.05}));
        gasTurb.setBoundaryConditionAttribute(cool, cool.P_LOSS, Arrays.asList(new Double[]{0.05}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }
    
    
    @Test
    public void Real_Gas_GT_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle gasTurb = new Cycle("Gas Turbine Test");
        gasTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = gasTurb.createIdealGas("Air", 1.4, 287.0);
        IdealGas realAir = gasTurb.createRedlichKwongGas("Real Air", 28, 37.36e5 ,132.63);

        // create components
        Compressor comp = gasTurb.createCompressor("Compressor");
        Combustor comb = gasTurb.createCombustor("Combustor");
        Turbine turb = gasTurb.createTurbine("Turbine");
        HeatSink sink = gasTurb.createHeatSink("Heat Sink");

        // create connections
        gasTurb.createConnection(comp.getFlowNode("Outlet"), comb.getFlowNode("Inlet"));
        gasTurb.createConnection(comb.getFlowNode("Outlet"), turb.getFlowNode("Inlet"));
        gasTurb.createConnection(turb.getFlowNode("Outlet"), sink.getFlowNode("Inlet"));
        gasTurb.createConnection(sink.getFlowNode("Outlet"), comp.getFlowNode("Inlet"));

        // set fluid
        gasTurb.setFluid(comp.getFlowNode("Inlet"), air);

        //  set boundary conditions
        gasTurb.setBoundaryConditionMass(comp.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        gasTurb.setBoundaryConditionProperty(comp.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comp.getFlowNode("Inlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{gasTurb.getAmbient(Fluid.TEMPERATURE).getAsDouble()}));
        gasTurb.setBoundaryConditionProperty(comb.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[]{1300.0}));
        gasTurb.setBoundaryConditionAttribute(comp, comp.P_RATIO, Arrays.asList(new Double[]{5.0}));
        gasTurb.setBoundaryConditionAttribute(comp, comp.EFFICIENCY, Arrays.asList(new Double[]{0.9}));
        gasTurb.setBoundaryConditionAttribute(comb, comb.P_LOSS, Arrays.asList(new Double[]{0.05}));
        gasTurb.setBoundaryConditionAttribute(turb, turb.EFFICIENCY, Arrays.asList(new Double[]{0.95}));
        gasTurb.setBoundaryConditionAttribute(sink, sink.P_LOSS, Arrays.asList(new Double[]{0.05}));

        // solve
        boolean solve = gasTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }
    
    @Test
    public void ST_Test() throws InterruptedException, ExecutionException {

        // create cycle
        Cycle steamTurb = new Cycle("Steam Turbine Test");
        steamTurb.setAmbient(101325, 300);

        // create fluids
        IdealGas air = steamTurb.createIdealGas("Air", 1.4, 287.0);
        Steam steam = steamTurb.createSteam();

        // create components
        Compressor pump = steamTurb.createCompressor("Feed pump");
        Combustor boiler = steamTurb.createCombustor("Boiler");
        Turbine turb = steamTurb.createTurbine("Turbine");
        HeatSink cond = steamTurb.createHeatSink("Condenser");

        // create connections
        steamTurb.createConnection(pump.getFlowNode("Outlet"), boiler.getFlowNode("Inlet"));
        steamTurb.createConnection(boiler.getFlowNode("Outlet"), turb.getFlowNode("Inlet"));
        steamTurb.createConnection(turb.getFlowNode("Outlet"), cond.getFlowNode("Inlet"));
        steamTurb.createConnection(cond.getFlowNode("Outlet"), pump.getFlowNode("Inlet"));

        // set fluid
        steamTurb.setFluid(pump.getFlowNode("Inlet"), steam);

        //  set boundary conditions
        steamTurb.setBoundaryConditionMass(pump.getFlowNode("Inlet"), Arrays.asList(new Double[]{1.0}));
        steamTurb.setBoundaryConditionProperty(pump.getFlowNode("Inlet"), Fluid.PRESSURE, Arrays.asList(new Double[]{steamTurb.getAmbient(Fluid.PRESSURE).getAsDouble()}));
        steamTurb.setBoundaryConditionProperty(pump.getFlowNode("Inlet"), Fluid.QUALITY, Arrays.asList(new Double[]{0.0}));
        steamTurb.setBoundaryConditionProperty(boiler.getFlowNode("Outlet"), Fluid.TEMPERATURE, Arrays.asList(new Double[] {400.0}));
        steamTurb.setBoundaryConditionProperty(turb.getFlowNode("Outlet"), Fluid.QUALITY, Arrays.asList(new Double[]{1.0}));

        steamTurb.setBoundaryConditionAttribute(pump, pump.P_RATIO, Arrays.asList(new Double[]{10.0}));
        steamTurb.setBoundaryConditionAttribute(pump, pump.EFFICIENCY, Arrays.asList(new Double[]{0.9}));
        steamTurb.setBoundaryConditionAttribute(boiler, boiler.P_LOSS, Arrays.asList(new Double[]{0.05}));
        steamTurb.setBoundaryConditionAttribute(turb, turb.EFFICIENCY, Arrays.asList(new Double[]{0.95}));
        steamTurb.setBoundaryConditionAttribute(cond, cond.P_LOSS, Arrays.asList(new Double[]{0.05}));

        // solve
        boolean solve = steamTurb.solveParametric();

        // assertions
        assertEquals(solve, true);

    }

}
