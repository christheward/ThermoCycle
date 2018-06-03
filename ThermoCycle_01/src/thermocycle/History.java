/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris
 */
public class History {
    
    private final List<Method> historyMethods;
    private final List<Object[]> historyArguments;
    
    protected History() {
        historyMethods = new ArrayList();
        historyArguments = new ArrayList();
    }
    
    protected void add(Method method, Object[] arguments) {
        List<Integer> removeIdx = new ArrayList();
        ListIterator methodIterator = historyMethods.listIterator();
        
        while(methodIterator.hasNext()) {
            int currentIndex = methodIterator.nextIndex();
            Method currentMethod = (Method)methodIterator.next();
            
            if (method.equals(currentMethod)) {
                if (arguments.length == historyArguments.get(currentIndex).length) {
                    List<Object> args1 = Arrays.asList(arguments);
                    List<Object> args2 = Arrays.asList(historyArguments.get(currentIndex));
                    
                    // remove OptionalDoubles
                    //args1.removeIf(a -> a instanceof OptionalDouble.class);
                    //args2.removeIf(a -> a instanceof OptionalDouble.class);
                    
                    ListIterator argumentIterator = args1.listIterator();
                    while (argumentIterator.hasNext()) {
                        int idx = argumentIterator.nextIndex();
                        if (!(argumentIterator.next().equals(args2.get(idx)))) {
                            break;
                        }
                    }
                    removeIdx.add(currentIndex);
                }
            }
        }
    }
    
    /**
     * Removes i'th element from the history
     * @param i The element to remove.
     */
    protected void remove(int i) {
        historyMethods.remove(i);
        historyArguments.remove(i);
    }
    
    protected int size() {
        return historyMethods.size();
    }
    
    protected void invoke(Cycle model) {
        try {
            for (int i=1; i<=historyMethods.size(); i++) {
                historyMethods.get(i).invoke(model, historyArguments.get(i));
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    /**
    public boolean incudes(history h) {
            if (method.equals(h.method)) {
                List args1 = Arrays.asList(arguments);
                List args2 = Arrays.asList(h.arguments);
                args1.removeIf(a -> a instanceof OptionalDouble);
                args2.removeIf(a -> a instanceof OptionalDouble);
                if (args1.size() == args2.size()) {
                    ListIterator li = args1.listIterator();
                    while (li.hasNext()) {
                        if (!(args2.get(li.nextIndex()).equals(li.next()))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }
    */
}
