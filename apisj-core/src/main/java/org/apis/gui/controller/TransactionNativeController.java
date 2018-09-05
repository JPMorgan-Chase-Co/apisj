package org.apis.gui.controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apis.db.sql.AccountRecord;
import org.apis.db.sql.DBManager;
import org.apis.db.sql.TransactionRecord;
import javafx.scene.paint.Color;
import org.apis.db.sql.AccountRecord;
import org.apis.db.sql.DBManager;
import org.apis.db.sql.TransactionRecord;
import org.apis.gui.manager.AppManager;
import org.apis.gui.manager.KeyStoreManager;
import org.spongycastle.util.encoders.Hex;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionNativeController implements Initializable {
    @FXML
    private AnchorPane txDetailsAnchor, dropBoxList, txAnchor, dropBoxBtn;
    @FXML
    private VBox txList, addrList;
    @FXML
    private Label dropBoxLabel;
    @FXML
    private ImageView dropBoxImg;
    @FXML
    private TransactionNativeDetailsController detailsController;

    private Image dropDownImg, dropUpImg;
    private boolean dropBoxMouseFlag = false;
    private boolean dropBoxBtnFlag = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tab Added
        AppManager.getInstance().guiFx.setTransactionNative(this);

        // Add Drop List
        addDropList();
        // Add List
        addList();

        // Initiate DropBox
        dropDownImg = new Image("image/btn_drop_down@2x.png");
        dropUpImg = new Image("image/btn_drop_up@2x.png");
        dropBoxList.setVisible(false);

        // Hide Details
        detailsController.setHandler(new TransactionNativeDetailsController.TransactionNativeDetailsImpl() {
            @Override
            public void hideDetails() {
                txDetailsAnchor.setVisible(false);
            }
        });

        dropBoxList.setOnMouseEntered(event -> dropBoxMouseFlag = true);
        dropBoxList.setOnMouseExited(event -> dropBoxMouseFlag = false);
        dropBoxLabel.setOnMouseEntered(event -> dropBoxBtnFlag = true);
        dropBoxLabel.setOnMouseExited(event -> dropBoxBtnFlag = false);
        txAnchor.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!dropBoxMouseFlag && !dropBoxBtnFlag) {
                    dropBoxList.setVisible(false);
                    dropBoxImg.setImage(dropDownImg);
                }
            }
        });
    }

    public void update() {
        for(int i=0; i<AppManager.getInstance().getKeystoreList().size(); i++) {
            System.out.println(AppManager.getInstance().getKeystoreList().get(i).address);
        }
    }

    public void addDropList() {
        for(int i=0; i<10; i++) {
            addDropItem();
        }
    }

    public void addDropItem() {
        //item
        try {
            URL labelUrl = new File("apisj-core/src/main/resources/scene/transaction_native_drop_list.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(labelUrl);
            AnchorPane item = loader.load();
            addrList.getChildren().add(item);

            TransactionNativeDropListController itemController = (TransactionNativeDropListController)loader.getController();
            itemController.setHandler(new TransactionNativeDropListController.TransactionNativeDropListImpl() {
                @Override
                public void setDropLabel() {
                    String addr = itemController.getWalletAddr()+" ("+itemController.getAddrMasking()+")";
                    dropBoxLabel.setText(addr);
                    dropBoxLabel.setTextFill(Color.web("#ffffff"));
                    dropBoxList.setVisible(false);
                    dropBoxImg.setImage(dropDownImg);

                    //db
                    byte[] address = Hex.decode(itemController.getWalletAddr());
                    List<TransactionRecord> list = DBManager.getInstance().selectTransactions(address);
                    //int pageNum = 0;
                    //int pageSize = 7;
                    //DBManager.getInstance().selectTransactions(address,  0, 7);
                    for(int i=0; i<list.size();i++){
                        System.out.println(list.get(i).getHash());
                        System.out.println(list.get(i).getStatus());
                        System.out.println(list.get(i).getAmount());
                        System.out.println(list.get(i).getSender());
                        System.out.println(list.get(i).getReceiver());
                        System.out.println(list.get(i).getGasUsed());
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addList() {
        //item
        try {
            URL labelUrl = new File("apisj-core/src/main/resources/scene/transaction_native_list.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(labelUrl);
            AnchorPane item = loader.load();
            txList.getChildren().add(item);

            TransactionNativeListController itemController = (TransactionNativeListController)loader.getController();
            itemController.setHandler(new TransactionNativeListController.TransactionNativeListImpl() {
                @Override
                public void showDetails() {
                    txDetailsAnchor.setVisible(true);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void dropBoxBtnClicked(InputEvent event) {
        if(dropBoxList.isVisible()) {
            dropBoxList.setVisible(false);
            dropBoxImg.setImage(dropDownImg);
        } else {
            dropBoxList.setVisible(true);
            dropBoxImg.setImage(dropUpImg);
        }
        event.consume();
    }

}
