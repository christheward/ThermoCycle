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
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        heatNodes.add(new HeatNode(OUTLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Loss());
        createAttribute(PLOSS);
    }
    
    /**
     * Gets the heat sink inlet.
     * @return Returns the inlet flow node.
     */
    public FlowNode getInlet() {
        return flowNodes.get(0);
    }
    
    /**
     * Gets the heat sink outlet.
     * @return Returns the outlet flow node.
     */
    public FlowNode getOutlet() {
        return flowNodes.get(1);
    }
    
    /**
     * Gets the heat sink heat lost.
     * @return Returns the outlet heat node.
     */
    public HeatNode getSink() {
        return heatNodes.get(0);
    }
    
    @Override
    protected double heatExergyIn() {
        return 0;
    }
    @Override
    protected double heatExergyOut() {
        return heatTransferProcessExergy(thermodynamicProcess(getInlet(), getOutlet(), ENTHALPY, PRESSURE));
    }
    
    /**
     * Mass balance across Heat Sink 
     */
    private class Mass_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Mass_Balance() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatSink.this.getInlet().getMass());
            variables.put("m out", HeatSink.this.getOutlet().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = HeatSink.this.getOutlet().getMass();
                    break;
                }
                case "m out": {
                    value = HeatSink.this.getInlet().getMass();
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "m in": {
                    HeatSink.this.getInlet().setMass(value);
                    return HeatSink.this.getInlet();
                }
                case "m out": {
                    HeatSink.this.getOutlet().setMass(value);
                    return HeatSink.this.getOutlet();
                }
            }
            return null;
        }  
    }
    
    /**
     * Heat  Sink energy balance
     */
    private class Energy_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Energy_Balance() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q", HeatSink.this.getSink().getHeat());
            variables.put("m", HeatSink.this.getInlet().getMass());
            variables.put("h in", HeatSink.this.getInlet().getState(ENTHALPY));
            variables.put("h out", HeatSink.this.getOutlet().getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "Q": {
                    value = OptionalDouble.of(HeatSink.this.getInlet().getMass().getAsDouble() * (HeatSink.this.getInlet().getState(ENTHALPY).getAsDouble() - HeatSink.this.getOutlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "m": {
                    if (!HeatSink.this.getSink().getHeat().equals(Double.valueOf(0))) {
                        value = OptionalDouble.of(HeatSink.this.getSink().getHeat().getAsDouble() / (HeatSink.this.getInlet().getState(ENTHALPY).getAsDouble() - (HeatSink.this.getOutlet().getState(ENTHALPY).getAsDouble())));
                    }
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of(HeatSink.this.getOutlet().getState(ENTHALPY).getAsDouble() + (HeatSink.this.getSink().getHeat().getAsDouble() / (HeatSink.this.getInlet().getMass().getAsDouble())));
                    break;
                }
                case "h out": {
                    value = OptionalDouble.of(HeatSink.this.getInlet().getState(ENTHALPY).getAsDouble() - (HeatSink.this.getSink().getHeat().getAsDouble() / (HeatSink.this.getInlet().getMass().getAsDouble())));
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "Q": {
                    HeatSink.this.getSink().setHeat(value);
                    return HeatSink.this.getSink();
                }
                case "m": {
                    HeatSink.this.getInlet().setMass(value);
                    return HeatSink.this.getInlet();
                }
                case "h in": {
                    HeatSink.this.getInlet().setState(ENTHALPY,value);
                    return HeatSink.this.getInlet();
                }
                case "h out": {
                    HeatSink.this.getOutlet().setState(ENTHALPY,value);
                    return HeatSink.this.getOutlet();
                }
            }
            return null;
        }
    }
    
    /**
     * Pressure loss across heat sink
     */
    private class Pressure_Loss extends Equation{
        
        /**
         * Constructor
         */
        private Pressure_Loss() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", HeatSink.this.getAttribute(PLOSS));
            variables.put("p in", HeatSink.this.getInlet().getState(PRESSURE));
            variables.put("p out", HeatSink.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "pr": {
                    value = OptionalDouble.of(1 - (HeatSink.this.getOutlet().getState(PRESSURE).getAsDouble() / HeatSink.this.getInlet().getState(PRESSURE).getAsDouble()));
                    break;
                }
                case "p in": {
                    value = OptionalDouble.of((HeatSink.this.getOutlet().getState(PRESSURE).getAsDouble() / (1 - HeatSink.this.getAttribute(PLOSS).getAsDouble())));
                    break;
                }
                case "p out": {
                    value = OptionalDouble.of((HeatSink.this.getInlet().getState(PRESSURE).getAsDouble() * (1 - HeatSink.this.getAttribute(PLOSS).getAsDouble())));
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "pr": {
                    HeatSink.this.setAttribute(PLOSS, value);
                    return null;
                }
                case "p in": {
                    HeatSink.this.getInlet().setState(PRESSURE,value);
                    return HeatSink.this.getInlet();
                }
                case "p out": {
                    HeatSink.this.getOutlet().setState(PRESSURE,value);
                    return HeatSink.this.getOutlet();
                }
            }
            return null;
        }
    }
}
