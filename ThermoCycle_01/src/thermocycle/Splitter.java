/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.*;
import static thermocycle.Attributes.Attribute.SPLIT;
import static thermocycle.Node.Port.*;
import static thermocycle.Properties.Property.*;


/**
 *
 * @author Chris
 */
final class Splitter extends Component {
    
    /**
     * Constructor
     * @param name The name of the component.
     * @param ambient The ambient state of the component.
     */
    protected Splitter(String name, State ambient){
        super(name, ambient);
        flowNodes.add(new FlowNode(INLET));
        flowNodes.add(new FlowNode(OUTLET));
        flowNodes.add(new FlowNode(OUTLET));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(1)));
        internals.add(new Connection(flowNodes.get(0),flowNodes.get(2)));
        createAttribute(SPLIT);
    }
    
    /**
     * Gets the splitter inlet.
     * @return Return the inlet flow node.
     */
    public FlowNode getInlet() {
        return flowNodes.get(0);
    }
    
    /**
     * Gets the first splitter outlet.
     * @return Returns the first outlet flow node.
     */
    public FlowNode getOutlet1() {
        return flowNodes.get(1);
    }
    
    /**
     * Gets the second splitter outlet.
     * @return Returns the second outlet flow node.
     */
    public FlowNode getOutlet2() {
        return flowNodes.get(2);
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
        paths.add(thermodynamicProcess(getInlet(), getOutlet1(), ENTHALPY, ENTROPY));
        paths.add(thermodynamicProcess(getInlet(), getOutlet2(), ENTHALPY, ENTROPY));
        return paths;
    }
    
    /**
     * Mass balance across the splitter.
     */
    private class Eqn_Mass extends Equation{
        
        /**
         * Constructor.
         */
        private Eqn_Mass() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("m in", Splitter.this.getInlet().getMass());
            variables.put("m out 1", Splitter.this.getOutlet1().getMass());
            variables.put("m out 2", Splitter.this.getOutlet2().getMass());
            return variables;
        }
                
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "m in": {
                    value = OptionalDouble.of(Splitter.this.getOutlet1().getMass().getAsDouble() + Splitter.this.getOutlet2().getMass().getAsDouble());
                    break;
                }
                case "m out 1": {
                    value = OptionalDouble.of(Splitter.this.getInlet().getMass().getAsDouble() - Splitter.this.getOutlet2().getMass().getAsDouble());
                    break;
                }
                case "m out 2": {
                    value = OptionalDouble.of(Splitter.this.getInlet().getMass().getAsDouble() - Splitter.this.getOutlet1().getMass().getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "m in": {
                    Splitter.this.getInlet().setMass(value);
                    return Splitter.this.getInlet();
                }
                case "m out 1": {
                    Splitter.this.getOutlet1().setMass(value);
                    return Splitter.this.getOutlet1();
                }
                case "m out 2": {
                    Splitter.this.getOutlet2().setMass(value); return Splitter.this.getOutlet2();
                }
            }
            return null;
        }
    }
    
    /**
     * Mass split across the first branch in splitter.
     */
    private class Eqn_Split1 extends Equation{
        
        /**
         * Constructor.
         */
        private Eqn_Split1() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("split", Splitter.this.getAttribute(SPLIT));
            variables.put("m in", Splitter.this.getInlet().getMass());
            variables.put("m out 1", Splitter.this.getOutlet1().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "split": {
                    value = OptionalDouble.of(Splitter.this.getOutlet1().getMass().getAsDouble() / Splitter.this.getInlet().getMass().getAsDouble());
                    break;}
                case "m in": {
                    value = OptionalDouble.of(Splitter.this.getOutlet1().getMass().getAsDouble() / Splitter.this.getAttribute(SPLIT).getAsDouble());
                    break;
                }
                case "m out 1": {
                    value = OptionalDouble.of(Splitter.this.getInlet().getMass().getAsDouble() * Splitter.this.getAttribute(SPLIT).getAsDouble());
                    break;
                }
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "split": {
                    Splitter.this.setAttribute(SPLIT, value);
                    return null;
                }
                case "m in": {
                    Splitter.this.getInlet().setMass(value);
                    return Splitter.this.getInlet();
                }
                case "m out 1": {
                    Splitter.this.getOutlet1().setMass(value);
                    return Splitter.this.getOutlet1();
                }
            }
            return null;
        }
    }
    
    /**
     * Mass split across the second branch in the splitter.
     */
    private class Eqn_Split2 extends Equation{
        
        /**
         * Constructor.
         */
        private Eqn_Split2() {}
        
        @Override
        protected Map<String, OptionalDouble> getVariables() {
            Map<String, OptionalDouble> variables = new HashMap();
            variables.put("split", Splitter.this.getAttribute(SPLIT));
            variables.put("m in", Splitter.this.getInlet().getMass());
            variables.put("m out 2", Splitter.this.getOutlet2().getMass());
            return variables;
        }
        
        @Override
        protected OptionalDouble solveVariable(String variable) {
            OptionalDouble value = OptionalDouble.empty();
            switch (variable) {
                case "split": {
                    value = OptionalDouble.of((1 - Splitter.this.getOutlet2().getMass().getAsDouble()) / Splitter.this.getInlet().getMass().getAsDouble());
                    break;
                }
                case "m in": {
                    value = OptionalDouble.of(Splitter.this.getOutlet2().getMass().getAsDouble()  / (1 - Splitter.this.getAttribute(SPLIT).getAsDouble()));
                    break;
                }
                case "mc out 2": {
                    value = OptionalDouble.of(Splitter.this.getInlet().getMass().getAsDouble() * (1 - Splitter.this.getAttribute(SPLIT).getAsDouble()));
                    break;}
            }
            return value;
        }
        
        @Override
        protected Node saveVariable(String variable, OptionalDouble value) {
            switch (variable) {
                case "split": {
                    Splitter.this.setAttribute(SPLIT, value);
                    return null;
                }
                case "m in": {
                    Splitter.this.getInlet().setMass(value);
                    return Splitter.this.getInlet();
                }
                case "m out 2": {
                    Splitter.this.getOutlet1().setMass(value);
                    return Splitter.this.getOutlet1();
                
                }
            }
            return null;
        }
    }
}