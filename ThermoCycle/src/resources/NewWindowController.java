/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import thermocycle.Cycle;

/**
 * FXML Controller class
 *
 * @author Chris
 */
public class NewWindowController implements Initializable {
    
    private Cycle model;
    
    @FXML
    private TextField cycleName;
    @FXML
    private TextField pressure;
    @FXML
    private TextField temperature;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // force the field to be numeric only
        pressure.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    pressure.setText(oldValue);
                }
            }
        });
        // force the field to be numeric only
        temperature.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                 if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    temperature.setText(oldValue);
                }
            }
        });
    }
    
    protected void setModel(Cycle model) {
        this.model = model;
    }
    
    @FXML
    private void handlerOk(ActionEvent event) {
        model = new Cycle(cycleName.toString());
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handlerCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    
}
