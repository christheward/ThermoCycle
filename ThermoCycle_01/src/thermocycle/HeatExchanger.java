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
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        internals.add(new Connection(flowNodes.get(2),flowNodes.get(3)));
        equations.add(new Mass_Balance_Hot());
        equations.add(new Mass_Balance_Cold());
        equations.add(new Energy_Balance_Hot());
        equations.add(new Energy_Balance_Cold());
        equations.add(new Pressure_Loss_Hot());
        equations.add(new Pressure_Loss_Cold());
        equations.add(new Effectiveness());
        equations.add(new Ideal_Heat_Transfer());
    }
    
    /**
     * Gets the heat exchanger hot stream inlet.
     * @return Returns the hot stream inlet flow node.
     */
    public FlowNode getInletHot() {
        return flowNodes.get(0);
    }
    
    /**
     * Gets the heat exchanger hot stream outlet.
     * @return Returns the hot stream outlet flow node.
     */
    public FlowNode getOutletHot() {
        return flowNodes.get(1);
    }
    
    /**
     * Gets the heat exchanger cold stream inlet.
     * @return Returns the cold stream inlet flow node.
     */
    public FlowNode getInletCold() {
        return flowNodes.get(2);
    }
    
    /**
     * Gets the heat exchanger cold stream outlet.
     * @return Returns the hot stream outlet flow node.
     */
    public FlowNode getOutletCold() {
        return flowNodes.get(3);
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
        paths.add(thermodynamicProcess(getInletHot(), getOutletHot(), ENTHALPY, ENTROPY));
        paths.add(thermodynamicProcess(getInletCold(), getOutletCold(), ENTHALPY, ENTROPY));
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
    private class Mass_Balance_Hot extends Equation{
        
        /**
         * Constructor.
         */
        private Mass_Balance_Hot() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatExchanger.this.getInletHot().getMass());
            variables.put("m out", HeatExchanger.this.getOutletHot().getMass());
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
                    HeatExchanger.this.getInletHot().setMass(value);
                    return HeatExchanger.this.getInletHot();
                }
                case "m out": {
                    HeatExchanger.this.getOutletHot().setMass(value);
                    return HeatExchanger.this.getOutletHot();
                }
            }
            return null;
        }  
    }
    
    /**
     * Mass balance on the cold side of the heat exchanger.
     */
    private class Mass_Balance_Cold extends Equation{
        
        /**
         * Constructor.
         */
        private Mass_Balance_Cold() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatExchanger.this.getInletCold().getMass());
            variables.put("m out", HeatExchanger.this.getOutletCold().getMass());
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
                    HeatExchanger.this.getInletCold().setMass(value);
                    return HeatExchanger.this.getInletCold();
                }
                case "m out": {
                    HeatExchanger.this.getOutletCold().setMass(value);
                    return HeatExchanger.this.getOutletCold();
                }
            }
            return null;
        }  
    }
    
    /**
     * Pressure loss on the hot side of the heat exchanger.
     */
    private class Pressure_Loss_Hot extends Equation{
        
        /**
         * Constructor.
         */
        private Pressure_Loss_Hot() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("p in", HeatExchanger.this.getInletHot().getState(PRESSURE));
            variables.put("p out", HeatExchanger.this.getOutletHot().getState(PRESSURE));
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
                    HeatExchanger.this.getInletHot().setProperty(PRESSURE,value);
                    return HeatExchanger.this.getInletHot();
                }
                case "p out": {
                    HeatExchanger.this.getOutletHot().setProperty(PRESSURE,value);
                    return HeatExchanger.this.getOutletHot();
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure loss on the cold side of the heat exchanger.
     */
    private class Pressure_Loss_Cold extends Equation{
        
        /**
         * Constructor.
         */
        private Pressure_Loss_Cold() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("p in", HeatExchanger.this.getInletCold().getState(PRESSURE));
            variables.put("p out", HeatExchanger.this.getOutletCold().getState(PRESSURE));
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
                    HeatExchanger.this.getInletCold().setProperty(PRESSURE,value);
                    return HeatExchanger.this.getInletCold();
                }
                case "p out": {
                    HeatExchanger.this.getOutletCold().setProperty(PRESSURE,value);
                    return HeatExchanger.this.getOutletCold();
                }
            }
            return null;
        }
    }
    
    /**
     * Heat exchanger effectiveness.
     */
    private class Effectiveness extends Equation{
        
        /**
         * Constructor.
         */
        private Effectiveness() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("e", HeatExchanger.this.getAttribute(EFFECTIVENESS));
            variables.put("Q actual", HeatExchanger.this.getAttribute(AHEATTRANSFER));
            variables.put("Q ideal", HeatExchanger.this.getAttribute(IHEATTRANSFER));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q ideal").getAsDouble()*variables.get("e").getAsDouble() - variables.get("Q actual").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "e": {
                    HeatExchanger.this.setAttribute(EFFECTIVENESS,value);
                    return null;
                }
                case "Q actual": {
                    HeatExchanger.this.setAttribute(AHEATTRANSFER,value);
                    return null;
                }
                case "Q ideal": {
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
    private class Energy_Balance_Hot extends Equation{
        
        /**
         * Constructor.
         */
        private Energy_Balance_Hot() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m", HeatExchanger.this.getInletHot().getMass());
            variables.put("h in", HeatExchanger.this.getInletHot().getState(ENTHALPY));
            variables.put("h out", HeatExchanger.this.getOutletHot().getState(ENTHALPY));
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
                    HeatExchanger.this.getInletHot().setMass(value);
                    return HeatExchanger.this.getInletHot();
                }
                case "h in": {
                    HeatExchanger.this.getInletHot().setProperty(ENTHALPY, value);
                    return HeatExchanger.this.getInletHot();
                }
                case "h out": {
                    HeatExchanger.this.getOutletHot().setProperty(ENTHALPY, value);
                    return HeatExchanger.this.getOutletHot();
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
    private class Energy_Balance_Cold extends Equation{
        
        /**
         * Constructor.
         */
        private Energy_Balance_Cold() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m", HeatExchanger.this.getInletCold().getMass());
            variables.put("h in", HeatExchanger.this.getInletCold().getState(ENTHALPY));
            variables.put("h out", HeatExchanger.this.getOutletCold().getState(ENTHALPY));
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
                    HeatExchanger.this.getInletCold().setProperty(ENTHALPY, value);
                    return HeatExchanger.this.getInletCold();
                }
                case "h out": {
                    HeatExchanger.this.getOutletCold().setProperty(ENTHALPY, value);
                    return HeatExchanger.this.getOutletCold();
                }
                case "Q": {
                    HeatExchanger.this.getInletCold().setMass(value);
                    return HeatExchanger.this.getInletCold();
                }
            }
            return null;
        }  
    }
    
    /**
     * Ideal heat transfer through the heat exchanger.
     */
    private class Ideal_Heat_Transfer extends Equation{
        
        /**
         * Constructor.
         */
        private Ideal_Heat_Transfer() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q ideal", HeatExchanger.this.getAttribute(IHEATTRANSFER));
            variables.put("h in hot", HeatExchanger.this.getInletHot().getState(ENTHALPY));
            variables.put("T in cold", HeatExchanger.this.getInletCold().getState(TEMPERATURE));
            variables.put("p out hot", HeatExchanger.this.getOutletHot().getState(PRESSURE));
            variables.put("m hot", HeatExchanger.this.getInletHot().getMass());
            variables.put("h in cold", HeatExchanger.this.getInletCold().getState(ENTHALPY));
            variables.put("T in hot", HeatExchanger.this.getInletHot().getState(TEMPERATURE));
            variables.put("p out cold", HeatExchanger.this.getOutletCold().getState(PRESSURE));
            variables.put("m cold", HeatExchanger.this.getInletCold().getMass());
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            
            FlowNode hotOutletMin = new FlowNode(INTERNAL);
            hotOutletMin.setFluid(HeatExchanger.this.getInletHot().getFluid().get());
            OptionalDouble Q_ideal_h2c = OptionalDouble.of(0.0);
            FlowNode coldOutletMax = new FlowNode(INTERNAL);
            coldOutletMax.setFluid(HeatExchanger.this.getInletCold().getFluid().get());
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
