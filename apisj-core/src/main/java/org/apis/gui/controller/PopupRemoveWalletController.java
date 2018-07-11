package org.apis.gui.controller;

import javafx.fxml.Initializable;
import org.apis.gui.manager.AppManager;

import java.net.URL;
import java.util.ResourceBundle;

public class PopupRemoveWalletController implements Initializable {


    public void exit(){
        AppManager.getInstance().guiFx.hidePopup(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
