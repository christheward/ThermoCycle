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
public final class HeatSink extends Component {
    
    /**
     * Constructor
     * @param name The component name.
     * @param ambient The component ambient state.
     */
    protected HeatSink(String name, State ambient) {
        super(name, ambient);
        flowNodes.put("Inlet",new FlowNode(INLET));
        flowNodes.put("Outlet",new FlowNode(OUTLET));
        heatNodes.put("Sink",new HeatNode(OUTLET));
        internals.add(new Connection(flowNodes.get("Inlet"),flowNodes.get("Outlet")));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Loss());
    }
    
    @Override
    protected double heatExergyIn() {
        return 0;
    }
    
    @Override
    protected double heatExergyOut() {
        return heatTransferProcessExergy(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), ENTHALPY, PRESSURE));
    }
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), ENTHALPY, ENTROPY));
        return paths;
    }
    
    @Override
    public Set<Attributes.Attribute> getAllowableAtributes() {
        Set<Attributes.Attribute> attributes = new HashSet();
        attributes.add(PLOSS);
        return attributes;
   }
    
    /**
     * Mass balance across Heat Sink 
     */
    private class Mass_Balance extends ComponentEquation{
        
        /**
         * Constructor
         */
        private Mass_Balance() {super("m_in = m_out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m_in", HeatSink.this.flowNodes.get("Inlet").getMass());
            variables.put("m_out", HeatSink.this.flowNodes.get("Outlet").getMass());
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
                    HeatSink.this.flowNodes.get("Inlet").setMass(value);
                    return HeatSink.this.flowNodes.get("Inlet");
                }
                case "m_out": {
                    HeatSink.this.flowNodes.get("Outlet").setMass(value);
                    return HeatSink.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }  
    }
    
    /**
     * Heat  Sink energy balance
     */
    private class Energy_Balance extends ComponentEquation{
        
        /**
         * Constructor
         */
        private Energy_Balance() {super("Q = m (h_in - h_out)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q", HeatSink.this.heatNodes.get("Sink").getHeat());
            variables.put("m", HeatSink.this.flowNodes.get("Inlet").getMass());
            variables.put("h_in", HeatSink.this.flowNodes.get("Inlet").getState(ENTHALPY));
            variables.put("h_out", HeatSink.this.flowNodes.get("Outlet").getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h_in").getAsDouble() - variables.get("h_out").getAsDouble());
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "Q": {
                    HeatSink.this.heatNodes.get("Sink").setHeat(value);
                    return HeatSink.this.heatNodes.get("Sink");
                }
                case "m": {
                    HeatSink.this.flowNodes.get("Inlet").setMass(value);
                    return HeatSink.this.flowNodes.get("Inlet");
                }
                case "h_in": {
                    HeatSink.this.flowNodes.get("Inlet").setProperty(ENTHALPY,value);
                    return HeatSink.this.flowNodes.get("Inlet");
                }
                case "h_out": {
                    HeatSink.this.flowNodes.get("Outlet").setProperty(ENTHALPY,value);
                    return HeatSink.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure loss across heat sink
     */
    private class Pressure_Loss extends ComponentEquation{
        
        /**
         * Constructor
         */
        private Pressure_Loss() {super("p_in = (1 - pr) p_out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", HeatSink.this.getAttribute(PLOSS));
            variables.put("p_in", HeatSink.this.flowNodes.get("Inlet").getState(PRESSURE));
            variables.put("p_out", HeatSink.this.flowNodes.get("Outlet").getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p_in").getAsDouble()*(1 - variables.get("pr").getAsDouble()) - variables.get("p_out").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    HeatSink.this.setAttribute(PLOSS, value);
                    return null;
                }
                case "p_in": {
                    HeatSink.this.flowNodes.get("Inlet").setProperty(PRESSURE,value);
                    return HeatSink.this.flowNodes.get("Inlet");
                }
                case "p_out": {
                    HeatSink.this.flowNodes.get("Outlet").setProperty(PRESSURE,value);
                    return HeatSink.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }
    }
}
