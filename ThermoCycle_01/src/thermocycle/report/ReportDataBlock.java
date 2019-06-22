/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class ReportDataBlock {
    
    /**
     * The name of the report data block.
     */
    public final String name;
    
    /**
     * The sub data blocks.
     */
    private final List<ReportDataBlock> subBlocks;
    
    /**
     * The report data block data.
     */
    private final Map<String,Object> data;
    
    /**
     * Constructor
     * @param name the report data block name 
     */
    public ReportDataBlock(String name) {
        this.name = name;
        this.data = new HashMap<String, Object>();  // Argument types are needed here for some reason/
        this.subBlocks = new ArrayList();
    }
    
    /**
     * Add data item to the report data
     * @param name the name of the data item.
     * @param value the name of the data item.
     */
    public void addData(String name, Object value) {
        data.put(name, value);
    }
    
    /**
     * Adds a new sub data block to this data block.
     * @param dataBlock 
     */
    public void addDataBlock(ReportDataBlock dataBlock) {
        subBlocks.add(dataBlock);
    }
    
    /**
     * Gets an unmodifiable map of the data.
     * @return an unmodifiable data map.
     */
    public Map<String,String> getData() {
        Map<String,String> outData = new HashMap();
        data.keySet().stream().forEach(k -> outData.put(k, data.get(k).toString()));
        return Collections.unmodifiableMap(outData);
    }
    
    /**
     * Gets an unmodifiable list of the sub data blocks.
     * @return and unmodifiable list of sub data blocks.
     */
    public List<ReportDataBlock> getSubBlocks() {
        return Collections.unmodifiableList(subBlocks);
    }
    
}
