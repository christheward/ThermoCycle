/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tui;

import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import thermocycle.Component;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
@Command (description = "connect two nodes.")
public class Connect {
    
    @Parameters(hidden = false)
    List<String> allParameters;
    
    @Parameters(index = "0") String connectionType;
    @Parameters(index = "1") Component component1;
    @Parameters(index = "2..*") String node1;
    @Parameters(index = "1") Component component2;
    @Parameters(index = "2..*") String node2;
    
}