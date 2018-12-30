/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import utilities.FileHandler;
import utilities.GsonHandler;

/**
 *
 * @author Chris
 */
public class MenubarController extends MenuBar {
    
    // FXML variables
    @FXML private MenuItem fileNew;
    @FXML private MenuItem fileOpen;
    @FXML private MenuItem fileClose;
    @FXML private MenuItem fileSave;
    @FXML private MenuItem fileSaveas;
    @FXML private MenuItem fileImport;
    @FXML private MenuItem fileExport;
    @FXML private MenuItem fileExit;
    @FXML private MenuItem editDelete;
    @FXML private MenuItem cycleSolve;
    @FXML private MenuItem cycleClear;
    @FXML private MenuItem cyclePlot;
    @FXML private MenuItem cycleChart;
    @FXML private MenuItem cycleReport;
    
    // GUI variables
    private final MasterSceneController master;
    
    // Dialogues
    Stage fileBrowserDialogue = new Stage();
    FileChooser fileChooser = new FileChooser();                
    File file;
    
    /**
     * Constructor
     */
    public MenubarController(MasterSceneController master) {
        
        // Set Master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Menubar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        fileChooser.getExtensionFilters().add(new ExtensionFilter("ThermoCycle files (*.cyc)","*.cyc"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("All files (*.*)","*.*"));
        
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Set up menu handlers
        buildMenuHandlers();
        
    }
    
    /**
     * Build menu handlers
     */
    private void buildMenuHandlers() {
        fileNew.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.setModel(new thermocycle.Cycle("New Cycle"));
            }
        });
        fileClose.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.closeModel();
            }
        });
        fileOpen.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Open Model File");
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                /**
                String json;
                try {
                    json = FileUtils.readFileToString(file, Charset.defaultCharset());
                }
                catch (IOException ex) {
                    json = "";
                    Logger.getLogger(MenubarController.class.getName()).log(Level.SEVERE, null, ex);
                }
                master.setModel(GsonHandler.gsonCanvas.fromJson(json,thermocycle.Cycle.class));
                */
                try {
                master.setModel((thermocycle.Cycle)FileHandler.read(file));
                }
                catch (FileNotFoundException ex) {
                    // DO something
                }
                catch (ClassNotFoundException ex) {
                    // Do something
                }
                catch (IOException ex) {
                    // Do something
                }
                master.canvas.buildFromModel();
                event.consume();
            }
        });
        fileSave.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (file == null) {
                    fileChooser.setTitle("Save Model File");
                    file = fileChooser.showSaveDialog(fileBrowserDialogue);
                }
                 /**
                 * try (FileWriter writer = new FileWriter(file)) {
                 * //writer.write(master.canvas.getJson());
                 * writer.write(GsonHandler.gsonModel.toJson(master.getModel()));
                 * }
                 * catch (IOException ex) {
                 * //Logger.getLogger(MenubarController.class.getName()).log(Level.SEVERE, null, ex);
                 * }
                 */
                try {
                    FileHandler.write(master.getModel(), file);
                } catch (IOException ex) {
                    Logger.getLogger(MenubarController.class.getName()).log(Level.SEVERE, null, ex);
                }
                event.consume();
            }
        });
        fileSaveas.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Save Model File");
                fileChooser.setInitialFileName(master.getModel().getName());
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
                    os.writeObject(master.getModel());
                }
                catch(IOException e) {
                    System.err.println("I/O error. " + e.getMessage());
                }
                event.consume();
            }
        });
        fileExport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Export Fluids Library");
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                master.getModel().exportFluidLibrary(file);
            }            
        });
        fileImport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Import Fluids Library");
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                master.getModel().importFluidLibrary(file);
            }
        });
        fileExit.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                Stage stage = (Stage) master.getScene().getWindow();
                stage.close();
            }
        });
        
        editDelete.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                // TODO: How do you work out what to delete?
                event.consume();
            }
        });
        cycleSolve.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.getModel().solve();
                event.consume();
            }
        });
        cycleClear.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.getModel().reset();
                event.consume();
            }
        });
        cyclePlot.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.openGraph();
            }            
        });
        cycleChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.openChart();
            }            
        });
        cycleReport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                event.consume();
            }
        });
    }
        
}
