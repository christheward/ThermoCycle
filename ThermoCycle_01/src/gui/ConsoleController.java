/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author Chris
 */
public class ConsoleController extends VBox {
    
    // FXML variables
    @FXML private TextField input;
    @FXML private TextArea output;
    
    // GUI variables
    private final MasterSceneController master;
    
    private StringBuilder text;
    private List<String> history;
    private final tui.TextUserInterface tui;
    private int historyIdx;
    
    /**
     * Constructor
     * @param master The parent master
     */
    protected ConsoleController (MasterSceneController master) {
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Console.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.master = master;
        
        // create the history
        history = new ArrayList<>();
        // set default string
        text = new StringBuilder();
        // create new tui
        this.tui = new tui.TextUserInterface(this.master.getModel());
    }
    
    /**
     * Initializer
     */
    public void initialize() {
        buildInputHandlers();
    }
    
    /**
     * Builds input handers for dealing with key strokes.
     */
    private void buildInputHandlers() {
        input.setOnAction(new EventHandler <ActionEvent> () {
            @Override
            public void handle(ActionEvent event) {
                if (!input.getText().isEmpty()) {
                    text.append(input.getText()).append('\n');
                    // add commands to history
                    history.add(0, input.getText());
                    // process commands
                    tui.readCommand(input.getText());
                    // update terminal
                    output.setText(text.toString());
                    // scroll to bottom on terminal
                    output.end();
                }
                historyIdx = 0;
                showHistory();
            }
        });
        
        input.setOnKeyPressed(new EventHandler <KeyEvent> () {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.UP)) {
                    if (historyIdx < history.size()) {
                        historyIdx = historyIdx + 1;
                        showHistory();
                    }
                }
                else if (event.getCode() == KeyCode.DOWN) {
                    if (historyIdx > 0) {
                        historyIdx = historyIdx - 1;
                        showHistory();
                    }
                }
            }
            
        });
        output.positionCaret(0);
    }
    
    /**
     * Shows the previously submitted commands.
     */
    private void showHistory() {
        if (historyIdx == 0) {
            input.clear();
        }
        else {
            input.setText(history.get(historyIdx - 1));
        }
        input.end();
    }
    
}
