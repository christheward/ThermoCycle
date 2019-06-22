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
import thermocycle.Node;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
@Command (description = "set.")
public class Set {
    
    @Parameters(hidden = false)
    List<String> allParameters;
    
    @Parameters(index = "0") String type;
    @Parameters(index = "1") Component component;
    @Parameters(index = "2") Node node;
    @Parameters(index = "3") Double value;
    
}
