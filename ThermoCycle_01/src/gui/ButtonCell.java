/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 * @param <S>
 * @param <T>
 */
public class ButtonCell<S,T> extends TableCell<S,T> {
    
    protected Button button;
    
    public ButtonCell(final TableView<T> tableView) {
        super();
        button = new Button("X");
        button.getStyleClass().add("buttonn-delete");
    }
    
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(button);
        }
    }
    
    
    
}
