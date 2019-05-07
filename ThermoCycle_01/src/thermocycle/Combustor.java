/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Properties.Property.*;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Node.Port.*;

/**
 *
 * @author Chris
 */
public final class Combustor extends Component {
    
    /**
     * Constructor.
     * @param name Combustor name.
     * @param ambient Ambient conditions.
     */
    protected Combustor(String name, State ambient) {
        super(name, ambient);
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        heatNodes.add(new HeatNode(INLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Loss());
    }
    
    /**
     * Gets the combustor inlet.
     * @return Returns the inlet flow node.
     */
    public FlowNode getInlet() {
        return flowNodes.get(0);
    }
    /**
     * Gets the combustor outlet.
     * @return Returns the outlet flow node.
     */
    public FlowNode getOutlet() {
        return flowNodes.get(1);
    }
    /**
     * Gets the combustor heat input.
     * @return Returns the inlet heat node.
     */
    public HeatNode getSupply() {
        return heatNodes.get(0);
    }
    
    @Override
    protected double heatExergyIn() {
        return heatTransferProcessExergy(thermodynamicProcess(getInlet(), getOutlet(), ENTHALPY, PRESSURE));
    }
    
    @Override
    protected double heatExergyOut() {
        return 0;
    }
    
    @Override
    protected List<List<FlowNode>> plotData() {
        List paths = new ArrayList();
        paths.add(thermodynamicProcess(getInlet(), getOutlet(), ENTHALPY, PRESSURE));
        return paths;
    }
    
    @Override
    public Set<Attributes.Attribute> getAllowableAtributes() {
        Set<Attributes.Attribute> attributes = new HashSet();
        attributes.add(PLOSS);
        return attributes;
   }
    
    /**
     * Mass balance across the combustor.
     */
    private class Mass_Balance extends Equation{
                
        /**
         * Constructor.
         */
        private Mass_Balance() {super(1e-3);}
        
        @Override
        protected Map<String,OptionalDouble> getVariables() {
            Map<String,OptionalDouble> variables = new HashMap();
            variables.put("m in", Combustor.this.getInlet().getMass());
            variables.put("m out", Combustor.this.getOutlet().getMass());
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
                    Combustor.this.getInlet().setMass(value);
                    return Combustor.this.getInlet();
                }
                case "m out": {
                    Combustor.this.getOutlet().setMass(value);
                    return Combustor.this.getOutlet();
                }
            }
            return null;
        }
    }
    
    /**
     * Energy balance across the combustor.
     */
    private class Energy_Balance extends Equation{
        
        /**
         * Constructor.
         */
        private Energy_Balance() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q", Combustor.this.getSupply().getHeat());
            variables.put("m", Combustor.this.getInlet().getMass());
            variables.put("h in", Combustor.this.getInlet().getState(ENTHALPY));
            variables.put("h out", Combustor.this.getOutlet().getState(ENTHALPY));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("Q").getAsDouble() - variables.get("m").getAsDouble()*(variables.get("h out").getAsDouble() - variables.get("h in").getAsDouble());
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "Q": {
                    Combustor.this.getSupply().setHeat(value);
                    return Combustor.this.getSupply();}
                case "m": {
                    Combustor.this.getInlet().setMass(value);
                    return Combustor.this.getInlet();}
                case "h in": {
                    Combustor.this.getInlet().setProperty(ENTHALPY,value);
                    return Combustor.this.getInlet();}
                case "h out": {
                    Combustor.this.getOutlet().setProperty(ENTHALPY,value);
                    return Combustor.this.getOutlet();}
            }
            return null;
        }
    }
    
    /**
     * Pressure loss across the combustor.
     */
    private class Pressure_Loss extends Equation{
        
        /**
         * Constructor.
         */
        private Pressure_Loss() {super(1e-3);}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Combustor.this.getAttribute(PLOSS));
            variables.put("p in", Combustor.this.getInlet().getState(PRESSURE));
            variables.put("p out", Combustor.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        
        @Override
        protected Double function(Map<String, OptionalDouble> variables) {
            return variables.get("p in").getAsDouble()*(1-variables.get("pr").getAsDouble()) - variables.get("p out").getAsDouble();
        }
                
        @Override
        protected Node saveVariable(String variable, Double value) {
            switch (variable) {
                case "pr": {
                    Combustor.this.setAttribute(PLOSS, value);
                    return null;
                }
                case "p in": {
                    Combustor.this.getInlet().setProperty(PRESSURE,value);
                    return Combustor.this.getInlet();
                }
                case "p out": {
                    Combustor.this.getOutlet().setProperty(PRESSURE,value);
                    return Combustor.this.getOutlet();
                }
            }
            return null;
        }
    }
}
