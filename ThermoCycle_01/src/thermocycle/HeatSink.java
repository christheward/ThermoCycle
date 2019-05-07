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
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(getInlet(), getOutlet(), ENTHALPY, ENTROPY));
        return paths;
    }
    
    @Override
    public Set<Attributes.Attribute> getAllowableAtributes() {
        Set<Attributes.Attribute> attributes = new HashSet();
        attributes.add(PLOSS);
        return attributes;
   }
    
    /**
     * Mass balance across Heat Sink 
     */
    private class Mass_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Mass_Balance() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", HeatSink.this.getInlet().getMass());
            variables.put("m out", HeatSink.this.getOutlet().getMass());
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
        private Energy_Balance() {super(1e-3);}
        
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
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h in").getAsDouble() - variables.get("h out").getAsDouble());
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
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
                    HeatSink.this.getInlet().setProperty(ENTHALPY,value);
                    return HeatSink.this.getInlet();
                }
                case "h out": {
                    HeatSink.this.getOutlet().setProperty(ENTHALPY,value);
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
        private Pressure_Loss() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", HeatSink.this.getAttribute(PLOSS));
            variables.put("p in", HeatSink.this.getInlet().getState(PRESSURE));
            variables.put("p out", HeatSink.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p in").getAsDouble()*(1 - variables.get("pr").getAsDouble()) - variables.get("p out").getAsDouble();
        }
        
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    HeatSink.this.setAttribute(PLOSS, value);
                    return null;
                }
                case "p in": {
                    HeatSink.this.getInlet().setProperty(PRESSURE,value);
                    return HeatSink.this.getInlet();
                }
                case "p out": {
                    HeatSink.this.getOutlet().setProperty(PRESSURE,value);
                    return HeatSink.this.getOutlet();
                }
            }
            return null;
        }
    }
}
