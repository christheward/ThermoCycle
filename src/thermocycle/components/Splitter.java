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
import static thermocycle.Node.Port.*;
import thermocycle.UnitsControl.UnitsType;


/**
 *
 * @author Chris
 */
final class Splitter extends Component {
    
    public static final Attribute SPLIT = new Attribute("Split", "x", UnitsType.DIMENSIONLESS, 0.0, 1.0);
        
    /**
     * Constructor
     * @param name The name of the component.
     * @param ambient The ambient state of the component.
     */
    protected Splitter(String name, State ambient){
        super(name, ambient);
        flowNodes.put("Inlet",new FlowNode(INLET));
        flowNodes.put("Outlet 1",new FlowNode(OUTLET));
        flowNodes.put("Outlet 2",new FlowNode(OUTLET));
        internals.add(new Connection(flowNodes.get("Inlet"),flowNodes.get("Outlet 1")));
        internals.add(new Connection(flowNodes.get("Inlet"),flowNodes.get("Outlet 2")));
        equations.add(new Mass_Balance());
        equations.add(new Mass_Split());
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
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet 1"), Fluid.ENTHALPY, Fluid.ENTROPY));
        paths.add(thermodynamicProcess(flowNodes.get("Inlet"), flowNodes.get("Outlet 2"), Fluid.ENTHALPY, Fluid.ENTROPY));
        return paths;
    }
    
    @Override
    public Set<Attribute> getAllowableAtributes() {
        Set<Attribute> attributes = new HashSet();
        attributes.add(SPLIT);
        return attributes;
   }
    
    /**
     * Mass balance across the splitter.
     */
    private class Mass_Balance extends ComponentEquation{
        
        private final EquationVariable SPLIT = new EquationVariable(Splitter.SPLIT);
        private final EquationVariable M_IN = new EquationVariable("Mass inlet", "m_in", FlowNode.class);
        private final EquationVariable M_OUT_1 = new EquationVariable("Mass outlet 1", "m_out_1", FlowNode.class);
        private final EquationVariable M_OUT_2 = new EquationVariable("Mass outlet 2", "m_out_2", FlowNode.class);
        
        /**
         * Constructor.
         */
        private Mass_Balance() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return M_IN + " = " + M_OUT_1 + " + " + M_OUT_2;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(M_OUT_1).getAsDouble() + variables.get(M_OUT_2).getAsDouble() - variables.get(M_IN).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(M_IN, Splitter.this.flowNodes.get("Inlet").getMass());
            variables.put(M_OUT_1, Splitter.this.flowNodes.get("Outlet 1").getMass());
            variables.put(M_OUT_2, Splitter.this.flowNodes.get("Outlet 2").getMass());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN)) {
                Splitter.this.flowNodes.get("Inlet").setMass(value);
                return Splitter.this.flowNodes.get("Inlet");
            }
            if (variable.equals(M_OUT_1)) {
                Splitter.this.flowNodes.get("Outlet 1").setMass(value);
                return Splitter.this.flowNodes.get("Outlet 1");
            }
            if (variable.equals(M_OUT_2)) {
                Splitter.this.flowNodes.get("Outlet 2").setMass(value);
                return Splitter.this.flowNodes.get("Outlet 2");
            }
            return null;
        }
    }
    
    /**
     * Mass split across the first branch in splitter.
     */
    private class Mass_Split extends ComponentEquation{
        
        private final EquationVariable SPLIT = new EquationVariable(Splitter.SPLIT);
        private final EquationVariable M_IN = new EquationVariable("Mass inlet", "m_in", FlowNode.class);
        private final EquationVariable M_OUT_1 = new EquationVariable("Mass outlet 1", "m_out_1", FlowNode.class);
        
        /**
         * Constructor.
         */
        private Mass_Split() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return M_OUT_1 + " = " + M_IN + " " + SPLIT;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(M_IN).getAsDouble()*variables.get(SPLIT).getAsDouble() - variables.get(M_OUT_1).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(SPLIT, Splitter.this.getAttribute(Splitter.SPLIT));
            variables.put(M_IN, Splitter.this.flowNodes.get("Inlet").getMass());
            variables.put(M_OUT_1, Splitter.this.flowNodes.get("Outlet 1").getMass());
            return variables;
        }
                
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(SPLIT)) {
                Splitter.this.setAttribute(Splitter.SPLIT, value);
                return null;
            }
            if (variable.equals(M_IN)) {
                Splitter.this.flowNodes.get("Inlet").setMass(value);
                return Splitter.this.flowNodes.get("Inlet");
            }
            if (variable.equals(M_OUT_1)) {
                Splitter.this.flowNodes.get("Outlet 1").setMass(value);
                return Splitter.this.flowNodes.get("Outlet 1");
            }
            return null;
        }
    }
    
}