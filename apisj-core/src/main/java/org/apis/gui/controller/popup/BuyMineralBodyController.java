package org.apis.gui.controller.popup;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apis.gui.common.JavaFXStyle;
import org.apis.gui.controller.base.BaseViewController;
import org.apis.gui.controller.module.ApisSelectBoxController;

import java.net.URL;
import java.util.ResourceBundle;

public class BuyMineralBodyController extends BaseViewController {
    private final String[] chargeAmountSelectTextList = { "1", "10", "100", "1,000", "10,000", "100,000", "1,000,000"};
    private final String[] mineralDetailSelectTextList = {
            "1 APIS = 1 MNR (0% 보너스)",
            "10 APIS = 11 MNR (10%)",
            "100 APIS = 120 MNR (20%)",
            "1,000 APIS = 1,300 MNR (30%)",
            "10,000 APIS = 14,000 MNR (40%)",
            "100,000 APIS = 150,000 MNR (50%)",
            "1,000,000 APIS = 1,600,000 MNR (60%)"};

    @FXML private ApisSelectBoxController beneficiaryController, payerController;
    @FXML private VBox chargeAmountSelectChild, mineralDetailSelectChild;
    @FXML private VBox chargeAmountSelectList, mineralDetailSelectList;
    @FXML private ScrollPane chargeAmountSelectListView, mineralDetailSelectListView;
    @FXML private Label chargeAmountSelectHead, mineralDetailSelectHead;
    @FXML private TextField chargeAmount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        beneficiaryController.init(ApisSelectBoxController.SELECT_BOX_TYPE_ALIAS);
        payerController.init(ApisSelectBoxController.SELECT_BOX_TYPE_ALIAS);

        initChargeAmountSelectBox();
        initMineralDetailSelectBox();
        hideSelectList(chargeAmountSelectListView, chargeAmountSelectList);
        hideSelectList(mineralDetailSelectListView, mineralDetailSelectList);

    }

    @FXML
    public void onMouseClickedChargeAmount(){
        if(chargeAmountSelectListView.isVisible()){
            hideSelectList(chargeAmountSelectListView, chargeAmountSelectList);
        }else{
            showSelectList(chargeAmountSelectListView, chargeAmountSelectList);
        }
    }

    @FXML
    public void onMouseClickedDetail(){
        if(mineralDetailSelectListView.isVisible()){
            hideSelectList(mineralDetailSelectListView, mineralDetailSelectList);
        }else{
            showSelectList(mineralDetailSelectListView, mineralDetailSelectList);
        }
    }



    public void initChargeAmountSelectBox(){
        chargeAmountSelectList.getChildren().clear();
        for(int i=0; i<chargeAmountSelectTextList.length; i++){
            addSelectBoxItem(chargeAmountSelectList, chargeAmountSelectTextList[i]+" APIS");
        }

        chargeAmountSelectChild.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hideSelectList(chargeAmountSelectListView, chargeAmountSelectList);
            }
        });
    }

    public void initMineralDetailSelectBox(){
        mineralDetailSelectList.getChildren().clear();
        for(int i=0; i<mineralDetailSelectTextList.length; i++){
            addSelectBoxItem(mineralDetailSelectList, mineralDetailSelectTextList[i]);
        }

        mineralDetailSelectChild.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hideSelectList(mineralDetailSelectListView, mineralDetailSelectList);
            }
        });
    }

    private void addSelectBoxItem(VBox list, String text){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle(new JavaFXStyle(anchorPane.getStyle()).add("-fx-background-color","#ffffff").toString());
        Label label = new Label();
        label.setText(text);
        label.setPadding(new Insets(8,16,8,16));
        label.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                label.setStyle(new JavaFXStyle(label.getStyle()).add("-fx-background-color","#f2f2f2").toString());
            }
        });
        label.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                label.setStyle(new JavaFXStyle(label.getStyle()).add("-fx-background-color","#ffffff").toString());
            }
        });

        // method list click
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(list == chargeAmountSelectList){
                    chargeAmountSelectHead.setText(label.getText());
                    chargeAmount.setText(label.getText().split(" ")[0].replaceAll(",",""));
                }
            }
        });
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        anchorPane.getChildren().add(label);
        list.getChildren().add(anchorPane);
    }

    public void showSelectList(ScrollPane scrollPane, VBox list){
        scrollPane.setVisible(true);
        scrollPane.prefHeightProperty().setValue(-1);
        list.prefHeightProperty().setValue(-1);
    }

    public void hideSelectList(ScrollPane scrollPane, VBox list){
        scrollPane.setVisible(false);
        scrollPane.prefHeightProperty().setValue(0);
        list.prefHeightProperty().setValue(40);
    }

}