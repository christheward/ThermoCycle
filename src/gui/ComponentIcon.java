/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author Chris
 */
public enum ComponentIcon {
    
    // Components
    COMPRESSOR ("Compressor", "icon-compressor", thermocycle.components.Compressor.class, new int[][] {{0,3},{6,3},{3,0}}, "Inlet", "Outlet", "Shaft"),
    TURBINE ("Turbine", "icon-turbine", thermocycle.components.Turbine.class, new int[][] {{0,3},{6,3},{3,0}}, "Inlet", "Outlet", "Shaft"),
    COMBUSTOR ("Combustor", "icon-combustor", thermocycle.components.Combustor.class, new int[][] {{0,3},{6,3},{3,0}}, "Inlet", "Outlet", "Supply"),
    HEAT_SINK ("Heat Sink", "icon-heatsink",thermocycle.components.HeatSink.class, new int[][] {{0,3},{6,3},{3,6}}, "Inlet", "Outlet", "Sink"),
    HEAT_EXCHANGER ("Heat Exchanger", "icon-heatexchanger",thermocycle.components.HeatExchanger.class, new int[][] {{0,2},{6,2},{0,4},{6,4}}, "Hot Side Inlet", "Hot Side Outlet", "Cold Side Inlet", "Cold Side Outlet"),
    NKNOWN("Unknown", "icon-combustor",null,new int[][] {{}});
    
    protected final String name;
    protected final String css;
    protected final Class<?> type;
    protected final Map<String, int[]> nodes;
    
    ComponentIcon(String name, String css, Class<?> type, int[][] nodeLocations, String... nodeNames) {
        this.name = name;
        this.css = css;
        this.type = type;
        this.nodes = new HashMap();
        ListIterator<String> li = Arrays.asList(nodeNames).listIterator();
        while(li.hasNext()) {
            int i = li.nextIndex();
            this.nodes.put(li.next(), nodeLocations[i]);
        };
    }
}

