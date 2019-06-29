/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.OptionalDouble;
import thermocycle.Component;
import thermocycle.Cycle;

/**
 *s
 * @author Chris
 */
public final class TextUserInterface {
    
    /**
     * The model controlled by this TUI.
     */
    private Cycle model;
    
    /**
     * Constructor
     */
    public TextUserInterface(Cycle model) {
        this.model = model;
    }
    
    private void createMenu() {
        /**
        MenuItem root = new MenuItem("Root", null);
        MenuItem file = new MenuItem("File", root);
        MenuItem open = new MenuItem("Open", file);
        MenuItem close = new MenuItem("Close", file);
        MenuItem cycle = new MenuItem("Cycle", root);
        MenuItem solve = new MenuItem("Solve", cycle);
        MenuItem fluid = new MenuItem("Fluid", root);
        MenuItem ideal = new MenuItem("IdealGas", fluid);
        MenuItem steam = new MenuItem("Steam", fluid);
        MenuItem vanderwaals = new MenuItem("VanDerWaals", fluid);
        MenuItem component = new MenuItem("Component", root);
        MenuItem compressor = new MenuItem("Compressor", component);
        MenuItem turbine = new MenuItem("Turbine", component);
        MenuItem combustor = new MenuItem("Combustor", component);
        MenuItem heatsink = new MenuItem("HeatSink", component);
        MenuItem heatexchanger = new MenuItem("HeatExchanger", component);
        MenuItem connect = new MenuItem("Connect", root);
        MenuItem flownodes = new MenuItem("FlowNodes", connect);
        MenuItem worknodes = new MenuItem("WorkNodes", connect);
        MenuItem heatnodes = new MenuItem("HeatNodes", connect);
        MenuItem set = new MenuItem("Set", root);
        MenuItem mass = new MenuItem("Mass", set);
        MenuItem work = new MenuItem("Work", set);
        MenuItem head = new MenuItem("Heat", set);
        MenuItem state = new MenuItem("State", set);
        MenuItem fluid2 = new MenuItem("Fluid", set);
        MenuItem attribute = new MenuItem("Attribute", set);
        MenuItem plot = new MenuItem("Plot", root);
        */
    }
    
    /**
     * Reads a single command
     * @param command The command to read
     */
    public void readCommand(String command) {
        readLine(CSVReaderWriter.readLine(command));
    }
    
    /**
     * Reads a single command
     * @param commands The command line string
     */
    private void readLine(ArrayList<String> commands) {
        try {
            switch(commands.get(0)) {
                case "CYCLE": {
                    cycle(commands);
                    break;
                }
                case "FLUID": {
                    fluid(commands);
                    break;
                }
                case "COMPONENT": {
                    //component(commands);
                    break;
                }
                case "CONNECT": {
                    connect(commands);
                    break;
                }
                case "SET": {
                    set(commands);
                    break;
                }
                case "SOLVE": {
                    solve(commands);
                    break;
                }
                case "PLOT": {
                    break;
                }
                case "": {
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invlaid COMMAND. Must be CYCLE, FLUID, COMPONENT, CONNECT, SET, SOLVE, REPORT, or PLOT.");
                }
            }
        }
        catch (Exception e) {
            System.err.println("Cannot read line: " + commands.toString().replace("[", "").replace("]", ""));
        }
    }
    
    /**
     * Loads a cycle from a saved file.
     * @param filename The file to load. 
     */
    private void loadCycle(String filename) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename))) {
            System.out.println("Loading model from " + filename);
            model = (Cycle)is.readObject();
            System.out.println("Load complete");
        }
        catch(ClassNotFoundException e) {
            System.err.println("Class not found. " + e.getMessage());
        }
        catch(IOException e) {
            System.err.println("I/O error. " + e.getMessage());
        }
    }
    
    /**
     * Saves the cycle to a new file.
     * @param filename The file locations 
     */
    private void saveCycle(String filename) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename))) {
            System.out.println("Saving model to " + filename);
            os.writeObject(model);
            System.out.println("Save complete");
        }
        catch(IOException e) {
            System.err.println("I/O error. " + e.getMessage());
        }
    }
    
    /**
     * Command for creating importing or exporting a cycle
     * @param commands Command line string
     */
    private void cycle(ArrayList<String> commands) {
        try {
            switch(commands.get(1)) {
                case "NEW": {
                    model = new Cycle(commands.get(2));
                    model.setAmbient(Double.parseDouble(commands.get(3)), Double.parseDouble(commands.get(4)));
                    break;
                }
                case "EXPORT": {
                    saveCycle(commands.get(2));
                    break;
                }
                case "IMPORT": {
                    loadCycle(commands.get(2));
                }
                default: {
                }
            }
        }
        catch (Exception e) {
            System.err.println("Uh oh somethings gone wrong.");
        }
    }
    
    /**
     * Command for creating fluids.
     * @param commands Command line string
     */
    private void fluid(ArrayList<String> commands) {
        try {
            switch(commands.get(1)) {
                case "IDEAL": {
                    model.createIdealGas(commands.get(2), Double.parseDouble(commands.get(3)), Double.parseDouble(commands.get(4)));
                }
                default: {
                    throw new IllegalArgumentException("Invlaid FLUID type. Must be IDEAL.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    // COMPONENT Command
    /**
     * Create optional double based on value in string
     * @param string The string to to interpret
     * @return Returns an optional double with a value of that described by the string.
     */
    private OptionalDouble valueOf(String string) {
        return OptionalDouble.of(Double.parseDouble(string));
    }
    
    /**
     * Commands for creating a component
     * @param commands Command line string
     */
    /**
    private void component(ArrayList<String> commands) {
        try {
            switch(commands.get(1)) {
                case "COMPRESSOR": {
                    Component comp = model.createCompressor(commands.get(2));
                    model.setBoundaryConditionAttribute(comp, EFFICIENCY, Arrays.asList(a)new Double[] {});
                    model.setBoundaryConditionAttribute(comp, EFFICIENCY, new Double[] {Double.parseDouble(commands.get(3))});
                    model.setBoundaryConditionAttribute(comp, PRATIO, new Double[] {Double.parseDouble(commands.get(4))});
                    break;
                }
                case "TURBINE": {
                    Component comp = model.createTurbine(commands.get(2));
                    model.setBoundaryConditionAttribute(comp, EFFICIENCY, new double[] {Double.parseDouble(commands.get(3))});
                    model.setBoundaryConditionAttribute(comp, PRATIO, new double[] {Double.parseDouble(commands.get(4))});
                    break;
                }
                case "COMBUSTOR": {
                    Component comp = model.createCombustor(commands.get(2));
                    model.setBoundaryConditionAttribute(comp, PRATIO, new double[] {Double.parseDouble(commands.get(3))});
                    break;
                }
                case "HEAT_SINK": {
                    Component comp = model.createHeatSink(commands.get(2));
                    model.setBoundaryConditionAttribute(comp, PRATIO, new double[] {Double.parseDouble(commands.get(3))});
                    break;
                }
                case "HEAT_EXCHANGER": {
                    Component comp = model.createHeatExchanger(commands.get(2));
                    model.setBoundaryConditionAttribute(comp, EFFECTIVENESS, new double[] {Double.parseDouble(commands.get(3))});
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invlaid COMPONENT type. Allowable types are: COMPRESSOR, TURBINE, COMBUSTOR, HEAT_SINK, HEAT_EXCHANGER.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    /**
     * Command for connecting nodes
     * @param commands Command line string
     */
    private void connect(ArrayList<String> commands) {
        try {
            Component c1 = model.getComponent(commands.get(2));
            Component c2 = model.getComponent(commands.get(4));
            switch(commands.get(1)) {
                case "WORK": {
                    model.createConnection(c1.workNodes.get(Integer.parseInt(commands.get(3))), c2.workNodes.get(Integer.parseInt(commands.get(5))));
                    break;
                }
                case "HEAT": {
                    model.createConnection(c1.heatNodes.get(Integer.parseInt(commands.get(3))), c2.heatNodes.get(Integer.parseInt(commands.get(5))));
                    break;
                }
                case "FLOW": {
                    model.createConnection(c1.flowNodes.get(Integer.parseInt(commands.get(3))), c2.flowNodes.get(Integer.parseInt(commands.get(5))));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invlaid CONNECTION type, must be WORK, HEAT, or FLOW.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    /**
     * Command for setting values prior to solving
     * @param commands Command line string.
     */
    private void set(ArrayList<String> commands) {
        try {
            switch (commands.get(1)) {
                case "WORK": {
                    //model.setBoundaryConditionWork(model.getComponent(commands.get(2)).workNodes.get(Integer.parseInt(commands.get(3))), new double[] {Double.parseDouble(commands.get(4))});
                    break;
                }
                case "HEAT": {
                    //model.setBoundaryConditionHeat(model.getComponent(commands.get(2)).heatNodes.get(Integer.parseInt(commands.get(3))), new double[] {Double.parseDouble(commands.get(4))});
                    break;
                }
                case "MASS": {
                    //model.setBoundaryConditionMass(model.getComponent(commands.get(2)).flowNodes.get(Integer.parseInt(commands.get(3))), new double[] {Double.parseDouble(commands.get(4))});
                    break;
                }
                case "STATE": {
                    //model.setBoundaryConditionProperty(model.getComponent(commands.get(2)).flowNodes.get(Integer.parseInt(commands.get(3))), Property.valueOf(commands.get(4)), new double[] {Double.parseDouble(commands.get(5))});
                    break;
                }
                case "FLUID": {
                    //model.setFluid(model.getComponent(commands.get(2)).flowNodes.get(Integer.parseInt(commands.get(3))), model.getFluid(commands.get(4)).get());
                }
                default: {
                    throw new IllegalArgumentException("Invlaid SET type. Must be WORK, HEAT, MASS, STATE, or FLUID.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    /**
     * Command for solving cycle
     * @param commands Command line string
     */
    private void solve(ArrayList<String> commands) {
        model.solveParametric();
    }
    
    private void readData() {
        
    }
}
