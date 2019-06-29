/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import thermocycle.Cycle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class MenuItem {
    
    private final String name;
    private final Set<String> alliases;
    private final MenuCommand command;
    
    public MenuItem(String name, MenuCommand command) {
        this.name = name;
        this.alliases = new HashSet();
        this.command = command;
    }
    
    protected void execute(Cycle cycle) {
        if (command != null) {
            command.execute(cycle);
        }
    }
    
    protected void addAllias(String allias) {
        alliases.add(allias);
    }
        
}
