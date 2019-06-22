/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import thermocycle.Cycle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public abstract class MenuCommand {
    
    public final List<String> inputs;
    private final List<String> prompts;
    
    public MenuCommand() {
        inputs = new ArrayList();
        prompts = new ArrayList();
    }
    
    /**
     * Adds an input to the command input deck
     * @param string the input to add to the command input deck.
     * @return true if the command is sucessfully added, otherwise false.
     */
    public final boolean addInput(String string) {
        if (inputs.size() < prompts.size()) {
            return inputs.add(string);
        }
        return false;
    }
    
    /**
     * Adds a prompt for the command input deck.
     * @param string the prompt to add to the command input deck.
     */
    public final void addPrompt(String string) {
        prompts.add(string);
    }
    
    /**
     * Gets the prompt for the next input.
     * @return the prompt for the next input.
     */
    public String recievePrompt() {
        return prompts.get(inputs.size());
    }
    
    public abstract void execute(Cycle cycle);
    
}
