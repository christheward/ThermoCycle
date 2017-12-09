/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import static java.awt.geom.PathIterator.*;
import static thermocycle.Properties.Property.*;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import marchingsquares.Algorithm;
import java.io.Serializable;
import java.text.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author Chris
 */
public class Cycle implements Properties, Ports, Serializable {
    
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
    private final Set<Connection> connections;
    private final Set<Component> components;
    private final Set<Fluid> fluids;
    private final State ambient;
    private final List<Set<FlowNode>> paths;
    private Integer iteration;
    
    /**
     * Constructor
     * @param name The name of the new cycle. 
     */
    public Cycle(String name) {
        this.name = name;
        connections = new LinkedHashSet<>();
        components = new LinkedHashSet<>();
        fluids = new LinkedHashSet<>();
        ambient = new State();
        paths = new ArrayList<>();
        ambient.putIfAbsent(PRESSURE, OptionalDouble.of(101325));
        ambient.putIfAbsent(TEMPERATURE, OptionalDouble.of(300));
    }
    
    // getters
    //State getAmbient() {return ambient;}
    OptionalDouble getAmbient(Property p) {return ambient.get(p);}
    
    /**
     * Gets a component given its id.
     * @param id The id of the component.
     * @return Returns the component.
     */
    Component getComponent(int id) {
        return components.stream().filter(c -> c.id == id).collect(singletonCollector());
    }
    
    /**
     * Gets a component given its name.
     * @param name The id of the component.
     * @return Returns the component.
     */
    Component getComponent(String name) {
        return components.stream().filter(c -> c.name.equals(name)).collect(singletonCollector());
    }
    
    public Set<Component> getComponents() {return components;}
    public Set<Connection> getConnections() {return connections;};
    
    /**
     * Gets all the flow connections in the cycle
     * @return A set of the flow connections.
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
    private Fluid getFluid(int id) {return fluids.stream().filter(f -> f.getId() == id).collect(singletonCollector());}
    Fluid getFluid(String name) {return fluids.stream().filter(f -> f.getName().equals(name)).collect(singletonCollector());}
    public Set<Fluid> getFluids() {return fluids;}
    String getName(){return name;}
    private Set<FlowNode> getPath(FlowNode n) {return paths.stream().filter(p -> p.contains(n)).collect(singletonCollector());}
    //private Component getNode(int id) {return getComponents().stream().filter(c -> c.getNodes().stream().anyMatch(n -> n.getId() == id)).collect(singletonCollector());}
    
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
     * Sets the fluid at a particular cycle node.
     * @param node The node in the system to set the fluid at. The methods sets all nodes in the same flow path to have the same fluid.
     * @param fluid The fluid to set.
     */
    public void setFluid(FlowNode node, Fluid fluid) {
        paths.stream().filter(p -> p.contains(node)).collect(singletonCollector()).forEach(n -> n.setFluid(fluid));
    }
    
    // Build methods
    // =============
    // components
    public Combustor createCombustor(String s) {
        components.add(new Combustor(s, ambient));
        pathFinder();
        return (Combustor)getComponent(Component.idCount());
    }
    public Compressor createCompressor(String s) {
        components.add(new Compressor(s, ambient));
        pathFinder();
        return (Compressor)getComponent(Component.idCount());
    }
    public HeatExchanger createHeatExchanger(String s) {
        components.add(new HeatExchanger(s, ambient));
        pathFinder();
        return (HeatExchanger)getComponent(Component.idCount());
    }
    public HeatSink createHeatSink(String s) {
        components.add(new HeatSink(s, ambient));
        pathFinder();
        return (HeatSink)getComponent(Component.idCount());
    }
    public Turbine createTurbine(String s) {
        components.add(new Turbine(s, ambient));
        pathFinder();
        return (Turbine)getComponent(Component.idCount());
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
            pathFinder();
        }
    }
    
    // fluids
    public IdealGas createIdealGas(String s, Double ga, Double r) {
        fluids.add(new IdealGas(s,ga,r));
        return (IdealGas)getFluid(Fluid.idCount());
    }

    // connections
    public boolean createConnection(Node n1, Node n2) {
        if (connections.stream().anyMatch(c -> c.contains(n1))) {
            throw new IllegalStateException("Node [" + n1.id + "] is already connected.");
        }  // check n1 is not currently connected to other nodes
        if (connections.stream().anyMatch(c -> c.contains(n2))) {
            throw new IllegalStateException("Node [" + n2.id + "] is already connected.");
        }  // check n2 is not currently connected to other nodes
        pathFinder();
        return connections.add(new Connection(n1,n2));
    }
    public void removeConnection(Node node) {
        connections.removeAll(connections.stream().filter(c -> c.contains(node)).collect(Collectors.toSet()));  // only remove connections beloning to the cycle, not internal connections belonging to the component.
        pathFinder();
    }
    public void removeConnection(Connection connection) {
        connections.remove(connection);
    }
    
    // flow paths
    private Set<FlowNode> nodeNeighbours(FlowNode n) {                                                                      // get flow nodes neighbours
        Set<FlowNode> neighbours = new HashSet<>();
        getFlowConnections().forEach(c -> neighbours.add((FlowNode)c.getPair(n)));                                     // because n is a FlowNode the getPair(n) is also always a FlowNode
        neighbours.remove(null);                                                                                            // remove the null neighbour which occurs if a node is not connected to another
        return neighbours;
    }
    private Set<FlowNode> pathFinder(FlowNode start) {                                                                      // get all flow nodes in flow path containing specified start node
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
    
    private void pathFinder() {                                                                                           // find all distinct paths in current setup
        Set<FlowNode> nodes = new HashSet();
        nodes.addAll(getFlowNodes());
        paths.clear();
        while (!nodes.isEmpty()) {
            paths.add(pathFinder(nodes));
            nodes.removeAll(paths.get(paths.size()-1));
        }
    }
        // solve methods
    // =============
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
                    comp.getNodes().forEach(n -> {
                        System.out.println(n.toString());
                    });
                    throw new IllegalStateException("Components are incompatible!!!");
                }
            });
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
     * @return Returns thee rational efficiency.
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
    
    // reporting methods
    final void reportError() {
        System.out.println();
        System.out.println("Error Report");
        System.out.println("============");
        System.out.println();
    }
    final void reportExergyAnalysis() {
        System.out.println();
        System.out.println("Exergy Analysis");
        System.out.println("===============");
        System.out.println();
        // collate data
        DefaultPieDataset dataset = new DefaultPieDataset();
        components.stream().forEach(c -> {
            dataset.setValue(c.name, c.exergyLoss());
            System.out.println(c.name + ": " + Cycle.doubleFormat.format(c.exergyLoss()) + "W");
        });
        JFreeChart chart = ChartFactory.createPieChart("Exergy Analysis", dataset);
        // plot figure
        ChartFrame frame = new ChartFrame(name + " Exergy Anlysis", chart);
        frame.pack();
        frame.setVisible(true);
    }
    final void reportResults() {
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
    final void reportSetup() {
        System.out.println();
        System.out.println("SETUP REPORT");
        System.out.println("============");
        System.out.println();
        System.out.println("ENVIRONMENT REPORT");
        System.out.println("----------------");
        System.out.println("Ambient pressure and temperature are: " + ambient.get(Property.PRESSURE) + " Pa and " + ambient.get(Property.TEMPERATURE)+ " K");
        System.out.println("System contains: " + fluids.size() + " Fluids");
        fluids.forEach(f -> {f.report();});
        System.out.println();
        System.out.println("COMPONENT REPORT");
        System.out.println("----------------");
        System.out.println("System contains: " + components.size() + " Components");
        components.forEach(c -> {c.reportSetup();});
        System.out.println();
        System.out.println("PATH REPORT");
        System.out.println("-----------");
        System.out.println("System contains: " + paths.size() + " Paths");
        paths.forEach(p -> {
            System.out.print("Path [" + (paths.indexOf(p)+1) + "] contains " + p.size() + " nodes: ");
            p.forEach(n -> {System.out.print(" [" + n.id + "]");});
            System.out.print("\n");
        });
    }
    final void  reportSolver() {
        System.out.println();
        System.out.println("Solver Report");
        System.out.println("=============");
        System.out.println();
        if (iteration.equals(Cycle.maxIterations)) {System.out.println("Maximum number of itterations reached: " + iteration + ". Solution is not complete.");}
        else {System.out.println("Solution achieved in " + iteration + " itterations.");}
        Set<Component> unsolved = new HashSet<>(components.stream().filter(c -> !c.isComplete()).collect(Collectors.toSet()));
        if (unsolved.size() > 0) {
            System.out.println("The following components are unsolved:");
            components.stream().filter(c -> !c.isComplete()).forEach(c -> {System.out.println("\t[" + c.id + "] " + c.name);});
        }
        else {System.out.println("All components have been solved.");}
    }
    
    // plotting methodss
    final XYPlot plot(Property X, Property Y) {
        // collate data
        DefaultXYDataset dataset = new DefaultXYDataset();
        XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer();
        components.stream().forEach(c -> {
            int nSets = c.plotData(dataset, X, Y);
            for (int n = 1; n <=  nSets; n++) {
                r1.setSeriesPaint(dataset.getSeriesCount() - n, Color.BLUE);
                r1.setSeriesShapesVisible(dataset.getSeriesCount() - n, false);
                r1.setSeriesVisibleInLegend(dataset.getSeriesCount() - n, true);
            }
        });
        // format plot
        JFreeChart chart = ChartFactory.createXYLineChart(X.symbol + "-" + Y.symbol + " diagram", X.fullName + " [" + X.units + "]", Y.fullName + " [" + Y.units + "] ", dataset);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRenderer(r1);
        chart.removeLegend();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.GRAY);
        // plot figure
        ChartFrame frame = new ChartFrame(name + " Cycle Performance",chart);
        frame.pack();
        frame.setVisible(true);
        return plot;
    }
    final void plotContours(Property X, Property Y, Property Z, double[] contourValues) throws InterruptedException, ExecutionException {
        XYPlot plot = plot(X, Y);
        DefaultXYDataset dataset = (DefaultXYDataset)plot.getDataset();
        XYLineAndShapeRenderer r1 = (XYLineAndShapeRenderer)plot.getRenderer();
        // create contour grid
        int xPoints = 1000;
        int yPoints = 1000;
        ValueAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        Double xMin = xAxis.getLowerBound();
        Double xMax = xAxis.getUpperBound();
        Double yMin = yAxis.getLowerBound();
        Double yMax = yAxis.getUpperBound();
        Double xDelta = (xMax - xMin)/(double)(xPoints - 1);
        Double yDelta = (yMax - yMin)/(double)(yPoints - 1);
        // collate data
        State s = new State();
        double[][] data = new double[xPoints][yPoints];
        Double zMin = null;
        Double zMax = null;
        for (int i=0; i<xPoints; i++) {
            for (int j=0; j<yPoints; j++) {
                s.putIfAbsent(X, OptionalDouble.of(xMin + xDelta*i));
                s.putIfAbsent(Y, OptionalDouble.of(yMin + yDelta*j));
                // TODO: option to choose which fluid to use for contours
                getFluids().stream().findFirst().get().computeState(s);
                if (s.get(Z) == null) {data[i][j] = 0;}
                else {data[i][j] = s.get(Z).getAsDouble();}
                if((i==0) & (j==0)) {zMin = s.get(Z).getAsDouble(); zMax = s.get(Z).getAsDouble();}
                else if (s.get(Z).getAsDouble() < zMin) {zMin = s.get(Z).getAsDouble();}
                else if (s.get(Z).getAsDouble() > zMax) {zMax = s.get(Z).getAsDouble();}
                s.reset();
            }
        }
        // reset axis limits
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        // calculate contourLines
        Algorithm marchingSquares = new Algorithm();
        GeneralPath[] contours;
        contours = marchingSquares.buildContours(data, contourValues);
        paths2series(contours, new double[] {xMin, yMin}, new double[] {xDelta, yDelta}).forEach((double[][] contour) -> {
            dataset.addSeries(Arrays.hashCode(contour),contour);
            r1.setSeriesPaint(dataset.getSeriesCount() - 1, Color.GRAY);
            r1.setSeriesShapesVisible(dataset.getSeriesCount() - 1, false);
            r1.setSeriesVisibleInLegend(dataset.getSeriesCount() - 1, false);
        });
    }
    static List<double[][]> paths2series(GeneralPath[] paths, double[] min, double[] delta) {
        // check
        if (min.length != 2) {throw new IllegalStateException("X and Y minimum values required.");}
        if (delta.length != 2){throw new IllegalStateException("X and Y delta values required.");}
        List<double[][]> pointsLists = new ArrayList<>();
        List<double[]> pointsList = new ArrayList<>();
        List<Double> xPointsList = new ArrayList<>();
        List<Double> yPointsList = new ArrayList<>();
        double[] coords = new double[6];
        // loop over al paths
        for (GeneralPath path : paths) {
            // iterate over each general path
            for (PathIterator pi = path.getPathIterator(null); !pi.isDone(); pi.next()) {
                switch (pi.currentSegment(coords)) {
                    case SEG_MOVETO: {pointsList.add(Arrays.copyOfRange(coords,0,2)); break;}
                    case SEG_LINETO: {pointsList.add(Arrays.copyOfRange(coords,0,2)); break;}
                    case SEG_QUADTO: {pointsList.add(Arrays.copyOfRange(coords,0,2)); pointsList.add(Arrays.copyOfRange(coords,2,4)); break;}
                    case SEG_CUBICTO: {pointsList.add(Arrays.copyOfRange(coords,0,2)); pointsList.add(Arrays.copyOfRange(coords,2,4)); pointsList.add(Arrays.copyOfRange(coords,4,6)); break;}
                    case SEG_CLOSE: {
                        pointsList.add(pointsList.get(0));
                        double[][] data = new double[2][pointsList.size()];
                        for(int i=0; i<pointsList.size(); i++) {
                            data[0][i] = (pointsList.get(i)[1])*delta[0] + min[0];
                            data[1][i] = (pointsList.get(i)[0])*delta[1] + min[1];
                        }
                        pointsLists.add(data);
                        pointsList.clear();
                        break;
                    }
                }
            }
        }
        return pointsLists;
    }
}
