/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.OptionalDouble;
import static thermocycle.Properties.Property.PRESSURE;
import static thermocycle.Properties.Property.TEMPERATURE;
import static thermocycle.Attributes.Attribute.*;

/**
 *
 * @author Chris
 */
public class TextUserInterface {

    private Cycle model;
    private ArrayList<ArrayList<String>> outputs;
    
    // I/O functions
    public void load(String filename) {
        CSVReaderWriter.read(filename, ",").forEach(commands -> {
            readLine(commands);
        });
    }
    public void save(String filename) {
        CSVReaderWriter.write(filename, ",",outputs);
    }
    public void loadState(String filename) {
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
    public void saveState(String filename) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename))) {
            System.out.println("Saving model to " + filename);
            os.writeObject(model);
            System.out.println("Save complete");
        }
        catch(IOException e) {
            System.err.println("I/O error. " + e.getMessage());
        }
    }    
    // Write TUI line
    void writeLine() {
        // Write CASE
        ArrayList<String> commands = new ArrayList();
        commands.add("CYCLE");
        commands.add(model.getName());
        commands.add(model.getAmbient(PRESSURE).toString());
        commands.add(model.getAmbient(TEMPERATURE).toString());
        outputs.add(commands);
        // Write FLUID
        model.getFluids().forEach(fluid -> {
            commands.add("FLLUID");
            commands.add(fluid.getClass().toString());
        });
        commands.clear();
    }
    
    // Read TUI line
    void readLine(ArrayList<String> commands) {
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
                    component(commands);
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
                case "REPORT": {
                    report(commands);
                    break;
                }
                case "PLOT": {
                    plot(commands);
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
    
    // CYCLE command
    private void cycle(ArrayList<String> commands) {
        try {
            switch(commands.get(1)) {
                case "NEW": {
                    model = new Cycle(commands.get(2));
                    model.setAmbient(Double.parseDouble(commands.get(3)), Double.parseDouble(commands.get(4)));
                    break;
                }
                case "EXPORT": {
                    saveState(commands.get(2));
                    break;
                }
                case "IMPORT": {
                    loadState(commands.get(2));
                }
                default: {
                }
            }
        }
        catch (Exception e) {
            System.err.println("Uh oh somethings gone wrong.");
        }
    }
    
    // FLUID Command
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
        return OptionalDouble.of(Double.valueOf(string));
    }
    
    private void component(ArrayList<String> commands) {
        try {
            switch(commands.get(1)) {
                case "COMPRESSOR": {
                    model.createCompressor(commands.get(2));
                    ((Compressor)model.getComponent(Component.idCount())).setAttribute(EFFICIENCY, valueOf(commands.get(3)));
                    ((Compressor)model.getComponent(Component.idCount())).setAttribute(PRATIO, valueOf(commands.get(4)));
                    break;
                }
                case "TURBINE": {
                    model.createTurbine(commands.get(2));
                    ((Turbine)model.getComponent(Component.idCount())).setAttribute(EFFICIENCY, valueOf(commands.get(3)));
                    ((Turbine)model.getComponent(Component.idCount())).setAttribute(PRATIO, valueOf(commands.get(4)));
                    break;
                }
                case "COMBUSTOR": {
                    model.createCombustor(commands.get(2));
                    ((Combustor)model.getComponent(Component.idCount())).setAttribute(PRATIO, valueOf(commands.get(3)));
                    break;
                }
                case "HEAT_SINK": {
                    model.createHeatSink(commands.get(2));
                    ((HeatSink)model.getComponent(Component.idCount())).setAttribute(PRATIO, valueOf(commands.get(3)));
                    break;
                }
                case "HEAT_EXCHANGER": {
                    model.createHeatExchanger(commands.get(2));
                    ((HeatExchanger)model.getComponent(Component.idCount())).setAttribute(EFFECTIVENESS, valueOf(commands.get(3)));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invlaid COMPONENT type. Allowable types are: COMPRESSORE, TURBINE, COMBUSTOR, HEAT_SINK, HEAT_EXCHANGER.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    // CONNECT Command
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
    
    // SET Command
    private void set(ArrayList<String> commands) {
        try {
            switch (commands.get(1)) {
                case "WORK": {
                    model.getComponent(commands.get(2)).workNodes.get(Integer.valueOf(commands.get(3))).setWork(valueOf(commands.get(4)));
                    break;
                }
                case "HEAT": {
                    model.getComponent(commands.get(2)).heatNodes.get(Integer.valueOf(commands.get(3))).setHeat(valueOf(commands.get(4)));
                    break;
                }
                case "MASS": {
                    model.getComponent(commands.get(2)).flowNodes.get(Integer.valueOf(commands.get(3))).setMass(valueOf(commands.get(4)));
                    break;
                }
                case "STATE": {
                    model.getComponent(commands.get(2)).flowNodes.get(Integer.valueOf(commands.get(3))).setState(Properties.Property.valueOf(commands.get(4)), valueOf(commands.get(5)));
                    break;
                }
                case "FLUID": {
                    model.setFluid(model.getComponent(commands.get(2)).flowNodes.get(Integer.valueOf(commands.get(3))), model.getFluid(commands.get(4)));
                }
                default: {
                    throw new IllegalArgumentException("Invlaid SET type. Must be WORK, HEAT, MASS, STATE, or FLUID.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    // SOLVE Command
    private void solve(ArrayList<String> commands) {
        model.solve();
    }
    
    // REPORT Command
    private void report(ArrayList<String> commands) {
        try {
            switch (commands.get(1)) {
                case "SETUP": {
                    model.reportSetup();
                    break;
                }
                case "RESULTS": {
                    model.reportResults();
                    break;
                }
                case "EXERGY": {
                    model.reportExergyAnalysis();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invlaid REPORT type. Must be SETUP, RESULTS, or EXERGY.");
                }
            }
        }
        catch (Exception e) {
        }
    }
    
    // PLOT Command
    private void plot(ArrayList<String> commands) {
        try {
            model.plot(Properties.Property.valueOf(commands.get(1)), Properties.Property.valueOf(commands.get(2)));
        }
        catch (Exception e) {
        }
    }
    
}
