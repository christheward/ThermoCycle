/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import thermocycle.Attributes.Attribute;
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
        flowNodes.put("Inlet",new FlowNode(INLET));
        flowNodes.put("Outlet",new FlowNode(OUTLET));
        workNodes.put("Shaft",new WorkNode(INLET));
        internals.add(new Connection(flowNodes.get("Inlet"),flowNodes.get("Outlet")));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Ratio());
        equations.add(new Efficiency());
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
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), ENTHALPY, ENTROPY));
        return paths;
    }

    @Override
    public Set<Attribute> getAllowableAtributes() {
        Set<Attribute> attributes = new HashSet();
        attributes.add(PRATIO);
        attributes.add(EFFICIENCY);
        return attributes;
   }
    
    /**
     * Mass balance across the compressor.
     */
    private class Mass_Balance extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Mass_Balance() {super("m_in = m_out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m_in", Compressor.this.flowNodes.get("Inlet").getMass());
            variables.put("m_out", Compressor.this.flowNodes.get("Outlet").getMass());
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("m_in").getAsDouble() - variables.get("m_out").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "m_in": {
                    Compressor.this.flowNodes.get("Inlet").setMass(value);
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "m_out": {
                    Compressor.this.flowNodes.get("Outlet").setMass(value);
                    return Compressor.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }  
    }
    
    /**
     * Energy balance across the compressor
     */
    private class Energy_Balance extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Energy_Balance() {super("W = m (h_out - h_in)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Compressor.this.workNodes.get("Shaft").getWork());
            variables.put("m", Compressor.this.flowNodes.get("Inlet").getMass());
            variables.put("h_in", Compressor.this.flowNodes.get("Inlet").getState(ENTHALPY));
            variables.put("h_out", Compressor.this.flowNodes.get("Outlet").getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("W").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h_out").getAsDouble() - variables.get("h_in").getAsDouble());
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "W": {
                    Compressor.this.workNodes.get("Shaft").setWork(value);
                    return Compressor.this.workNodes.get("Shaft");
                }
                case "m": {
                    Compressor.this.flowNodes.get("Inlet").setMass(value);
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "h_in": {
                    Compressor.this.flowNodes.get("Inlet").setProperty(ENTHALPY,value);
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "h_out": {
                    Compressor.this.flowNodes.get("Outlet").setProperty(ENTHALPY,value);
                    return Compressor.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure ratio across compressor.
     */
    private class Pressure_Ratio extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Pressure_Ratio() {super("p_out = p_in * pr", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Compressor.this.getAttribute(PRATIO));
            variables.put("p_in", Compressor.this.flowNodes.get("Inlet").getState(PRESSURE));
            variables.put("p_out", Compressor.this.flowNodes.get("Outlet").getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p_out").getAsDouble() - variables.get("p_in").getAsDouble()*variables.get("pr").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    Compressor.this.setAttribute(PRATIO, value);
                    return null;
                }
                case "p_in": {
                    Compressor.this.flowNodes.get("Inlet").setProperty(PRESSURE,value); 
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "p_out": {
                    Compressor.this.flowNodes.get("Outlet").setProperty(PRESSURE,value);
                    return Compressor.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }
        
    }
    
    /**
     * Efficiency of compressor.
     */
    private class Efficiency extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Efficiency() {super("W = m n (" + func + "(s_in, p out) - h_in)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("W", Compressor.this.workNodes.get("Shaft").getWork());
            variables.put("n", Compressor.this.getAttribute(EFFICIENCY));
            variables.put("m", Compressor.this.flowNodes.get("Inlet").getMass());
            variables.put("h_in", Compressor.this.flowNodes.get("Inlet").getState(ENTHALPY));
            variables.put("s_in", Compressor.this.flowNodes.get("Inlet").getState(ENTROPY));
            variables.put("p_out", Compressor.this.flowNodes.get("Outlet").getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            FlowNode isen = new FlowNode(INTERNAL);
            isen.setFluid(Compressor.this.flowNodes.get("Inlet").getFluid().get());
            isen.setProperty(ENTROPY, variables.get("s_in").getAsDouble());
            isen.setProperty(PRESSURE, variables.get("p_out").getAsDouble());
            return variables.get("W").getAsDouble() - (isen.getState(ENTHALPY).getAsDouble() - variables.get("h_in").getAsDouble())/variables.get("n").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "W": {
                    Compressor.this.workNodes.get("Shaft").setWork(value);
                    return Compressor.this.workNodes.get("Shaft");
                }
                case "n": {
                    Compressor.this.setAttribute(EFFICIENCY,value);
                    return null;
                }
                case "m": {
                    Compressor.this.flowNodes.get("Inlet").setMass(value);
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "h_in": {
                    Compressor.this.flowNodes.get("Inlet").setProperty(ENTHALPY,value);
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "s_in": {
                    Compressor.this.flowNodes.get("Inlet").setProperty(ENTROPY,value);
                    return Compressor.this.flowNodes.get("Inlet");
                }
                case "p_out": {
                    Compressor.this.flowNodes.get("Outlet").setProperty(PRESSURE,value);
                    return Compressor.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }  
    }
}