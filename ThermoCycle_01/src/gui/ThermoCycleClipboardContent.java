/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class ThermoCycleClipboardContent extends ClipboardContent {
    
    // Data formats
    public static final DataFormat ACTION = new DataFormat("Action");
    public static final DataFormat COMPONENT_TYPE = new DataFormat("Component_type");
    public static final DataFormat NODE = new DataFormat("Node");
    public static final DataFormat SCENE_COORDINATES = new DataFormat("Scene_coordinates");
    
    // Operations
    public static enum OPERATION {CREATE, MOVE, CONNECT, SELECT};
    
    // Constructor
    public ThermoCycleClipboardContent() {
    }
    
    public boolean putAction(OPERATION operation) {
        this.put(ACTION, operation);
        return true;
    }
    
    public boolean putComponentType(ComponentIcon type) {
        put(COMPONENT_TYPE, type);
        return true;
    }
    
    public OPERATION getAction() {
        return (OPERATION)get(ACTION);
    }
    
}
