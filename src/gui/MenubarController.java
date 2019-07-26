/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import thermocycle.BoundaryCondition;
import thermocycle.Cycle;
import thermocycle.UnitsControl;
import thermocycle.UnitsControl.UnitsSystem;
import utilities.FileHandler;

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
    @FXML private Menu editUnits;
    @FXML private MenuItem cycleSolve;
    @FXML private MenuItem cycleClear;
    @FXML private MenuItem cyclePlot;
    @FXML private MenuItem cycleChart;
    @FXML private MenuItem cycleReport;
    @FXML private CheckMenuItem cycleNodeVisibility;
    @FXML private CheckMenuItem cycleNameVisibility;
    @FXML private CheckMenuItem cycleToolboxLock;
    
    // GUI variables
    private final MasterSceneController master;
    
    // Dialogues
    private Stage fileBrowserDialogue = new Stage();
    private FileChooser fileChooser = new FileChooser();                
    private File file;
    
    // File Handler
    private FileHandler fileHandler = new FileHandler();
    
    /**
     * Constructor
     */
    public MenubarController(MasterSceneController master) {
        
        // Set Master
        this.master = master;
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Menubar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Set menu options
        
        
        // Set up file chooser
        fileChooser.getExtensionFilters().add(new ExtensionFilter("ThermoCycle files (*.cyc)","*.cyc"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("All files (*.*)","*.*"));
        
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        
        // Setup handlers
        buildMenuHandlers();
        
        // Setup bindings
        fileClose.disableProperty().bind(master.modelReadOnly.isNull());
        fileSave.disableProperty().bind(master.modelReadOnly.isNull());
        fileSaveas.disableProperty().bind(master.modelReadOnly.isNull());
        editDelete.disableProperty().bind(master.modelReadOnly.isNull());
        editUnits.disableProperty().bind(master.modelReadOnly.isNull());
        cycleSolve.disableProperty().bind(master.modelReadOnly.isNull());
        cycleClear.disableProperty().bind(master.modelReadOnly.isNull());
        cyclePlot.disableProperty().bind(master.modelReadOnly.isNull());
        cycleChart.disableProperty().bind(master.modelReadOnly.isNull());
        cycleReport.disableProperty().bind(master.modelReadOnly.isNull());
        cycleNodeVisibility.disableProperty().bind(master.modelReadOnly.isNull());
        master.nodeVisibility.bindBidirectional(cycleNodeVisibility.selectedProperty());
        cycleNameVisibility.disableProperty().bind(master.modelReadOnly.isNull());
        master.nameVisibility.bindBidirectional(cycleNameVisibility.selectedProperty());
        cycleToolboxLock.disableProperty().bind(master.modelReadOnly.isNull());
        master.toolboxLock.bindBidirectional(cycleToolboxLock.selectedProperty());
        
        // Add items to units menu
        editUnits.getItems().addAll(Arrays.asList(UnitsControl.UnitsSystem.values()).stream().sorted().map(us -> new CheckMenuItem(us.name)).collect(Collectors.toList()));
        // Add listener to units system
        master.unitsSystem.addListener(new ChangeListener<UnitsSystem>() {
            @Override
            public void changed(ObservableValue<? extends UnitsSystem> observable, UnitsSystem oldValue, UnitsSystem newValue) {
                editUnits.getItems().stream().filter(us -> us instanceof CheckMenuItem).map(us -> (CheckMenuItem)us).forEach(us -> us.setSelected(us.getText().equals(newValue.name)));
            }
        });
        // Set the units system
        master.unitsSystem.setValue(UnitsSystem.SI);
        // Add listener to each units menu item
        editUnits.getItems().stream().filter(us -> us instanceof CheckMenuItem).map(us -> (CheckMenuItem)us).forEach(us -> us.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    if (!master.unitsSystem.equals(us.getText())) {
                        Arrays.asList(UnitsSystem.values()).stream().filter(u -> u.name.equals(us.getText())).findFirst().ifPresent(u -> master.unitsSystem.setValue(u));
                    }
                }
            }
        }));
        
    }
    
    /**
     * Build menu handlers
     */
    private void buildMenuHandlers() {
        fileNew.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                // If model already exisits, check what to do with the current model.
                if (master.modelReadOnly.isNotNull().getValue()) {
                    // Do something
                    
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Are you sure?");
                    alert.setHeaderText("You will loose your current model.");
                    alert.setContentText("Are you sure you want to create a new cycle?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() != ButtonType.OK) {
                        return;
                    }
                    
                }
                master.setModel(new thermocycle.Cycle("New Cycle"));
            }
        });
        fileClose.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.setModel(null);
            }
        });
        fileOpen.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Open Model File");
                file = fileChooser.showOpenDialog(fileBrowserDialogue);
                try {
                    Cycle model = new Cycle(file.getName());
                    FileHandler.openReadStream(file);
                    FileHandler.loadModel(model);
                    master.setModel(model);
                    FileHandler.loadLayout(master.canvas);
                    FileHandler.closeReadStream();
                }
                catch (Exception ex) {
                    System.err.print(ex.getMessage());
                }
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
                try {
                    FileHandler.openWriteStream(file);
                    System.out.print("Saving model...");
                    FileHandler.saveModel(master.getModel());
                    System.out.println("Done");
                    System.out.print("Saving layout...");
                    FileHandler.saveLayout(master.canvas);
                    System.out.println("Done");
                    FileHandler.closeWriteStream();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                event.consume();
            }
        });
        fileSaveas.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                fileChooser.setTitle("Save Model File");
                fileChooser.setInitialFileName(master.getModel().getName());
                file = fileChooser.showSaveDialog(fileBrowserDialogue);
                try {
                    FileHandler.openWriteStream(file);
                    System.out.print("Saving model...");
                    FileHandler.saveModel(master.getModel());
                    System.out.println("Done");
                    System.out.print("Saving layout...");
                    FileHandler.saveLayout(master.canvas);
                    System.out.println("Done");
                    FileHandler.closeWriteStream();
                } catch (IOException ex) {
                    ex.printStackTrace();
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
        editUnits.getItems().stream().forEach(us -> {
            us.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    Arrays.asList(UnitsControl.UnitsSystem.values()).stream().filter(u -> u.name.equals(us.getText())).findFirst().ifPresent(u -> master.unitsSystem.setValue(u));
                }
            });
        });
        cycleSolve.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                master.getModel().solve(BoundaryCondition.getIdx());
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
                master.getModel().report();
                event.consume();
            }
        });
    }
}
