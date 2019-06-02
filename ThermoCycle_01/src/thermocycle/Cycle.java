/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import utilities.DimensionedDouble;
import report.ReportDataBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static thermocycle.Attributes.Attribute;
import static thermocycle.Properties.Property.*;
import static thermocycle.Node.Port;
import thermocycle.Properties.Property;
import report.ReportBuilder;
import report.Reportable;
import utilities.Units.UNITS_TYPE;
import utilities.SingletonCollector;
import static utilities.SingletonCollector.singletonCollector;

/**
 *
 * @author Chris
 */
public class Cycle extends Observable implements Serializable, Reportable {
    
    // static variables
    static private DecimalFormat doubleFormat = new DecimalFormat("#.#E0#");
    static private DecimalFormat percentageFormat = new DecimalFormat("##.#%");
    static private Integer maxIterations = 100;
    static private final Logger logger = LogManager.getLogger("DebugLog");
    static private final Logger report = LogManager.getLogger("ReportLog");
    
    // variables
    private String name;
    private final State ambient;
    private final ObservableList<Connection> connections;
    private final ObservableList<Component> components;
    private final ObservableList<Fluid> fluids;
    private final ObservableList<Set<FlowNode>> paths;
    private final ObservableList<BoundaryCondition> boundaryConditions;
    public final ObservableList<Connection> connectionsReadOnly;
    public final ObservableList<Component> componentsReadOnly;
    public final ObservableList<Fluid> fluidsReadOnly;
    public final ObservableList<Set<FlowNode>> pathsReadOnly;
    public final ObservableList<BoundaryCondition> boundaryConditionsReadOnly;
    private final ArrayList<ArrayList<Component>> results;
    
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
        boundaryConditions = FXCollections.observableList(new ArrayList<>());
        connectionsReadOnly = FXCollections.unmodifiableObservableList(connections);
        componentsReadOnly = FXCollections.unmodifiableObservableList(components);
        fluidsReadOnly = FXCollections.unmodifiableObservableList(fluids);
        pathsReadOnly = FXCollections.unmodifiableObservableList(paths);
        boundaryConditionsReadOnly = FXCollections.unmodifiableObservableList(boundaryConditions);
        ambient.setProperty(PRESSURE, 101325.0);
        ambient.setProperty(TEMPERATURE, 300.0);
        results = new ArrayList<>();
        
    }
    
    /**
     * Determines if the model connection contains the model node
     * @param connection The connection to get the nodes for.
     * @param node The node to look for.
     * @return true if the connection contains the node, otherwise returns false.
     */
    public boolean containsNode(Connection connection, Node node) {
        return connection.contains(node);
    }
    
    /**
     * Create a combustor
     * @param name The name to give the combustor.
     * @return the new combustor component
     */
    public Combustor createCombustor(String name) {
        components.add(new Combustor(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (Combustor)components.get(components.size() - 1);
    }
    
    /**
     * Create a compressor
     * @param name The name to give the compressor.
     * @return the new compressor object.
     */
    public Compressor createCompressor(String name) {
        components.add(new Compressor(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (Compressor)components.get(components.size() - 1);
    }
    
    /**
     * Creates a connection between two nodes.
     * @param node1 The first node.
     * @param node2 The second node.
     * @return the connection object.
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
        
        logger.trace("Created " + node1.getType() + " connection between " + getComponent(node1).name + " " + node1.port.toString() + " and " + getComponent(node2).name + " " + node2.port.toString());
        return connection;
    }
    
    /**
     * Gets the component that contains the node
     * @param node the node to search for.
     * @return the component that owns teh node.
     */
    private Component getComponent(Node node) {
        return this.components.stream().filter(c -> c.getNodes().contains(node)).collect(SingletonCollector.singletonCollector());
    }
    
    /**
     * Create a heat exchanger
     * @param name The name to give the heat exchanger.
     * @return the heat exchanger object.
     */
    public HeatExchanger createHeatExchanger(String name) {
        components.add(new HeatExchanger(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (HeatExchanger)components.get(components.size() - 1);
    }
    
    /**
     * Create a heat sink
     * @param name The name to give the heat sink.
     * @return the heat sink object.
     */
    public HeatSink createHeatSink(String name) {
        components.add(new HeatSink(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (HeatSink)components.get(components.size() - 1);
    }
    
    /**
     * Creates a new ideal gas.
     * @param name the name of the new ideal gas.
     * @param ga the ratio of specific heats for the new ideal gas.
     * @param r the specific gas constant for the new ideal gas.
     * @return an instance of the new ideal gas.
     */
    public IdealGas createIdealGas(String name, Double ga, Double r) {
        fluids.add(new IdealGas(name,ga,r));
        logger.info("Created " + fluids.get(fluids.size()-1));
        return (IdealGas)fluids.get(fluids.size()-1);
    }
    
    /**
     * Create steam.
     * @return an instance of the new steam fluid.
     */
    public Steam createSteam() {
        fluids.add(new Steam());
        logger.info("Created " + fluids.get(fluids.size()-1));
        return (Steam)fluids.get(fluids.size()-1);
    }
    
    /**
     * Create a turbine.
     * @param name The name to give the turbine.
     * @return the turbine object.
     */
    public Turbine createTurbine(String name) {
        components.add(new Turbine(name, ambient));
        cycleChange();
        logger.info("Created " + components.get(components.size()-1));
        return (Turbine)components.get(components.size() - 1);
    }
    
    /**
     * Finds flow paths and notifies listeners of a change to the cycle 
     */
    private void cycleChange() {
        pathFinder();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Gets the ambient state values
     * @param property The ambient state property to get.
     * @return Returns the ambient state property value.
     */
    public OptionalDouble getAmbient(Property property) {
        return ambient.getProperty(property);
    }
    
    /**
     * Get the set of attributes that belong to this component.
     * @param component The component of interest.
     * @return Returns a set of attributes belong to the component.
     */
    public Set<Attribute> getAttributes(Component component) {
        return component.getAllowableAtributes();
    }
    
    /**
     * Gets the attribute boundary condition on a component.
     * @param component The component to get the attribute boundary condition for.
     * @param attribute The attribute to get.
     * @return an optional containing the attribute boundary condition.
     */
    public Optional<BoundaryConditionAttribute> getBoundaryConditionAttribute(Component component, Attribute attribute) {
        return boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionAttribute(component, attribute, new ArrayList<Double>()))).map(bc -> (BoundaryConditionAttribute)bc).findFirst();
    }
    
    /**
     * Gets the heat boundary condition on a heat node.
     * @param node The heat node to get the boundary condition for.
     * @return an optional containing the heat boundary condition.
     */
    public Optional<BoundaryConditionHeat> getBoundaryConditionHeat(HeatNode node) {
        return boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionHeat(node, new ArrayList<Double>()))).map(bc -> (BoundaryConditionHeat)bc).findFirst();
    }
    
    /**
     * Gets the mass boundary condition on a flow node.
     * @param node The flow node to get the boundary condition for.
     * @return an optional containing the work boundary condition.
     */
    public Optional<BoundaryConditionMass> getBoundaryConditionMass(FlowNode node) {
        return boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionMass(node, new ArrayList<Double>()))).map(bc -> (BoundaryConditionMass)bc).findFirst();
    }
    
    /**
     * Gets the property boundary condition on a flow node.
     * @param node The flow node to get the property boundary condition for.
     * @param property The property to get.
     * @return an optional containing the property boundary condition.
     */
    public Optional<BoundaryConditionProperty> getBoundaryConditionProperty(FlowNode node, Property property) {
        return boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionProperty(node, property, new ArrayList<Double>()))).map(bc -> (BoundaryConditionProperty)bc).findFirst();
    }
    
    /**
     * Gets the work boundary condition on a work node.
     * @param node The work node to get the boundary condition for.
     * @return an optional containing the work boundary condition.
     */
    public Optional<BoundaryConditionWork> getBoundaryConditionWork(WorkNode node) {
        return boundaryConditions.stream().filter(bc -> bc.match(new BoundaryConditionWork(node, new ArrayList<Double>()))).map(bc -> (BoundaryConditionWork)bc).findFirst(); 
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
        components.forEach(c -> nodes.addAll(c.flowNodes.values()));
        return nodes;
    }
    
    /**
     * Gets the fluid for the flow node.
     * @param node The node to get the fluid for
     * @return REturns the fluid.
     */
    public Optional<Fluid> getFluid(FlowNode node) {
        return node.getFluid();
    }
    
    /**
     * Gets a fluid given its name.
     * @param name The name of the fluid to get.
     * @return Returns the fluid.
     */
    public Optional<Fluid> getFluid(String name) {
        return fluids.stream().filter(f -> f.name.equals(name)).findFirst();
    }
    
    /**
     * Gets all the component flow nodes in the cycle.
     * @return Returns a set of the component flow nodes in the cycle.
     */
    private Set<HeatNode> getHeatNodes() {
        Set<HeatNode> nodes = new HashSet<>();
        components.forEach(c -> nodes.addAll(c.heatNodes.values()));
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
        components.forEach(c -> nodes.addAll(c.workNodes.values()));
        return nodes;
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
     * Gets component plot data
     * @param component TEh component to get the plot data for.
     * @return A list of the internal plot data nodes.
     */
    public List<List<FlowNode>> plotData(Component component) {
        return component.plotData();
    }
    
    /**
     * Removes a boundary condition from the model
     * @param boundaryCondition The boundary condition to remove.
     */
    public void removeBoundaryCondition(BoundaryCondition boundaryCondition) {
        if (boundaryConditions.removeIf(bc -> bc.match(boundaryCondition))) {logger.info("Exisitng boundary condition removed.");};
    }
    
    /**
     * Removes a component from the cycle. All connections with the component are also removed.
     * @param component The component to remove.
     */
    public void removeComponent(Component component) {
        if (components.contains(component)) {
            component.flowNodes.values().forEach(n -> {
                removeBoundaryCondition(new BoundaryConditionMass(n, new ArrayList<Double>()));
                for (Property property : Properties.Property.values()) {
                    removeBoundaryCondition(new BoundaryConditionProperty(n, property, new ArrayList<Double>()));
                }
                removeConnection(n);
            });
            component.heatNodes.values().forEach(n -> {
                removeBoundaryCondition(new BoundaryConditionHeat(n, new ArrayList<Double>()));
                removeConnection(n);
            });
            component.workNodes.values().forEach(n -> {
                removeBoundaryCondition(new BoundaryConditionWork(n, new ArrayList<Double>()));
                removeConnection(n);
            });
            for (Attribute attribute : Attributes.Attribute.values()) {
                removeBoundaryCondition(new BoundaryConditionAttribute(component, attribute, new ArrayList<Double>()));
            }
            components.remove(component);
            cycleChange();
            logger.info("Removed " + component);
        }
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
     * Reset the cycle to its state prior to solving.
     */
    public final void reset() {
        // clears all values from components
        components.stream().forEach(c -> {
            c.clear();
        });
    }
    
    /**
     * Sets the ambient pressure and temperature
     * @param pressure The ambient pressure
     * @param temperature  The ambient temperature
     */
    public void setAmbient(double pressure, double temperature) {
        ambient.setProperty(PRESSURE, pressure);
        ambient.setProperty(TEMPERATURE, temperature);
    }
    
    /**
     * Set's an ambient property
     * @param property The property to set. Must be Pressure or Temperature.
     * @param value The value to set the property to.
     */
    public void setAmbient(Property property, double value) {
        if (property.equals(Properties.Property.PRESSURE) || property.equals(Properties.Property.TEMPERATURE)) {
            ambient.setProperty(property, value);
        }
        else {
            logger.warn("Cannot set ambient " + property.toString() + ". Ignoring command.");
        }
    }
    
    /**
     * Sets the attribute values for a component.
     * @param component The component.
     * @param attribute The attribute.
     * @param values The attribute values.
     * @return The new boundaryCondition
     */
    public BoundaryConditionAttribute setBoundaryConditionAttribute(Component component, Attribute attribute, List<Double> values) {
        BoundaryConditionAttribute boundaryCondition = new BoundaryConditionAttribute(component, attribute, values);
        boundaryConditions.removeIf(bc -> bc.match(boundaryCondition));
        boundaryConditions.add(boundaryCondition);
        return boundaryCondition;
    }
    
    /**
     * Sets the heat values for the heat node.
     * @param node The heat node.
     * @param values The heat values.
     * @return The new boundaryCondition
     */
    public BoundaryConditionHeat setBoundaryConditionHeat(HeatNode node, List<Double> values) {
        BoundaryConditionHeat boundaryCondition = new BoundaryConditionHeat(node, values);
        boundaryConditions.removeIf(bc -> bc.match(boundaryCondition));
        boundaryConditions.add(boundaryCondition);
        return boundaryCondition;
    }
    
    /**
     * Sets the mass values for the flow node.
     * @param node The flow node.
     * @param values The mass values.
     * @return The new boundaryCondition
     */
    public BoundaryConditionMass setBoundaryConditionMass(FlowNode node, List<Double> values) {
        BoundaryConditionMass boundaryCondition = new BoundaryConditionMass(node, values);
        boundaryConditions.removeIf(bc -> bc.match(boundaryCondition));
        boundaryConditions.add(boundaryCondition);
        return boundaryCondition;
    }
    
    /**
     * Sets the property values for the flow node.
     * @param node The flow node.
     * @param property The property.
     * @param values The property values.
     * @return The new boundaryCondition
     */
    public BoundaryConditionProperty setBoundaryConditionProperty(FlowNode node, Property property, List<Double> values) {
        BoundaryConditionProperty boundaryCondition = new BoundaryConditionProperty(node, property, values);
        boundaryConditions.removeIf(bc -> bc.match(boundaryCondition));
        boundaryConditions.add(boundaryCondition);
        return boundaryCondition;
    }
    
    /**
     * Sets the work values for the work node.
     * @param node The work node.
     * @param values The work values.
     * @return The new boundaryCondition
     */
    public BoundaryConditionWork setBoundaryConditionWork(WorkNode node, List<Double> values) {
        BoundaryConditionWork boundaryCondition = new BoundaryConditionWork(node, values);
        boundaryConditions.removeIf(bc -> bc.match(boundaryCondition));
        boundaryConditions.add(boundaryCondition);
        return boundaryCondition;
    }
    
    /**
     * Sets the fluid at a particular cycle node.
     * @param node The node in the system to set the fluid at. The methods sets all nodes in the same flow path to have the same fluid.
     * @param fluid The fluid to set.
     */
    public void setFluid(FlowNode node, Fluid fluid) {
        logger.info("Setting fluid to " + fluid.name);
        paths.stream().filter(p -> p.contains(node)).collect(singletonCollector()).forEach(n -> n.setFluid(fluid));
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
     * Sets the cycle name.
     * @param name The name of the cycle to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Solves the current cycle
     * @return Returns true if a solution is achieved.
     */
    public boolean solve(int idx) {
        logger.trace("Applying boundary conditions.");
        boundaryConditions.forEach(h -> {
            h.execute();
        });
        try {
            // Solve inital conditions
            getFlowNodes().forEach(n -> {n.computeState();});                                              // update all flow node states (ensures all nodes states are internally compatible first).
            connections.stream().forEach(x -> {x.update();});                                              // update all node connections (ensures state values are transfered between nodes, checks for inconsistencies).
            Set<Node> updatedNodes = new HashSet<>();
            // Loop until converged or max itterations reached
            Integer iteration = 0;
            do {
                updatedNodes.clear();
                iteration++;
                // for each unsolved component update the component and add updated nodes to the updated nodes
                components.filtered(c -> !c.isComplete()).forEach(c -> {
                    updatedNodes.addAll(c.update());
                });
                // for each updated node find connnections and update them 
                updatedNodes.forEach(n -> {
                    connections.stream().filter(x -> x.contains(n)).forEach(x -> {
                        x.update();
                    });
                });
            } while(updatedNodes.size() > 0 && iteration < Cycle.maxIterations);
            
            // Check to see if itteration limit reached
            if (iteration.equals(Cycle.maxIterations)) {
                logger.error("Maximum number of iterations reached.");
            }
            else {
                // Notify listeners
                setChanged();
                notifyObservers();
                logger.info("System solved.");
            }
            return (iteration < Cycle.maxIterations);
        }
        catch (Exception exc) {
            logger.error("Unknown exception");
            logger.error(exc.getMessage());
        }
        return false;
    }
    
    /**
     * Solve the cycle sweeping through the parameters
     */
    public boolean solveParametric() {
        
        logger.info("Performing parametric solve for " + this.name);
        
        // Get number of boundary condition values
        int max = boundaryConditions.stream().mapToInt(bc -> bc.getSize()).max().getAsInt();
        int min = boundaryConditions.stream().mapToInt(bc -> bc.getSize()).min().getAsInt();
        
        // Check boundary condition lengths
        if (min != max) {
            logger.warn("Boundary conditions are of differnet lengths. Minimum length is used.");
        }
        
        // Rest boundary condition index
        BoundaryCondition.setIndx(1);
        
        boolean trip = true;
        do {
            
            logger.trace("Solving for boundary condition Set " + BoundaryCondition.getIdx() + " of " + min);
            // Solve system for current index
            if (solve(BoundaryCondition.getIdx()) == false) {
                logger.info("BC Set " + BoundaryCondition.getIdx() + " failed.");
                trip = false;
            };
            
            // Copy results
            logger.debug("TO DO: Store parametric results.");
            
            // Increment boundary condition index
            BoundaryCondition.incrementIndex();
        
        } while (BoundaryCondition.getIdx() < min);
        
        report();
        
        return trip;
        
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
     * Saves the model to file
     * @param stream The output stream to save the model to.
     * @throws IOException 
     */
    public void saveModel(ObjectOutputStream stream) throws IOException {
        logger.info("Saving model.");
        stream.writeObject(name);
        stream.writeObject(ambient);
        logger.info("Saving components.");
        stream.writeObject(new ArrayList(components));
        logger.info("Saving connections.");
        stream.writeObject(new ArrayList(connections));
        logger.info("Saving fluids.");
        stream.writeObject(new ArrayList(fluids));
        logger.info("Saving paths.");
        stream.writeObject(new HashSet(paths));
        logger.info("Saving boundary conditions.");
        stream.writeObject(new ArrayList(boundaryConditions));
        logger.info("Saving compelte.");
    }
    
    /**
     * Loads the model from a file
     * @param stream The input stream to read the model from.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    public void loadModel(ObjectInputStream stream) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        logger.info("Loading model.");
        Class c = Cycle.class;
        Field ambientField = c.getDeclaredField("ambient");
        name = (String)stream.readObject();
        ambientField.setAccessible(true);
        ambientField.set(this, (State)stream.readObject());
        logger.info("Loading components.");
        components.addAll((List<Component>)stream.readObject());
        logger.info("Loading connections.");
        connections.addAll((List<Connection>)stream.readObject());
        logger.info("Loading fluids.");
        fluids.addAll((List<Fluid>)stream.readObject());
        logger.info("Loading paths.");
        paths.addAll((Set<FlowNode>)stream.readObject());
        logger.info("Loading boundary conditions.");
        boundaryConditions.addAll((ArrayList<BoundaryCondition>)stream.readObject());
        logger.info("Loading complete.");
    }
    
    /**
     * Generates a report after solving.
     */
    public void report() {
        PrintWriter out = null;
        try {
            out = new PrintWriter("Report.txt");
            out.write(ReportBuilder.generateReport(getReportData()).toString());
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cycle.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
    
    
    @Override
    public ReportDataBlock getReportData() {
        
        ReportDataBlock rdb = new ReportDataBlock(name);
        
        // Report ambient conditions
        ReportDataBlock ambientDataBlock = new ReportDataBlock("Ambient conditions");
        ambientDataBlock.addData("Pressure", DimensionedDouble.valueOfSI(ambient.getProperty(PRESSURE).getAsDouble(), UNITS_TYPE.PRESSURE));
        ambientDataBlock.addData("Temperature", DimensionedDouble.valueOfSI(ambient.getProperty(TEMPERATURE).getAsDouble(), UNITS_TYPE.TEMPERATURE));
        rdb.addDataBlock(ambientDataBlock);
        
        // Report fluids
        ReportDataBlock fluidsDataBlock = new ReportDataBlock("Fluids");
        fluids.stream().forEach(f -> {
            fluidsDataBlock.addDataBlock(f.getReportData());
        });
        rdb.addDataBlock(fluidsDataBlock);
        
        // Report paths
        ReportDataBlock pathsDataBlock = new ReportDataBlock("Paths");
        pathsDataBlock.addData("Number of streams", paths.size());
        fluids.stream().forEach(f -> {
            Long nStreams = paths.stream().map(p -> p.stream().findFirst().get().getFluid()).filter(o -> o.isPresent()).map(s -> s.get()).filter(r -> r.equals(f)).count();
            pathsDataBlock.addData(f.name, nStreams + " streams");
        });
        rdb.addDataBlock(pathsDataBlock);
        
        // Report components
        ReportDataBlock componentsDataBlock = new ReportDataBlock("Components");
        components.stream().forEach(c -> {
            componentsDataBlock.addDataBlock(c.getReportData());
        });
        rdb.addDataBlock(componentsDataBlock);
        
        // Report cycle performance
        ReportDataBlock performanceDataBlock = new ReportDataBlock("Cycle performance");
        //performanceDataBlock.addData("Thermal efficiency: ", DimensionedDouble.valueOfSI(efficiencyThermal(), UNITS_TYPE.DIMENSIONLESS));
        //performanceDataBlock.addData("Rational efficiency: ", DimensionedDouble.valueOfSI(efficiencyRational(), UNITS_TYPE.DIMENSIONLESS));
        //performanceDataBlock.addData("Heat input: ", DimensionedDouble.valueOfSI(this.heatIn(), UNITS_TYPE.POWER));
        //performanceDataBlock.addData("Heat output: ", DimensionedDouble.valueOfSI(this.heatOut(), UNITS_TYPE.POWER));
        //performanceDataBlock.addData("Work input: ", DimensionedDouble.valueOfSI(this.workIn(), UNITS_TYPE.POWER));
        //performanceDataBlock.addData("Work output: ", DimensionedDouble.valueOfSI(this.workOut(), UNITS_TYPE.POWER));
        rdb.addDataBlock(performanceDataBlock);
        
        return rdb;
    }
    
}
