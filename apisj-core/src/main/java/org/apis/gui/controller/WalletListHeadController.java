package org.apis.gui.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apis.gui.common.JavaFXStyle;
import org.apis.gui.controller.base.BaseViewController;
import org.apis.gui.manager.ImageManager;
import org.apis.gui.model.WalletItemModel;
import org.apis.gui.model.base.BaseModel;
import org.apis.util.blockchain.ApisUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class WalletListHeadController extends BaseViewController {
    private WalletListGroupController.GroupType groupType;

    public static final int HEADER_STATE_CLOSE = 0;
    public static final int HEADER_STATE_OPEN = 1;

    private static final int HEADER_COPY_STATE_NONE = 0;
    private static final int HEADER_COPY_STATE_NORMAL = 1;
    private static final int HEADER_COPY_STATE_ACTIVE = 2;
    private boolean btnCopyClickedFlag = false;


    private WalletItemModel model;
    private boolean isChecked = false;
    private String prevOnMouseClickedEventFxid = "";

    private WalletListHeaderInterface handler;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private GridPane groupTypePane, unitTypePane;

    @FXML
    private ImageView walletIcon;
    @FXML
    private ImageView btnCheckBox, btnAddressMasking, btnTransfer, foldIcon;
    @FXML
    private Label btnCopy, labelWalletAlias, labelWalletAddress, labelAddressMasking, valueNatural, valueUnit;
    @FXML
    private Pane leftLine;
    @FXML
    private AnchorPane miningPane;
    @FXML private Label tagLabel;

    @FXML
    private ImageView walletIcon1, foldIcon1;
    @FXML
    private Label name, valueNatural1, valueUnit1;



    private Image apisIcon = ImageManager.apisIcon;
    private Image mineraIcon = ImageManager.mineraIcon;
    private Image imageFold = new Image("image/btn_fold@2x.png");
    private Image imageUnFold = new Image("image/btn_unfold@2x.png");
    private Image imageCheck = new Image("image/btn_circle_red@2x.png");
    private Image imageUnCheck = new Image("image/btn_circle_none@2x.png");


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // set a clip to apply rounded border to the original image.
        Rectangle clip = new Rectangle(this.walletIcon.getFitWidth()-0.5,this.walletIcon.getFitHeight()-0.5);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        walletIcon.setClip(clip);

        setCopyState(HEADER_COPY_STATE_NONE);
        setCheck(false);
        setState(HEADER_STATE_CLOSE);

        setBalance("0");
        setMask(null);
    }

    @FXML
    public void onMouseClicked(InputEvent event){
        String id = ((Node)event.getSource()).getId();
        if(id.equals("rootPane")){
            if(handler != null
                    && ( prevOnMouseClickedEventFxid.equals("") || prevOnMouseClickedEventFxid.equals("rootPane"))){
                handler.onClickEvent(event, this.model);
            }

            prevOnMouseClickedEventFxid = "rootPane";
        }else if(id.equals("btnCheckBox")){
            setCheck(!this.isChecked);
            if(handler != null){
                handler.onChangeCheck(model, isChecked);
            }

            prevOnMouseClickedEventFxid = "btnCheckBox";
        }else if(id.equals("btnCopy")){
            btnCopyClickedFlag = true;

            prevOnMouseClickedEventFxid = "btnCopy";
            String text = labelWalletAddress.getText();
            StringSelection stringSelection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            if(handler != null){
                handler.onClickCopy(text, this.model);
            }

        }else if(id.equals("btnAddressMasking")){

            prevOnMouseClickedEventFxid = "btnAddressMasking";
            if(handler != null){
                handler.onClickAddressMasking(event, this.model);
            }
        }else if(id.equals("btnTransfer")){

            prevOnMouseClickedEventFxid = "btnTransfer";
            if(handler != null){
                handler.onClickTransfer(event, this.model);
            }
        }
    }
    @FXML
    public void onMouseEntered(InputEvent event){
        btnCopyClickedFlag = false;

        String id = ((Node)event.getSource()).getId();
        if(id.equals("paneAddress")){
            setCopyState(HEADER_COPY_STATE_NORMAL);
        }else if(id.equals("btnCopy")){
            setCopyState(HEADER_COPY_STATE_ACTIVE);
        }
    }
    @FXML
    public void onMouseExited(InputEvent event){
        String id = ((Node)event.getSource()).getId();
        if(id.equals("paneAddress")){
            setCopyState(HEADER_COPY_STATE_NONE);
        }else if(id.equals("btnCopy")){
            if(btnCopyClickedFlag == true) {
                setCopyState(HEADER_COPY_STATE_NONE);
            } else {
                setCopyState(HEADER_COPY_STATE_NORMAL);
            }
        }
    }

    public void setMask(String mask){
        if(mask != null && mask.length() > 0){
            labelAddressMasking.setVisible(true);
            labelAddressMasking.setText(mask);

            btnAddressMasking.setVisible(false);
        }else{
            labelAddressMasking.setVisible(false);
            labelAddressMasking.setText("");

            btnAddressMasking.setVisible(true);
        }
    }

    @Override
    public void setModel(BaseModel model){
        this.model = (WalletItemModel)model;

        this.walletIcon.setImage(ImageManager.getIdenticons(this.model.getAddress()));
        this.labelWalletAlias.setText(this.model.getAddress());

        // 마이닝 / 마스터노드 체크
        if(this.model.isMining()){
            this.tagLabel.setVisible(true);
            this.tagLabel.setText("MINING");
            this.tagLabel.setPrefWidth(-1);
            GridPane.setMargin(this.tagLabel, new Insets(0,4,0,0));
        }else if(this.model.isMasterNode()){
            this.tagLabel.setVisible(true);
            this.tagLabel.setText("MASTERNODE");
            this.tagLabel.setPrefWidth(-1);
            GridPane.setMargin(this.tagLabel, new Insets(0,4,0,0));
        }else{
            this.tagLabel.setVisible(true);
            this.tagLabel.setText("");
            this.tagLabel.setPrefWidth(0);
            GridPane.setMargin(this.tagLabel, new Insets(0,0,0,0));
        }


    }


    public void setCheck(boolean isChecked){
        this.isChecked = isChecked;
        if(isChecked){
            btnCheckBox.setImage(imageCheck);
        }else{
            btnCheckBox.setImage(imageUnCheck);
        }
    }

    private void setCopyState(int state){
        switch (state){
            case HEADER_COPY_STATE_NONE :
                this.btnCopy.setStyle( new JavaFXStyle(this.btnCopy.getStyle()).add("-fx-background-color","#999999").toString() );
                this.btnCopy.setVisible(false);
                this.labelWalletAddress.setStyle( new JavaFXStyle(this.labelWalletAddress.getStyle()).remove("-fx-underline").toString() );
                break;

            case HEADER_COPY_STATE_NORMAL :
                this.btnCopy.setStyle( new JavaFXStyle(this.btnCopy.getStyle()).add("-fx-background-color","#999999").toString() );
                this.btnCopy.setVisible(true);
                this.labelWalletAddress.setStyle( new JavaFXStyle(this.labelWalletAddress.getStyle()).add("-fx-underline","true").toString() );
                break;

            case HEADER_COPY_STATE_ACTIVE :
                this.btnCopy.setStyle( new JavaFXStyle(this.btnCopy.getStyle()).add("-fx-background-color","#910000").toString() );
                this.btnCopy.setVisible(true);
                this.labelWalletAddress.setStyle( new JavaFXStyle(this.labelWalletAddress.getStyle()).add("-fx-underline","true").toString() );
                break;
        }
    }

    public void setState(int state){
        switch (state){
            case HEADER_STATE_CLOSE :
                rootPane.setStyle( new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#ffffff").toString() );
                foldIcon.setImage(imageUnFold);
                foldIcon1.setImage(imageUnFold);
                rootPane.setEffect(null);
                leftLine.setVisible(false);
                break;

            case HEADER_STATE_OPEN :
                rootPane.setStyle( new JavaFXStyle(rootPane.getStyle()).add("-fx-background-color", "#eaeaea").toString() );
                foldIcon.setImage(imageFold);
                foldIcon1.setImage(imageFold);
                rootPane.setEffect(new DropShadow(10, Color.color(0,0,0,0.2)));
                leftLine.setVisible(true);
                break;
        }
    }

    public void setBalance(String balance){
        if(balance == null) return;
        valueNatural.setText(ApisUtil.readableApis(new BigInteger(balance), ',', false));
    }

    public WalletListHeadController setGroupType(WalletListGroupController.GroupType groupType) {
        this.groupType = groupType;

        if(this.groupType == WalletListGroupController.GroupType.WALLET){
            unitTypePane.setVisible(false);
            groupTypePane.setVisible(true);
        }else if(this.groupType == WalletListGroupController.GroupType.TOKEN){
            unitTypePane.setVisible(true);
            groupTypePane.setVisible(false);
        }
        return this;

    }


    public interface WalletListHeaderInterface{
        void onClickEvent(InputEvent event, WalletItemModel model);
        void onClickTransfer(InputEvent event, WalletItemModel model);
        void onChangeCheck(WalletItemModel model, boolean isChecked);
        void onClickCopy(String address, WalletItemModel model);
        void onClickAddressMasking(InputEvent event, WalletItemModel model);
    }
    public void setHandler(WalletListHeaderInterface handler){this.handler = handler; }

}
