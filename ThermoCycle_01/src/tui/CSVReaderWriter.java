/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Chris
 */
public class CSVReaderWriter {
    
   private static String line = "";
   private static String deliminator = ",";
   
   
   protected static void setDeliminator(String deliminator) {
       CSVReaderWriter.deliminator = deliminator;
   }
   
   /**
    * Reads a CSV file and returns the arguments as a list of strings.
    * @param csvFile The CSV file path to read
    * @return Returns a list of the arguments.
    */
   protected static ArrayList<ArrayList<String>> read(String csvFile) {
       ArrayList<ArrayList<String>> inputs = new ArrayList();
       try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
           while ((line = br.readLine()) != null) {
               inputs.add(readLine(line));
           }
       }
       catch (FileNotFoundException e) {}
       catch (IOException e) {}
       return inputs;
   }
   
   /**
    * Reads a single line and returns the values as a list
    * @param line The line to read.
    * @return Returns a list of strings.
    */
   protected static ArrayList<String> readLine(String line) {
       return new ArrayList<>(Arrays.asList(line.split(deliminator)));
   }
}
