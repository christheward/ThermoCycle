/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Node.Port.*;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris
 */
public final class HeatSink extends Component {
    
    public static final Attribute P_LOSS = new Attribute("Pressure Loss", "p_loss", UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0);
    
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
    public Set<Attribute> getAllowableAtributes() {
        Set<Attribute> attributes = new HashSet();
        attributes.add(P_LOSS);
        return attributes;
    }
    
    @Override
    protected double heatExergyIn() {
        return 0;
    }
    
    @Override
    protected double heatExergyOut() {
        return heatTransferProcessExergy(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), Fluid.ENTHALPY, Fluid.PRESSURE));
    }
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet"), Fluid.ENTHALPY, Fluid.ENTROPY));
        return paths;
    }
    
    /**
     * Mass balance across Heat Sink 
     */
    private class Mass_Balance extends ComponentEquation{
        
        private final EquationVariable M_IN = new EquationVariable("Inlet Mass Flow", "m_in", FlowNode.class);
        private final EquationVariable M_OUT = new EquationVariable("Outlet Mass Flow", "m_out", FlowNode.class);
        
        /**
         * Constructor
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
            variables.put(M_IN, HeatSink.this.flowNodes.get("Inlet").getMass());
            variables.put(M_OUT, HeatSink.this.flowNodes.get("Outlet").getMass());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN)) {
                HeatSink.this.flowNodes.get("Inlet").setMass(value);
                return HeatSink.this.flowNodes.get("Inlet");
            }
            if (variable.equals(M_OUT)) {
                HeatSink.this.flowNodes.get("Outlet").setMass(value);
                return HeatSink.this.flowNodes.get("Outlet");
            }
            return null;
        }  
    }
    
    /**
     * Heat  Sink energy balance
     */
    private class Energy_Balance extends ComponentEquation{
        
        private final EquationVariable H_IN = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable H_OUT = new EquationVariable("Outlet Enthalpy", "h_out", Fluid.ENTHALPY);
        private final EquationVariable M = new EquationVariable("Inlet Mass Flow", "m", FlowNode.class);
        private final EquationVariable Q = new EquationVariable("Heat Output", "Q", HeatNode.class);
        
        /**
         * Constructor
         */
        private Energy_Balance() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return Q + " = " + M + " (" + H_IN + " - " + H_OUT + ")";
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(Q).getAsDouble() - variables.get(M).getAsDouble()*(variables.get(H_IN).getAsDouble() - variables.get(H_OUT).getAsDouble());
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(H_IN, HeatSink.this.flowNodes.get("Inlet").getState(Fluid.ENTHALPY));
            variables.put(H_OUT, HeatSink.this.flowNodes.get("Outlet").getState(Fluid.ENTHALPY));
            variables.put(M, HeatSink.this.flowNodes.get("Inlet").getMass());
            variables.put(Q, HeatSink.this.heatNodes.get("Sink").getHeat());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(H_IN)) {
                HeatSink.this.flowNodes.get("Inlet").setProperty(Fluid.ENTHALPY,value);
                return HeatSink.this.flowNodes.get("Inlet");
            }
            if (variable.equals(H_OUT)) {
                HeatSink.this.flowNodes.get("Outlet").setProperty(Fluid.ENTHALPY,value);
                return HeatSink.this.flowNodes.get("Outlet");
            }
            if (variable.equals(M)) {
                HeatSink.this.flowNodes.get("Inlet").setMass(value);
                return HeatSink.this.flowNodes.get("Inlet");
            }
            if (variable.equals(Q)) {
                HeatSink.this.heatNodes.get("Sink").setHeat(value);
                return HeatSink.this.heatNodes.get("Sink");
            }
            return null;
        }
    }
    
    /**
     * Pressure loss across heat sink
     */
    private class Pressure_Loss extends ComponentEquation{
        
        private final EquationVariable P_LOSS = new EquationVariable(HeatSink.P_LOSS);
        private final EquationVariable P_IN = new EquationVariable("Inlet Pressure", "p_in", Fluid.PRESSURE);
        private final EquationVariable P_OUT = new EquationVariable("Outlet Pressure", "p_out", Fluid.PRESSURE);
        
        /**
         * Constructor
         */
        private Pressure_Loss() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return P_OUT + " = " + P_IN + " (1 - " + P_LOSS + ")";
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(P_IN).getAsDouble()*(1 - variables.get(P_LOSS).getAsDouble()) - variables.get(P_OUT).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(P_LOSS, HeatSink.this.getAttribute(HeatSink.P_LOSS));
            variables.put(P_IN, HeatSink.this.flowNodes.get("Inlet").getState(Fluid.PRESSURE));
            variables.put(P_OUT, HeatSink.this.flowNodes.get("Outlet").getState(Fluid.PRESSURE));
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(P_LOSS)) {
                HeatSink.this.setAttribute(HeatSink.P_LOSS, value);
                return null;
            }
            if (variable.equals(P_IN)) {
                HeatSink.this.flowNodes.get("Inlet").setProperty(Fluid.PRESSURE,value);
                return HeatSink.this.flowNodes.get("Inlet");
            }
            if (variable.equals(P_OUT)) {
                HeatSink.this.flowNodes.get("Outlet").setProperty(Fluid.PRESSURE,value);
                return HeatSink.this.flowNodes.get("Outlet");
            }
            return null;
        }
    }
}
