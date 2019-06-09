/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Node.Port.*;
import utilities.Units;

/**
 *
 * @author Chris
 */
public final class HeatExchanger extends Component {
    
    public static final Attribute EFFECTIVENESS  = new Attribute("Effectiveness", "Epsilon", Units.UNITS_TYPE.DIMENSIONLESS, 0.0, 1.0);
    public static final Attribute Q_ACTUAL = new Attribute("Actual Heat Transfer","Q_Actual",Units.UNITS_TYPE.POWER,0.0,Double.POSITIVE_INFINITY);
    public static final Attribute Q_IDEAL = new Attribute("Ideal Heat Transfer","Q_Ideal",Units.UNITS_TYPE.POWER,0.0,Double.POSITIVE_INFINITY);
    
    /**
     * Constructor
     * @param name The component name.
     * @param ambient The component ambient state.
     */
    protected HeatExchanger(String name, State ambient) {
        super(name, ambient);
        flowNodes.put("Hot Side Inlet",new FlowNode(INLET));
        flowNodes.put("Hot Side Outlet",new FlowNode(OUTLET));
        flowNodes.put("Cold Side Inlet",new FlowNode(INLET));
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
        paths.add(thermodynamicProcess(flowNodes.get("Hot Side Inlet"), flowNodes.get("Hot Side Outlet"), Fluid.ENTHALPY, Fluid.ENTROPY));
        paths.add(thermodynamicProcess(flowNodes.get("Cold Side Inlet"), flowNodes.get("Cold Side Outlet"), Fluid.ENTHALPY, Fluid.ENTROPY));
        return paths;
    }
    
    @Override
    public Set<Attribute> getAllowableAtributes() {
        Set<Attribute> attributes = new HashSet();
        attributes.add(EFFECTIVENESS);
        attributes.add(Q_ACTUAL);
        attributes.add(Q_IDEAL);
        return attributes;
   }
    
    /**
     * Mass balance on the hot side of the heat exchanger.
     */
    private class Mass_Balance_Hot extends ComponentEquation{
        
        private final EquationVariable M_IN = new EquationVariable("Inlet Mass Flow", "m_in", FlowNode.class);
        private final EquationVariable M_OUT = new EquationVariable("Outlet Mass Flow", "m_out", FlowNode.class);
        
        /**
         * Constructor.
         */
        private Mass_Balance_Hot() {
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
            variables.put(M_IN, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getMass());
            variables.put(M_OUT, HeatExchanger.this.flowNodes.get("Hot Side Outlet").getMass());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN)) {
                HeatExchanger.this.flowNodes.get("Hot Side Inlet").setMass(value);
                return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
            }
            if (variable.equals(M_OUT)) {
                HeatExchanger.this.flowNodes.get("Hot Side Outlet").setMass(value);
                return HeatExchanger.this.flowNodes.get("Hot Side Outlet");
            }
            return null;
        }  
    }
    
    /**
     * Mass balance on the cold side of the heat exchanger.
     */
    private class Mass_Balance_Cold extends ComponentEquation{
        
        private final EquationVariable M_IN = new EquationVariable("Inlet Mass Flow", "m_in", FlowNode.class);
        private final EquationVariable M_OUT = new EquationVariable("Outlet Mass Flow", "m_out", FlowNode.class);
        
        /**
         * Constructor.
         */
        private Mass_Balance_Cold() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return M_IN + " = " + M_OUT;
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(M_IN, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getMass());
            variables.put(M_OUT, HeatExchanger.this.flowNodes.get("Cold Side Outlet").getMass());
            return variables;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(M_IN).getAsDouble() - variables.get(M_OUT).getAsDouble();
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN)) {
                HeatExchanger.this.flowNodes.get("Cold Side Inlet").setMass(value);
                return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
            }
            if (variable.equals(M_OUT)) {
                HeatExchanger.this.flowNodes.get("Cold Side Outlet").setMass(value);
                return HeatExchanger.this.flowNodes.get("Cold Side Outlet");
            }
            return null;
        }  
    }
    
    /**
     * Pressure loss on the hot side of the heat exchanger.
     */
    private class Pressure_Loss_Hot extends ComponentEquation{
        
        public final EquationVariable P_IN = new EquationVariable("Inlet Pressure", "P_in", Fluid.PRESSURE);
        public final EquationVariable P_OUT = new EquationVariable("Outlet Pressure", "P_out", Fluid.PRESSURE);
        
        /**
         * Constructor.
         */
        private Pressure_Loss_Hot() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return P_IN + " = " + P_OUT;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(P_IN).getAsDouble() - variables.get(P_OUT).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(P_IN, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(Fluid.PRESSURE));
            variables.put(P_OUT, HeatExchanger.this.flowNodes.get("Hot Side Outlet").getState(Fluid.PRESSURE));
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(P_IN)) {
                HeatExchanger.this.flowNodes.get("Hot Side Inlet").setProperty(Fluid.PRESSURE,value);
                return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
            }
            if (variable.equals(P_OUT)) {
                HeatExchanger.this.flowNodes.get("Hot Side Outlet").setProperty(Fluid.PRESSURE,value);
                return HeatExchanger.this.flowNodes.get("Hot Side Outlet");
            }
            return null;
        }
    }
    
    /**
     * Pressure loss on the cold side of the heat exchanger.
     */
    private class Pressure_Loss_Cold extends ComponentEquation{
        
        public final EquationVariable P_IN = new EquationVariable("Inlet Pressure", "P_in", Fluid.PRESSURE);
        public final EquationVariable P_OUT = new EquationVariable("Outlet Pressure", "P_out", Fluid.PRESSURE);
        
        /**
         * Constructor.
         */
        private Pressure_Loss_Cold() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return P_IN + " = " + P_OUT;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(P_IN).getAsDouble() - variables.get(P_OUT).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(P_IN, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(Fluid.PRESSURE));
            variables.put(P_OUT, HeatExchanger.this.flowNodes.get("Cold Side Outlet").getState(Fluid.PRESSURE));
            return variables;
        }
                
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(P_IN)) {
                HeatExchanger.this.flowNodes.get("Cold Side Inlet").setProperty(Fluid.PRESSURE,value);
                return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
            }
            if (variable.equals(P_OUT)) {
                HeatExchanger.this.flowNodes.get("Cold Side Outlet").setProperty(Fluid.PRESSURE,value);
                return HeatExchanger.this.flowNodes.get("Cold Side Outlet");
            }
            return null;
        }
    }
    
    /**
     * Heat exchanger effectiveness.
     */
    private class Effectiveness extends ComponentEquation{
        
        public final EquationVariable EFFECTIVENESS = new EquationVariable(HeatExchanger.EFFECTIVENESS);
        public final EquationVariable Q_ACTUAL = new EquationVariable(HeatExchanger.Q_ACTUAL);
        public final EquationVariable Q_IDEAL = new EquationVariable(HeatExchanger.Q_IDEAL);
        
        /**
         * Constructor.
         */
        private Effectiveness() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return Q_ACTUAL + " = " + Q_IDEAL + " " + EFFECTIVENESS;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(Q_IDEAL).getAsDouble()*variables.get(EFFECTIVENESS).getAsDouble() - variables.get(Q_ACTUAL).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(EFFECTIVENESS, HeatExchanger.this.getAttribute(HeatExchanger.EFFECTIVENESS));
            variables.put(Q_ACTUAL, HeatExchanger.this.getAttribute(HeatExchanger.Q_ACTUAL));
            variables.put(Q_IDEAL, HeatExchanger.this.getAttribute(HeatExchanger.Q_IDEAL));
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(EFFECTIVENESS)) {
                HeatExchanger.this.setAttribute(HeatExchanger.EFFECTIVENESS,value);
                return null;
            }
            if (variable.equals(Q_ACTUAL)) {
                HeatExchanger.this.setAttribute(HeatExchanger.Q_ACTUAL,value);
                return null;
            }
            if (variable.equals(Q_IDEAL)) {
                HeatExchanger.this.setAttribute(HeatExchanger.Q_IDEAL,value);
                return null;
            }
            return null;
        }  
    }
    
    /**
     * Energy balance on the hot side of the heat exchanger.
     */
    private class Energy_Balance_Hot extends ComponentEquation{
        
        private final EquationVariable H_IN = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable H_OUT = new EquationVariable("Outlet Enthalpy", "h_out", Fluid.ENTHALPY);
        private final EquationVariable M = new EquationVariable("Inlet Mass Flow", "m", FlowNode.class);
        public final EquationVariable Q = new EquationVariable(HeatExchanger.Q_ACTUAL);
        
        /**
         * Constructor.
         */
        private Energy_Balance_Hot() {
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
            variables.put(H_IN, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(Fluid.ENTHALPY));
            variables.put(H_OUT, HeatExchanger.this.flowNodes.get("Hot Side Outlet").getState(Fluid.ENTHALPY));
            variables.put(M, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getMass());
            variables.put(Q, HeatExchanger.this.getAttribute(HeatExchanger.Q_ACTUAL));
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(H_IN)) {
                HeatExchanger.this.flowNodes.get("Hot Side Inlet").setProperty(Fluid.ENTHALPY, value);
                return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
            }
            if (variable.equals(H_OUT)) {
                HeatExchanger.this.flowNodes.get("Hot Side Outlet").setProperty(Fluid.ENTHALPY, value);
                return HeatExchanger.this.flowNodes.get("Hot Side Outlet");
            }
            if (variable.equals(M)) {
                HeatExchanger.this.flowNodes.get("Hot Side Inlet").setMass(value);
                return HeatExchanger.this.flowNodes.get("Hot Side Inlet");
            }
            if (variable.equals(Q)) {
                HeatExchanger.this.setAttribute(HeatExchanger.Q_ACTUAL, value);
                return null;
            }
            return null;
        }  
    }
    
    /**
     * Energy balance on the cold side of the heat exchanger.
     */
    private class Energy_Balance_Cold extends ComponentEquation{
        
        private final EquationVariable H_IN = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable H_OUT = new EquationVariable("Outlet Enthalpy", "h_out", Fluid.ENTHALPY);
        private final EquationVariable M = new EquationVariable("Mass Flow", "m", FlowNode.class);
        private final EquationVariable Q = new EquationVariable("Heat Out", "Q", HeatNode.class);
        
        /**
         * Constructor.
         */
        private Energy_Balance_Cold() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return Q + " = " + M + " (" + H_OUT + " - " + H_IN + ")";
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(H_IN, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(Fluid.ENTHALPY));
            variables.put(H_OUT, HeatExchanger.this.flowNodes.get("Cold Side Outlet").getState(Fluid.ENTHALPY));
            variables.put(M, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getMass());
            variables.put(Q, HeatExchanger.this.getAttribute(Q_ACTUAL));
            return variables;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(Q).getAsDouble() - variables.get(M).getAsDouble() * (variables.get(H_OUT).getAsDouble() - variables.get(H_IN).getAsDouble());
        }
                
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(H_IN)) {
                HeatExchanger.this.flowNodes.get("Cold Side Inlet").setProperty(Fluid.ENTHALPY, value);
                return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
            }
            if (variable.equals(H_OUT)) {
                HeatExchanger.this.flowNodes.get("Cold Side Outlet").setProperty(Fluid.ENTHALPY, value);
                return HeatExchanger.this.flowNodes.get("Cold Side Outlet");
            }
            if (variable.equals(M)) {
                HeatExchanger.this.setAttribute(Q_ACTUAL, value);
                return null;
            }
            if (variable.equals(Q)) {
                HeatExchanger.this.flowNodes.get("Cold Side Inlet").setMass(value);
                return HeatExchanger.this.flowNodes.get("Cold Side Inlet");
            }
            return null;
        }  
    }
    
    /**
     * Ideal heat transfer through the heat exchanger.
     */
    private class Ideal_Heat_Transfer extends ComponentEquation{
        
        private final EquationVariable H_IN_COLD = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable H_IN_HOT = new EquationVariable("Inlet Enthalpy", "h_in", Fluid.ENTHALPY);
        private final EquationVariable M_COLD = new EquationVariable("Outlet Mass Flow", "m_out", FlowNode.class);
        private final EquationVariable M_HOT = new EquationVariable("Inlet Mass Flow", "m_in", FlowNode.class);
        public final EquationVariable P_OUT_COLD = new EquationVariable("Outlet Pressure", "P_out", Fluid.PRESSURE);
        public final EquationVariable P_OUT_HOT = new EquationVariable("Inlet Pressure", "P_in", Fluid.PRESSURE);
        public final EquationVariable Q = new EquationVariable(HeatExchanger.Q_IDEAL);
        private final EquationVariable T_IN_COLD = new EquationVariable("Cold Side Inlet Temperature", "T_in", Fluid.TEMPERATURE);
        private final EquationVariable T_IN_HOT = new EquationVariable("Hot Side Inlet Temperature", "T_in", Fluid.TEMPERATURE);
        
        /**
         * Constructor.
         */
        private Ideal_Heat_Transfer() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return Q + " = min(Q_hot_max, Q_cold_max)";
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(H_IN_COLD, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(Fluid.ENTHALPY));
            variables.put(H_IN_HOT, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(Fluid.ENTHALPY));
            variables.put(M_COLD, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getMass());
            variables.put(M_HOT, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getMass());
            variables.put(P_OUT_COLD, HeatExchanger.this.flowNodes.get("Cold Side Outlet").getState(Fluid.PRESSURE));
            variables.put(P_OUT_HOT, HeatExchanger.this.flowNodes.get("Hot Side Outlet").getState(Fluid.PRESSURE));
            variables.put(Q, HeatExchanger.this.getAttribute(HeatExchanger.Q_IDEAL));
            variables.put(T_IN_COLD, HeatExchanger.this.flowNodes.get("Cold Side Inlet").getState(Fluid.TEMPERATURE));
            variables.put(T_IN_HOT, HeatExchanger.this.flowNodes.get("Hot Side Inlet").getState(Fluid.TEMPERATURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            
            FlowNode hotOutletMin = new FlowNode(INTERNAL);
            hotOutletMin.setFluid(HeatExchanger.this.flowNodes.get("Hot Side Inlet").getFluid().get());
            OptionalDouble Q_ideal_h2c = OptionalDouble.of(0.0);
            FlowNode coldOutletMax = new FlowNode(INTERNAL);
            coldOutletMax.setFluid(HeatExchanger.this.flowNodes.get("Cold Side Inlet").getFluid().get());
            OptionalDouble Q_ideal_c2h = OptionalDouble.of(0.0);
            
            hotOutletMin.setProperty(Fluid.TEMPERATURE, variables.get(T_IN_COLD).getAsDouble());
            hotOutletMin.setProperty(Fluid.PRESSURE, variables.get(P_OUT_HOT).getAsDouble());
            Q_ideal_h2c = OptionalDouble.of(variables.get(M_HOT).getAsDouble() * (variables.get(H_IN_HOT).getAsDouble() - hotOutletMin.getState(Fluid.ENTHALPY).getAsDouble()));

            coldOutletMax.setProperty(Fluid.TEMPERATURE, variables.get(T_IN_HOT).getAsDouble());
            coldOutletMax.setProperty(Fluid.PRESSURE, variables.get(P_OUT_COLD).getAsDouble());
            Q_ideal_c2h = OptionalDouble.of(variables.get(M_COLD).getAsDouble() * (coldOutletMax.getState(Fluid.ENTHALPY).getAsDouble() - variables.get(H_IN_COLD).getAsDouble()));
            
            return variables.get(Q).getAsDouble() - Math.min(Q_ideal_h2c.getAsDouble(),Q_ideal_c2h.getAsDouble());
            
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(Q)) {
                HeatExchanger.this.setAttribute(HeatExchanger.Q_IDEAL, value);
                return null;
            }
            return null;
        }
    }
    
}
