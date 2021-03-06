package org.apis.gui.controller.popup;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import org.apis.gui.controller.module.AddressLabelController;
import org.apis.gui.controller.module.textfield.ApisTextFieldController;
import org.apis.gui.controller.base.BasePopupController;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.PopupManager;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class PopupTransferSendController extends BasePopupController {

    @FXML private AnchorPane rootPane;
    @FXML private Label sendAmount, totalAmount, aferBalance, btnSendTransfer, timeLabel;
    @FXML private ApisTextFieldController passwordController;
    @FXML private AddressLabelController sendingAddressController, receiveAddressController;

    public void exit(){
        PopupManager.getInstance().hideMainPopup(0);
        if(handler != null){
            handler.close();
        }
        parentRequestFocus();
    }


    @FXML
    private void onMouseClicked(InputEvent event){
        String id = ((Node)event.getSource()).getId();
        char[] password = passwordController.getText().trim().toCharArray();
        char[] knowledgeKey = passwordController.getText().trim().toCharArray();
        if(id.equals("btnSendTransfer")){
            if(passwordController.getText().trim().length() > 0){
                if(handler != null){
                    handler.send(this, password, knowledgeKey);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordController.setHandler(new ApisTextFieldController.ApisTextFieldControllerInterface() {
            @Override
            public void onFocusOut() {

            }

            @Override
            public void change(String old_text, String new_text) {
                if(new_text.length() > 0){
                    btnSendTransfer.setStyle("-fx-border-radius : 24 24 24 24; -fx-background-radius: 24 24 24 24; -fx-background-color:#b01e1e; ");
                }else{
                    btnSendTransfer.setStyle("-fx-border-radius : 24 24 24 24; -fx-background-radius: 24 24 24 24; -fx-background-color:#d8d8d8; ");
                }
            }

            @Override
            public void onAction() {

            }

            @Override
            public void onKeyTab(){

            }
        });

        sendingAddressController.setHandler(addressCopyEvent);
        receiveAddressController.setHandler(addressCopyEvent);
    }

    private AddressLabelController.AddressLabelImpl addressCopyEvent = new AddressLabelController.AddressLabelImpl() {
        @Override
        public void onMouseClicked(String address) {

            PopupCopyController controller = (PopupCopyController)PopupManager.getInstance().showMainPopup(rootPane, "popup_copy.fxml", 0);
            controller.setCopyWalletAddress(address);

        }
    };


    public void succeededForm() {
        passwordController.succeededForm();
    }

    public void failedForm(String text){
        passwordController.failedForm(text);
    }

    public void init(String sendAddr, String receivAddr, String sendAmount, String totalAmount, String aferBalance) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY HH:mm");
        int utc = TimeZone.getDefault().getRawOffset()/1000/3600;

        // 어드레스마스킹 체크
        if(sendAddr.indexOf("@") >=0 ){
            this.sendingAddressController.setAddress(AppManager.getInstance().getAddressWithMask(sendAddr));
            this.sendingAddressController.setTooltip(sendAddr);
        }else {
            this.sendingAddressController.setAddress(sendAddr);
            this.sendingAddressController.setTooltip(AppManager.getInstance().getMaskWithAddress(sendAddr));
        }

        if(receivAddr.indexOf("@") >=0 ){
            this.receiveAddressController.setAddress(AppManager.getInstance().getAddressWithMask(receivAddr));
            this.receiveAddressController.setTooltip(receivAddr);
        }else {
            this.receiveAddressController.setAddress(receivAddr);
            this.receiveAddressController.setTooltip(AppManager.getInstance().getMaskWithAddress(receivAddr));
        }
        this.timeLabel.textProperty().setValue(dateFormat.format(new Date()).toUpperCase()+"(UTC+"+utc+")");

        this.sendAmount.textProperty().setValue(sendAmount+" APIS");
        this.totalAmount.textProperty().setValue(totalAmount+" APIS");
        this.aferBalance.textProperty().setValue(aferBalance+" APIS");
    }

    public void initToken(String sendAddr, String receivAddr, String sendAmount, String totalAmount, String aferBalance, String symbol) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY HH:mm");
        int utc = TimeZone.getDefault().getRawOffset()/1000/3600;

        this.timeLabel.textProperty().setValue(dateFormat.format(new Date()).toUpperCase()+"(UTC+"+utc+")");
        this.sendingAddressController.setAddress(sendAddr);
        this.receiveAddressController.setAddress(receivAddr);
        this.sendAmount.textProperty().setValue(sendAmount+" "+symbol);
        this.totalAmount.textProperty().setValue(totalAmount+" "+symbol);
        this.aferBalance.textProperty().setValue(aferBalance+" "+symbol);
    }

    private PopupTransferSendImpl handler;
    public void setHandler(PopupTransferSendImpl handler){
        this.handler = handler;
    }

    public interface PopupTransferSendImpl {
        void send(PopupTransferSendController controller, char[] password, char[] knowledgeKey);
        void close();
    }
}
