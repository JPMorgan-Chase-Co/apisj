package org.apis.gui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import org.apis.core.CallTransaction;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.HttpRequestManager;
import org.apis.gui.manager.StringManager;
import org.spongycastle.util.encoders.Hex;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AddressMaskingController implements Initializable {



    @FXML
    private Label tabLabel1, tabLabel2, sideTabLabel1, sideTabLabel2;
    @FXML
    private Pane tabLinePane1, tabLinePane2, sideTabLinePane1, sideTabLinePane2;
    @FXML
    private AnchorPane tab1LeftPane, tab1RightPane, tab2LeftPane1, tab2LeftPane2, tab2LeftPane3;
    @FXML
    private GridPane commercialDescGrid, publicDescGrid, tab2RightPane1;
    @FXML
    private ImageView domainDragDrop, domainRequestBtn, idIcon, registerAddressIcon;
    @FXML
    private Label idIcon2;
    @FXML
    private TextField addrMaskingIDTextField, commercialDomainTextField, publicDomainTextField, emailTextField;
    @FXML
    private TextArea publicTextArea;
    @FXML
    private Label selectedDomainLabel, totalFeeAliaValue, totalFeeValue, totalWalletAddressValue;

    private Image domainDragDropGrey, domainDragDropColor, domainDragDropCheck;
    private Image downGreen = new Image("image/ic_check_green@2x.png");
    private Image downRed = new Image("image/ic_error_red@2x.png");

    @FXML
    private ApisSelectBoxController selectAddressController, selectDomainController, selectPayerController;

    // Multilingual Support Label
    @FXML
    private Label tabTitle, registerAddressLabel, registerAddressDesc, registerAddressMsg, selectDomainLabel, selectDomainDesc, selectDomainMsg,
                  registerIdLabel, registerIdDesc, totalFeeTitle, totalFeeAddress, totalFeeAlias, totalFeeLabel, totalFeePayer, totalFeeDesc, totalFeePayBtn,
                  registerDomainLabel, registerDomainDesc, sideTab1Desc1, sideTab1Desc2, sideTab1Desc3, sideTab2Desc1, sideTab2Desc2, sideTab2Desc3, sideTab2Desc4,
                  commercialDomainTitle, commercialDomainDesc, commercialDomainDesc1, commercialDomainDesc2, commercialDomainDesc3, commercialDomainMsg, fileFormMsg,
                  emailAddrLabel, emailDesc1, emailDesc2, emailDesc3, requestBtnLabel, publicDomainTitle, publicDomainDesc, publicDomainDesc1, publicDomainDesc2,
                  publicDomainDesc3, publicDomainDesc4, publicDomainMsg, publicMessageTitle, publicMessageDesc, idMsg, idMsg2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppManager.getInstance().guiFx.setAddressMasking(this);

        // Multilingual Support
        languageSetting();

        // Initialize Images
        domainDragDropGrey = new Image("image/bg_domain_dragdrop_grey@2x.png");
        domainDragDropColor = new Image("image/bg_domain_dragdrop_color@2x.png");
        domainDragDropCheck = new Image("image/bg_domain_dragdrop_check@2x.png");

        this.tab1LeftPane.setVisible(true);
        this.tab1RightPane.setVisible(true);
        this.tab2LeftPane1.setVisible(false);
        this.addrMaskingIDTextField.setText("");
        this.addrMaskingIDTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                settingLayoutData();
            }
        });

        this.tabLabel1.setTextFill(Color.web("#910000"));
        this.tabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
        this.tabLinePane1.setVisible(true);
        this.tabLabel2.setTextFill(Color.web("#999999"));
        this.tabLabel2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:12px;");
        this.tabLinePane2.setVisible(false);

        this.commercialDomainTextField.focusedProperty().addListener(textFieldListener);
        this.publicDomainTextField.focusedProperty().addListener(textFieldListener);

        selectAddressController.init(ApisSelectBoxController.SELECT_BOX_TYPE_ADDRESS);
        selectAddressController.setHandler(new ApisSelectBoxController.ApisSelectBoxImpl() {
            @Override
            public void onSelectItem() {
                settingLayoutData();
            }

            @Override
            public void onMouseClick() {

            }
        });

        selectDomainController.init(ApisSelectBoxController.SELECT_BOX_TYPE_DOMAIN);
        selectDomainController.setHandler(new ApisSelectBoxController.ApisSelectBoxImpl() {
            @Override
            public void onSelectItem() {
                settingLayoutData();
            }

            @Override
            public void onMouseClick() {

            }
        });

        selectPayerController.init(ApisSelectBoxController.SELECT_BOX_TYPE_ONLY_ADDRESS);
        selectPayerController.setHandler(new ApisSelectBoxController.ApisSelectBoxImpl() {
            @Override
            public void onSelectItem() {

            }

            @Override
            public void onMouseClick() {

            }
        });
        selectPayerController.setStage( ApisSelectBoxController.STAGE_DEFAULT);

        settingLayoutData();
        initTab(0);
    }

    public void languageSetting() {
        tabTitle.textProperty().bind(StringManager.getInstance().addressMasking.tabTitle);
        tabLabel1.textProperty().bind(StringManager.getInstance().addressMasking.tabLabel1);
        tabLabel2.textProperty().bind(StringManager.getInstance().addressMasking.tabLabel2);
        registerAddressLabel.textProperty().bind(StringManager.getInstance().addressMasking.registerAddressLabel);
        registerAddressDesc.textProperty().bind(StringManager.getInstance().addressMasking.registerAddressDesc);
        registerAddressMsg.textProperty().bind(StringManager.getInstance().addressMasking.registerAddressMsg);
        selectDomainLabel.textProperty().bind(StringManager.getInstance().addressMasking.selectDomainLabel);
        selectDomainDesc.textProperty().bind(StringManager.getInstance().addressMasking.selectDomainDesc);
        registerIdLabel.textProperty().bind(StringManager.getInstance().addressMasking.registerIdLabel);
        registerIdDesc.textProperty().bind(StringManager.getInstance().addressMasking.registerIdDesc);
        addrMaskingIDTextField.promptTextProperty().bind(StringManager.getInstance().addressMasking.registerIdPlaceholder);
        totalFeeTitle.textProperty().bind(StringManager.getInstance().addressMasking.totalFeeTitle);
        totalFeeAddress.textProperty().bind(StringManager.getInstance().addressMasking.totalFeeAddress);
        totalFeeAlias.textProperty().bind(StringManager.getInstance().addressMasking.totalFeeAlias);
        totalFeeLabel.textProperty().bind(StringManager.getInstance().addressMasking.totalFeeLabel);
        totalFeePayer.textProperty().bind(StringManager.getInstance().addressMasking.totalFeePayer);
        totalFeeDesc.textProperty().bind(StringManager.getInstance().addressMasking.totalFeeDesc);
        totalFeePayBtn.textProperty().bind(StringManager.getInstance().addressMasking.totalFeePayBtn);
        registerDomainLabel.textProperty().bind(StringManager.getInstance().addressMasking.registerDomainLabel);
        registerDomainDesc.textProperty().bind(StringManager.getInstance().addressMasking.registerDomainDesc);
        sideTabLabel1.textProperty().bind(StringManager.getInstance().addressMasking.sideTabLabel1);
        sideTabLabel2.textProperty().bind(StringManager.getInstance().addressMasking.sideTabLabel2);
        sideTab1Desc1.textProperty().bind(StringManager.getInstance().addressMasking.sideTab1Desc1);
        sideTab1Desc2.textProperty().bind(StringManager.getInstance().addressMasking.sideTab1Desc2);
        sideTab1Desc3.textProperty().bind(StringManager.getInstance().addressMasking.sideTab1Desc3);
        sideTab2Desc1.textProperty().bind(StringManager.getInstance().addressMasking.sideTab2Desc1);
        sideTab2Desc2.textProperty().bind(StringManager.getInstance().addressMasking.sideTab2Desc2);
        sideTab2Desc3.textProperty().bind(StringManager.getInstance().addressMasking.sideTab2Desc3);
        sideTab2Desc4.textProperty().bind(StringManager.getInstance().addressMasking.sideTab2Desc4);
        commercialDomainTitle.textProperty().bind(StringManager.getInstance().addressMasking.commercialDomainTitle);
        commercialDomainDesc.textProperty().bind(StringManager.getInstance().addressMasking.commercialDomainDesc);
        commercialDomainDesc1.textProperty().bind(StringManager.getInstance().addressMasking.commercialDomainDesc1);
        commercialDomainDesc2.textProperty().bind(StringManager.getInstance().addressMasking.commercialDomainDesc2);
        commercialDomainDesc3.textProperty().bind(StringManager.getInstance().addressMasking.commercialDomainDesc3);
        commercialDomainTextField.promptTextProperty().bind(StringManager.getInstance().addressMasking.commercialDomainPlaceholder);
        commercialDomainMsg.textProperty().bind(StringManager.getInstance().addressMasking.commercialDomainMsg);
        fileFormMsg.textProperty().bind(StringManager.getInstance().addressMasking.fileFormMsg);
        emailAddrLabel.textProperty().bind(StringManager.getInstance().addressMasking.emailAddrLabel);
        emailTextField.promptTextProperty().bind(StringManager.getInstance().addressMasking.emailPlaceholder);
        emailDesc1.textProperty().bind(StringManager.getInstance().addressMasking.emailDesc1);
        emailDesc2.textProperty().bind(StringManager.getInstance().addressMasking.emailDesc2);
        emailDesc3.textProperty().bind(StringManager.getInstance().addressMasking.emailDesc3);
        requestBtnLabel.textProperty().bind(StringManager.getInstance().addressMasking.requestBtnLabel);
        publicDomainTitle.textProperty().bind(StringManager.getInstance().addressMasking.publicDomainTitle);
        publicDomainDesc.textProperty().bind(StringManager.getInstance().addressMasking.publicDomainDesc);
        publicDomainDesc1.textProperty().bind(StringManager.getInstance().addressMasking.publicDomainDesc1);
        publicDomainDesc2.textProperty().bind(StringManager.getInstance().addressMasking.publicDomainDesc2);
        publicDomainDesc3.textProperty().bind(StringManager.getInstance().addressMasking.publicDomainDesc3);
        publicDomainDesc4.textProperty().bind(StringManager.getInstance().addressMasking.publicDomainDesc4);
        publicDomainTextField.promptTextProperty().bind(StringManager.getInstance().addressMasking.publicDomainPlaceholder);
        publicMessageTitle.textProperty().bind(StringManager.getInstance().addressMasking.publicMessageTitle);
        publicMessageDesc.textProperty().bind(StringManager.getInstance().addressMasking.publicMessageDesc);
        publicTextArea.promptTextProperty().bind(StringManager.getInstance().addressMasking.publicTextareaPlaceholder);
    }

    private ChangeListener<Boolean> textFieldListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Focus in Function
            if(newValue) {
                if(tab2LeftPane2.isVisible()) {
                    commercialDomainTextField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #999999; -fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 12px;" +
                            " -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4; -fx-prompt-text-fill: #999999; -fx-text-fill: #2b2b2b;");
                } else if(tab2LeftPane3.isVisible()) {
                    publicDomainTextField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #999999; -fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 12px;" +
                            " -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4; -fx-prompt-text-fill: #999999; -fx-text-fill: #2b2b2b;");
                }
            }

            // Focus out Function
            else {
                if(tab2LeftPane2.isVisible()) {
                    commercialDomainTextField.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 12px;" +
                            " -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4; -fx-prompt-text-fill: #999999; -fx-text-fill: #2b2b2b;");
                } else if(tab2LeftPane3.isVisible()) {
                    publicDomainTextField.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 12px;" +
                            " -fx-border-radius : 4 4 4 4; -fx-background-radius: 4 4 4 4; -fx-prompt-text-fill: #999999; -fx-text-fill: #2b2b2b;");
                }
            }
        }
    };

    @FXML
    private void onMouseClicked(InputEvent event){
        String id = ((Node)event.getSource()).getId();

        if(id.equals("tab1")) {
            initTab(0);

        } else if(id.equals("tab2")) {
            initTab(1);

        } else if(id.equals("sideTab1")) {
            initSideTab(0);

        } else if(id.equals("sideTab2")) {
            initSideTab(1);

        } else if(id.equals("domainRequestBtn")) {
            if(commercialDescGrid.isVisible()) {
                this.tab2LeftPane1.setVisible(false);
                this.tab2RightPane1.setVisible(true);
                this.tab2LeftPane2.setVisible(true);
                this.commercialDomainTextField.setText("");
                this.emailTextField.setText("");
            } else {
                this.tab2LeftPane1.setVisible(false);
                this.tab2LeftPane3.setVisible(true);
                this.publicDomainTextField.setText("");
                this.publicTextArea.setText("");

                //publicSendBtn
                // 오른쪽 뷰 보이
                this.tab2RightPane1.setVisible(true);
                this.emailTextField.setText("");
            }

        } else if(id.equals("commercialBackBtn")) {
            this.tab2LeftPane2.setVisible(false);
            this.tab2RightPane1.setVisible(false);
            this.tab2LeftPane1.setVisible(true);

        } else if(id.equals("publicBackBtn")) {
            this.tab2LeftPane3.setVisible(false);
            this.tab2RightPane1.setVisible(false);
            this.tab2LeftPane1.setVisible(true);

        } else if(id.equals("btnPay")){

            String abi = "[{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"owners\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"getCountOfDefaultFeeChangeConfirms\",\"outputs\":[{\"name\":\"count\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"ownerChangeCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint24\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_domainId\",\"type\":\"uint32\"}],\"name\":\"getRegistrationFee\",\"outputs\":[{\"name\":\"registrationFee\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"faces\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"revokeDomainRegistrationConfirmation\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint24\"},{\"name\":\"\",\"type\":\"address\"}],\"name\":\"ownerChangeConfirmations\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_defaultFee\",\"type\":\"uint256\"}],\"name\":\"registerDefaultFeeChange\",\"outputs\":[{\"name\":\"id\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint32\"},{\"name\":\"\",\"type\":\"address\"}],\"name\":\"domainConfigChangeConfirms\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint24\"}],\"name\":\"requirementChanges\",\"outputs\":[{\"name\":\"requirement\",\"type\":\"uint16\"},{\"name\":\"executed\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"revokeDefaultFeeChangeConfirmation\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"executeDefaultFeeChange\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"isOwner\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_domainAddress\",\"type\":\"address\"},{\"name\":\"_domainFee\",\"type\":\"uint256\"},{\"name\":\"_foundationFee\",\"type\":\"uint256\"},{\"name\":\"_needApproval\",\"type\":\"bool\"},{\"name\":\"_isOpened\",\"type\":\"bool\"}],\"name\":\"registerDomain\",\"outputs\":[{\"name\":\"id\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"confirmDefaultFeeChange\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"domainConfigChangeCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_addressMask\",\"type\":\"string\"}],\"name\":\"getFaceAddress\",\"outputs\":[{\"name\":\"faceAddress\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_owners\",\"type\":\"address[]\"},{\"name\":\"_required\",\"type\":\"uint16\"}],\"name\":\"init\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint24\"}],\"name\":\"ownerChanges\",\"outputs\":[{\"name\":\"owner\",\"type\":\"address\"},{\"name\":\"isAdd\",\"type\":\"bool\"},{\"name\":\"executed\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_faceAddress\",\"type\":\"address\"},{\"name\":\"_name\",\"type\":\"string\"},{\"name\":\"_domainId\",\"type\":\"uint32\"}],\"name\":\"registerMask\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"defaultFee\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"confirmDomainConfigChange\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"domainContractAddresses\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"confirmDomainRegistration\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"isDefaultFeeChangeConfirmed\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"revokeDomainConfigChangeConfirmation\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_changeId\",\"type\":\"uint24\"}],\"name\":\"confirmRequirementChange\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint24\"},{\"name\":\"\",\"type\":\"address\"}],\"name\":\"requirementChangeConfirmations\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_domainId\",\"type\":\"uint32\"}],\"name\":\"getDomainInfo\",\"outputs\":[{\"name\":\"domainId\",\"type\":\"uint32\"},{\"name\":\"domainAddress\",\"type\":\"address\"},{\"name\":\"domainName\",\"type\":\"string\"},{\"name\":\"domainFee\",\"type\":\"uint256\"},{\"name\":\"foundationFee\",\"type\":\"uint256\"},{\"name\":\"needApproval\",\"type\":\"bool\"},{\"name\":\"isOpened\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_changeId\",\"type\":\"uint24\"}],\"name\":\"getCountOfRequirementChangeConfirms\",\"outputs\":[{\"name\":\"count\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"isDomainRegistered\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"defaultFeeChangeCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"registerOwnerRemove\",\"outputs\":[{\"name\":\"ownerChangeId\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"MAX_NAME_LENGTH\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_changeId\",\"type\":\"uint24\"}],\"name\":\"revokeOwnerChangeConfirmation\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"maskNames\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"name\":\"defaultFeeChanges\",\"outputs\":[{\"name\":\"registered\",\"type\":\"bool\"},{\"name\":\"defaultFee\",\"type\":\"uint256\"},{\"name\":\"executed\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"domainCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"name\":\"domainRegistrations\",\"outputs\":[{\"name\":\"domainAddress\",\"type\":\"address\"},{\"name\":\"domainFee\",\"type\":\"uint256\"},{\"name\":\"foundationFee\",\"type\":\"uint256\"},{\"name\":\"domainName\",\"type\":\"string\"},{\"name\":\"isOpened\",\"type\":\"bool\"},{\"name\":\"needApproval\",\"type\":\"bool\"},{\"name\":\"executed\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"domainConfigs\",\"outputs\":[{\"name\":\"domainAddress\",\"type\":\"address\"},{\"name\":\"domainFee\",\"type\":\"uint256\"},{\"name\":\"foundationFee\",\"type\":\"uint256\"},{\"name\":\"domainName\",\"type\":\"string\"},{\"name\":\"needApproval\",\"type\":\"bool\"},{\"name\":\"isOpened\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_domainId\",\"type\":\"uint256\"},{\"name\":\"_domainFee\",\"type\":\"uint256\"},{\"name\":\"_foundationFee\",\"type\":\"uint256\"},{\"name\":\"_needApproval\",\"type\":\"bool\"},{\"name\":\"_isOpened\",\"type\":\"bool\"}],\"name\":\"registerDomainConfigChange\",\"outputs\":[{\"name\":\"id\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"registerOwnerAdd\",\"outputs\":[{\"name\":\"ownerChangeId\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_changeId\",\"type\":\"uint24\"}],\"name\":\"getCountOfOwnerChangeConfirms\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint32\"},{\"name\":\"\",\"type\":\"address\"}],\"name\":\"defaultFeeChangeConfirms\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_changeId\",\"type\":\"uint24\"}],\"name\":\"revokeRequirementChangeConfirmation\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_changeId\",\"type\":\"uint24\"}],\"name\":\"confirmOwnerChange\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"MAX_OWNER_COUNT\",\"outputs\":[{\"name\":\"\",\"type\":\"uint16\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"required\",\"outputs\":[{\"name\":\"\",\"type\":\"uint16\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"getCountOfDomainRegistrationConfirms\",\"outputs\":[{\"name\":\"count\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"sizeOfDomain\",\"outputs\":[{\"name\":\"size\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint32\"},{\"name\":\"\",\"type\":\"address\"}],\"name\":\"domainRegistrationConfirms\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"name\":\"domainConfigChanges\",\"outputs\":[{\"name\":\"domainFee\",\"type\":\"uint256\"},{\"name\":\"foundationFee\",\"type\":\"uint256\"},{\"name\":\"domainId\",\"type\":\"uint256\"},{\"name\":\"registered\",\"type\":\"bool\"},{\"name\":\"isOpened\",\"type\":\"bool\"},{\"name\":\"needApproval\",\"type\":\"bool\"},{\"name\":\"executed\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"domainRegistrationCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"address\"}],\"name\":\"masks\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_faceAddress\",\"type\":\"address\"}],\"name\":\"getMaskName\",\"outputs\":[{\"name\":\"maskName\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_id\",\"type\":\"uint32\"}],\"name\":\"getCountOfDomainConfigChangeConfirms\",\"outputs\":[{\"name\":\"count\",\"type\":\"uint32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"requirementChangeCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint24\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_faceAddress\",\"type\":\"address\"}],\"name\":\"getMaskHash\",\"outputs\":[{\"name\":\"maskHash\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_requirement\",\"type\":\"uint16\"}],\"name\":\"registerRequirementChange\",\"outputs\":[{\"name\":\"requirementChangeId\",\"type\":\"uint24\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_newAddress\",\"type\":\"address\"}],\"name\":\"handOverMask\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"},{\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"fallback\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"face\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"mask\",\"type\":\"string\"}],\"name\":\"MaskAddition\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"mask\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"oldAddress\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"newAddress\",\"type\":\"address\"}],\"name\":\"MaskHandOver\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainRegistrationId\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"domainAddress\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"domainFee\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"foundationFee\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"domainName\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"isOpened\",\"type\":\"bool\"}],\"name\":\"DomainRegistrationSubmission\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainRegistrationId\",\"type\":\"uint256\"}],\"name\":\"DomainRegistrationConfirmation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainRegistrationId\",\"type\":\"uint256\"}],\"name\":\"DomainRegistrationRevocation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainRegistrationId\",\"type\":\"uint256\"}],\"name\":\"DomainRegistrationExecution\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"defaultFeeChangeId\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"aApis\",\"type\":\"uint256\"}],\"name\":\"DefaultFeeChangeSubmission\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"defaultFeeChangeId\",\"type\":\"uint256\"}],\"name\":\"DefaultFeeChangeConfirmation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"defaultFeeChangeId\",\"type\":\"uint256\"}],\"name\":\"DefaultFeeChangeRevocation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"defaultFeeChangeId\",\"type\":\"uint256\"}],\"name\":\"DefaultFeeChangeExecution\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainConfigChangeId\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"domainId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"domainFee\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"foundationFee\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"isOpened\",\"type\":\"bool\"}],\"name\":\"DomainConfigChangeSubmission\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainConfigChangeId\",\"type\":\"uint256\"}],\"name\":\"DomainConfigChangeConfirmation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainConfigChangeId\",\"type\":\"uint256\"}],\"name\":\"DomainConfigChangeRevocation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"domainConfigChangeId\",\"type\":\"uint256\"}],\"name\":\"DomainConfigChangeExecution\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"ownerChangeId\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"message\",\"type\":\"string\"}],\"name\":\"OwnerChangeSubmission\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"changeId\",\"type\":\"uint256\"}],\"name\":\"OwnerChangeConfirmation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"changeId\",\"type\":\"uint256\"}],\"name\":\"OwnerChangeRevocation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"OwnerAddition\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"OwnerRemoval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"requiredChangeId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"require\",\"type\":\"uint256\"}],\"name\":\"RequirementChangeSubmission\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"changeId\",\"type\":\"uint256\"}],\"name\":\"RequirementChangeConfirmation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"changeId\",\"type\":\"uint256\"}],\"name\":\"RequirementChangeRevocation\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"changeId\",\"type\":\"uint256\"}],\"name\":\"RequirementChangeExecution\",\"type\":\"event\"}]";
            byte[] contractAddress = Hex.decode("1000000000000000000000000000000000037449");

            String faceAddress = selectAddressController.getAddress();
            String name = addrMaskingIDTextField.getText();
            String domainId = "0";

            String address = selectPayerController.getAddress();
            String value = "10000000000000000000";
            String gasLimit = "1000000";
            String gasPrice = "50000000000";

            CallTransaction.Contract contract = new CallTransaction.Contract(abi);
            CallTransaction.Function setter = contract.getByName("registerMask");
            Object[] args = new Object[3];
            args[0] = Hex.decode(faceAddress);   //_faceAddress
            args[1] = name;   //_name
            args[2] = new BigInteger(domainId);   //_domainId
            byte[] functionCallBytes = setter.encode(args);


            System.out.println("faceAddress : "+faceAddress);
            System.out.println("name : "+name);
            System.out.println("domainId : "+domainId);
            System.out.println("address : "+address);
            System.out.println("value : "+value);
            System.out.println("gasLimit : "+gasLimit);
            System.out.println("gasPrice : "+gasPrice);
            for(int i=0; i<args.length; i++){
                System.out.println("args : "+args[i]);
            }


            // 완료 팝업 띄우기
            PopupContractWarningController controller = (PopupContractWarningController) AppManager.getInstance().guiFx.showMainPopup("popup_contract_warning.fxml", 0);
            controller.setData(address, value, gasPrice, gasLimit, contractAddress, functionCallBytes);
            controller.setHandler(new PopupContractWarningController.PopupContractWarningImpl() {
                @Override
                public void success() {
                    System.out.println("success");
                    // 컨트렉트 생성 후, 화면 초기화
                    // initLayoutData(1);
                }
            });
        }

    }

    public void initTab(int index){
        if(index == 0) {
            //Register Alias
            this.tab1LeftPane.setVisible(true);
            this.tab1RightPane.setVisible(true);
            this.tab2LeftPane1.setVisible(false);
            this.tab2LeftPane2.setVisible(false);
            this.tab2LeftPane3.setVisible(false);
            this.tab2RightPane1.setVisible(false);
            this.addrMaskingIDTextField.setText("");
            this.tabLabel1.setTextFill(Color.web("#910000"));
            this.tabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            this.tabLinePane1.setVisible(true);
            this.tabLabel2.setTextFill(Color.web("#999999"));
            this.tabLabel2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:12px;");
            this.tabLinePane2.setVisible(false);

        } else if(index == 1) {
            //Register Domain
            this.tab1LeftPane.setVisible(false);
            this.tab2LeftPane2.setVisible(false);
            this.tab2LeftPane3.setVisible(false);
            this.tab1RightPane.setVisible(false);
            this.tab2RightPane1.setVisible(false);
            this.tab2LeftPane1.setVisible(true);
            this.tabLabel2.setTextFill(Color.web("#910000"));
            this.tabLabel2.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size:12px;");
            this.tabLinePane2.setVisible(true);
            this.tabLabel1.setTextFill(Color.web("#999999"));
            this.tabLabel1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size:12px;");
            this.tabLinePane1.setVisible(false);

            this.publicDescGrid.setVisible(false);
            this.commercialDescGrid.setVisible(true);
            this.sideTabLabel1.setTextFill(Color.web("#910000"));
            this.sideTabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 14px;");
            this.sideTabLinePane1.setVisible(true);
            this.sideTabLabel2.setTextFill(Color.web("#999999"));
            this.sideTabLabel2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size: 14px;");
            this.sideTabLinePane2.setVisible(false);

            initSideTab(0);
        }
    }
    public void initSideTab(int index){
        if(index == 0) {
            //Commercial domain
            this.commercialDescGrid.setVisible(true);
            this.publicDescGrid.setVisible(false);
            this.sideTabLabel1.setTextFill(Color.web("#910000"));
            this.sideTabLabel1.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 14px;");
            this.sideTabLinePane1.setVisible(true);
            this.sideTabLabel2.setTextFill(Color.web("#999999"));
            this.sideTabLabel2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size: 14px;");
            this.sideTabLinePane2.setVisible(false);
            this.domainRequestBtn.setVisible(false);

        } else if(index == 1) {
            //Public domain
            this.commercialDescGrid.setVisible(false);
            this.publicDescGrid.setVisible(true);
            this.sideTabLabel2.setTextFill(Color.web("#910000"));
            this.sideTabLabel2.setStyle("-fx-font-family: 'Open Sans SemiBold'; -fx-font-size: 14px;");
            this.sideTabLinePane2.setVisible(true);
            this.sideTabLabel1.setTextFill(Color.web("#999999"));
            this.sideTabLabel1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-size: 14px;");

            this.sideTabLinePane1.setVisible(false);
            this.domainRequestBtn.setVisible(true);
        }
    }

    public void settingLayoutData() {
        String address = selectAddressController.getAddress();
        String mask = AppManager.getInstance().getMaskWithAddress(address);
        String domain = selectDomainController.getDomain();
        String maskingId = addrMaskingIDTextField.getText();
        String fee = selectDomainController.getFee();


        this.selectedDomainLabel.setText(domain);
        this.selectDomainMsg.setText(domain+" is "+fee+"APIS");

        this.totalWalletAddressValue.setText(address);
        this.totalFeeAliaValue.setText(maskingId+domain);
        this.totalFeeValue.setText(fee+" APIS");

        // 도메인 체크
        if(mask != null && mask.length() > 0){
            this.registerAddressIcon.setVisible(true);
            this.registerAddressIcon.setImage(downRed);

            this.registerAddressMsg.textProperty().unbind();
            this.registerAddressMsg.textProperty().bind(StringManager.getInstance().addressMasking.registerAddressMsg2);
        }else{
            this.registerAddressIcon.setVisible(true);
            this.registerAddressIcon.setImage(downGreen);

            this.registerAddressMsg.textProperty().unbind();
            this.registerAddressMsg.textProperty().bind(StringManager.getInstance().addressMasking.registerAddressMsg);
        }

        if(maskingId != null && maskingId.length() > 0){

            String addressUsed = AppManager.getInstance().getAddressWithMask(maskingId+domain);
            if(addressUsed != null){
                // used
                this.idIcon.setVisible(true);
                this.idIcon.setImage(downRed);
                this.idMsg.setVisible(true);
                this.idMsg.setTextFill(Color.web("#910000"));
                this.idMsg.setText(maskingId+domain+" is already in use.");

                this.idIcon2.setVisible(true);
                this.idMsg2.setVisible(true);
                this.idMsg2.setText(address);
            }else{
                // not used
                this.idIcon.setVisible(true);
                this.idIcon.setImage(downGreen);
                this.idMsg.setVisible(true);
                this.idMsg.setTextFill(Color.web("#36b25b"));
                this.idMsg.setText(maskingId+domain+" is available");

                this.idIcon2.setVisible(false);
                this.idMsg2.setVisible(false);
                this.idMsg2.setText("");
            }
        }else{
            this.idIcon.setVisible(false);
            this.idMsg.setVisible(false);
            this.idIcon2.setVisible(false);
            this.idMsg2.setVisible(false);
            this.idMsg2.setText("");
        }

    }

    public void domainDragDropMouseEntered() {
        this.domainDragDrop.setImage(domainDragDropColor);
    }

    public void domainDragDropMouseExited() {
        this.domainDragDrop.setImage(domainDragDropGrey);
    }

    public void domainRequestMouseClicked(){
        String domain = this.publicDomainTextField.getText();
        String message = this.publicTextArea.getText();
        String email = this.emailTextField.getText();
        System.out.println("domain : " + domain);
        System.out.println("message : " + message);
        System.out.println("email : " + email);

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("domain" , domain);
            params.put("message" , message);
            params.put("email" , email);

            String response = HttpRequestManager.sendRequestPublicDomain(params);
            System.out.println("response > \n" + response);

        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 완료 팝업
        AppManager.getInstance().guiFx.showMainPopup("popup_success.fxml",1);

        this.publicDomainTextField.setText("");
        this.publicTextArea.setText("");
        this.emailTextField.setText("");
    }

    public void update() {

    }
}
