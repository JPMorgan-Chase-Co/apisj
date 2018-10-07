package org.apis.gui.controller.popup;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;
import javafx.scene.control.TextField;
import org.apis.core.CallTransaction;
import org.apis.db.sql.DBManager;
import org.apis.gui.controller.base.BasePopupController;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.PopupManager;
import org.apis.gui.manager.StringManager;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class PopupAddTokenController extends BasePopupController {

    @FXML private ImageView addrCircleImg, resultAddrCircleImg;
    @FXML private TextField tokenAddressTextField, nameTextField, symbolTextField, decimalTextField, totalSupplyTextField;
    @FXML private Label addTokenTitle, addTokenDesc, contractAddrLabel, nameLabel, minNumLabel, previewLabel, noBtn, addBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Multilingual Support
        languageSetting();

        Ellipse ellipse = new Ellipse(12, 12);
        ellipse.setCenterX(12);
        ellipse.setCenterY(12);

        addrCircleImg.setClip(ellipse);
        resultAddrCircleImg.imageProperty().bind(addrCircleImg.imageProperty());

        tokenAddressTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if(oldValue){

                    String contractAddress = tokenAddressTextField.getText();
                    String tokenName = AppManager.getInstance().getTokenName(contractAddress);
                    String tokenSymbol = AppManager.getInstance().getTokenSymbol(contractAddress);
                    BigInteger totalSupply = AppManager.getInstance().getTokenTotalSupply(contractAddress);
                    long decimal = AppManager.getInstance().getTokenDecimals(contractAddress);

                    nameTextField.setText(tokenName);
                    symbolTextField.setText(tokenSymbol);
                    totalSupplyTextField.setText(totalSupply.toString());
                    decimalTextField.setText(Long.toString(decimal));

                }

            }
        });

    }

    public void languageSetting() {
        addTokenTitle.textProperty().bind(StringManager.getInstance().popup.tokenAddAddTokenTitle);
        addTokenDesc.textProperty().bind(StringManager.getInstance().popup.tokenAddAddTokenDesc);
        contractAddrLabel.textProperty().bind(StringManager.getInstance().popup.tokenEditContractAddrLabel);
        nameLabel.textProperty().bind(StringManager.getInstance().popup.tokenEditNameLabel);
        nameTextField.promptTextProperty().bind(StringManager.getInstance().popup.tokenEditNamePlaceholder);
        minNumLabel.textProperty().bind(StringManager.getInstance().popup.tokenEditMinNumLabel);
        previewLabel.textProperty().bind(StringManager.getInstance().popup.tokenEditPreviewLabel);
        noBtn.textProperty().bind(StringManager.getInstance().popup.tokenEditNoBtn);
        addBtn.textProperty().bind(StringManager.getInstance().popup.tokenAddAddBtn);
    }

    public void addBtnClicked() {
        String tokenAddress = tokenAddressTextField.getText();
        String tokenName = nameTextField.getText();
        String tokenSymbol = symbolTextField.getText();
        String tokenDecimal = decimalTextField.getText();
        String totalSupply = totalSupplyTextField.getText();

        byte[] addr = Hex.decode(tokenAddress);
        long decimal = Long.parseLong(tokenDecimal);
        BigInteger supply = new BigInteger(totalSupply);
        DBManager.getInstance().updateTokens(addr, tokenName, tokenSymbol, decimal, supply);
        AppManager.getInstance().initTokens();

        exit();
    }

    @Override
    public void exit(){
        PopupManager.getInstance().showMainPopup("popup_token_add_edit.fxml", zIndex);
    }
}
