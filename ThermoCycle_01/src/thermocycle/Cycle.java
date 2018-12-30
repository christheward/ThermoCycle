/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static thermocycle.Attributes.Attribute;
import static thermocycle.Properties.Property.*;
import static thermocycle.Node.Port;
import utilities.GsonHandler;
import static utilities.SingletonCollector.singletonCollector;

/**
 *
 * @author Chris
 */
public class Cycle extends Observable implements Properties, Serializable {
    
    // static variables
    static private DecimalFormat doubleFormat = new DecimalFormat("#.#E0#");
    static private DecimalFormat percentageFormat = new DecimalFormat("##.#%");
    static private Integer maxIterations = 100;
    static private final Logger logger = LogManager.getLogger("DebugLog");
    static private final Logger report = LogManager.getLogger("ReportLog");
    
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
    private final ArrayDeque<BoundaryCondition> boundaryConditions;
    
    /**
     * Constructor
     * @param name The name of the new cycle. 
     */
    public Cycle(String name) {
        
        // Initialise cycle components
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
        ambient.put(PRESSURE, OptionalDouble.of(101325.0));
        ambient.put(TEMPERATURE, OptionalDouble.of(300.0));
        boundaryConditions = new ArrayDeque();
        
        // Add  default fluid - find a better wt to to this later.
        this.createIdealGas("Air", 1.4, 287.0);
        this.createIdealGas("N2", 1.4, 287.0);
    }
    
    /**
     * Gets the ambient state values
     * @param property The ambient state property to get.
     * @return Returns the ambient state property value.
     */
    public OptionalDouble getAmbient(Property property) {
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
     * Gets the name of a component
     * @param component The component to get the name of.
     * @return The component name.
     */
    public String getName(Component component) {
        return component.name;
    }
    
    /**
     * Gets all the component work nodes in the cycle.
     * @return Returns a set of the component work nodes in the cycle.
     */
    private Set<WorkNode> getWorkNodes() {
        Set<WorkNode> nodes = new HashSet<>();
        components.forEach(c -> nodes.addAll(c.workNodes));
        return nodes;
    }
    
    /**
     * Gets all the component flow nodes in the cycle.
     * @return Returns a set of the component flow nodes in the cycle.
     */
    private Set<HeatNode> getHeatNodes() {
        Set<HeatNode> nodes = new HashSet<>();
        components.forEach(c -> nodes.addAll(c.heatNodes));
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
     * Gets the fluid for the flow node.
     * @param node The node to get the fluid for
     * @return REturns the fluid.
     */
    public Fluid getFluid(FlowNode node) {
        return node.getFluid();
    }
    
    /**
     * Determine if thee fluid has been set for this flow node
     * @param node The flow node to check
     * @return A boolean
     */
    public Boolean isFluidSet(FlowNode node) {
        return node.isFluidSet();
    }
    
    /**
     * Gets the cycle name.
     * @return REturns the cycle name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the work value for the work node.
     * @param node The work node.
     * @return The work value.
     */
    /**
    public OptionalDouble getWork(WorkNode node) {
        return node.getWork();
    }
    */
    public OptionalDouble getWorkBoundaryCondition(WorkNode node) {
        BoundaryCondition match = boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionWork(node, OptionalDouble.empty()))).collect(singletonCollector());
        if (match == null) {
            return OptionalDouble.empty();
        }
        return match.value();
    }
    
    /**
     * Gets the heat value for the heat node.
     * @param node The heat node.
     * @return The heat value.
     */
    /**
    public OptionalDouble getHeat(HeatNode node) {
        return node.getHeat();
    }
    */
    public OptionalDouble getHeatBoundaryCondition(HeatNode node) {
        BoundaryCondition match = boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionHeat(node, OptionalDouble.empty()))).collect(singletonCollector());
        if (match == null) {
            return OptionalDouble.empty();
        }
        return match.value();
    }
    
    /**
     * Gets the mass value for the flow node.
     * @param node The flow node.
     * @return The mass value.
     */
    /**
    public OptionalDouble getMass(FlowNode node) {
        return node.getMass();
    }
    */
    public OptionalDouble getMassBoundaryCondition(FlowNode node) {
        BoundaryCondition match = boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionMass(node, OptionalDouble.empty()))).collect(singletonCollector());
        if (match == null) {
            return OptionalDouble.empty();
        }
        return match.value();
    }
    
    /**
     * Gets the state value for the flow node.
     * @param node The flow node.
     * @param property The property to get.
     * @return The state value.
     */
    public OptionalDouble getProperty(FlowNode node, Property property) {
        return node.getState(property);
    }
    public OptionalDouble getPropertyBoundaryCondition(FlowNode node, Property property) {
        BoundaryCondition match = boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionProperty(node, property, OptionalDouble.empty()))).collect(singletonCollector());
        if (match == null) {
            return OptionalDouble.empty();
        }
        return match.value();
    }
    
    /**
     * Get the value of the attribute belonging to this component.
     * @param component The component of interest.
     * @param attribute The attribute of interest.
     * @return Returns the value of the attribute belonging to the component.
     */
    /**
    public OptionalDouble getAttribute(Component component, Attribute attribute) {
        return component.getAttribute(attribute);
    }
    */
    public OptionalDouble getAttributeBoundaryCondition(Component component, Attribute attribute) {
        BoundaryCondition match = boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionAttribute(component, attribute, OptionalDouble.empty()))).collect(singletonCollector());
        if (match == null) {
            return OptionalDouble.empty();
        }
        return match.value();
    }
    
    /**
     * Clears the nodes values.
     * @param node The node to clear.
     */
    public void clearValue(Node node) {
        node.clear();
    }
    
    /**
     * Clears a node's mass flow rate value.
     * @param node The node's mass flow rate to clear.
     */
    public void clearMass(FlowNode node) {
        node.clearMass();
    }
    
    /**
     * Clears a node's property.
     * @param node The node to clear a property from.
     * @param property The property to clear.
     */
    public void clearState(FlowNode node, Property property) {
        node.clearState(property);
    }
    
    /**
     * Clears all a node's properties
     * @param node The node to clear properties from.
     */
    public void clearState(FlowNode node) {
        node.clearState();
    }
    
    /**
     * Clears a component's attribute.
     * @param component The component to clear and attribute from.
     * @param attribute The attribute to clear.
     */
    public void clearAttribute(Component component, Attribute attribute) {
        component.clearAttribute(attribute);
    }
    
    /**
     * Gets all the allowable properties for a flow node based on it's fluid type
     * @param node The flow node to get allowable properties for.
     * @return A set of allowable properties
     */
    /**
    public Set<Property> getAllowableProperties(FlowNode node) {
        return node.allowableProperties();
    }
    */
    
    /**
     * Gets all the allowable properties for a fluid type
     * @param node The flow node to get allowable properties for.
     * @return A set of allowable properties
     */
    public Set<Property> getAllowableProperties(Fluid fluid) {
        return fluid.fluidState();
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
        ambient.put(PRESSURE, OptionalDouble.of(pressure));
        ambient.put(TEMPERATURE, OptionalDouble.of(temperature));
    }
    
    /**
     * Set's an ambient property
     * @param property The property to set. Must be Pressure or Temperature.
     * @param value The value to set the property to.
     */
    public void setAmbient(Property property, double value) {
        if (property.equals(Properties.Property.PRESSURE) || property.equals(Properties.Property.TEMPERATURE)) {
            ambient.put(property, OptionalDouble.of(value));
        }
        else {
            logger.warn("Cannot set ambient " + property.toString() + ". Ignoring command.");
        }
    }
    
    /**
     * Sets the maximum number of allowable iterations when solving
     * @param iterations The maximum number of allowable iterations to set.
     */
    public void setMaxIterations(int iterations) {
        Cycle.maxIterations = iterations;
        logger.info("Maximum number of iterations set to %", Integer.toString(Cycle.maxIterations));
    }
    
    /**
     * Sets the cycle name.
     * @param name The name of the cycle to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets a component name ensuring uniqueness.
     * @param component The component to set the name of.
     * @param name The new name.
     */
    public void setName(Component component, String name) {
        if (componentsReadOnly.stream().filter(c -> c.name.equals(name)).count() == 0) {
            component.name = name;
        }
        else {
            try {
                String baseName = name.substring(1,name.length()-1);
                String lastCharacter = name.substring(name.length()-1);
                int newNumber = Integer.parseInt(lastCharacter) + 1;
                setName(component, baseName + String.valueOf(newNumber));
            }
            catch (NumberFormatException e) {
                setName(component, name + "_1");
            }
        }
    }
    
    /**
     * Sets the work value for the work node.
     * @param node The work node.
     * @param value The work value.
     */
    public void setWork(WorkNode node, double value) {
        boundaryConditions.add(new BoundaryConditionWork(node, OptionalDouble.of(value)));
    }

    /**
     * Sets the heat value for the heat node.
     * @param node The heat node.
     * @param value The heat value.
     */
    public void setHeat(HeatNode node, double value) {
        boundaryConditions.add(new BoundaryConditionHeat(node, OptionalDouble.of(value)));
    }
    
    /**
     * Sets the mass value for the flow node.
     * @param node The flow node.
     * @param value The mass value.
     */
    public void setMass(FlowNode node, double value) {
        boundaryConditions.add(new BoundaryConditionMass(node, OptionalDouble.of(value)));
    }
    
    /**
     * Sets the property value for the flow node.
     * @param node The flow node.
     * @param property The state property to set.
     * @param value The property value.
     */
    public void setState(FlowNode node, Property property, double value) {
        boundaryConditions.add(new BoundaryConditionProperty(node, property, OptionalDouble.of(value)));
    }
    
    /**
     * Sets a component attribute
     * @param component The component to set.
     * @param attribute The attribute to set.
     * @param value The value to set thee component attribute to.
     */
    public void setAttribute(Component component, Attribute attribute, double value) {
        boundaryConditions.add(new BoundaryConditionAttribute(component, attribute, OptionalDouble.of(value)));
        logger.info(component + " " + attribute + " set to " + value);
    }
    
    /**
     * Sets the fluid at a particular cycle node.
     * @param node The node in the system to set the fluid at. The methods sets all nodes in the same flow path to have the same fluid.
     * @param fluid The fluid to set.
     */
    public void setFluid(FlowNode node, Fluid fluid) {
        //boundaryConditions.add(new SetFluid(node, fluid));
        paths.stream().filter(p -> p.contains(node)).collect(singletonCollector()).forEach(n -> n.setFluid(fluid));
    }
    
    /**
     * Finds flow paths and notifies listeners of a change to the cycle 
     */
    private void cycleChange() {
        pathFinder();
        setChanged();
        notifyObservers();
        logger.trace("Cycle change notification.");
    }
    
    /**
     * Component creation methods
     */
    public Combustor createCombustor(String name) {
        components.add(new Combustor(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (Combustor)components.get(components.size() - 1);
    }
    public Compressor createCompressor(String name) {
        components.add(new Compressor(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (Compressor)components.get(components.size() - 1);
    }
    public HeatExchanger createHeatExchanger(String name) {
        components.add(new HeatExchanger(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (HeatExchanger)components.get(components.size() - 1);
    }
    public HeatSink createHeatSink(String name) {
        components.add(new HeatSink(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (HeatSink)components.get(components.size() - 1);
    }
    public Turbine createTurbine(String name) {
        components.add(new Turbine(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (Turbine)components.get(components.size() - 1);
    }
    
    /**
     * Removes an existing boundary condition
     * @param bc 
     */
    public void removeBoundaryCondition(HeatNode node) {
        boundaryConditions.removeIf(bc -> bc.match(new BoundaryConditionHeat(node, OptionalDouble.empty()))); 
    }
    public void removeBoundaryCondition(WorkNode node) {
        boundaryConditions.removeIf(bc -> bc.match(new BoundaryConditionWork(node, OptionalDouble.empty()))); 
    }
    public void removeBoundaryCondition(FlowNode node) {
        boundaryConditions.removeIf(bc -> bc.match(new BoundaryConditionMass(node, OptionalDouble.empty()))); 
    }
    public void removeBoundaryCondition(FlowNode node, Property property) {
        boundaryConditions.removeIf(bc -> bc.match(new BoundaryConditionProperty(node, property, OptionalDouble.empty()))); 
    }
    public void removeBoundaryCondition(Component component, Attribute attribute) {
        boundaryConditions.removeIf(bc -> bc.match(new BoundaryConditionAttribute(component, attribute, OptionalDouble.empty()))); 
    }
    
    /**
     * Removes a component from the cycle. All connections with the component are also removed.
     * @param component The component to remove.
     */
    public void removeComponent(Component component) {
        if (components.contains(component)) {
            component.flowNodes.forEach(n -> {
                removeBoundaryCondition(n);
                for (Property property : Properties.Property.values()) {
                    removeBoundaryCondition(n, property);
                }
                removeConnection(n);
            });
            component.heatNodes.forEach(n -> {
                removeBoundaryCondition(n);
                removeConnection(n);
            });
            component.workNodes.forEach(n -> {
                removeBoundaryCondition(n);
                removeConnection(n);
            });
            for (Attribute attribute : Attributes.Attribute.values()) {
                removeBoundaryCondition(component, attribute);
            }
            components.remove(component);
            cycleChange();
            logger.info("Removed " + component);
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
        logger.info("Created " + fluids.get(fluids.size()-1));
        return (IdealGas)fluids.get(fluids.size()-1);
    }
    
    /**
     * Exports the current fluids to  library
     * @param filename The file name of the library file.
     * @return 
     */
    public void exportFluidLibrary(File filename) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename))) {
            os.writeObject(fluids);
            logger.info("Fluids library exported to: " + filename.toString());
        }
        catch(IOException e) {
            logger.error("Error writing fluids library. " + e.getMessage());
        }
    }
    
    /**
     * Imports the fluids library from file
     * @param filename The file name of the library file.
     * @return 
     */
    public void importFluidLibrary(File filename) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename))) {
            fluids.clear();
            fluids.addAll((ObservableList<Fluid>)is.readObject());
            logger.info("Fluids library impotred from: " + filename.toString());
        }
        catch(IOException e) {
            logger.error("Error impoting fluids library. " + e.getMessage());
        }
        catch(ClassNotFoundException e) {
            logger.error("Class not found. " + e.getMessage());
        }
    }
    
    /**
     * Creates a connection between two nodes.
     * @param node1 The first node.
     * @param node2 The second node.
     * @return Returns the connection object.
     */
    public Connection createConnection(Node node1, Node node2) {
        // check n1 is not currently connected to other nodes
        if (connections.stream().anyMatch(c -> c.contains(node1))) {
            logger.warn("Cannot create connection - Node 1 is already connected.");
            throw new IllegalStateException("Node is already connected.");
        }
        // check n2 is not currently connected to other nodes
        if (connections.stream().anyMatch(c -> c.contains(node2))) {
            logger.warn("Cannot create connection - Node 2 is already connected.");
            throw new IllegalStateException("Node is already connected.");
        }
        Connection connection = new Connection(node1,node2);
        connections.add(connection);
        cycleChange();
        logger.trace("Created connection");
        return connection;
    }
        
    /**
     * Removes a connection from the model.
     * @param connection The connection to remove.
     */
    public void removeConnection(Connection connection) {
        connections.remove(connection);
        cycleChange();
        logger.trace("Removed connection");
    }
    
    /**
     * Removes connections from the model that contain this node.
     * @param node The node to remove connections from.
     */
    private void removeConnection(Node node) {
        connections.removeAll(connections.stream().filter(c -> c.contains(node)).collect(Collectors.toSet()));  // only remove connections beloning to the cycle, not internal connections belonging to the component.
        cycleChange();
        logger.trace("Removed connection");
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
    
    public List<List<FlowNode>> plotData(Component component) {
        return component.plotData();
    }
    
    /**
     * Gets a list of all the variables names and values.
     * @return Returns a HashMap of all the variables and unique names.
     */
    private Map<String, OptionalDouble> getVariables() {
        Map variables = new HashMap<String, OptionalDouble>();
        
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
     * Reset the cycle to its state prior to solving.
     */
    public final void reset() {
        // clears all values from components
        components.stream().forEach(c -> {
            c.clear();
        });
    }
    
    
    /**
     * Solves the current cycle
     * @return Returns true if a solution is achieved.
     */
    public boolean solve() {
        logger.info("Applying boundary conditions");
        boundaryConditions.forEach(h -> h.execute());
        logger.info("Solving system");
        try {
            // Solve inital conditions
            getFlowNodes().forEach(n -> {n.computeState();});                                              // update all flow node states (ensures all nodes states are internally compatible first).
            connections.stream().forEach(x -> {x.update();});                                              // update all node connections (ensures state values are transfered between nodes, checks for inconsistencies).
            Set<Component> unsolved = new HashSet<>(components);                                           // update all connections (updates nodes across components)
            Set<Node> updatedNodes = new HashSet<>();
            // Loop until converged or max itterations reached
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
            
            // Check to see if itteration limit reached
            if (iteration.equals(Cycle.maxIterations)) {
                logger.error("Maximum number of iterations reached");
            }
            else {
                // Notify listeners
                setChanged();
                notifyObservers();
                logger.info("System solved");
            }
            return unsolved.isEmpty();
        }
        catch (Exception exc) {
        }
        return false;
    }
    
    /**
     * Solve the cycle sweeping through the parameters
     */
    public void solveParametric() {
        // Loop
        
                                            // Set the parameteer value
        solve();                            // Sole the system
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
     * Calculate the work input to the cycle using connections. This assumes that all optionalDoubles are complete.
     * @return Returns the total work input
     */
    public double workIn() {
        return getWorkNodes().stream().filter(n -> notConnected(n)).filter(n -> n.port.equals(Port.INLET)).mapToDouble(n -> n.getWork().getAsDouble()).sum();
    }
    
    /**
     * Calculate the work output from the cycle using connections. This assumes that all optionalDoubles are complete.
     * @return Returns the total work input
     */
    public double workOut() {
        return getWorkNodes().stream().filter(n -> notConnected(n)).filter(n -> n.port.equals(Port.OUTLET)).mapToDouble(n -> n.getWork().getAsDouble()).sum();
    }
    
    /**
     * Calculate the heat input to the cycle using connections. This assumes that all optionalDoubles are complete.
     * @return Returns the total work input
     */
    public double heatIn() {
        return getHeatNodes().stream().filter(n -> notConnected(n)).filter(n -> n.port.equals(Port.INLET)).mapToDouble(n -> n.getHeat().getAsDouble()).sum();
    }
    
    /**
     * Calculate the heat output from the cycle using connections. This assumes that all optionalDoubles are complete.
     * @return Returns the total work input
     */
    public double heatOut() {
        return getHeatNodes().stream().filter(n -> notConnected(n)).filter(n -> n.port.equals(Port.OUTLET)).mapToDouble(n -> n.getHeat().getAsDouble()).sum();
    }
    
    /**
     * Determines in the node is connected to another node in the system.
     * @param n The node of interest.
     * @return Returns true if the node is connected to another node.
     */
    private boolean notConnected(Node n) {
        return connectionsReadOnly.filtered(c -> c.contains(n)).isEmpty();
    }
        
    /**
     * Sets the fluid of a flow path
     */
    /**
    public class SetFluid extends Condition {
        
        private final FlowNode node;
        private final Fluid fluid;
        
        public SetFluid(FlowNode node, Fluid fluid) {
            this.node = node;
            this.fluid = fluid;
        }
        
        @Override
        public void execute() {
            paths.stream().filter(p -> p.contains(node)).collect(singletonCollector()).forEach(n -> n.setFluid(fluid));
        }
        
        @Override
        public boolean match(Condition cnd) {
            if (cnd instanceof SetFluid) {
                if (this.node == ((SetFluid) cnd).node) {
                        return true;
                }
            }
            return false;    
        }
    }
    **/
    
}
