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
public final class HeatExchanger extends Component {
    
    /**
     * Constructor
     * @param name The component name.
     * @param ambient The component ambient state.
     */
    protected HeatExchanger(String name, State ambient) {
        super(name, ambient);
        flowNodes.put("Hot Side Inlet",new FlowNode(INLET));
        flowNodes.put("Hot Side Outlet",new FlowNode(OUTLET));
        flowNodes.put("Cold Side Inelt",new FlowNode(INLET));
        flowNodes.put("Cold Side Outlet",new FlowNode(OUTLET));
        internals.add(new Connection(flowNodes.get("Hot Side Inlet"),flowNodes.get("Hot Side Outlet")));
        internals.add(new Connection(flowNodes.get("Cold Side Inlet"),flowNodes.get("Cold Side Outlet")));
        equations.add(new Mass_Balance_Hot());
        equations.add(new Mass_Balance_Cold());
        equations.add(new Energy_Balance_Hot());
        equations.add(new Energy_Balance_Cold());
        equations.add(new Pressure_Loss_Hot());
        equations.add(new Pressure_Loss_Cold());
        equations.add(new Effectiveness());
        equations.add(new Ideal_Heat_Transfer());
    }
    
    @Override
    protected double heatExergyIn() {
        return 0;
    };

    @Override
    protected double heatExergyOut() {
        return 0;
    }
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(flowNodes.get("Hot Side Inlet"), flowNodes.get("Hot Side Outlet"), ENTHALPY, ENTROPY));
        paths.add(thermodynamicProcess(flowNodes.get("Cold Side Inlet"), flowNodes.get("Cold Side Outlet"), ENTHALPY, ENTROPY));
        return paths;
    }
    
    @Override
    public Set<Attributes.Attribute> getAllowableAtributes() {
        Set<Attributes.Attribute> attributes = new HashSet();
        attributes.add(EFFECTIVENESS);
        attributes.add(AHEATTRANSFER);
        attributes.add(IHEATTRANSFER);
        return attributes;
   }
    
    /**
     * Mass balance on the hot side of the heat exchanger.
     */
    private class Mass_Balance_Hot extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Mass_Balance_Hot() {super("m in = m out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getMass());
            variables.put("m out", HeatExchanger.this.flowNodes.get("Hot Side Outlet").getMass());
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
                    HeatExchanger.this.flowNodes.get("Hot Side Inlet").setMass(value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
                }
                case "m out": {
                    HeatExchanger.this.flowNodes.get("Hot Side Outlet").setMass(value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Outlet");
                }
            }
            return null;
        }  
    }
    
    /**
     * Mass balance on the cold side of the heat exchanger.
     */
    private class Mass_Balance_Cold extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Mass_Balance_Cold() {super("m in = m out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getMass());
            variables.put("m out", HeatExchanger.this.flowNodes.get("Cold Side Outlet").getMass());
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
                    HeatExchanger.this.flowNodes.get("Cold Side Inlet").setMass(value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
                }
                case "m out": {
                    HeatExchanger.this.flowNodes.get("Cold Side Outlet").setMass(value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Outlet");
                }
            }
            return null;
        }  
    }
    
    /**
     * Pressure loss on the hot side of the heat exchanger.
     */
    private class Pressure_Loss_Hot extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Pressure_Loss_Hot() {super("p in = p out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("p in", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(PRESSURE));
            variables.put("p out", HeatExchanger.this.flowNodes.get("Hot Side Outlet").getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p in").getAsDouble() - variables.get("p out").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "p in": {
                    HeatExchanger.this.flowNodes.get("Hot Side Inlet").setProperty(PRESSURE,value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
                }
                case "p out": {
                    HeatExchanger.this.flowNodes.get("Hot Side Outlet").setProperty(PRESSURE,value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Outlet");
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure loss on the cold side of the heat exchanger.
     */
    private class Pressure_Loss_Cold extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Pressure_Loss_Cold() {super("p_in = p_out", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("p_in", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(PRESSURE));
            variables.put("p_out", HeatExchanger.this.flowNodes.get("Cold Side Outlet").getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p_in").getAsDouble() - variables.get("p_out").getAsDouble();
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "p_in": {
                    HeatExchanger.this.flowNodes.get("Cold Side Inlet").setProperty(PRESSURE,value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
                }
                case "p_out": {
                    HeatExchanger.this.flowNodes.get("Cold Side Outlet").setProperty(PRESSURE,value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Outlet");
                }
            }
            return null;
        }
    }
    
    /**
     * Heat exchanger effectiveness.
     */
    private class Effectiveness extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Effectiveness() {super("Q_actual = Q_ideal * \u03B5", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("\u03B5", HeatExchanger.this.getAttribute(EFFECTIVENESS));
            variables.put("Q_actual", HeatExchanger.this.getAttribute(AHEATTRANSFER));
            variables.put("Q_ideal", HeatExchanger.this.getAttribute(IHEATTRANSFER));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q_ideal").getAsDouble()*variables.get("\u03B5").getAsDouble() - variables.get("Q_actual").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "\u03B5": {
                    HeatExchanger.this.setAttribute(EFFECTIVENESS,value);
                    return null;
                }
                case "Q_actual": {
                    HeatExchanger.this.setAttribute(AHEATTRANSFER,value);
                    return null;
                }
                case "Q_ideal": {
                    HeatExchanger.this.setAttribute(IHEATTRANSFER,value);
                    return null;
                }
            }
            return null;
        }  
    }
    
    /**
     * Energy balance on the hot side of the heat exchanger.
     */
    private class Energy_Balance_Hot extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Energy_Balance_Hot() {super("Q = m * (h out - h in)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getMass());
            variables.put("h in", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(ENTHALPY));
            variables.put("h out", HeatExchanger.this.flowNodes.get("Hot Side Outlet").getState(ENTHALPY));
            variables.put("Q", HeatExchanger.this.getAttribute(AHEATTRANSFER));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h in").getAsDouble() - variables.get("h out").getAsDouble());
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "m": {
                    HeatExchanger.this.flowNodes.get("Hot Side Inlet").setMass(value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
                }
                case "h in": {
                    HeatExchanger.this.flowNodes.get("Hot Side Inlet").setProperty(ENTHALPY, value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
                }
                case "h out": {
                    HeatExchanger.this.flowNodes.get("Hot Side Outlet").setProperty(ENTHALPY, value);
                    return HeatExchanger.this.flowNodes.get("Hot Side Outlet");
                }
                case "Q": {
                    HeatExchanger.this.setAttribute(AHEATTRANSFER, value);
                    return null;
                }
            }
            return null;
        }  
    }
    
    /**
     * Energy balance on the cold side of the heat exchanger.
     */
    private class Energy_Balance_Cold extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Energy_Balance_Cold() {super("Q = m * (h out - h in)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getMass());
            variables.put("h in", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(ENTHALPY));
            variables.put("h out", HeatExchanger.this.flowNodes.get("Cold Side Outlet").getState(ENTHALPY));
            variables.put("Q", HeatExchanger.this.getAttribute(AHEATTRANSFER));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q").getAsDouble() - variables.get("m").getAsDouble() * (variables.get("h out").getAsDouble() - variables.get("h in").getAsDouble());
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "m": {
                    HeatExchanger.this.setAttribute(AHEATTRANSFER, value);
                    return null;
                }
                case "h in": {
                    HeatExchanger.this.flowNodes.get("Cold Side Inlet").setProperty(ENTHALPY, value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
                }
                case "h out": {
                    HeatExchanger.this.flowNodes.get("Cold Side Outlet").setProperty(ENTHALPY, value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Outlet");
                }
                case "Q": {
                    HeatExchanger.this.flowNodes.get("Cold Side Inlet").setMass(value);
                    return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
                }
            }
            return null;
        }  
    }
    
    /**
     * Ideal heat transfer through the heat exchanger.
     */
    private class Ideal_Heat_Transfer extends ComponentEquation{
        
        /**
         * Constructor.
         */
        private Ideal_Heat_Transfer() {super("Q ideal = f(h in hot, T in cold, p out hot, m hot, h in cold, T in hot, p out cold, m cold)", 1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q ideal", HeatExchanger.this.getAttribute(IHEATTRANSFER));
            variables.put("h in hot", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(ENTHALPY));
            variables.put("T in cold", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(TEMPERATURE));
            variables.put("p out hot", HeatExchanger.this.flowNodes.get("Hot Side Outlet").getState(PRESSURE));
            variables.put("m hot", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getMass());
            variables.put("h in cold", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(ENTHALPY));
            variables.put("T in hot", HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(TEMPERATURE));
            variables.put("p out cold", HeatExchanger.this.flowNodes.get("Cold Side Outlet").getState(PRESSURE));
            variables.put("m cold", HeatExchanger.this.flowNodes.get("Cold Side Inlet").getMass());
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            
            FlowNode hotOutletMin = new FlowNode(INTERNAL);
            hotOutletMin.setFluid(HeatExchanger.this.flowNodes.get("Hot Side Inlet").getFluid().get());
            OptionalDouble Q_ideal_h2c = OptionalDouble.of(0.0);
            FlowNode coldOutletMax = new FlowNode(INTERNAL);
            coldOutletMax.setFluid(HeatExchanger.this.flowNodes.get("Cold Side Inlet").getFluid().get());
            OptionalDouble Q_ideal_c2h = OptionalDouble.of(0.0);
            
            hotOutletMin.setProperty(TEMPERATURE, variables.get("T in cold").getAsDouble());
            hotOutletMin.setProperty(PRESSURE, variables.get("p out hot").getAsDouble());
            Q_ideal_h2c = OptionalDouble.of(variables.get("m hot").getAsDouble() * variables.get("h in hot").getAsDouble() - hotOutletMin.getState(ENTHALPY).getAsDouble());

            coldOutletMax.setProperty(TEMPERATURE, variables.get("T in hot").getAsDouble());
            coldOutletMax.setProperty(PRESSURE, variables.get("p out cold").getAsDouble());
            Q_ideal_c2h = OptionalDouble.of(variables.get("m hot").getAsDouble() * variables.get("h in hot").getAsDouble() - hotOutletMin.getState(ENTHALPY).getAsDouble());
            
            return variables.get("Q ideal").getAsDouble() - Math.min(Q_ideal_h2c.getAsDouble(),Q_ideal_c2h.getAsDouble());
            
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "Q ideal": {
                    HeatExchanger.this.setAttribute(IHEATTRANSFER, value);
                    return null;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
