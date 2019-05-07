/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.DataFormat;
import javafx.util.Pair;

/**
 *
 * @author Chris
 */
public class DragContainerController implements Serializable {
    
    // Drag container data
    private final ArrayList<Pair<DATA_TYPE, Object>> mDataPairs = new ArrayList();
    
    // Drag data formats
    public static final DataFormat CREATE_COMPONENT = new DataFormat("application.CreateComponent");
    public static final DataFormat MOVE_COMPONENT = new DataFormat("application.MoveComponent");
    public static final DataFormat CREATE_CONNECTION = new DataFormat("application.CreateConnection");
    public static final DataFormat SELECT = new DataFormat("application.Select");
    
    // Drag data types
    public static enum DATA_TYPE {LOCATION, NODE, COMPONENT};
    
    /**
     * Constructor
     */
    public DragContainerController () {
    }
    
    /**
     * Add data to the DragContainerController object
     * @param key The key for the DragContainerController object
     * @param value The object to add to the DragContainerController
     */
    public void addData(DATA_TYPE key, Object value) {
        mDataPairs.add(new Pair(key, value));        
    }
    
    /**
     * Add data to the DragContainerController object
     * @param values The values to add to the DragCOntainerController object
     */
    public void addData(DragContainerController values) {
        mDataPairs.addAll(values.mDataPairs);
    }
    
    /**
     * Gets the object from the DragContainerController
     * @param <T> ?????
     * @param key The key for the object to retrieve
     * @return Returns the object from the DragContainerController
     */
    public  <T> T getValue (DATA_TYPE key) {
        for (Pair<DATA_TYPE, Object> data: mDataPairs) {
            if (data.getKey().equals(key)) {
                return (T) data.getValue();
            }
         }
        return null;
    }
    
    /**
     * Get a list of the key-object pairs in the DragContainerController object
     * @return Returns the list of key-object pairs
     */
    public List <Pair<DATA_TYPE, Object> > getData () {
        return mDataPairs;
    }
}
