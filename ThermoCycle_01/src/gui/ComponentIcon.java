/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author Chris
 */
public enum ComponentIcon {
    
    // Components
    COMPRESSOR ("Compressor", "icon-compressor", thermocycle.Compressor.class, new int[][] {{0,3},{6,3}}, new int[][] {{3,0}}, new int[][] {{}}),
    TURBINE ("Turbine", "icon-turbine", thermocycle.Turbine.class, new int[][] {{0,3},{6,3}}, new int[][] {{3,0}}, new int[][] {{}}),
    COMBUSTOR ("Combustor", "icon-combustor", thermocycle.Combustor.class, new int[][] {{0,3},{6,3}}, new int[][] {{}}, new int[][] {{3,0}}),
    HEAT_SINK ("Heat Sink", "icon-heatsink",thermocycle.HeatSink.class, new int[][] {{0,3},{6,3}}, new int[][] {{}}, new int[][] {{3,6}}),
    HEAT_EXCHANGER ("Heat Exchanger", "icon-heatexchanger",thermocycle.HeatExchanger.class, new int[][] {{0,2},{6,2},{0,4},{6,4}}, new int[][] {{}}, new int[][] {{}});
    
    protected final String name;
    protected final String css;
    protected final Class<?> type;
    protected final int[][] flownodes;
    protected final int[][] worknodes;
    protected final int[][] heatnodes;
    
    ComponentIcon(String name, String css, Class<?> type, int[][] flownodes, int[][] worknodes, int[][] heatnodes) {
        this.name = name;
        this.css = css;
        this.type = type;
        this.flownodes = flownodes;
        this.worknodes = worknodes;
        this.heatnodes = heatnodes;
    }
    
}

