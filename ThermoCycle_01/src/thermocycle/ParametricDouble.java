/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;

/**
 *
 * @author Chris
 */
public class ParametricDouble {
    
    static public ParametricDouble of(Integer value) {
        return of(value.doubleValue());
    }
    
    /**
    static public ParametricDouble of(Double value) {
        ParametricDouble pd = new ParametricDouble();
        pd.type = Type.SET;
        pd.values = new ArrayList();
        pd.values.add(value);
        pd.itt = pd.values.iterator();
        pd.value = OptionalDouble.of(pd.itt.next());
        return pd;
    }
    */
    
    static public ParametricDouble of(double value) {
        ParametricDouble pd = new ParametricDouble();
        pd.type = Type.SET;
        pd.values = new ArrayList();
        pd.values.add(value);
        pd.itt = pd.values.iterator();
        pd.value = OptionalDouble.of(pd.itt.next());
        return pd;
    }
    
    static public ParametricDouble of(List<Double> values) {
        if (values.isEmpty()) {
            // THROW ERROR
        }
        ParametricDouble pd = new ParametricDouble();
        pd.type = Type.SET;
        pd.values = new ArrayList();
        pd.values.addAll(values);
        pd.itt = pd.values.iterator();
        pd.value = OptionalDouble.of(pd.itt.next());
        return pd;
    }
    
    static public ParametricDouble empty() {
        ParametricDouble pd = new ParametricDouble();
        pd.type = Type.SOLVE;
        pd.values = new ArrayList();
        pd.itt = pd.values.iterator();
        pd.value = OptionalDouble.empty();
        return pd;
    }
    
    enum Type {SOLVE, SET;}
    
    private Type type;
    private ArrayList<Double> values;
    private Iterator<Double> itt;
    private OptionalDouble value;
    
    /**
     * Constructor
     */
    private ParametricDouble() {
    }
    
    /**
     * Returns true if there is a value present otherwise returns false.
     * @return true if value is present.
     */
    public boolean isPresent() {
        return value.isPresent();
    }
    
    /**
     * Gets the value of the ParametricDouble.
     * @return the value held by the ParametricDouble.
     */
    public double getAsDouble() {
        return value.getAsDouble();
    }
    
    /**
     * Determines if the there is another value in the parametric list.
     * @return Returns true if there are still values left in the list.
     */
    public boolean hasNext() {
        return itt.hasNext();
    }
    
    /**
     * Updates the value of the ParametricDouble during parametric studies.
     * @param next 
     */
    public void reset(boolean next) {
        switch (type) {
            case SOLVE: {
                value = OptionalDouble.empty();
                break;
            }
            case SET: {
                if (next) {
                    // Check if next exists.
                    value = OptionalDouble.of(itt.next());
                }
                break;
            }
        }
    }
    
    @Override
    public boolean equals(Object object) {
        // self check
        if (this == object) {
            return true;
        }
        // type check and cast
        if (getClass() != object.getClass()) {
            return false;
        }
        ParametricDouble pd = (ParametricDouble) object;
        // field comparison
        if (this.value.equals(pd.value)) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
}
