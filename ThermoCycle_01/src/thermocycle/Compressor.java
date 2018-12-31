/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Properties.Property.*;
import static thermocycle.Node.Port.*;


/**
 *
 * @author Chris
 */
public final class Compressor extends Component {
    
    /**
     * Constructor
     * @param name The component name.
     * @param ambient The component ambient state.
     */
    protected Compressor(String name, State ambient){
        super(name, ambient);
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        workNodes.add(new WorkNode(INLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Ratio());
        equations.add(new Efficiency());
        attributes.add(PRATIO);
        attributes.add(EFFICIENCY);
    }
    
    /**
     * Gets the compressor inlet.
     * @return Returns the inlet flow node.
     */
    public FlowNode getInlet() {
        return flowNodes.get(0);
    }
    /**
     * Gets the compressor outlet.
     * @return Returns the outlet flow node.
     */
    public FlowNode getOutlet() {
        return flowNodes.get(1);
    }
    /**
     * Gets the compressor shaft.
     * @return Returns the inlet work node.
     */
    public WorkNode getShaft() {
        return workNodes.get(0);
    }
    
    @Override
    protected double heatExergyIn() {
        return 0;
    }
    
    @Override
    protected double heatExergyOut() {
        return 0;
    }    
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(getInlet(), getOutlet(), ENTHALPY, ENTROPY));
        return paths;
    }
    
    /**
     * Mass balance across the compressor.
     */
    private class Mass_Balance extends Equation{
        
        /**
         * Constructor.
         */
        private Mass_Balance() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", Compressor.this.getInlet().getMass());
            variables.put("m out", Compressor.this.getOutlet().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
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
        protected Node saveVariable(String variable, Double value) {
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
         * Constructor.
         */
        private Energy_Balance() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Compressor.this.getShaft().getWork());
            variables.put("m", Compressor.this.getInlet().getMass());
            variables.put("h in", Compressor.this.getInlet().getState(ENTHALPY));
            variables.put("h out", Compressor.this.getOutlet().getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
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
        protected Node saveVariable(String variable, Double value) {
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
                    Compressor.this.getInlet().setProperty(ENTHALPY,value);
                    return Compressor.this.getInlet();
                }
                case "h out": {
                    Compressor.this.getOutlet().setProperty(ENTHALPY,value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure ratio across compressor.
     */
    private class Pressure_Ratio extends Equation{
        
        /**
         * Constructor.
         */
        private Pressure_Ratio() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Compressor.this.getAttribute(PRATIO));
            variables.put("p in", Compressor.this.getInlet().getState(PRESSURE));
            variables.put("p out", Compressor.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
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
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    Compressor.this.setAttribute(PRATIO, value);
                    return null;
                }
                case "p in": {
                    Compressor.this.getInlet().setProperty(PRESSURE,value); 
                    return Compressor.this.getInlet();
                }
                case "p out": {
                    Compressor.this.getOutlet().setProperty(PRESSURE,value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Efficiency of compressor.
     */
    private class Efficiency extends Equation{
        
        /**
         * Constructor.
         */
        private Efficiency() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
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
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            FlowNode isen = new FlowNode(INTERNAL);
            isen.setFluid(Compressor.this.getInlet().getFluid());
            if (variable.equals("h in") | variable.equals("n") | variable.equals("W")) {
                isen.setProperty(ENTROPY, Compressor.this.getInlet().getState(ENTROPY).getAsDouble());
                isen.setProperty(PRESSURE, Compressor.this.getOutlet().getState(PRESSURE).getAsDouble());
            }
            else {
                isen.setProperty(ENTHALPY, Compressor.this.getInlet().getState(ENTHALPY).getAsDouble() + (Compressor.this.getShaft().getWork().getAsDouble() * Compressor.this.getAttribute(EFFICIENCY).getAsDouble() / (Compressor.this.getInlet().getMass().getAsDouble())));
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
                    isen.setProperty(PRESSURE, Compressor.this.getOutlet().getState(PRESSURE).getAsDouble());
                    value = isen.getState(ENTROPY);
                    break;
                }
                case "p out": {
                    isen.setProperty(ENTROPY, Compressor.this.getInlet().getState(ENTROPY).getAsDouble());
                    value = isen.getState(PRESSURE);
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
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
                    Compressor.this.getInlet().setProperty(ENTHALPY,value);
                    return Compressor.this.getInlet();
                }
                case "s in": {
                    Compressor.this.getInlet().setProperty(ENTROPY,value);
                    return Compressor.this.getInlet();
                }
                case "p out": {
                    Compressor.this.getOutlet().setProperty(PRESSURE,value);
                    return Compressor.this.getOutlet();
                }
            }
            return null;
        }  
    }
}