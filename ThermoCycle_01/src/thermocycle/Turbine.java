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
        equations.add(new Efficiency());
    }
    
    /**
     * Gets the turbine inlet.
     * @return The inlet flow node.
     */
    public FlowNode getInlet() {
        return flowNodes.get(0);
    }
    
    /**
     * Gets the turbine outlet.
     * @return The outlet flow node.
     */
    public FlowNode getOutlet() {
        return flowNodes.get(1);
    }
    
    /**
     * Gets the turbine shaft.
     * @return The outlet work node.
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
    
    @Override
    public Set<Attributes.Attribute> getAllowableAtributes() {
        Set<Attributes.Attribute> attributes = new HashSet();
        attributes.add(PRATIO);
        attributes.add(EFFICIENCY);
        return attributes;
   }
    
    /**
     * Mass balance across turbine.
     */
    private class Mass_Balance extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Mass_Balance() {super("m_in = m_out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", Turbine.this.getInlet().getMass());
            variables.put("m out", Turbine.this.getOutlet().getMass());
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("m in").getAsDouble() - variables.get("m out").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
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
    private class Energy_Balance extends ComponentEquation{
                
        /**
         * Constructor.
         */
        private Energy_Balance() {super("W = m (h_in - h_out)", 1e-3);}
        
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
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("W").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h in").getAsDouble() - variables.get("h out").getAsDouble());
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
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
                    Turbine.this.getInlet().setProperty(ENTHALPY,value);
                    return Turbine.this.getInlet();
                }
                case "h out": {
                    Turbine.this.getOutlet().setProperty(ENTHALPY,value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure ratio across the turbine.
     */
    private class Pressure_Ratio extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Pressure_Ratio() {super("pr = p_in / p_out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Turbine.this.getAttribute(PRATIO));
            variables.put("p in", Turbine.this.getInlet().getState(PRESSURE));
            variables.put("p out", Turbine.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p in").getAsDouble() - variables.get("p out").getAsDouble()*variables.get("pr").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    Turbine.this.setAttribute(PRATIO, value);
                    return null;
                }
                case "p in": {
                    Turbine.this.getInlet().setProperty(PRESSURE,value);
                    return Turbine.this.getInlet();
                }
                case "p out": {
                    Turbine.this.getOutlet().setProperty(PRESSURE,value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Efficiency of the turbine.
     */
    private class Efficiency extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Efficiency() {super("W = m eta (h_in - h_out,isen), h_out,isen = f(s_in, p_out)", 1e-3);}
        
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
        protected Double function(Map<String, OptionalDouble> variables) {
            
            FlowNode isen = new FlowNode(INTERNAL);
            isen.setFluid(Turbine.this.getInlet().getFluid().get());
            isen.setProperty(ENTROPY, variables.get("s in").getAsDouble());
            isen.setProperty(PRESSURE, variables.get("p out").getAsDouble());
            
            return variables.get("W").getAsDouble() - (variables.get("h in").getAsDouble() - isen.getState(ENTHALPY).getAsDouble())*variables.get("n").getAsDouble();
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
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
                    Turbine.this.getInlet().setProperty(ENTHALPY,value);
                    return Turbine.this.getInlet();
                }
                case "s in": {
                    Turbine.this.getInlet().setProperty(ENTROPY,value);
                    return Turbine.this.getInlet();
                }
                case "p out": {
                    Turbine.this.getOutlet().setProperty(PRESSURE,value);
                    return Turbine.this.getOutlet();
                }
            }
            return null;
        }  
    }
}