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
import org.jfree.data.xy.DefaultXYDataset;
import static thermocycle.Properties.Property.*;
import static thermocycle.Ports.Let.*;


/**
 *
 * @author Chris
 */
public abstract class Component implements Attributes, Properties, Ports, Serializable {
    
    // static variables
    private static int idCount = 0;                             // Compoet id counter
    static int nIntStates = 100;                                // Number of internal states
    
    /**
     * The number of internal nodes used for plotting and calculating the exergy changes due to heat transfer.
     * @return Returns the number of internal nodes used.
     */
    static int idCount() {
        return Component.idCount;
    }
    
    // variables
    String name;                                        // Component name
    final int id;                                       // Component id number
    final State ambient;                                // Component ambient state
    final List<Connection> internals;                   // Component internal flow paths
    final List<Equation> equations;                     // Component equations
    final List<FlowNode> flowNodes;                     // Component flow node
    final List<WorkNode> workNodes;                     // Component work nodes
    final List<HeatNode> heatNodes;                     // Component heat nodes
    final Map<Attribute, OptionalDouble> attributes;    // Component attributes
    
    // constructors
    Component(String name, State ambient) {
        Component.idCount++;
        id = idCount;
        this.name = name;
        this.ambient = ambient;
        flowNodes = new ArrayList();
        workNodes = new ArrayList();
        heatNodes = new ArrayList();
        internals = new ArrayList();
        equations = new ArrayList();
        attributes = new HashMap();
    }
    
    @Override
    public String toString() {
        return ("[" + id + "] " + name + " (" + getClass().getSimpleName() + ")");
    }
    
    // getters
    final OptionalDouble getAmbient(Property property) {
        return ambient.get(property);
    }
    private List<FlowNode> getFlowInlets() {
        return flowNodes.stream().filter(n -> n.let.equals(INLET)).collect(Collectors.toList());
    }
    private List<FlowNode> getFlowOutlets() {
        return flowNodes.stream().filter(n -> n.let.equals(OUTLET)).collect(Collectors.toList());
    }
    private List<HeatNode> getHeatInlets() {
        return heatNodes.stream().filter(n -> n.let.equals(INLET)).collect(Collectors.toList());
    }
    private List<HeatNode> getHeatOutlets() {
        return heatNodes.stream().filter(n -> n.let.equals(OUTLET)).collect(Collectors.toList());
    }
    private List<WorkNode> getWorkInlets() {
        return workNodes.stream().filter(n -> n.let.equals(INLET)).collect(Collectors.toList());
    }
    private List<WorkNode> getWorkOutlets() {
        return workNodes.stream().filter(n -> n.let.equals(OUTLET)).collect(Collectors.toList());
    }
    final List<Node> getNodes() {
        List<Node> nodes = new ArrayList();
        nodes.addAll(flowNodes);
        nodes.addAll(workNodes);
        nodes.addAll(heatNodes);
        return nodes;
    }
    
    /**
     * Determine if a specific attribute is present.
     * @param name The name of the attribute.
     * @return REturns true if the specific attribute is present.
     */
    final boolean getAttributeIsPresent(Attribute name) {return attributes.get(name).isPresent();}

    /**
     * Gets the value of the specified attribute.
     * @param name The name of the required attribute.
     * @return Returns the value of the attribute.
     */
    final OptionalDouble getAttribute(Attribute name) {
        return attributes.get(name);
    }
    
    /**
     * Creates an attribute.
     * @param name The name of the attribute to create.
     */
    final void createAttribute(Attribute name) {
        attributes.put(name, OptionalDouble.empty());
    };
    
    /**
     * Sets the value of the attribute.
     * @param name The name of the attribute to set.
     * @param value The value to set the attribute to.
     * @throws IllegalArgumentException if thee attribute is not valid.
     * @throws IllegalStateException if thee attribute has already been set.
     */
    final void setAttribute(Attribute name, OptionalDouble value) {
        if (!attributes.containsKey(name)) {
            throw new IllegalArgumentException(name + " is not a valid attribute for component type " + this.getClass().getSimpleName());
        }
        if (attributes.get(name).isPresent()) {
            throw new IllegalStateException(name + " has already been set in component " + this.name);
        }
        attributes.put(name, value);
    }
    
    //final void setName(String name) {this.name = name;}
   
    /**
     * Checks to see if the component is complete. A component is complete if all its nodes are complete and equations have been checked for compatibility.
     * @return Returns True if all Nodes associated with the component are complete.
     */
    final boolean isComplete() {
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
    final boolean isCompatible() {
        equations.stream().forEach(equation -> {
            if (!equation.isCompatible()) {
                System.out.println("Incompatible equation: " + equation.getClass().getSimpleName());
            }
        });
        return equations.stream().allMatch(equation -> equation.isCompatible());
    }
    
    /**
     * Updates the component.
     * @return REturns a list of all Nodes that are updated during the computation
     */
    final Set<Node> update() {
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
        //equations.stream().forEach(equation -> {
        //    updatedNodes.add(equation.solve());
        //});
        return updatedNodes;
    }

    /**
     * Get the net exergy change across the component.
     * @return Returns the net exergy change across the component.
     */
    final double exergyLoss() {
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
            dead.reset();
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
            dead.reset();
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
    abstract double heatExergyIn();
    
    /**
     * Get the total exergy output from the component due to heat transfer.
     * @return Returns the total exergy output via heat transfer.
     */
    abstract double heatExergyOut();
    
    /**
     * Get the total heat input to the component. Currently assumes that all heat values are present.
     * @return Returns the total heat input.
     */
    final double heatIn() {
        double Q = 0;
        for (HeatNode n : getHeatInlets()) {
            Q = Q + n.getHeat().getAsDouble();}
        return Q;
    }
    
    /**
     * Get the total heat output from the component. Currently assumes that all heat values are present.
     * @return Returns the total heat output.
     */
    final double heatOut() {
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
    final double workIn() {
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
    final double workOut() {
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
    final List<FlowNode> thermodynamicProcess(FlowNode start, FlowNode end, Property x, Property y) {
        List<FlowNode> process = new ArrayList();
        for (int i=0; i<Component.nIntStates; i++) {
            process.add(new FlowNode(INTERNAL, ambient, start.getFluid()));
            process.get(i).setMass(start.getMass());
            process.get(i).setState(x, OptionalDouble.of(start.getState(x).getAsDouble() + (i * (end.getState(x).getAsDouble() - start.getState(x).getAsDouble()) / (Component.nIntStates-1))));
            process.get(i).setState(y, OptionalDouble.of(start.getState(y).getAsDouble() + (i * (end.getState(y).getAsDouble() - start.getState(y).getAsDouble()) / (Component.nIntStates-1))));
        }
        return process;
    }
    
    final double heatTransferProcessExergy(List<FlowNode> process) {
        double E = 0;
        for (int i=0; i<(process.size()-1); i++) {
            double dQ = (process.get(i+1).getMass().getAsDouble() * process.get(i+1).getState(ENTHALPY).getAsDouble()) - (process.get(i).getMass().getAsDouble() * (process.get(i).getState(ENTHALPY).getAsDouble()));
            double fT = (1 - (2 * ambient.get(TEMPERATURE).getAsDouble() / (process.get(i).getState(TEMPERATURE).getAsDouble() + process.get(i+1).getState(TEMPERATURE).getAsDouble())));
            E = E + (fT * dQ);
        }
        return E;
    }
    
    abstract int plotData(DefaultXYDataset dataset, Property x, Property y);
    
    // reporting methods
    final void reportSetup() {
        System.out.println("[" + id + "] " + name + "(" + getClass().getSimpleName() + ")");
        if (getFlowInlets().size() > 0) {System.out.println("\t " + getFlowInlets().size() + " Flow Inlets  (" + getFlowInlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getFlowOutlets().size() > 0) {System.out.println("\t " + getFlowOutlets().size() + " Flow Outlets (" + getFlowOutlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getWorkInlets().size() > 0) {System.out.println("\t " + getWorkInlets().size() + " Work Inlets  (" + getWorkInlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getWorkOutlets().size() > 0) {System.out.println("\t " + getWorkOutlets().size() + " Work Outlets (" + getWorkOutlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getHeatInlets().size() > 0) {System.out.println("\t " + getHeatInlets().size() + " Heat Inlets  (" + getHeatInlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
        if (getHeatOutlets().size() > 0) {System.out.println("\t " + getHeatOutlets().size() + " Heat Outlets (" + getHeatOutlets().stream().filter(n -> !n.isComplete()).collect(Collectors.toList()).size() + " incomplete)");}
    }
    final void reportResults(DecimalFormat df) {
        System.out.println("[" + id + "] " + name + "(" + getClass().getSimpleName() + ")");
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
}
