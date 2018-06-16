/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Attributes.Attribute.EFFICIENCY;
import static thermocycle.Attributes.Attribute.PRATIO;
import static thermocycle.Properties.Property.*;
import static thermocycle.Node.Port.*;


/**
 *
 * @author Chris
 */
public final class Turbine extends Component {
    
    /**
     * Constructor.
     * @param name The name of the component.
     * @param ambient The ambient state of the component.
     */
    protected Turbine(String name, State ambient){
        super(name, ambient);
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        workNodes.add(new WorkNode(OUTLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Ratio());
        equations.add(new Efficicnecy());
        createAttribute(EFFICIENCY);
        createAttribute(PRATIO);
    }
    
    /**
     * Gets the turbine inlet.
     * @return Returns the inlet flow node.
     */
    public FlowNode getInlet() {
        return flowNodes.get(0);
    }
    
    /**
     * Gets the turbine outlet.
     * @return Returns the outlet flow node.
     */
    public FlowNode getOutlet() {
        return flowNodes.get(1);
    }
    
    /**
     * Gets the turbine shaft.
     * @return Returns the outlet work node.
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
    
    /**
     * Mass balance across turbine.
     */
    private class Mass_Balance extends Equation{
        
        /**
         * Constructor.
         */
        private Mass_Balance() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", Turbine.this.getInlet().getMass());
            variables.put("m out", Turbine.this.getOutlet().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = OptionalDouble.of(Turbine.this.getOutlet().getMass().getAsDouble());
                    break;
                }
                case "m out": {
                    value = OptionalDouble.of(Turbine.this.getInlet().getMass().getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "m in": {
                    Turbine.this.getInlet().setMass(value);
                    return Turbine.this.getInlet();
                }
                case "m out": {
                    Turbine.this.getOutlet().setMass(value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Energy balance across turbine.
     */
    private class Energy_Balance extends Equation{
        
        /**
         * Constructor.
         */
        private Energy_Balance() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Turbine.this.getShaft().getWork());
            variables.put("m", Turbine.this.getInlet().getMass());
            variables.put("h in", Turbine.this.getInlet().getState(ENTHALPY));
            variables.put("h out", Turbine.this.getOutlet().getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "W": {
                    value = OptionalDouble.of(Turbine.this.getInlet().getMass().getAsDouble() * (Turbine.this.getInlet().getState(ENTHALPY).getAsDouble() - Turbine.this.getOutlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "m": {
                    value = OptionalDouble.of(Turbine.this.getShaft().getWork().getAsDouble() / (Turbine.this.getInlet().getState(ENTHALPY).getAsDouble() - Turbine.this.getOutlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of(Turbine.this.getOutlet().getState(ENTHALPY).getAsDouble() + (Turbine.this.getShaft().getWork().getAsDouble() / Turbine.this.getInlet().getMass().getAsDouble()));
                    break;
                }
                case "h out": {
                    value = OptionalDouble.of(Turbine.this.getInlet().getState(ENTHALPY).getAsDouble()  - (Turbine.this.getShaft().getWork().getAsDouble() / Turbine.this.getInlet().getMass().getAsDouble()));
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "W": {
                    Turbine.this.getShaft().setWork(value);
                    return Turbine.this.getShaft();
                }
                case "m": {
                    Turbine.this.getInlet().setMass(value);
                    return Turbine.this.getInlet();
                }
                case "h in": {
                    Turbine.this.getInlet().setState(ENTHALPY,value);
                    return Turbine.this.getInlet();
                }
                case "h out": {
                    Turbine.this.getOutlet().setState(ENTHALPY,value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure ratio across the turbine.
     */
    private class Pressure_Ratio extends Equation{
        
        /**
         * Constructor.
         */
        private Pressure_Ratio() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Turbine.this.getAttribute(PRATIO));
            variables.put("p in", Turbine.this.getInlet().getState(PRESSURE));
            variables.put("p out", Turbine.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "pr": {
                    value = OptionalDouble.of(Turbine.this.getInlet().getState(PRESSURE).getAsDouble() / Turbine.this.getOutlet().getState(PRESSURE).getAsDouble());
                    break;
                }
                case "p in": {
                    value = OptionalDouble.of(Turbine.this.getOutlet().getState(PRESSURE).getAsDouble() * Turbine.this.getAttribute(PRATIO).getAsDouble());
                    break;
                }
                case "p out": {
                    value = OptionalDouble.of(Turbine.this.getInlet().getState(PRESSURE).getAsDouble() / Turbine.this.getAttribute(PRATIO).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "pr": {
                    Turbine.this.setAttribute(PRATIO, value);
                    return null;
                }
                case "p in": {
                    Turbine.this.getInlet().setState(PRESSURE,value);
                    return Turbine.this.getInlet();
                }
                case "p out": {
                    Turbine.this.getOutlet().setState(PRESSURE,value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Efficiency of the turbine.
     */
    private class Efficicnecy extends Equation{
        
        /**
         * Constructor.
         */
        private Efficicnecy() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Turbine.this.getShaft().getWork());
            variables.put("n", Turbine.this.getAttribute(EFFICIENCY));
            variables.put("m", Turbine.this.getInlet().getMass());
            variables.put("h in", Turbine.this.getInlet().getState(ENTHALPY));
            variables.put("s in", Turbine.this.getInlet().getState(ENTROPY));
            variables.put("p out", Turbine.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            FlowNode isen = new FlowNode(INTERNAL);
            isen.setFluid(Turbine.this.getInlet().getFluid());
            if (variable.equals("h in") || variable.equals("W") || variable.equals("n")) {
                isen.setState(ENTROPY, Turbine.this.getInlet().getState(ENTROPY));
                isen.setState(PRESSURE, Turbine.this.getOutlet().getState(PRESSURE));
            }
            else {
                isen.setState(ENTHALPY, OptionalDouble.of(Turbine.this.getInlet().getState(ENTHALPY).getAsDouble() - (Turbine.this.getShaft().getWork().getAsDouble() / (Turbine.this.getAttribute(EFFICIENCY).getAsDouble() * Turbine.this.getInlet().getMass().getAsDouble()))));
            }
            switch (variable) {
                case "W": {
                    value = OptionalDouble.of(Turbine.this.getAttribute(EFFICIENCY).getAsDouble() * Turbine.this.getInlet().getMass().getAsDouble() * (Turbine.this.getInlet().getState(ENTHALPY).getAsDouble() - (isen.getState(ENTHALPY).getAsDouble())));
                    break;
                }
                case "n": {
                    value = OptionalDouble.of(Turbine.this.getShaft().getWork().getAsDouble() / (Turbine.this.getInlet().getMass().getAsDouble() * (Turbine.this.getInlet().getState(ENTHALPY).getAsDouble() - (isen.getState(ENTHALPY).getAsDouble()))));
                    break;
                }
                case "m": {
                    value = OptionalDouble.of(Turbine.this.getShaft().getWork().getAsDouble() / (Turbine.this.getAttribute(EFFICIENCY).getAsDouble() * (Turbine.this.getInlet().getState(ENTHALPY).getAsDouble() - (isen.getState(ENTHALPY).getAsDouble()))));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of(isen.getState(ENTHALPY).getAsDouble() + (Turbine.this.getShaft().getWork().getAsDouble() / (Turbine.this.getAttribute(EFFICIENCY).getAsDouble() * (Turbine.this.getInlet().getMass().getAsDouble()))));
                    break;
                }
                case "s in": {
                    isen.setState(PRESSURE, Turbine.this.getOutlet().getState(PRESSURE));
                    value = OptionalDouble.of(isen.getState(ENTROPY).getAsDouble());
                    break;
                }
                case "p out": {
                    isen.setState(ENTROPY, Turbine.this.getInlet().getState(ENTROPY));
                    value = OptionalDouble.of(isen.getState(PRESSURE).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "W": {
                    Turbine.this.getShaft().setWork(value);
                    return Turbine.this.getShaft();
                }
                case "n": {
                    Turbine.this.setAttribute(EFFICIENCY,value);
                    return null;
                }
                case "m": {
                    Turbine.this.getInlet().setMass(value);
                    return Turbine.this.getInlet();
                }
                case "h in": {
                    Turbine.this.getInlet().setState(ENTHALPY,value);
                    return Turbine.this.getInlet();
                }
                case "s in": {
                    Turbine.this.getInlet().setState(ENTROPY,value);
                    return Turbine.this.getInlet();
                }
                case "p out": {
                    Turbine.this.getOutlet().setState(PRESSURE,value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }  
    }
}