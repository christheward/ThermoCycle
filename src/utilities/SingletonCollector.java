/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

/**
 *
 * @author Chris Ward
 */
public final class SingletonCollector {
    
    /**
     * Private constructor to simulate static class behaviour in Java
     */
    private SingletonCollector() {}
    
    /**
     * Singleton Collector
     * @param <T>
     * @return 
     */
    public static <T> Collector<T, List <T>, T> singletonCollector() {
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
    
}
