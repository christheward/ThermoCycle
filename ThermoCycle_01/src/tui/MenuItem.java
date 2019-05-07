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

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class MenuItem {
    
    private final MenuItem parentMenuItem;
    private final Set<MenuItem> childMenuItems;
    private final String name;
    private final String help;
    
    public MenuItem(String name, MenuItem parent, String help) {
        this.name = name;
        this.parentMenuItem = parent;
        this.childMenuItems = new HashSet();
        this.help = help;
    }
    
    protected void addChildMenuItem(MenuItem child) {
        this.childMenuItems.add(child);
    }
    
    protected void parse() {
        
    }
    
    public Set<MenuItem> getChildren() {
        return childMenuItems;
    }
    
    
}
