/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
@Command(mixinStandardHelpOptions = true, version = "v3.0.0",
         header = "Encrypt FILE(s), or standard input, to standard output or to the output file.")
public class CommandLineArguments {
    
    @Option(names = {"-b", "--batch"}, description = "Run in batch mode.")
    private boolean batch;
    
    @Option(names = {"-c", "--cycle"}, paramLabel = "FILE", description = "The cycle file to run.")
    private File file;
    
    @Option(names = {"-d", "--developer"}, description = "Developer mode.", hidden = true)
    private boolean developer;
    
    @Option(names = {"-v", "--verbose"}, description = "Use verbose output.")
    private boolean verbose;
    
}
