/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Properties.Property.*;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Node.Port.*;

/**
 *
 * @author Chris
 */
public final class Combustor extends Component {
    
    /**
     * Constructor.
     * @param name Combustor name.
     * @param ambient Ambient conditions.
     */
    protected Combustor(String name, State ambient) {
        super(name, ambient);
        flowNodes.put("Inlet",new FlowNode(INLET));
        flowNodes.put("Outlet",new FlowNode(OUTLET));
        heatNodes.put("Supply",new HeatNode(INLET));
        internals.add(new Connection(flowNodes.get("Inlet"),flowNodes.get("Outlet")));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Loss());
    }
    
    @Override
    protected double heatExergyIn() {
        return heatTransferProcessExergy(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), ENTHALPY, PRESSURE));
    }
    
    @Override
    protected double heatExergyOut() {
        return 0;
    }
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), ENTHALPY, PRESSURE));
        return paths;
    }
    
    @Override
    public Set<Attributes.Attribute> getAllowableAtributes() {
        Set<Attributes.Attribute> attributes = new HashSet();
        attributes.add(PLOSS);
        return attributes;
   }
    
    /**
     * Mass balance across the combustor.
     */
    private class Mass_Balance extends ComponentEquation{
                
        /**
         * Constructor.
         */
        private Mass_Balance() {super("m in = m out", 1e-3);}
        
        @Override
        protected Map<String,OptionalDouble> getVariables() {
            Map<String,OptionalDouble> variables = new HashMap();
            variables.put("m in", Combustor.this.flowNodes.get("Inlet").getMass());
            variables.put("m out", Combustor.this.flowNodes.get("Outlet").getMass());
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
                    Combustor.this.flowNodes.get("Inlet").setMass(value);
                    return Combustor.this.flowNodes.get("Inlet");
                }
                case "m out": {
                    Combustor.this.flowNodes.get("Outlet").setMass(value);
                    return Combustor.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }
    }
    
    /**
     * Energy balance across the combustor.
     */
    private class Energy_Balance extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Energy_Balance() {super("Q = m * (h out - h in)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q", Combustor.this.heatNodes.get("Supply").getHeat());
            variables.put("m", Combustor.this.flowNodes.get("Inlet").getMass());
            variables.put("h in", Combustor.this.flowNodes.get("Inlet").getState(ENTHALPY));
            variables.put("h out", Combustor.this.flowNodes.get("Outlet").getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h out").getAsDouble() - variables.get("h in").getAsDouble());
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "Q": {
                    Combustor.this.heatNodes.get("Supply").setHeat(value);
                    return Combustor.this.heatNodes.get("Supply");}
                case "m": {
                    Combustor.this.flowNodes.get("Inlet").setMass(value);
                    return Combustor.this.flowNodes.get("Inlet");}
                case "h in": {
                    Combustor.this.flowNodes.get("Inlet").setProperty(ENTHALPY,value);
                    return Combustor.this.flowNodes.get("Inlet");}
                case "h out": {
                    Combustor.this.flowNodes.get("Outlet").setProperty(ENTHALPY,value);
                    return Combustor.this.flowNodes.get("Outlet");}
            }
            return null;
        }
    }
    
    /**
     * Pressure loss across the combustor.
     */
    private class Pressure_Loss extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Pressure_Loss() {super("p out = p in * (1 - pr)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Combustor.this.getAttribute(PLOSS));
            variables.put("p in", Combustor.this.flowNodes.get("Inlet").getState(PRESSURE));
            variables.put("p out", Combustor.this.flowNodes.get("Outlet").getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p in").getAsDouble()*(1-variables.get("pr").getAsDouble()) - variables.get("p out").getAsDouble();
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    Combustor.this.setAttribute(PLOSS, value);
                    return null;
                }
                case "p in": {
                    Combustor.this.flowNodes.get("Inlet").setProperty(PRESSURE,value);
                    return Combustor.this.flowNodes.get("Inlet");
                }
                case "p out": {
                    Combustor.this.flowNodes.get("Outlet").setProperty(PRESSURE,value);
                    return Combustor.this.flowNodes.get("Outlet");
                }
            }
            return null;
        }
    }
}
