/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import utilities.DimensionedDouble;
import report.ReportDataBlock;
import report.Reportable;
import java.util.*;
import java.io.Serializable;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thermocycle.Attributes.Attribute;
import static thermocycle.Properties.Property.*;
import static thermocycle.Node.Port.*;
import thermocycle.Properties.Property;
import utilities.Units.UNITS_TYPE;

/**
 *
 * @author Chris
 */
public abstract class Component implements Serializable, Reportable {
    
    /**
     * The logger instance.
     */
    private static final Logger logger = LogManager.getLogger("DebugLog");
    
    /**
     * The number of internal states used for plotting and exergy calculations.
     */
    private static final int nIntStates = 100;
    
    /**
     * The component's name.
     */
    protected String name;
    
    /**
     * The component's unique reference number
     */
    private final UUID id;
    
    /**
     * The component's ambient reference state. 
     */
    private final State ambient;
    
    /**
     * A list of all the internal connections within the component.
     */
    protected final List<Connection<FlowNode>> internals;
    
    /**
     * A list of all the equations relating to this component.
     */
    protected final List<ComponentEquation> equations;
    
    /**
     * A list of all the flow nodes in this component.
     */
    public final Map<String,FlowNode> flowNodes;
    
    /**
     * A list of all the work nodes in this component.
     */
    public final Map<String,WorkNode> workNodes;
    
    /**
     * A list of all the heat nodes in this component.
     */
    public final Map<String,HeatNode> heatNodes;
    
    /**
     * A map containing the value of all component attributes that have been set.
     */
    public final Map<Attribute, Double> attributes;
    
    /**
     * Constructor
     * @param name The name of the new component.
     * @param ambient The ambient state of the new component.
     */
    protected Component(String name, State ambient) {
        id = UUID.randomUUID();
        this.name = name;
        this.ambient = ambient;
        this.flowNodes = new HashMap();
        this.workNodes = new HashMap();
        this.heatNodes = new HashMap();
        this.internals = new ArrayList();
        this.equations = new ArrayList();
        this.attributes = new HashMap();
    }
    
    /**
     * Clears all the values of the component and resets all the component's equations to unsolved.
     */
    protected final void clear() {
        // clear condition
        attributes.clear();
        // clear equations
        equations.stream().forEach(e -> {
            e.reset();
        });
        // clear nodes
        getNodes().stream().forEach(n -> {
            n.clear();
        });
    }
    
    /**
     * Gets all the inlet flow nodes for this component.
     * @return A list of the inlet flow nodes.
     */
    private List<FlowNode> getFlowInlets() {
        return flowNodes.values().stream().filter(n -> n.port.equals(INLET)).collect(Collectors.toList());
    }

    /**
     * Gets all the outlet flow nodes for this component.
     * @return A list of the outlet flow nodes.
     */
    private List<FlowNode> getFlowOutlets() {
        return flowNodes.values().stream().filter(n -> n.port.equals(OUTLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets all the inlet heat nodes for this component.
     * @return A list of the inlet heat nodes.
     */
    private List<HeatNode> getHeatInlets() {
        return heatNodes.values().stream().filter(n -> n.port.equals(INLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets all the outlet heat nodes for this component.
     * @return A list of the outlet heat nodes.
     */
    private List<HeatNode> getHeatOutlets() {
        return heatNodes.values().stream().filter(n -> n.port.equals(OUTLET)).collect(Collectors.toList());
    }

    /**
     * Gets all the inlet work nodes for this component.
     * @return A list of the inlet work nodes.
     */
    private List<WorkNode> getWorkInlets() {
        return workNodes.values().stream().filter(n -> n.port.equals(INLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets all the outlet work nodes for this component.
     * @return A list of the outlet work nodes.
     */
    private List<WorkNode> getWorkOutlets() {
        return workNodes.values().stream().filter(n -> n.port.equals(OUTLET)).collect(Collectors.toList());
    }
    
    /**
     * Gets a list of all the nodes for this component
     * @return A list of all nodes.
     */
    protected final List<Node> getNodes() {
        List<Node> nodes = new ArrayList();
        nodes.addAll(flowNodes.values());
        nodes.addAll(workNodes.values());
        nodes.addAll(heatNodes.values());
        return nodes;
    }
    
    /**
     * Gets the set of component attributes.
     * @return A set of component attributes.
     */
    public abstract Set<Attribute> getAllowableAtributes();
    
    /**
     * Gets the value of an attribute if it has been set.
     * The presence of the attribute should be checked prior to getting the value using isSet().
     * @param attribute The attribute to get the value of.
     * @return The value of the attribute.
     */
    public final OptionalDouble getAttribute(Attribute attribute) {
        if (attributes.containsKey(attribute)) {
            return OptionalDouble.of(attributes.get(attribute));
        }
        return OptionalDouble.empty();
    }
    
    /**
     * Sets the value of an attribute.
     * @param attribute The attribute to set.
     * @param value The value to set the attribute to.
     */
    protected final void setAttribute(Attribute attribute, Double value) {
        attributes.put(attribute, value);
    }
    
    /**
     * Clears the value of a component attribute.
     * @param attribute The component attribute to clear.
     */
    protected final void clearAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }
    
    /**
     * Checks if the component is complete. A component is complete when all its nodes are complete and equations have been checked for compatibility.
     * @return true if the component is complete, false otherwise.
     */
    public final boolean isComplete() {
        if (getNodes().stream().allMatch(n -> n.isComplete())) {
            if (equations.stream().allMatch(equation -> equation.isSolved())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Updates the component by continually solving its equations until all unknowns have been found.
     * @return a set of the nodes that have been updated during the update.
     */
    protected final Set<Node> update() {
        logger.info("Solving " + this);
        Set<Node> updatedNodes = new HashSet();
        while (updatedNodes.addAll(solve())) {}
        return updatedNodes;
    }
    
    /**
     * Solves the component's equations to find unknowns.
     * @return a set of the nodes that have been updated during the solve.
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
     * @return The net exergy change across the component.
     */
    public final double exergyLoss() {
        return ((flowExergyIn() + workIn() + heatExergyIn()) - (flowExergyOut() - workOut() - heatExergyOut()));
    }
    
    /**
     * Get the total exergy input to the component due to fluid flow.
     * @return The exergy input to the component due to fluid flow. If any of the flow nodes are not complete then returns Double.NaN.
     */
    private double flowExergyIn() {
        if (getFlowInlets().stream().allMatch(n -> n.isComplete())) {
            double E = 0;
            State dead = new State();
            for (FlowNode n : getFlowInlets()) {
                dead.clearState();
                dead.setProperty(ambient);
                n.getFluid().get().computeState(dead);
                E = E + n.getMass().getAsDouble() * ((n.getState(ENTHALPY).getAsDouble() - dead.getProperty(ENTHALPY).getAsDouble()) - (dead.getProperty(TEMPERATURE).getAsDouble() * (n.getState(ENTROPY).getAsDouble() - dead.getProperty(ENTROPY).getAsDouble())));
            }
            return E;
        }
        return Double.NaN;
    }
    
    /**
     * Get the total exergy output from the component due to fluid flow.
     * @return The total exergy output from the component due to fluid flow. If any of the flow nodes are not complete then returns Double.NaN.
     */
    private double flowExergyOut() {
        if (getFlowOutlets().stream().allMatch(n -> n.isComplete())) {
            double E = 0;
            State dead = new State();
            for (FlowNode n : getFlowOutlets()) {
                dead.clearState();
                dead.setProperty(ambient);
                n.getFluid().get().computeState(dead);
                E = E + n.getMass().getAsDouble() * ((n.getState(ENTHALPY).getAsDouble() - dead.getProperty(ENTHALPY).getAsDouble()) - (dead.getProperty(TEMPERATURE).getAsDouble() * (n.getState(ENTROPY).getAsDouble() - dead.getProperty(ENTROPY).getAsDouble())));
            }
            return E;
        }
        return Double.NaN;
    }
    
    /**
     * Gets the flow node.
     * @param name the name of the flow node to get.
     * @return the flow node object.
     */
    public FlowNode getFlowNode(String name) {
        return flowNodes.get(name);
    }
    
    /**
     * Gets the work node.
     * @param name the name of the work node to get.
     * @return the work node object.
     */
    public WorkNode getWorkNode(String name) {
        return workNodes.get(name);
    }
    
    /**
     * Gets the heat node.
     * @param name the name of the heat node to get.
     * @return the heat node object.
     */
    public HeatNode getHeatNode(String name) {
        return heatNodes.get(name);
    }
    
    /**
     * Get the total exergy input to the component due to heat transfer.
     * @return The total exergy input to the component due to heat transfer.
     */
    protected abstract double heatExergyIn();
    
    /**
     * Get the total exergy output from the component due to heat transfer.
     * @return The total exergy output from the component due to heat transfer.
     */
    protected abstract double heatExergyOut();
    
    /**
     * Get the total heat input to the component.
     * @return The total heat input to the component. If any of the heat nodes are not complete then returns Double.NaN.
     */
    protected final double heatIn() {
        if (getHeatInlets().stream().allMatch(n -> n.isComplete())) {
            return getHeatInlets().stream().mapToDouble(n -> n.getHeat().getAsDouble()).sum();
        }
        return Double.NaN;
    }
    
    /**
     * Get the total heat output from the component.
     * @return The total heat output from the component. If any of the heat nodes are not complete then returns Double.NaN.
     */
    protected final double heatOut() {
        if (getHeatOutlets().stream().allMatch(n -> n.isComplete())) {
            return getHeatOutlets().stream().mapToDouble(n -> n.getHeat().getAsDouble()).sum();
        }
        return Double.NaN;
    }
    
    /**
     * Get the total mass flow rate in to the component.
     * @return The total mass flow rate into the component. If any of the flow nodes are not complete then returns Double.NaN.
     */
    private double massIn() {
        if (getFlowInlets().stream().allMatch(n -> n.isComplete())) {
            return getFlowInlets().stream().mapToDouble(n -> n.getMass().getAsDouble()).sum();
        }
        return Double.NaN;
    }
    
    /**
     * Get the total mass flow rate out from the component. Currently assumes that all mass values are present.
     * @return The total mass flow rate out from the component. If any of the flow nodes are not complete then returns Double.NaN.
     */
    private double massOut() {
        if (getFlowOutlets().stream().allMatch(n -> n.isComplete())) {
            return getFlowOutlets().stream().mapToDouble(n -> n.getMass().getAsDouble()).sum();
        }
        return Double.NaN;
    }
    
    /**
     * Get the total work input to the component.
     * @return The total work input to the component. If any of the work nodes are not complete then returns Double.NaN.
     */
    protected double workIn() {
        if (getWorkInlets().stream().allMatch(n -> n.isComplete())) {
            return getWorkInlets().stream().mapToDouble(n -> n.getWork().getAsDouble()).sum();
        }
        return Double.NaN;
    }
    
    /**
     * Get the total work output from the component.
     * @return The total work output from the component. If any of the work nodes are not complete then returns Double.NaN.
     */
    protected double workOut() {
        if (getWorkOutlets().stream().allMatch(n -> n.isComplete())) {
            return getWorkOutlets().stream().mapToDouble(n -> n.getWork().getAsDouble()).sum();
        }
        return Double.NaN;
    }
    
    /**
     * Creates a set of internal flow nodes that describe a thermodynamic process between two states.
     * Two properties are specified that are assumed to vary linearly between the start and end states.
     * @param start The flow node that defines the start of the thermodynamic process.
     * @param end The flow node that defines the end of the thermodynamic process.
     * @param x The first property that varies linearly between the start and end states.
     * @param y The second property that varies linearly between the start and ed states.
     * @return A list of flow nodes representing the thermodynamic process.
     */
    protected final List<FlowNode> thermodynamicProcess(FlowNode start, FlowNode end, Property x, Property y) {
        List<FlowNode> process = new ArrayList();
        for (int i=0; i<Component.nIntStates; i++) {
            FlowNode node = new FlowNode(INTERNAL);
            node.setFluid(start.getFluid().get());
            process.add(node);
            process.get(i).setMass(start.getMass().getAsDouble());
            process.get(i).setProperty(x, start.getState(x).getAsDouble() + (i * (end.getState(x).getAsDouble() - start.getState(x).getAsDouble()) / (Component.nIntStates-1)));
            process.get(i).setProperty(y, start.getState(y).getAsDouble() + (i * (end.getState(y).getAsDouble() - start.getState(y).getAsDouble()) / (Component.nIntStates-1)));
        }
        return process;
    }
    
    /**
     * The exergy change due to a
     * @param process
     * @return The exergy change due to the heat transfer process.
     */
    protected final double heatTransferProcessExergy(List<FlowNode> process) {
        double E = 0;
        for (int i=0; i<(process.size()-1); i++) {
            double dQ = (process.get(i+1).getMass().getAsDouble() * process.get(i+1).getState(ENTHALPY).getAsDouble()) - (process.get(i).getMass().getAsDouble() * (process.get(i).getState(ENTHALPY).getAsDouble()));
            double fT = (1 - (2 * ambient.getProperty(TEMPERATURE).getAsDouble() / (process.get(i).getState(TEMPERATURE).getAsDouble() + process.get(i+1).getState(TEMPERATURE).getAsDouble())));
            E = E + (fT * dQ);
        }
        return E;
    }
    
    protected abstract List<List<FlowNode>> plotData();
    
    @Override
    public final String toString() {
        return name;
    }
    
    @Override
    public ReportDataBlock getReportData() {
        ReportDataBlock rdb = new ReportDataBlock(this.toString());
        rdb.addData("Status", isComplete()? "Solved" : "Unsolved");
        
        ReportDataBlock atbs = new ReportDataBlock("Attributes");
        getAllowableAtributes().stream().forEach(a -> {
            atbs.addData(a.fullName, this.getAttribute(a).isPresent() ? DimensionedDouble.valueOfSI(getAttribute(a).getAsDouble(), a.type) : "Unsolved");
        });
        rdb.addDataBlock(atbs);
        
        ReportDataBlock eqs = new ReportDataBlock("Equations");
        equations.stream().forEach(e -> {
            eqs.addData(e.toString(), e.isSolved()? "Solved" : "Unsolved");
        });
        rdb.addDataBlock(eqs);
        
        // Heat nodes
        if (heatNodes.size() > 0) {
            heatNodes.keySet().stream().forEach(h -> {
                ReportDataBlock htn = new ReportDataBlock(h);
                htn.addData("Heat", heatNodes.get(h).getHeat().isPresent() ? DimensionedDouble.valueOfSI(heatNodes.get(h).getHeat().getAsDouble(), UNITS_TYPE.POWER) : "Unsolved");
                rdb.addDataBlock(htn);
            });
        }
        
        if (workNodes.size() > 0) {
            workNodes.keySet().stream().forEach(w -> {
                ReportDataBlock wkn = new ReportDataBlock(w);
                wkn.addData("Work", workNodes.get(w).getWork().isPresent() ? DimensionedDouble.valueOfSI(workNodes.get(w).getWork().getAsDouble(), UNITS_TYPE.POWER) : "Unsolved");
                rdb.addDataBlock(wkn);
            });
        }
        
        if (flowNodes.size() > 0) {
            flowNodes.keySet().stream().forEach(f -> {
                ReportDataBlock fln = new ReportDataBlock(f);
                fln.addData("Mass", flowNodes.get(f).getMass().isPresent() ? DimensionedDouble.valueOfSI(flowNodes.get(f).getMass().getAsDouble(), UNITS_TYPE.FLOW_RATE) : "Unsolved");
                flowNodes.get(f).getAllowableProperties().stream().forEach(p -> {
                    fln.addData(p.toString(), flowNodes.get(f).getState(p).isPresent() ? DimensionedDouble.valueOfSI(flowNodes.get(f).getState(p).getAsDouble(), p.type) : "Unsolved.");
                });
                rdb.addDataBlock(fln);
            });
        }
        
        return rdb;
    }
    
}
