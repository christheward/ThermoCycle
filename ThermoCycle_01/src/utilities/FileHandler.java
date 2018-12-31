/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import thermocycle.Cycle;

/**
 *
 * @author Chris Ward
 */
public class FileHandler {
    
    static private FileOutputStream fostream;
    static private ObjectOutputStream oostream;
    static private FileInputStream fistream;
    static private ObjectInputStream oistream;
    
    private FileHandler() {}
    
    static public void write(Cycle model, File file) throws FileNotFoundException, IOException {
        System.out.println("here");
        fostream = new FileOutputStream(file);
        oostream = new ObjectOutputStream(fostream);
        model.writeObject(oostream);
        System.out.println("here2");
        oostream.close();
    }
    
    static public Cycle read(File file) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        fistream = new FileInputStream(file);
        oistream = new ObjectInputStream(fistream);
        Cycle model = new Cycle("Loading");
        model.readObject(oistream);
        oistream.close();
        return model;
    }
    
}
