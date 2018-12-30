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
    
    static public void write(Object object, File file) throws FileNotFoundException, IOException {
        fostream = new FileOutputStream(file);
        oostream = new ObjectOutputStream(fostream);
        oostream.writeObject(object);
        oostream.close();
        
    }
    
    static public Object read(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        fistream = new FileInputStream(file);
        oistream = new ObjectInputStream(fistream);
        Object object = oistream.readObject();
        oistream.close();
        return object;
    }
    
}
