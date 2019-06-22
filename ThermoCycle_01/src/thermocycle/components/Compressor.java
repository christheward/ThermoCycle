/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.components;

import java.util.*;
import thermocycle.Attribute;
import thermocycle.Component;
import thermocycle.ComponentEquation;
import thermocycle.Connection;
import thermocycle.EquationVariable;
import thermocycle.FlowNode;
import thermocycle.Fluid;
import thermocycle.Node;
import thermocycle.State;
import thermocycle.WorkNode;
import static thermocycle.Node.Port.*;
import utilities.Units.UNITS_TYPE;


/**
 *
 * @author Chris
 */
public final class Compressor extends Component {
    
    public static final Attribute P_RATIO = new Attribute("Pressure Loss", "Rp", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0);
    public static final Attribute EFFICIENCY = new Attribute("Efficiency", "eta", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0);
    
    /**
     * Constructor
     * @param name The component name.
     * @param ambient The component ambient state.
     */
    public Compressor(String name, State ambient){
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
    public Set<Attribute> getAllowableAtributes() {
        Set<Attribute> attributes = new HashSet();
        attributes.add(P_RATIO);
        attributes.add(EFFICIENCY);
        return attributes;
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
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), Fluid.ENTHALPY, Fluid.ENTROPY));
        return paths;
    }
    
    /**
     * Mass balance across the compressor.
     */
    private class Mass_Balance extends ComponentEquation{
        
        private final EquationVariable M_IN = new EquationVariable("Inlet Mass Flow", "m_in", FlowNode.class);
        private final EquationVariable M_OUT = new EquationVariable("Outlet Mass Flow", "m_out", FlowNode.class);
        
        /**
         * Constructor.
         */
        private Mass_Balance() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return M_IN + " = " + M_OUT;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(M_IN).getAsDouble() - variables.get(M_OUT).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(M_IN, Compressor.this.flowNodes.get("Inlet").getMass());
            variables.put(M_OUT, Compressor.this.flowNodes.get("Outlet").getMass());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN)) {
                Compressor.this.flowNodes.get("Inlet").setMass(value);
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(M_OUT)) {
                Compressor.this.flowNodes.get("Outlet").setMass(value);
                return Compressor.this.flowNodes.get("Outlet");
            }
            return null;
        }
        
    }
    
    /**
     * Energy balance across the compressor
     */
    private class Energy_Balance extends ComponentEquation{
        
        private final EquationVariable H_IN = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable H_OUT = new EquationVariable("Outlet Enthalpy", "h_out", Fluid.ENTHALPY);
        private final EquationVariable M = new EquationVariable("Inlet Mass Flow", "m", FlowNode.class);
        private final EquationVariable W = new EquationVariable("Work In", "W", WorkNode.class);
        
        /**
         * Constructor.
         */
        private Energy_Balance() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return W + " = " + M + "( " + H_OUT + " - " + H_IN + ")";
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(W).getAsDouble() - variables.get(M).getAsDouble()*(variables.get(H_OUT).getAsDouble() - variables.get(H_IN).getAsDouble());
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(H_IN, Compressor.this.flowNodes.get("Inlet").getState(Fluid.ENTHALPY));
            variables.put(H_OUT, Compressor.this.flowNodes.get("Outlet").getState(Fluid.ENTHALPY));
            variables.put(M, Compressor.this.flowNodes.get("Inlet").getMass());
            variables.put(W, Compressor.this.workNodes.get("Shaft").getWork());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(H_IN)) {
                Compressor.this.flowNodes.get("Inlet").setProperty(Fluid.ENTHALPY,value);
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(H_OUT)) {
                Compressor.this.flowNodes.get("Outlet").setProperty(Fluid.ENTHALPY,value);
                return Compressor.this.flowNodes.get("Outlet");
            }
            if (variable.equals(M)) {
                Compressor.this.flowNodes.get("Inlet").setMass(value);
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(W)) {
                Compressor.this.workNodes.get("Shaft").setWork(value);
                return Compressor.this.workNodes.get("Shaft");
            }
            return null;
        }
        
    }
    
    /**
     * Pressure ratio across compressor.
     */
    private class Pressure_Ratio extends ComponentEquation{
        
        private final EquationVariable P_RATIO = new EquationVariable(Compressor.P_RATIO);
        private final EquationVariable P_IN = new EquationVariable("Inlet Pressure", "P_in", Fluid.PRESSURE);
        private final EquationVariable P_OUT = new EquationVariable("Outlet Pressure", "P_out", Fluid.PRESSURE);
        
        /**
         * Constructor.
         */
        private Pressure_Ratio() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return P_OUT + " = " + P_IN + " " + P_RATIO;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(P_OUT).getAsDouble() - variables.get(P_IN).getAsDouble()*variables.get(P_RATIO).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(P_RATIO, Compressor.this.getAttribute(Compressor.P_RATIO));
            variables.put(P_IN, Compressor.this.flowNodes.get("Inlet").getState(Fluid.PRESSURE));
            variables.put(P_OUT, Compressor.this.flowNodes.get("Outlet").getState(Fluid.PRESSURE));
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(P_RATIO)) {
                Compressor.this.setAttribute(Compressor.P_RATIO, value);
                return null;
            }
            if (variable.equals(P_IN)) {
                Compressor.this.flowNodes.get("Inlet").setProperty(Fluid.PRESSURE,value); 
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(P_OUT)) {
                Compressor.this.flowNodes.get("Outlet").setProperty(Fluid.PRESSURE,value);
                return Compressor.this.flowNodes.get("Outlet");
            }
            return null;
        }
        
    }
    
    /**
     * Efficiency of compressor.
     */
    private class Efficiency extends ComponentEquation{
        
        private final EquationVariable EFFICIENCY = new EquationVariable(Compressor.EFFICIENCY);
        private final EquationVariable H_IN = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable M = new EquationVariable("Mass Flow", "m", FlowNode.class);
        private final EquationVariable P_OUT = new EquationVariable("Outlet Pressure", "P_out", Fluid.PRESSURE);
        private final EquationVariable S_IN = new EquationVariable("Inlet Entropy", "s_in", Fluid.ENTROPY);
        private final EquationVariable W = new EquationVariable("Work In", "W", WorkNode.class);
        
        /**
         * Constructor.
         */
        private Efficiency() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return W + " = " + M + " (h_isen - " + H_IN + "), h_isen = " + ComponentEquation.func + "(" + P_OUT + "," + S_IN + ")";
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            FlowNode isen = new FlowNode(INTERNAL);
            isen.setFluid(Compressor.this.flowNodes.get("Inlet").getFluid().get());
            isen.setProperty(Fluid.ENTROPY, variables.get(S_IN).getAsDouble());
            isen.setProperty(Fluid.PRESSURE, variables.get(P_OUT).getAsDouble());
            isen.computeState();
            return variables.get(W).getAsDouble() - variables.get(M).getAsDouble()*(isen.getState(Fluid.ENTHALPY).getAsDouble() - variables.get(H_IN).getAsDouble())/variables.get(EFFICIENCY).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(EFFICIENCY, Compressor.this.getAttribute(Compressor.EFFICIENCY));
            variables.put(H_IN, Compressor.this.flowNodes.get("Inlet").getState(Fluid.ENTHALPY));
            variables.put(M, Compressor.this.flowNodes.get("Inlet").getMass());
            variables.put(P_OUT, Compressor.this.flowNodes.get("Outlet").getState(Fluid.PRESSURE));
            variables.put(S_IN, Compressor.this.flowNodes.get("Inlet").getState(Fluid.ENTROPY));
            variables.put(W, Compressor.this.workNodes.get("Shaft").getWork());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(EFFICIENCY)) {
                Compressor.this.setAttribute(Compressor.EFFICIENCY,value);
                return null;
            }
            if (variable.equals(H_IN)) {
                Compressor.this.flowNodes.get("Inlet").setProperty(Fluid.ENTHALPY,value);
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(M)) {
                Compressor.this.flowNodes.get("Inlet").setMass(value);
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(P_OUT)) {
                Compressor.this.flowNodes.get("Outlet").setProperty(Fluid.PRESSURE,value);
                return Compressor.this.flowNodes.get("Outlet");
            }
            if (variable.equals(S_IN)) {
                Compressor.this.flowNodes.get("Inlet").setProperty(Fluid.ENTROPY,value);
                return Compressor.this.flowNodes.get("Inlet");
            }
            if (variable.equals(W)) {
                Compressor.this.workNodes.get("Shaft").setWork(value);
                return Compressor.this.workNodes.get("Shaft");
            }
            return null;
        }  
    }
}