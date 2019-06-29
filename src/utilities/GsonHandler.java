/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import org.hildan.fxgson.FxGson;

/**
 *
 * @author Chris Ward
 */
public final class GsonHandler {
    
    //public static Gson gsonModel = (new GsonBuilder()).serializeSpecialFloatingPointValues().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    //public static Gson gsonCanvas = (new GsonBuilder()).serializeSpecialFloatingPointValues().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    
    public static Gson gsonModel = FxGson.coreBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
    public static Gson gsonCanvas = FxGson.coreBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
    
}
