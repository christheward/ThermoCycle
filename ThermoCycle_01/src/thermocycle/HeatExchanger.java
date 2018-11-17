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
        createAttribute(EFFECTIVENESS);
        createAttribute(AHEATTRANSFER);
        createAttribute(IHEATTRANSFER);
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
    
    /**
     * Mass balance on the hot side of the heat exchanger.
     */
    private class Mass_Balance_Hot extends Equation{
        
        /**
         * Constructor.
         */
        private Mass_Balance_Hot() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatExchanger.this.getInletHot().getMass());
            variables.put("m out", HeatExchanger.this.getOutletHot().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = OptionalDouble.of(HeatExchanger.this.getOutletHot().getMass().getAsDouble());
                    break;
                }
                case "m out": {
                    value = OptionalDouble.of(HeatExchanger.this.getInletHot().getMass().getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
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
        private Mass_Balance_Cold() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatExchanger.this.getInletCold().getMass());
            variables.put("m out", HeatExchanger.this.getOutletCold().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = OptionalDouble.of(HeatExchanger.this.getOutletCold().getMass().getAsDouble());
                    break;
                }
                case "m out": {
                    value = OptionalDouble.of(HeatExchanger.this.getInletCold().getMass().getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
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
        private Pressure_Loss_Hot() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("p in", HeatExchanger.this.getInletHot().getState(PRESSURE));
            variables.put("p out", HeatExchanger.this.getOutletHot().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "p in": {
                    value = OptionalDouble.of(HeatExchanger.this.getOutletHot().getState(PRESSURE).getAsDouble());
                    break;
                }
                case "p out": {
                    value = OptionalDouble.of(HeatExchanger.this.getInletHot().getState(PRESSURE).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "p in": {
                    HeatExchanger.this.getInletHot().setState(PRESSURE,value);
                    return HeatExchanger.this.getInletHot();
                }
                case "p out": {
                    HeatExchanger.this.getOutletHot().setState(PRESSURE,value);
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
        private Pressure_Loss_Cold() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("p in", HeatExchanger.this.getInletCold().getState(PRESSURE));
            variables.put("p out", HeatExchanger.this.getOutletCold().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "p in": {
                    value = OptionalDouble.of(HeatExchanger.this.getOutletCold().getState(PRESSURE).getAsDouble());
                    break;
                }
                case "p out": {
                    value = OptionalDouble.of(HeatExchanger.this.getInletCold().getState(PRESSURE).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "p in": {
                    HeatExchanger.this.getInletCold().setState(PRESSURE,value);
                    return HeatExchanger.this.getInletCold();
                }
                case "p out": {
                    HeatExchanger.this.getOutletCold().setState(PRESSURE,value);
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
        private Effectiveness() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("e", HeatExchanger.this.getAttribute(EFFECTIVENESS));
            variables.put("Q actual", HeatExchanger.this.getAttribute(AHEATTRANSFER));
            variables.put("Q ideal", HeatExchanger.this.getAttribute(IHEATTRANSFER));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "e": {
                    value = OptionalDouble.of(HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble() / HeatExchanger.this.getAttribute(IHEATTRANSFER).getAsDouble());
                    break;
                }
                case "Q actual": {
                    value = OptionalDouble.of(HeatExchanger.this.getAttribute(IHEATTRANSFER).getAsDouble() * HeatExchanger.this.getAttribute(EFFECTIVENESS).getAsDouble());
                    break;
                }
                case "Q ideal": {
                    value = OptionalDouble.of(HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble() / HeatExchanger.this.getAttribute(EFFECTIVENESS).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
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
        private Energy_Balance_Hot() {}
        
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
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m": {
                    value = OptionalDouble.of(HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble() / (HeatExchanger.this.getInletHot().getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getOutletHot().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of((HeatExchanger.this.getOutletHot().getState(ENTHALPY).getAsDouble() + HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble()) / HeatExchanger.this.getInletHot().getMass().getAsDouble());
                    break;
                }
                case "h out": {
                    value = OptionalDouble.of((HeatExchanger.this.getInletHot().getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble()) / HeatExchanger.this.getInletHot().getMass().getAsDouble());
                    break;
                }
                case "Q": {
                    value = OptionalDouble.of(HeatExchanger.this.getInletHot().getMass().getAsDouble() * (HeatExchanger.this.getInletHot().getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getOutletHot().getState(ENTHALPY).getAsDouble()));
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "m": {
                    HeatExchanger.this.getInletHot().setMass(value);
                    return HeatExchanger.this.getInletHot();
                }
                case "h in": {
                    HeatExchanger.this.getInletHot().setState(ENTHALPY, value);
                    return HeatExchanger.this.getInletHot();
                }
                case "h out": {
                    HeatExchanger.this.getOutletHot().setState(ENTHALPY, value);
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
        private Energy_Balance_Cold() {}
        
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
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m": {
                    value = OptionalDouble.of(HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble() / (HeatExchanger.this.getOutletCold().getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getInletCold().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of((HeatExchanger.this.getOutletCold().getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble() / HeatExchanger.this.getInletCold().getMass().getAsDouble()));
                    break;
                }
                case "h out": {
                    value = OptionalDouble.of((HeatExchanger.this.getInletCold().getState(ENTHALPY).getAsDouble() + HeatExchanger.this.getAttribute(AHEATTRANSFER).getAsDouble() / HeatExchanger.this.getInletCold().getMass().getAsDouble()));
                    break;
                }
                case "Q": {
                    value = OptionalDouble.of(HeatExchanger.this.getInletCold().getMass().getAsDouble() * (HeatExchanger.this.getOutletCold().getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getInletCold().getState(ENTHALPY).getAsDouble()));
                    break;
                }

            }
            return value;
        }
                
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "m": {
                    HeatExchanger.this.setAttribute(AHEATTRANSFER, value);
                    return null;
                }
                case "h in": {
                    HeatExchanger.this.getInletCold().setState(ENTHALPY, value);
                    return HeatExchanger.this.getInletCold();
                }
                case "h out": {
                    HeatExchanger.this.getOutletCold().setState(ENTHALPY, value);
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
        private Ideal_Heat_Transfer() {}
        
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
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            FlowNode hotOutletMin = new FlowNode(INTERNAL);
            hotOutletMin.setFluid(HeatExchanger.this.getInletHot().getFluid());
            OptionalDouble Q_ideal_h2c = OptionalDouble.of(0.0);
            FlowNode coldOutletMax = new FlowNode(INTERNAL);
            coldOutletMax.setFluid(HeatExchanger.this.getInletCold().getFluid());
            OptionalDouble Q_ideal_c2h = OptionalDouble.of(0.0);
            
            if (!variable.equals("T in cold") && !variable.equals("p out hot")) {
                hotOutletMin.setState(TEMPERATURE, HeatExchanger.this.getInletCold().getState(TEMPERATURE));
                hotOutletMin.setState(PRESSURE, HeatExchanger.this.getOutletHot().getState(PRESSURE));
                if (!variable.equals("h in hot") && !variable.equals("m hot")) {
                    Q_ideal_h2c = OptionalDouble.of(HeatExchanger.this.getInletHot().getMass().getAsDouble() * (HeatExchanger.this.getInletHot().getState(ENTHALPY).getAsDouble() - hotOutletMin.getState(ENTHALPY).getAsDouble()));
                }
            }
            if (!variable.equals("T in hot") && !variable.equals("p out cold")) {
                coldOutletMax.setState(TEMPERATURE, HeatExchanger.this.getInletHot().getState(TEMPERATURE));
                coldOutletMax.setState(PRESSURE, HeatExchanger.this.getOutletCold().getState(PRESSURE));
                if (!variable.equals("h in cold") && !variable.equals("m cold")) {
                    Q_ideal_c2h = OptionalDouble.of(HeatExchanger.this.getInletCold().getMass().getAsDouble() * (coldOutletMax.getState(ENTHALPY).getAsDouble() - HeatExchanger.this.getInletCold().getState(ENTHALPY).getAsDouble()));
                }
            }
            
            switch (variable) {
                case "Q ideal":
                    value = OptionalDouble.of(Math.min(Q_ideal_h2c.getAsDouble(),Q_ideal_c2h.getAsDouble()));
                    break;
                case "h in hot":
                    if (Q_ideal_c2h.getAsDouble() > HeatExchanger.this.getAttribute(IHEATTRANSFER).getAsDouble()) {
                        // hot side limits heat transfer
                        value = OptionalDouble.of(hotOutletMin.getState(ENTHALPY).getAsDouble() + (HeatExchanger.this.getAttribute(IHEATTRANSFER).getAsDouble() / HeatExchanger.this.getInletHot().getMass().getAsDouble()));
                    }
                    break;
                case "h in cold":
                    if (Q_ideal_h2c.getAsDouble() > HeatExchanger.this.getAttribute(IHEATTRANSFER).getAsDouble()) {
                        // cold side limits heat transfer
                        value = OptionalDouble.of(coldOutletMax.getState(ENTHALPY).getAsDouble() - (HeatExchanger.this.getAttribute(IHEATTRANSFER).getAsDouble() / HeatExchanger.this.getInletCold().getMass().getAsDouble()));
                    }
                    break;
                default: {
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
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
