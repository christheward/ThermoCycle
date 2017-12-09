/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import org.jfree.data.xy.DefaultXYDataset;
import static thermocycle.Properties.Property.*;
import static thermocycle.Attributes.Attribute.*;
import static thermocycle.Ports.Let.*;

/**
 *
 * @author Chris
 */
public final class Combustor extends Component {
    
    /**
     * Constructor
     * @param name Combustor name
     * @param ambient Ambient conditions
     */
    Combustor(String name, State ambient) {
        super(name, ambient);
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        heatNodes.add(new HeatNode(INLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        equations.add(new Mass_Balance());
        equations.add(new Energy_Balance());
        equations.add(new Pressure_Loss());
        createAttribute(PLOSS);
    }
    
    int plotData(DefaultXYDataset dataset, Property x, Property y) {
        return 0;
    }
    
    // getters
    FlowNode getInlet() {
        return flowNodes.get(0);
    }
    FlowNode getOutlet() {
        return flowNodes.get(1);
    }
    HeatNode getSupply() {
        return heatNodes.get(0);
    }
    
    // methods
    @Override
    double heatExergyIn() {
        return heatTransferProcessExergy(thermodynamicProcess(getInlet(), getOutlet(), ENTHALPY, PRESSURE));
    }
    
    @Override
    double heatExergyOut() {
        return 0;
    }
    
    /**
     * Mass balance across the combustor.
     */
    private class Mass_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Mass_Balance() {}
        
        @Override
        Map<String,OptionalDouble> getVariables() {
            Map<String,OptionalDouble> variables = new HashMap();
            variables.put("m in", Combustor.this.getInlet().getMass());
            variables.put("m out", Combustor.this.getOutlet().getMass());
            return variables;
        }
        
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = OptionalDouble.of(Combustor.this.getOutlet().getMass().getAsDouble());
                    break;
                }
                case "m out": {
                    value = OptionalDouble.of(Combustor.this.getInlet().getMass().getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
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
    
    private class Energy_Balance extends Equation{
        
        /**
         * Constructor
         */
        private Energy_Balance() {}
        
        @Override
        Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("Q", Combustor.this.getSupply().getHeat());
            variables.put("m", Combustor.this.getInlet().getMass());
            variables.put("h in", Combustor.this.getInlet().getState(ENTHALPY));
            variables.put("h out", Combustor.this.getOutlet().getState(ENTHALPY));
            return variables;
        }
        
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "Q": {
                    value = OptionalDouble.of(Combustor.this.getInlet().getMass().getAsDouble() * (Combustor.this.getOutlet().getState(ENTHALPY).getAsDouble() - Combustor.this.getInlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "m": {
                    value = OptionalDouble.of(Combustor.this.getSupply().getHeat().getAsDouble() / (Combustor.this.getOutlet().getState(ENTHALPY).getAsDouble() - Combustor.this.getInlet().getState(ENTHALPY).getAsDouble()));
                    break;
                }
                case "h in": {
                    value = OptionalDouble.of(Combustor.this.getOutlet().getState(ENTHALPY).getAsDouble() - (Combustor.this.getSupply().getHeat().getAsDouble() / Combustor.this.getInlet().getMass().getAsDouble()));
                    break;}
                case "h out": {
                    value = OptionalDouble.of(Combustor.this.getInlet().getState(ENTHALPY).getAsDouble() + (Combustor.this.getSupply().getHeat().getAsDouble() / Combustor.this.getInlet().getMass().getAsDouble()));
                    break;
                }
            }
            return value;
        }
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "Q": {
                    Combustor.this.getSupply().setHeat(value);
                    return Combustor.this.getSupply();}
                case "m": {
                    Combustor.this.getInlet().setMass(value);
                    return Combustor.this.getInlet();}
                case "h in": {
                    Combustor.this.getInlet().setState(ENTHALPY,value);
                    return Combustor.this.getInlet();}
                case "h out": {
                    Combustor.this.getOutlet().setState(ENTHALPY,value);
                    return Combustor.this.getOutlet();}
            }
            return null;
        }
    }
    
    private class Pressure_Loss extends Equation{
        private Pressure_Loss() {}
        @Override
        Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("pr", Combustor.this.getAttribute(PLOSS));
            variables.put("p in", Combustor.this.getInlet().getState(PRESSURE));
            variables.put("p out", Combustor.this.getOutlet().getState(PRESSURE));
            return variables;
        }
        @Override
        OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            OptionalDouble pAmbient = Combustor.this.getAmbient(PRESSURE);
            switch (variable) {
                case "pr": {
                    value = OptionalDouble.of(1 - (Combustor.this.getOutlet().getState(PRESSURE).getAsDouble() / Combustor.this.getInlet().getState(PRESSURE).getAsDouble()));
                    break;}
                case "p in": {
                    value = OptionalDouble.of(Combustor.this.getOutlet().getState(PRESSURE).getAsDouble() / (1 - Combustor.this.getAttribute(PLOSS).getAsDouble()));
                    break;}
                case "p out": {
                    value = OptionalDouble.of(Combustor.this.getInlet().getState(PRESSURE).getAsDouble() * (1 - Combustor.this.getAttribute(PLOSS).getAsDouble()));
                    break;}
            }
            return value;
        }
        @Override
        Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "pr": {
                    Combustor.this.setAttribute(PLOSS, value);
                    return null;
                }
                case "p in": {
                    Combustor.this.getInlet().setState(PRESSURE,value);
                    return Combustor.this.getInlet();
                }
                case "p out": {
                    Combustor.this.getOutlet().setState(PRESSURE,value);
                    return Combustor.this.getOutlet();
                }
            }
            return null;
        }
    }
}
