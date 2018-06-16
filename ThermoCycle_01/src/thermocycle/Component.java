/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.text.*;
import java.util.*;
import java.io.Serializable;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import static thermocycle.Properties.Property.*;
import static thermocycle.Node.Port.*;

/**
 *
 * @author Chris
 */
public abstract class Component implements Attributes, Properties, Serializable {
    
    /**
     * The number of internal states used for plotting and exergy calculations.
     */
    static int nIntStates = 100;
    
    /**
     * The component name.
     */
    protected String name;
    
    /**
     * The components ambient reference state. 
     */
    protected final State ambient;
    
    /**
     * A list of all the internal connections within the component.
     */
    protected final List<Connection<FlowNode>> internals;
    
    /**
     * A list of all the equations relating to this component.
     */
    protected final List<Equation> equations;
    
    /**
     * A list of all the flow nodes in this component.
     */
    public final List<FlowNode> flowNodes;
    
    /**
     * A list of all the work nodes in this component.
     */
    public final List<WorkNode> workNodes;
    
    /**
     * A list of all the heat nodes in this component.
     */
    public final List<HeatNode> heatNodes;
    
    /**
     * A map of all this components attributes.
     */
    public final Map<Attribute, OptionalDouble> attributes;
    
    /**
     * Constructor
     * @param name The name of thee new component
     * @param ambient The ambient state of thee component.s
     */
    protected Component(String name, State ambient) {
        this.name = name;
        this.ambient = ambient;
        flowNodes = new ArrayList();
        workNodes = new ArrayList();
        heatNodes = new ArrayList();
        internals = new ArrayList();
        equations = new ArrayList();
        attributes = new HashMap();
    }
    
    /**
     * Clears all the values in the attribute and state values for the component.
     */
    /**
    protected final void clear() {
        attributes.keySet().stream().forEach(a -> {
            attributes.put(a, OptionalDouble.empty());
        });
        getNodes().stream().forEach(n -> {
            n.clear();
        });
    }
    */
    
    /**
     * Resets all the component's equations to unsolved for you with parametric studies.
     */
    protected final void reset() {
        equations.stream().forEach(e -> {
            e.reset();
        });
    }
    
    /**
     * Gets the ambient state property for this component.
     * @param property The property to get.
     * @return Returns the value of the property.
     */
    protected final OptionalDouble getAmbient(Property property) {
        return ambient.get(property);
    }
    
    /**
     * Gets all the inlet flow nodes for this component.
     * @return Returns a list of the inlet flow nodes.
     */
    private List<FlowNode> getFlowInlets() {
        return flowNodes.stream().filter(n -> n.port.equals(INLET)).collect(Collectors.toList());
    }

    /**
     * Gets all the outlet flow nodes for this component.
     * @return Returns a list of the outlet flow nodes.
     */
    private List<FlowNode> getFlowOutlets() {
        return flowNodes.stream().filter(n -> n.port.equals(OUTLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets all the inlet heat nodes for this component.
     * @return Returns a list of the inlet heat nodes.
     */
    private List<HeatNode> getHeatInlets() {
        return heatNodes.stream().filter(n -> n.port.equals(INLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets all the outlet heat nodes for this component.
     * @return Returns a list of the outlet heat nodes.
     */
    private List<HeatNode> getHeatOutlets() {
        return heatNodes.stream().filter(n -> n.port.equals(OUTLET)).collect(Collectors.toList());
    }

    /**
     * Gets all the inlet work nodes for this component.
     * @return Returns a list of the inlet work nodes.
     */
    private List<WorkNode> getWorkInlets() {
        return workNodes.stream().filter(n -> n.port.equals(INLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets all the outlet work nodes for this component.
     * @return Returns a list of the outlet work nodes.
     */
    private List<WorkNode> getWorkOutlets() {
        return workNodes.stream().filter(n -> n.port.equals(OUTLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets a list of all the nodes for this component
     * @return Returns a list of the nodes.
     */
    private final List<Node> getNodes() {
        List<Node> nodes = new ArrayList();
        nodes.addAll(flowNodes);
        nodes.addAll(workNodes);
        nodes.addAll(heatNodes);
        return nodes;
    }
    
    /**
     * Gets the value of the specified attribute.
     * @param name The name of the required attribute.
     * @return Returns the value of the attribute.
     */
    protected final OptionalDouble getAttribute(Attribute name) {
        return attributes.get(name);
    }
    
    /**
     * Gets the set of attributes for the component.
     * @return A set of valid attributes.
     */
    protected final Set<Attribute> getAtributes() {
        return attributes.keySet();
    }
    
    /**
     * Creates an attribute.
     * @param name The name of the attribute to create.
     */
    protected final void createAttribute(Attribute name) {
        attributes.put(name, OptionalDouble.empty());
    };
    
    /**
     * Sets the value of the attribute.
     * @param name The name of the attribute to set.
     * @param value The value to set the attribute to.
     * @throws IllegalArgumentException if thee attribute is not valid.
     * @throws IllegalStateException if thee attribute has already been set.
     */
    protected final void setAttribute(Attribute name, OptionalDouble value) {
        if (!attributes.containsKey(name)) {
            throw new IllegalArgumentException(name + " is not a valid attribute for component type " + this.getClass().getSimpleName());
        }
        if (attributes.get(name).isPresent()) {
            throw new IllegalStateException(name + " has already been set in component " + this.name);
        }
        attributes.put(name, value);
    }
   
    /**
     * Checks to see if the component is complete. A component is complete if all its nodes are complete and equations have been checked for compatibility.
     * @return Returns True if all Nodes associated with the component are complete.
     */
    protected final boolean isComplete() {
        if (getNodes().stream().allMatch(n -> n.isComplete())) {
            if (equations.stream().allMatch(equation -> equation.isSolved())) {
                return true;
            }
        }
        //return getNodes().stream().allMatch(n -> n.isComplete());
        return false;
    }
    
    /**
     * Checks to see if the component is compatible. A component is compatible if all its equations are compatible.
     * @return Returns true is the component is compatible.
     */
    protected final boolean isCompatible() {
        equations.stream().forEach(equation -> {
            if (!equation.isCompatible()) {
                System.out.println("Incompatible equation: " + equation.getClass().getSimpleName());
            }
        });
        return equations.stream().allMatch(equation -> equation.isCompatible());
    }
    
    /**
     * Updates the component.
     * @return Returns a list of all Nodes that are updated during the computation
     */
    protected final Set<Node> update() {
        System.out.println("Solving " + this.getClass().getName());
        Set<Node> updatedNodes = new HashSet();
        while (updatedNodes.addAll(solve())) {}                          // keep computing until no nodes are updated
        return updatedNodes;
    }
    
    /**
     * Solves the component's equations to find any unknowns.
     * @return Returns any Nodes that are updated during the computation.
     */
    private final Set<Node> solve() {
        Set<Node> updatedNodes = new HashSet();
        equations.stream().filter(equation -> !equation.isSolved()).forEach(equation -> {
            updatedNodes.add(equation.solve());
        });
        return updatedNodes;
    }

    /**
     * Get the net exergy change across the component.
     * @return Returns the net exergy change across the component.
     */
    private final double exergyLoss() {
        return ((flowExergyIn() + workIn() + heatExergyIn()) - (flowExergyOut() - workOut() - heatExergyOut()));
    }
    
    /**
     * Get the total exergy input to the component through fluid transport.
     * @return Returns the total exergy input via fluid transport.
     */
    private double flowExergyIn() {
        double E = 0;
        State dead = new State();
        for (FlowNode n : getFlowInlets()) {
            dead.clear();
            dead.putIfAbsent(ambient);
            n.getFluid().computeState(dead);
            E = E + n.getMass().getAsDouble() * ((n.getState(ENTHALPY).getAsDouble() - dead.get(ENTHALPY).getAsDouble()) - (dead.get(TEMPERATURE).getAsDouble() * (n.getState(ENTROPY).getAsDouble() - dead.get(ENTROPY).getAsDouble())));
        }
        return E;
    }
    
    /**
     * Get the total exergy output from the component through fluid transport.
     * @return Returns the total exergy output via fluid transport.
     */
    private double flowExergyOut() {
        double E = 0;
        State dead = new State();
        for (FlowNode n : getFlowOutlets()) {
            dead.clear();
            dead.putIfAbsent(ambient);
            n.getFluid().computeState(dead);
            E = E + n.getMass().getAsDouble() * ((n.getState(ENTHALPY).getAsDouble() - dead.get(ENTHALPY).getAsDouble()) - (dead.get(TEMPERATURE).getAsDouble() * (n.getState(ENTROPY).getAsDouble() - dead.get(ENTROPY).getAsDouble())));
        }
        return E;
    }
    
    /**
     * Get the total exergy input to the component due to heat transfer.
     * @return Returns the total exergy input via heat transfer.
     */
    protected abstract double heatExergyIn();
    
    /**
     * Get the total exergy output from the component due to heat transfer.
     * @return Returns the total exergy output via heat transfer.
     */
    protected abstract double heatExergyOut();
    
    /**
     * Get the total heat input to the component. Currently assumes that all heat values are present.
     * @return Returns the total heat input.
     */
    protected final double heatIn() {
        double Q = 0;
        for (HeatNode n : getHeatInlets()) {
            Q = Q + n.getHeat().getAsDouble();}
        return Q;
    }
    
    /**
     * Get the total heat output from the component. Currently assumes that all heat values are present.
     * @return Returns the total heat output.
     */
    protected final double heatOut() {
        double Q = 0;
        for (HeatNode n : getHeatOutlets()) {
            Q = Q + n.getHeat().getAsDouble();
        }
        return Q;
    }
    
    /**
     * Get the total mass input to the component. Currently assumes that all mass values are present.
     * @return Returns the total mass input.
     */
    private double massIn() {
        double m = 0;
        for (FlowNode n : getFlowInlets()) {
            m = m + n.getMass().getAsDouble();
        }
        return m;
    }
    
    /**
     * Get the total mass output from the component. Currently assumes that all mass values are present.
     * @return Returns the total mass output.
     */
    private double massOut() {
        double m = 0;
        for (FlowNode n : getFlowOutlets()) {
            m = m + n.getMass().getAsDouble();
        }
        return m;
    }
    
    /**
     * Get the total work input to the component. Currently assumes that all work values are present.
     * @return Returns the total work input.
     */
    protected double workIn() {
        double W = 0;
        for (WorkNode n : getWorkInlets()) {
            W = W + n.getWork().getAsDouble();
        }
        return W;
    }
    
    /**
     * Get the total work output from the component. Currently assumes that all work values are present.
     * @return Returns the total work output.
     */
    protected double workOut() {
        double W = 0;
        for (WorkNode n : getWorkOutlets()) {
            W = W + n.getWork().getAsDouble();
        }
        return W;
    }
    
    /**
     * Creates a set of INTERNAL FlowNodes nodes that describe a the thermodynamic process between two states.
     * @param start The FlowNode that defines the start of the thermodynamic process.
     * @param end The FlowNode that defines the end of the thermodynamic process.
     * @param x The first property
     * @param y The second property
     * @return Returns the list of FlowNode representing the thermodynamic process.
     */
    protected final List<FlowNode> thermodynamicProcess(FlowNode start, FlowNode end, Property x, Property y) {
        List<FlowNode> process = new ArrayList();
        for (int i=0; i<Component.nIntStates; i++) {
            FlowNode node = new FlowNode(INTERNAL);
            node.setFluid(start.getFluid());
            process.add(node);
            process.get(i).setMass(start.getMass());
            process.get(i).setState(x, OptionalDouble.of(start.getState(x).getAsDouble() + (i * (end.getState(x).getAsDouble() - start.getState(x).getAsDouble()) / (Component.nIntStates-1))));
            process.get(i).setState(y, OptionalDouble.of(start.getState(y).getAsDouble() + (i * (end.getState(y).getAsDouble() - start.getState(y).getAsDouble()) / (Component.nIntStates-1))));
        }
        return process;
    }
    
    protected final double heatTransferProcessExergy(List<FlowNode> process) {
        double E = 0;
        for (int i=0; i<(process.size()-1); i++) {
            double dQ = (process.get(i+1).getMass().getAsDouble() * process.get(i+1).getState(ENTHALPY).getAsDouble()) - (process.get(i).getMass().getAsDouble() * (process.get(i).getState(ENTHALPY).getAsDouble()));
            double fT = (1 - (2 * ambient.get(TEMPERATURE).getAsDouble() / (process.get(i).getState(TEMPERATURE).getAsDouble() + process.get(i+1).getState(TEMPERATURE).getAsDouble())));
            E = E + (fT * dQ);
        }
        return E;
    }
    
    // reporting methods
    protected final void reportSetup() {
        System.out.println(name + "(" + getClass().getSimpleName() + ")");
        if (getFlowInlets().size() > 0) {System.out.println("\t " + getFlowInlets().size() + " Flow Inlets  (" + getFlowInlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getFlowOutlets().size() > 0) {System.out.println("\t " + getFlowOutlets().size() + " Flow Outlets (" + getFlowOutlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getWorkInlets().size() > 0) {System.out.println("\t " + getWorkInlets().size() + " Work Inlets  (" + getWorkInlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getWorkOutlets().size() > 0) {System.out.println("\t " + getWorkOutlets().size() + " Work Outlets (" + getWorkOutlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getHeatInlets().size() > 0) {System.out.println("\t " + getHeatInlets().size() + " Heat Inlets  (" + getHeatInlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getHeatOutlets().size() > 0) {System.out.println("\t " + getHeatOutlets().size() + " Heat Outlets (" + getHeatOutlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
    }
    protected final void reportResults(DecimalFormat df) {
        System.out.println(name + "(" + getClass().getSimpleName() + ")");
        equations.forEach(e -> {e.toString();});
        if (isComplete()) {
            if (getFlowInlets().size() > 0) {System.out.println("\t Mass in: \t \t" + df.format(massIn()));}
            if (getFlowOutlets().size() > 0) {System.out.println("\t Mass out: \t \t" + df.format(massOut()));}
            if (getWorkInlets().size() > 0) {System.out.println("\t Work in: \t \t" + df.format(workIn()));}
            if (getWorkOutlets().size() > 0) {System.out.println("\t Work out: \t \t" + df.format(workOut()));}
            if (getHeatInlets().size() > 0) {System.out.println("\t Heat in: \t \t" + df.format(heatIn()));}
            if (getHeatOutlets().size() > 0) {System.out.println("\t Heat out: \t \t" + df.format(heatOut()));}
        }
        else {System.out.println("\t Component incomplete");}            
    }
    
    @Override
    public String toString() {
        return (name + " (" + getClass().getSimpleName() + ")");
    }
}
