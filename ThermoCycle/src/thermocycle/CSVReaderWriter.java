/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle;

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
   
   public void CSVReaderWriter() {
   }
   
   public static ArrayList<ArrayList<String>> read(String csvFile, String deliminator) {
       ArrayList<ArrayList<String>> inputs = new ArrayList();
       try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
           while ((line = br.readLine()) != null) {
               inputs.add(new ArrayList<>(Arrays.asList(line.split(deliminator))));
           }
       }
       catch (FileNotFoundException e) {}
       catch (IOException e) {}
       return inputs;
   }
   
   public static void write(String csvFile, String deliminator, ArrayList<ArrayList<String>> outputs){
       StringBuilder sb = new StringBuilder(); 
       try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
           outputs.forEach(output -> {
               output.forEach(cmd -> {
                   sb.append(cmd).append(deliminator);
               });
               sb.append(System.lineSeparator());
           });
           line = sb.toString();
           bw.write(line, 0, line.length());
           bw.newLine();
       }
       catch (IOException e) {}
   }
}
