/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import org.jfree.data.xy.DefaultXYDataset;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Properties.Property.*;
import static thermocycle.Ports.Let.*;


/**
 *
 * @author Chris
 */
public final class Compressor extends Component {
    
    // constructor
    Compressor(String name, State ambient){
        super(name, ambient);
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        workNodes.add(new WorkNode(INLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Ratio());
        equations.add(new Efficiency());
        createAttribute(PRATIO);
        createAttribute(EFFICIENCY);
    }
    
    // getters
    FlowNode getInlet() {return flowNodes.get(0);}
    FlowNode getOutlet() {return flowNodes.get(1);}
    WorkNode getShaft() {return workNodes.get(0);}
    
    // methods
    @Override
    double heatExergyIn() {
        return 0;
    }
    
    @Override
    double heatExergyOut() {
        return 0;
    }
    
    @Override
    int plotData(DefaultXYDataset dataset, Property x, Property y) {
        List<FlowNode> process = thermodynamicProcess(getInlet(),getOutlet(),ENTHALPY,ENTROPY);
        double data[][] = new double[2][Component.nIntStates];
        for (int i=0; i<Component.nIntStates; i++) {
            data[0][i] = process.get(i).getState(x).getAsDouble();
            data[1][i] = process.get(i).getState(x).getAsDouble();
        }
        //dataset.addSeries(getName(), data);
        return 1;
    }
    
    
    /**
     * Mass balance across the compressor
     */
    private class Mass_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Mass_Balance() {}
        
        @Override
        Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", Compressor.this.getInlet().getMass());
            variables.put("m out", Compressor.this.getOutlet().getMass());
            return variables;
        }
        
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = OptionalDouble.of(Compressor.this.getOutlet().getMass().getAsDouble());
                    break;
                }
                case "m out": {
                    value = OptionalDouble.of(Compressor.this.getInlet().getMass().getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "m in": {
                    Compressor.this.getInlet().setMass(value);
                    return Compressor.this.getInlet();
                }
                case "m out": {
                    Compressor.this.getOutlet().setMass(value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Energy balance across the compressor
     */
    private class Energy_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Energy_Balance() {}
        
        @Override
        Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Compressor.this.getShaft().getWork());
            variables.put("m", Compressor.this.getInlet().getMass());
            variables.put("h in", Compressor.this.getInlet().getState(ENTHALPY));
            variables.put("h out", Compressor.this.getOutlet().getState(ENTHALPY));
            return variables;
        }
        
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "W": {
                    value = OptionalDouble.of(Compressor.this.getInlet().getMass().getAsDouble() * (Compressor.this.getOutlet().getState(ENTHALPY).getAsDouble() - Compressor.this.getInlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "m": {
                    value = OptionalDouble.of(Compressor.this.getShaft().getWork().getAsDouble() / (Compressor.this.getOutlet().getState(ENTHALPY).getAsDouble() - Compressor.this.getInlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of(Compressor.this.getOutlet().getState(ENTHALPY).getAsDouble() - (Compressor.this.getShaft().getWork().getAsDouble() / (Compressor.this.getInlet().getMass().getAsDouble())));
                    break;
                }
                case "h out": {
                    value = OptionalDouble.of(Compressor.this.getInlet().getState(ENTHALPY).getAsDouble() + (Compressor.this.getShaft().getWork().getAsDouble() / Compressor.this.getInlet().getMass().getAsDouble()));
                    break;
                }
            }
            return value;
        }
        
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "W": {
                    Compressor.this.getShaft().setWork(value);
                    return Compressor.this.getShaft();
                }
                case "m": {
                    Compressor.this.getInlet().setMass(value);
                    return Compressor.this.getInlet();
                }
                case "h in": {
                    Compressor.this.getInlet().setState(ENTHALPY,value);
                    return Compressor.this.getInlet();
                }
                case "h out": {
                    Compressor.this.getOutlet().setState(ENTHALPY,value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure ratio across compressor
     */
    private class Pressure_Ratio extends Equation{
        
        /**
         * Constructor
         */
        private Pressure_Ratio() {}
        
        @Override
        Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Compressor.this.getAttribute(PRATIO));
            variables.put("p in", Compressor.this.getInlet().getState(PRESSURE));
            variables.put("p out", Compressor.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "pr": {
                    value = OptionalDouble.of(Compressor.this.getOutlet().getState(PRESSURE).getAsDouble() / Compressor.this.getInlet().getState(PRESSURE).getAsDouble());
                    break;
                }
                case "p in": {
                    value = OptionalDouble.of(Compressor.this.getOutlet().getState(PRESSURE).getAsDouble() / (Compressor.this.getAttribute(PRATIO).getAsDouble()));
                    break;
                }
                case "p out": {
                    value = OptionalDouble.of(Compressor.this.getInlet().getState(PRESSURE).getAsDouble() * Compressor.this.getAttribute(PRATIO).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "pr": {
                    Compressor.this.setAttribute(PRATIO, value);
                    return null;
                }
                case "p in": {
                    Compressor.this.getInlet().setState(PRESSURE,value); 
                    return Compressor.this.getInlet();
                }
                case "p out": {
                    Compressor.this.getOutlet().setState(PRESSURE,value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Efficiency of compressor
     */
    private class Efficiency extends Equation{
        
        /**
         * Constructor
         */
        private Efficiency() {}
        
        @Override
        Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Compressor.this.getShaft().getWork());
            variables.put("n", Compressor.this.getAttribute(EFFICIENCY));
            variables.put("m", Compressor.this.getInlet().getMass());
            variables.put("h in", Compressor.this.getInlet().getState(ENTHALPY));
            variables.put("s in", Compressor.this.getInlet().getState(ENTROPY));
            variables.put("p out", Compressor.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            FlowNode isen = new FlowNode(INTERNAL);
            isen.setFluid(Compressor.this.getInlet().getFluid());
            if (variable.equals("h in") | variable.equals("n") | variable.equals("W")) {
                isen.setState(ENTROPY, Compressor.this.getInlet().getState(ENTROPY));
                isen.setState(PRESSURE, Compressor.this.getOutlet().getState(PRESSURE));
            }
            else {
                isen.setState(ENTHALPY, OptionalDouble.of(Compressor.this.getInlet().getState(ENTHALPY).getAsDouble() + (Compressor.this.getShaft().getWork().getAsDouble() * Compressor.this.getAttribute(EFFICIENCY).getAsDouble() / (Compressor.this.getInlet().getMass().getAsDouble()))));
            }
            switch (variable) {
                case "W": {
                    value = OptionalDouble.of(Compressor.this.getInlet().getMass().getAsDouble() * (isen.getState(ENTHALPY).getAsDouble() - Compressor.this.getInlet().getState(ENTHALPY).getAsDouble()) / Compressor.this.getAttribute(EFFICIENCY).getAsDouble());
                    break;
                }
                case "n": {
                    value = OptionalDouble.of(Compressor.this.getInlet().getMass().getAsDouble() * (isen.getState(ENTHALPY).getAsDouble() - Compressor.this.getInlet().getState(ENTHALPY).getAsDouble()) / (Compressor.this.getShaft().getWork().getAsDouble()));
                    break;
                }
                case "m": {
                    value = OptionalDouble.of(Compressor.this.getShaft().getWork().getAsDouble() * Compressor.this.getAttribute(EFFICIENCY).getAsDouble() / ((isen.getState(ENTHALPY).getAsDouble() - Compressor.this.getInlet().getState(ENTHALPY).getAsDouble())));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of(isen.getState(ENTHALPY).getAsDouble() - (Compressor.this.getShaft().getWork().getAsDouble() * Compressor.this.getAttribute(EFFICIENCY).getAsDouble() / Compressor.this.getInlet().getMass().getAsDouble()));
                    break;
                }
                case "s in": {
                    isen.setState(PRESSURE, Compressor.this.getOutlet().getState(PRESSURE));
                    value = isen.getState(ENTROPY);
                    break;
                }
                case "p out": {
                    isen.setState(ENTROPY, Compressor.this.getInlet().getState(ENTROPY));
                    value = isen.getState(PRESSURE);
                    break;
                }
            }
            return value;
        }
        
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "W": {
                    Compressor.this.getShaft().setWork(value);
                    return Compressor.this.getShaft();
                }
                case "n": {
                    Compressor.this.setAttribute(EFFICIENCY,value);
                    return null;
                }
                case "m": {
                    Compressor.this.getInlet().setMass(value);
                    return Compressor.this.getInlet();
                }
                case "h in": {
                    Compressor.this.getInlet().setState(ENTHALPY,value);
                    return Compressor.this.getInlet();
                }
                case "s in": {
                    Compressor.this.getInlet().setState(ENTROPY,value);
                    return Compressor.this.getInlet();
                }
                case "p out": {
                    Compressor.this.getOutlet().setState(PRESSURE,value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }  
    }
}