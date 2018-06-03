/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import static thermocycle.Properties.Property.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thermocycle.Attributes.Attribute;
import thermocycle.Node.Port;

/**
 *
 * @author Chris
 */
public class Cycle extends Observable implements Properties, Serializable {
    
    // static variables
    static DecimalFormat doubleFormat = new DecimalFormat("#.#E0#");
    static DecimalFormat percentageFormat = new DecimalFormat("##.#%");
    static Integer maxIterations = 100;
    
    // static methods
    static <T> Collector<T, List <T>, T> singletonCollector() {
        return Collector.of(
                ArrayList::new,
                List::add,
                (left,right) -> {left.addAll(right); return left; },
                list -> {
                    if (list.size() > 1) {throw new IllegalStateException("Collection contains more than 1 element.");}
                    else if (list.isEmpty()) {return null;}
                    return list.get(0);
                }
        );
    }
    
    // variables
    private String name;
    private Integer iteration;
    private final State ambient;
    private final ObservableList<Connection> connections;
    private final ObservableList<Component> components;
    private final ObservableList<Fluid> fluids;
    private final ObservableList<Set<FlowNode>> paths;
    public final ObservableList<Connection> connectionsReadOnly;
    public final ObservableList<Component> componentsReadOnly;
    public final ObservableList<Fluid> fluidsReadOnly;
    public final ObservableList<Set<FlowNode>> pathsReadOnly;
    //private final History setValues;
    private final ObservableList<Map<String, ParametricDouble>> results;
    
    /**
     * Constructor
     * @param name The name of the new cycle. 
     */
    public Cycle(String name) {
        this.name = name;
        ambient = new State();
        connections = FXCollections.observableList(new ArrayList<>());
        components = FXCollections.observableList(new ArrayList<>());
        fluids = FXCollections.observableList(new ArrayList<>());
        paths = FXCollections.observableList(new ArrayList<>());
        connectionsReadOnly = FXCollections.unmodifiableObservableList(connections);
        componentsReadOnly = FXCollections.unmodifiableObservableList(components);
        fluidsReadOnly = FXCollections.unmodifiableObservableList(fluids);
        pathsReadOnly = FXCollections.unmodifiableObservableList(paths);
        ambient.putIfAbsent(PRESSURE, ParametricDouble.of(101325.0));
        ambient.putIfAbsent(TEMPERATURE, ParametricDouble.of(300.0));
        results = FXCollections.observableList(new ArrayList<>());
        //setValues = new History();
    }
    
    /**
     * Gets the ambient state values
     * @param property The ambient state property to get.
     * @return Returns the ambient state property value.
     */
    public ParametricDouble getAmbient(Property property) {
        return ambient.get(property);
    }
    
    /**
     * Get the set of attributes that belong to this component.
     * @param component The component of interest.
     * @return Returns a set of attributes belong to the component.
     */
    public Set<Attribute> getAttributes(Component component) {
        return component.getAtributes();
    }
    
    /**
     * Gets a component given its name.
     * @param name The id of the component.
     * @return Returns the component.
     */
    public Component getComponent(String name) {
        return components.stream().filter(c -> c.name.equals(name)).collect(singletonCollector());
    }
    
    /**
     * Gets all the flow connections in the cycle
     * @return Returns a set of the flow connections.
     */
    private Set<Connection> getFlowConnections() {
        Set<Connection> links = new HashSet<>();
        links.addAll(connections.stream().filter(c -> c.nodeType().equals(FlowNode.class)).collect(Collectors.toSet()));
        components.forEach(c -> links.addAll(c.internals));
        return links;
    }
    
    /**
     * Gets all the component flow nodes in the cycle.
     * @return Returns a set of the component flow nodes in the cycle.
     */
    private Set<FlowNode> getFlowNodes() {
        Set<FlowNode> nodes = new HashSet<>();
        components.forEach(c -> nodes.addAll(c.flowNodes));
        return nodes;
    }
    
    /**
     * Gets a fluid given its name.
     * @param name The name of the fluid to get.
     * @return Returns the fluid.
     */
    public Fluid getFluid(String name) {
        return fluids.stream().filter(f -> f.getName().equals(name)).collect(singletonCollector());
    }
    
    /**
     * Gets the cycle name.
     * @return REturns the cycle name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the Node port type.
     * @param node The node of interest.
     * @return Returns the node port type.
     */
    public Port getNodePort(Node node) {
        return node.port;
    }
    
    /**
     * Sets the ambient pressure and temperature
     * @param pressure The ambient pressure
     * @param temperature  The ambient temperature
     */
    public void setAmbient(double pressure, double temperature) {
        ambient.put(PRESSURE, ParametricDouble.of(pressure));
        ambient.put(TEMPERATURE, ParametricDouble.of(temperature));
    }
    
    /**
     * Sets the maximum number of allowable iterations when solving
     * @param iterations The maximum number of allowable iterations to set.
     */
    public void setMaxIterations(int iterations) {
        Cycle.maxIterations = iterations;
    }
    
    /**
     * Sets the cycle name.
     * @param name The name of the cycle to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets a component name ensuring uniqueness
     * @param component The component to set the name of.
     * @param name The new name.
     * @param force Determines if a new name must be 
     */
    public void setName(Component component, String name, Boolean force) {
        // Use @ character to define automated name
        if (name.charAt(0) != '@') {
            // Ensure uniqueness of component name
            if (!(components.stream().filter(c -> (!c.equals(component))).anyMatch(c -> c.name.equals(name)))) {
                component.name = name;
                return;
            }
        }
        // SOMETHING
    }
    
    /**
     * Sets the work value for the work node.
     * @param node The work node.
     * @param value The work value.
     */
    public void setWork(WorkNode node, ParametricDouble value) {
        node.setWork(value);
        /**
        try {
            Object[] args = {node, value};
            setValues.add(this.getClass().getMethod("setWork", WorkNode.class, OptionalDouble.class), args);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

    /**
     * Sets the heat value for the heat node.
     * @param node The heat node.
     * @param value The heat value.
     */
    public void setHeat(HeatNode node, ParametricDouble value) {
        node.setHeat(value);
        /**
        try {
            Object[] args = {node, value};
            setValues.add(this.getClass().getMethod("setHeat", HeatNode.class, OptionalDouble.class), args);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    /**
     * Sets the mass value for the flow node.
     * @param node The flow node.
     * @param value The mass value.
     */
    public void setMass(FlowNode node, ParametricDouble value) {
        node.setMass(value);
        /**
        try {
            Object[] args = {node, value};
            setValues.add(this.getClass().getMethod("setMAss", FlowNode.class, OptionalDouble.class), args);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    /**
     * Sets the property value for the flow node.
     * @param node The flow node.
     * @param property The state property to set.
     * @param value The property value.
     */
    public void setState(FlowNode node, Property property, ParametricDouble value) {
        node.setState(property, value);
        /**
        try {
            Object[] args = {node, value};
            setValues.add(this.getClass().getMethod("setState", FlowNode.class, Property.class, OptionalDouble.class), args);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    /**
     * Sets a component attribute
     * @param component The component to set.
     * @param attribute The attribute to set.
     * @param value The value to set thee component attribute to.
     */
    public void setAttribute(Component component, Attribute attribute, ParametricDouble value) {
        component.setAttribute(attribute, value);
        /**
        try {
            Object[] args = {component, attribute, value};
            setValues.add(this.getClass().getMethod("setAttribute", Component.class, Attribute.class , OptionalDouble.class), args);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    /**
     * Sets the fluid at a particular cycle node.
     * @param node The node in the system to set the fluid at. The methods sets all nodes in the same flow path to have the same fluid.
     * @param fluid The fluid to set.
     */
    public void setFluid(FlowNode node, Fluid fluid) {
        paths.stream().filter(p -> p.contains(node)).collect(singletonCollector()).forEach(n -> n.setFluid(fluid));
    }
    
    /**
     * Creates a component
     */
    private void cycleChange() {
        pathFinder();
        setChanged();
        notifyObservers();
    }
    
    public Combustor createCombustor(String name) {
        components.add(new Combustor(name, ambient));
        cycleChange();
        return (Combustor)components.get(components.size() - 1);
    }
    public Compressor createCompressor(String name) {
        components.add(new Compressor(name, ambient));
        cycleChange();
        return (Compressor)components.get(components.size() - 1);
    }
    public HeatExchanger createHeatExchanger(String name) {
        components.add(new HeatExchanger(name, ambient));
        cycleChange();
        return (HeatExchanger)components.get(components.size() - 1);
    }
    public HeatSink createHeatSink(String name) {
        components.add(new HeatSink(name, ambient));
        cycleChange();
        return (HeatSink)components.get(components.size() - 1);
    }
    public Turbine createTurbine(String name) {
        components.add(new Turbine(name, ambient));
        cycleChange();
        return (Turbine)components.get(components.size() - 1);
    }
    
    /**
     * Removes a component from the cycle. All connections with the component are also removed.
     * @param component The component to remove.
     */
    public void removeComponent(Component component) {
        if (components.contains(component)) {
            component.flowNodes.forEach(n -> removeConnection(n));
            component.heatNodes.forEach(n -> removeConnection(n));
            component.workNodes.forEach(n -> removeConnection(n));
            components.remove(component);
            cycleChange();
        }
    }
    
    /**
     * Creates a new ideal gas.
     * @param name The name of the new ideal gas.
     * @param ga The ratio of specific heats for the new ideal gas.
     * @param r The specific gas constant for the new ideal gas.
     * @return Returns an instance of the new ideal gas.
     */
    public IdealGas createIdealGas(String name, Double ga, Double r) {
        fluids.add(new IdealGas(name,ga,r));
        return (IdealGas)fluids.get(fluids.size()-1);
    }

    /**
     * Creates a connection between two nodes.
     * @param node1 The first node.
     * @param node2 The second node.
     * @return Returns the connection object.
     */
    public Connection createConnection(Node node1, Node node2) {
        if (connections.stream().anyMatch(c -> c.contains(node1))) {
            throw new IllegalStateException("Node is already connected.");
        }  // check n1 is not currently connected to other nodes
        if (connections.stream().anyMatch(c -> c.contains(node2))) {
            throw new IllegalStateException("Node is already connected.");
        }  // check n2 is not currently connected to other nodes
        Connection connection = new Connection(node1,node2);
        connections.add(connection);
        pathFinder();
        return connection;
    }
        
    /**
     * Removes a connection from the model.
     * @param connection The connection to remove.
     */
    public void removeConnection(Connection connection) {
        connections.remove(connection);
    }
    
    /**
     * Removes connections from the model that contain this node.
     * @param node The node to remove connections from.
     */
    private void removeConnection(Node node) {
        connections.removeAll(connections.stream().filter(c -> c.contains(node)).collect(Collectors.toSet()));  // only remove connections beloning to the cycle, not internal connections belonging to the component.
        pathFinder();
    }
    
    /**
     * Gets the neighbours of a flow node.
     * @param node The flow node of interest.
     * @return Returns a set of flow nodes that are directly connected to the node of interest.
     */
    private Set<FlowNode> nodeNeighbours(FlowNode node) {
        Set<FlowNode> neighbours = new HashSet<>();
        getFlowConnections().forEach(c -> neighbours.add((FlowNode)c.getPair(node)));     // because n is a FlowNode the getPair(n) is also always a FlowNode
        neighbours.remove(null);                                                          // remove the null neighbour which occurs if a node is not connected to another
        return neighbours;
    }
    
    /**
     * Gets all flow nodes in the flow path containing specified start node
     * @param start The node to start from
     * @return Returns the set of nodes in the flow path
     */
    private Set<FlowNode> pathFinder(FlowNode start) {
        Set<FlowNode> path = new HashSet<>();
        List<FlowNode> queue = new LinkedList<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            path.add(queue.get(0));
            queue.addAll(nodeNeighbours(queue.remove(0)));
            queue.removeAll(path);
        }
        return path;
    }
    
    /**
     * Gets all the nodes in the flow path containing a random starting node.
     * @param nodes The set of nodes to consider.
     * @return Returns a set of nodes that comprise a flow path.
     */
    private Set<FlowNode> pathFinder(Set<FlowNode> nodes) {
        return pathFinder(nodes.stream().findAny().get());
    }
    
    /**
     * Get all the distinct paths in the the current model
     */
    private void pathFinder() {
        Set<FlowNode> nodes = new HashSet();
        nodes.addAll(getFlowNodes());
        paths.clear();
        while (!nodes.isEmpty()) {
            paths.add(pathFinder(nodes));
            nodes.removeAll(paths.get(paths.size()-1));
        }
    }
    
    /**
     * Updates all the variables for the net parametric sweep. If these are parametric their values are updated, if this are of type 'Solve' their values are cleared.
     */
    private void parametricUpdate() {
        
        getVariables();
        // get parametric varaibles
        // filter out set values
        // clear all SOLVE values to empty
        // itterate through
        
    }
    
    /**
     * 
     */
    public void parametricSolve() {
        // Itterate around every combination of parametric variables
        getVariables().values().stream().forEach(v -> {
            // solve until entire sweep is complete.
            // Solve system
            solve();
            // Record values
            results.add(getVariables()); // TODO: Check that this doesn't link to the origional OptionalDouble objects - need to make copies.
        });
    }
    
    /**
     * Gets a list of all the variables names and values.
     * @return Returns a HashMap of all the variables and unique names.
     */
    private Map<String, ParametricDouble> getVariables() {
        Map variables = new HashMap<String, ParametricDouble>();
        
        // Ambient state values
        variables.put("Ambient pressure", ambient.get(PRESSURE));
        variables.put("Ambient temperature", ambient.get(TEMPERATURE));
        
        // Loop over all componanets
        this.components.stream().forEach(c -> {
            // Loop over flow nodes
            c.flowNodes.stream().forEach(n -> {
                // Get mass flow rates
                variables.put(c.name + "Mass" + c.flowNodes.indexOf(n), n.getMass());
                // Get state properties
                n.getFluid().fluidState().stream().forEach(s -> {
                    variables.put(c.name + s.name() + c.flowNodes.indexOf(n), n.getState(s));    // Need to make sure component name is unique
                });
            });
            // Loop over heat nodes
            c.heatNodes.stream().forEach(n -> {
                // Get heat values
                variables.put(c.name + "Heat" + c.heatNodes.indexOf(n), n.getHeat());
            });
            // Loop over work nodes
            c.workNodes.stream().forEach(n -> {
                // Get work values
                variables.put(c.name + "Work" + c.workNodes.indexOf(n), n.getWork());
            });
            // Loop over component attributes
            c.attributes.keySet().stream().forEach(a -> {
                // Get attribute values
                variables.put(c.name + a.name(), c.attributes.get(a));
            });
        });
        return variables;
    }
    
    /**
     * Solves the current cycle
     * @return Returns true if a solution is achieved.
     */
    public boolean solve() {
        try {
            getFlowNodes().forEach(n -> {n.computeState();});                                              // update all flow node states (ensures all nodes states are internally compatible first).
            connections.stream().forEach(x -> {x.update();});                                              // update all node connections (ensures state values are transfered between nodes, checks for inconsistencies).
            Set<Component> unsolved = new HashSet<>(components);                                           // update all connections (updates nodes across components)
            Set<Node> updatedNodes = new HashSet<>();
            iteration = 0;
            do {
                updatedNodes.clear();
                iteration++;
                // for each unsolved component update the component and add updated nodes to the updated nodes
                unsolved.forEach(c -> {
                    updatedNodes.addAll(c.update());
                });
                // for each updated node find connnections and update them 
                updatedNodes.forEach(n -> {
                    connections.stream().filter(x -> x.contains(n)).forEach(x -> {
                        x.update();
                    });
                });
                // remove complete components from the unsolved list
                unsolved.removeAll(unsolved.stream().filter(c -> c.isComplete()).collect(Collectors.toSet()));
            } while(updatedNodes.size() > 0 && iteration < Cycle.maxIterations);
            // Note that connection compatibility os checked during x.update()
            components.stream().forEach(comp -> {
                if (!comp.isCompatible()) {
                    System.out.println("Incompatible component: " + comp.name);
                    comp.attributes.keySet().forEach(at -> {
                        System.out.println(at.name() + " = " + comp.getAttribute(at));
                    });
                    throw new IllegalStateException("Components are incompatible!!!");
                }
            });
            // Notify listeners
            setChanged();
            notifyObservers();
            return unsolved.isEmpty();
        }
        catch (Exception exc) {
            System.out.println();
            System.out.println("ERROR REPORT");
            System.out.println("============");
            System.out.println(exc.getClass());
            System.out.println(exc.getMessage());
            exc.printStackTrace();
            reportSolver();
        }
        return false;
    }
    
    /**
     * Calculate the thermal efficiency of the cycle
     * @return Returns the thermal efficiency.
     */
    private double efficiencyThermal() {
        return (workOut() - workIn()) / heatIn();
    }
    
    /**
     * Calculate the rational efficiency of the cycle
     * @return Returns the rational efficiency.
     */
    private double efficiencyRational() {
        return (workOut() - workIn()) / heatExergyIn();
    }
    
    /**
     * Calculate the heat input to the cycle
     * @return Returns the total heat input
     */
    private double heatIn() {
        double Q = 0;
        for (Component c : components) {
            Q = Q + c.heatIn();
        }
        return Q;
    }
    
    /**
     * Calculate the heat output from the cycle
     * @return Returns the total heat output
     */
    private double heatOut() {
        double Q = 0;
        for (Component c : components) {
            Q = Q + c.heatOut();
        }
        return Q;
    }
    
    /**
     * Calculate the exergy input to the cycle
     * @return Returns the total exergy input
     */
    private double heatExergyIn() {
        double E = 0;
        for (Component c : components) {
            E = E + c.heatExergyIn();
        }
        return E;
    }
    
    /**
     * Calculate the exergy output from the cycle
     * @return Returns the total exergy output
     */
    private double heatExergyOut() {
        double E = 0;
        for (Component c : components) {
            E = E + c.heatExergyOut();
        }
        return E;
    }
    
    /**
     * Calculate the work input to the cycle
     * @return Returns the total work input
     */
    private double workIn() {
        double W = 0;
        for (Component c : components) {
            W = W + c.workIn();
        }
        return W;
    }
    
    /**
     * Calculate the work output from the cycle
     * @return Returns the total work output
     */
    private double workOut() {
        double W = 0;
        for (Component c : components) {
            W = W + c.workOut();
        }
        return W;
    }
    
    /**
     * Reset the cycle to it's state prior to solving.
     */
    /**
    private final void reset() {
        // remove all values
        components.stream().forEach(c -> {
            c.clear();
        });
        // reset values
        setValues.invoke(this);
    }
    */
    
    
    // reporting methods
    public final void reportExergyAnalysis() {
        System.out.println();
        System.out.println("Exergy Analysis");
        System.out.println("===============");
        System.out.println();
    }
    public final void reportResults() {
        System.out.println();
        System.out.println("RESULTS REPORT");
        System.out.println("==============");
        System.out.println();
        System.out.println("COMPONENT REPORT");
        System.out.println("----------------");
        components.forEach(c -> {c.reportResults(Cycle.doubleFormat);});
        System.out.println();
        System.out.println("CYCLE PERFORMANCE");
        System.out.println("-----------------");
        System.out.println("Thermal Efficiency: \t \t " + Cycle.percentageFormat.format(efficiencyThermal()) + "%");
        System.out.println("Rational Efficiency: \t \t " + Cycle.percentageFormat.format(efficiencyRational()) + "%");
        System.out.println("Net Work: \t \t \t " + Cycle.doubleFormat.format(workOut() - workIn()) + "W");
    }
    public final void reportSetup() {
        System.out.println();
        System.out.println("SETUP REPORT");
        System.out.println("============");
        System.out.println();
        System.out.println("ENVIRONMENT REPORT");
        System.out.println("----------------");
        System.out.println("Ambient pressure and temperature are: " + ambient.get(Property.PRESSURE) + " Pa and " + ambient.get(Property.TEMPERATURE)+ " K");
        System.out.println("System contains: " + fluids.size() + " Fluids");
        fluids.forEach(f -> {System.out.println(f);});
        System.out.println();
        System.out.println("COMPONENT REPORT");
        System.out.println("----------------");
        System.out.println("System contains: " + components.size() + " Components");
        components.forEach(c -> {c.reportSetup();});
        System.out.println();
        System.out.println("PATH REPORT");
        System.out.println("-----------");
        System.out.println("System contains: " + paths.size() + " Paths");
    }
    public final void  reportSolver() {
        System.out.println();
        System.out.println("Solver Report");
        System.out.println("=============");
        System.out.println();
        if (iteration.equals(Cycle.maxIterations)) {System.out.println("Maximum number of itterations reached: " + iteration + ". Solution is not complete.");}
        else {System.out.println("Solution achieved in " + iteration + " itterations.");}
        Set<Component> unsolved = new HashSet<>(components.stream().filter(c -> !c.isComplete()).collect(Collectors.toSet()));
        if (unsolved.size() > 0) {
            System.out.println("The following components are unsolved:");
            components.stream().filter(c -> !c.isComplete()).forEach(c -> {System.out.println(c.name);});
        }
        else {System.out.println("All components have been solved.");}
    }
}
