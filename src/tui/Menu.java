/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tui;

import utilities.TreeNode;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import thermocycle.Component;
import thermocycle.Cycle;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public class Menu {

    private Cycle model;
    private TreeNode<MenuItem> root;

    public Menu() {
    }

    private void createMenu() {
        MenuItem file = new MenuItem("File", null);
        file.addAllias("f");
        root.addChild(file);
        
        MenuItem open = new MenuItem("Open", null);
        open.addAllias("o");
        root.addChild(open);
        
        //MenuCommand openFile = new MenuCommand();
        

        root.addChild(new MenuItem("Create", null));
        root.getLast().getElement().addAllias("c");

        root.addChild(new MenuItem("Set", null));
        root.getLast().getElement().addAllias("s");

        
    }

    private class CmdOpen extends MenuCommand {

        public CmdOpen() {
            super();
            this.addPrompt("Filename.");
        }

        @Override
        public void execute(Cycle cycle) {
            String filename = inputs.get(0);
            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename))) {
                System.out.println("Loading model from " + filename);
                model = (Cycle)is.readObject();
                System.out.println("Load complete");
            }
            catch(ClassNotFoundException e) {
                System.err.println("Class not found. " + e.getMessage());
            }
            catch(IOException e) {
                System.err.println("I/O error. " + e.getMessage());
            }
        }

    }

    private class CmdCreateTurbine extends MenuCommand {

        public CmdCreateTurbine() {
            super();
            this.addPrompt("Turbine name.");
        }

        @Override
        public void execute(Cycle cycle) {
            Component comp = model.createTurbine(inputs.get(0));
            //model.setBoundaryConditionAttribute(comp, EFFICIENCY, new double[] {Double.parseDouble(commands.get(3))});
            //model.setBoundaryConditionAttribute(comp, PRATIO, new double[] {Double.parseDouble(commands.get(4))});
            //break;
        }

    }

}
