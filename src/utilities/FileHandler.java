/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import gui.CanvasController;
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
    
    public FileHandler() {}
    
    static public void openWriteStream(File file) throws FileNotFoundException, IOException {
        fostream = new FileOutputStream(file);
        oostream = new ObjectOutputStream(fostream);
    }
    
    static public void closeWriteStream() throws IOException {
        oostream.close();
        fostream.close();
    }
    
    static public void openReadStream(File file) throws FileNotFoundException, IOException {
        fistream = new FileInputStream(file);
        oistream = new ObjectInputStream(fistream);
    }
    
    static public void closeReadStream() throws IOException {
        oistream.close();
        fistream.close();
    }
    
    static public void saveModel(Cycle model) throws IOException {
        model.saveModel(oostream);
    }
    
    static public void loadModel(Cycle model) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        model.loadModel(oistream);
    }
    
    static public void saveLayout(CanvasController canvas) throws FileNotFoundException, IOException {
        canvas.saveLayout(oostream);
    }
    
    static public void loadLayout(CanvasController canvas) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        canvas.loadLayout(oistream);
    }
    
}
