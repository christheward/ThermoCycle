/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thermocycle.Fluid;

/**
 *
 * @author Chris
 */
public class fluidsLibrary {
    
    private final ObservableList<Fluid> fluids;
    public final ObservableList<Fluid> fluidsReadOnly;
    
    public fluidsLibrary() {
        fluids = FXCollections.observableList(new ArrayList<>());
        fluidsReadOnly = FXCollections.unmodifiableObservableList(fluids);
    }
    
}
