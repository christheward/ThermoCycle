/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import thermocycle.Attribute;
import thermocycle.Component;
import thermocycle.ComponentEquation;
import thermocycle.Connection;
import thermocycle.EquationVariable;
import thermocycle.FlowNode;
import thermocycle.Fluid;
import static thermocycle.Fluid.ENTHALPY;
import thermocycle.Node;
import static thermocycle.Node.Port.*;
import thermocycle.State;
import utilities.Units;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class SteamDrum extends Component {
    
    public static final Attribute P_RATIO = new Attribute("Pressure Loss", "Rp", Units.UNITS_TYPE.DIMENSIONLESS, 0.0, Double.POSITIVE_INFINITY);
    
    public SteamDrum(String name, State ambient) {
        super(name, ambient);
        flowNodes.put("Water Inlet",new FlowNode(INLET));
        flowNodes.put("Steam Outlet",new FlowNode(OUTLET));
        flowNodes.put("Water Supply",new FlowNode(OUTLET));
        flowNodes.put("Steam Return",new FlowNode(INLET));
        flowNodes.put("Drum",new FlowNode(INTERNAL));
        internals.add(new Connection(flowNodes.get("Water Inlet"),flowNodes.get("Drum")));
        internals.add(new Connection(flowNodes.get("Drum"),flowNodes.get("Water Supply")));
        internals.add(new Connection(flowNodes.get("Steam Return"),flowNodes.get("Drum")));
        internals.add(new Connection(flowNodes.get("Drum"),flowNodes.get("Steam Outlet")));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
    }
    
    @Override
    public Set<Attribute> getAllowableAtributes() {
        return new HashSet<Attribute>();
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
        paths.add(thermodynamicProcess(flowNodes.get("Water Inlet"), flowNodes.get("Water Supply"), Fluid.ENTHALPY, Fluid.ENTROPY));
        paths.add(thermodynamicProcess(flowNodes.get("Steam Retun"), flowNodes.get("Steam Outlet"), Fluid.ENTHALPY, Fluid.ENTROPY));
        return paths;
    }
    
    /**
     * Mass balance across turbine.
     */
    private class Mass_Balance extends ComponentEquation{
        
        private final EquationVariable M_IN_1 = new EquationVariable("Inlet 1 Mass Flow", "m_in_1", FlowNode.class);
        private final EquationVariable M_OUT_1 = new EquationVariable("Outlet 1 Mass Flow", "m_out_1", FlowNode.class);
        private final EquationVariable M_IN_2 = new EquationVariable("Inlet 2 Mass Flow", "m_in_2", FlowNode.class);
        private final EquationVariable M_OUT_2 = new EquationVariable("Outlet 2 Mass Flow", "m_out_2", FlowNode.class);
        
        /**
         * Constructor.
         */
        private Mass_Balance() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return M_IN_1 + " + " + M_IN_2 + " = " + M_OUT_1 + " + " + M_OUT_2;
        }
        
        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(M_IN_1).getAsDouble() + variables.get(M_IN_2).getAsDouble() - variables.get(M_OUT_1).getAsDouble() - variables.get(M_OUT_2).getAsDouble();
        }
        
        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(M_IN_1, SteamDrum.this.flowNodes.get("Water Inlet").getMass());
            variables.put(M_IN_1, SteamDrum.this.flowNodes.get("Steam Return").getMass());
            variables.put(M_OUT_1, SteamDrum.this.flowNodes.get("Water Supply").getMass());
            variables.put(M_OUT_1, SteamDrum.this.flowNodes.get("Steam Outlet").getMass());
            return variables;
        }
        
        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN_1)) {
                SteamDrum.this.flowNodes.get("Water Inlet").setMass(value);
                return SteamDrum.this.flowNodes.get("Water Inlet");
            }
            if (variable.equals(M_IN_2)) {
                SteamDrum.this.flowNodes.get("Steam Return").setMass(value);
                return SteamDrum.this.flowNodes.get("Steam Return");
            }
            if (variable.equals(M_OUT_1)) {
                SteamDrum.this.flowNodes.get("Water Supply").setMass(value);
                return SteamDrum.this.flowNodes.get("Water Supply");
            }
            if (variable.equals(M_OUT_2)) {
                SteamDrum.this.flowNodes.get("Steam Outlet").setMass(value);
                return SteamDrum.this.flowNodes.get("Steam Outlet");
            }
            return null;
        }  
    }
    
    private class Energy_Balance extends ComponentEquation {
        
        private final EquationVariable M_IN_1 = new EquationVariable("Inlet 1 Mass Flow", "m_in_1", FlowNode.class);
        private final EquationVariable M_OUT_1 = new EquationVariable("Outlet 1 Mass Flow", "m_out_1", FlowNode.class);
        private final EquationVariable M_IN_2 = new EquationVariable("Inlet 2 Mass Flow", "m_in_2", FlowNode.class);
        private final EquationVariable M_OUT_2 = new EquationVariable("Outlet 2 Mass Flow", "m_out_2", FlowNode.class);
        private final EquationVariable H_IN_1 = new EquationVariable("Inlet 1 Enthalpy", "h_in_1", FlowNode.class);
        private final EquationVariable H_OUT_1 = new EquationVariable("Outlet Enthalpy", "h_out_1", FlowNode.class);
        private final EquationVariable H_IN_2 = new EquationVariable("Inlet 2 Enthalpy", "h_in_2", FlowNode.class);
        private final EquationVariable H_OUT_2 = new EquationVariable("Outlet 2 Enthalpy", "h_out_2", FlowNode.class);
        
        /**
         * Constructor.
         */
        public Energy_Balance() {
            super(1e-3);
        }
        
        @Override
        public String equation() {
            return M_IN_1 + " " + H_IN_1 + " + " + M_IN_2 + " " + H_IN_2 + " = " + M_OUT_1 + " " + H_OUT_1 + " + " + M_OUT_2 + " " + H_OUT_2;
        }

        @Override
        protected Double function(Map<EquationVariable, OptionalDouble> variables) {
            return variables.get(M_IN_1).getAsDouble()*variables.get(H_IN_1).getAsDouble() + variables.get(M_IN_2).getAsDouble()*variables.get(H_IN_2).getAsDouble() - variables.get(M_OUT_1).getAsDouble()*variables.get(M_OUT_1).getAsDouble() - variables.get(M_OUT_2).getAsDouble()*variables.get(H_OUT_2).getAsDouble();
        }

        @Override
        protected Map<EquationVariable, OptionalDouble> getVariables() {
            Map<EquationVariable, OptionalDouble> variables = new HashMap();
            variables.put(M_IN_1, SteamDrum.this.flowNodes.get("Inlet 1 Mass Flow").getMass());
            variables.put(M_IN_2, SteamDrum.this.flowNodes.get("Inlet 2 Mass Flow").getMass());
            variables.put(M_OUT_1, SteamDrum.this.flowNodes.get("Outlet 1 Mass Flow").getMass());
            variables.put(M_OUT_2, SteamDrum.this.flowNodes.get("Outlet 2 Mass Flow").getMass());
            variables.put(H_IN_1, SteamDrum.this.flowNodes.get("Inlet 1 Mass Flow").getState(ENTHALPY));
            variables.put(H_IN_2, SteamDrum.this.flowNodes.get("Inlet 2 Mass Flow").getState(ENTHALPY));
            variables.put(H_OUT_1, SteamDrum.this.flowNodes.get("Outlet 1 Mass Flow").getState(ENTHALPY));
            variables.put(H_OUT_2, SteamDrum.this.flowNodes.get("Outlet 2 Mass Flow").getState(ENTHALPY));
            return variables;
        }

        @Override
        protected Node saveVariable(EquationVariable variable, Double value) {
            if (variable.equals(M_IN_1)) {
                SteamDrum.this.flowNodes.get("Water Inlet").setMass(value);
                return SteamDrum.this.flowNodes.get("Water Inlet");
            }
            if (variable.equals(M_IN_2)) {
                SteamDrum.this.flowNodes.get("Steam Return").setMass(value);
                return SteamDrum.this.flowNodes.get("Steam Return");
            }
            if (variable.equals(M_OUT_1)) {
                SteamDrum.this.flowNodes.get("Water Supply").setMass(value);
                return SteamDrum.this.flowNodes.get("Water Supply");
            }
            if (variable.equals(M_OUT_2)) {
                SteamDrum.this.flowNodes.get("Steam Outlet").setMass(value);
                return SteamDrum.this.flowNodes.get("Steam Outlet");
            }
            if (variable.equals(H_IN_1)) {
                SteamDrum.this.flowNodes.get("Water Inlet").setProperty(ENTHALPY,value);
                return SteamDrum.this.flowNodes.get("Water Inlet");
            }
            if (variable.equals(H_IN_2)) {
                SteamDrum.this.flowNodes.get("Steam Return").setProperty(ENTHALPY,value);
                return SteamDrum.this.flowNodes.get("Steam Return");
            }
            if (variable.equals(H_OUT_1)) {
                SteamDrum.this.flowNodes.get("Water Supply").setProperty(ENTHALPY,value);
                return SteamDrum.this.flowNodes.get("Water Supply");
            }
            if (variable.equals(H_OUT_2)) {
                SteamDrum.this.flowNodes.get("Steam Outlet").setProperty(ENTHALPY,value);
                return SteamDrum.this.flowNodes.get("Steam Outlet");
            }
            return null;
        }
    }
    
    // Outlets neet to be at the same pressure and temeprature
    // 
    
}
