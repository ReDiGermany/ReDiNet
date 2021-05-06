package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.view.Bookmark;
import com.github.redigermany.redinet.view.Observer;
import com.github.redigermany.redinet.view.WebTab;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainLayout extends Application {
    private Stage primaryStage;

    @FXML
    private Button prevBtn;

    @FXML
    private Button forwBtn;

    @FXML
    private Button reloadBtn;

    @FXML
    private TextField urlBar;

    @FXML
    private Button bookmarkBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private WebTab newTab;

    @FXML
    private TabPane tabPane;
    @FXML
    private HBox bookmarks;

    public String ____MYFUCKINGNICEVARIABLETHATIWILLSEEINSTANTLYONCEIHAVETHECORRECTITEM="____MYFUCKINGNICEVARIABLETHATIWILLSEEINSTANTLYONCEIHAVETHECORRECTITEM";

    public void newTab(String url){
        WebTab tab = new WebTab();
        if(url!=null) tab.setUrl(url);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }
    public void newTab(){
        newTab(null);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        URL xml = getClass().getResource("/com/github/redigermany/redinet/view/MainLayout.fxml");
        if(xml==null){
            System.out.println("File \"MainLayout.fxml\" not found!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(xml);
        loader.setController(this);
        AnchorPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateLayoutWidth();
        });
        updateLayoutWidth();
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateLayoutHeight();
        });
        updateLayoutHeight();
        prevBtn.setDisable(true);
        forwBtn.setDisable(true);
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            initUpdateTabInfo(t1);
        });
//        tabPane.getSelectionModel().clearAndSelect(0);
        initUpdateTabInfo(tabPane.getSelectionModel().getSelectedItem());
        newTab.setOnSelectionChanged(e->{
            newTab();
        });
        loadBookmarks();
    }

    private void loadBookmarks() {
        ArrayList<Bookmark> bm = new ArrayList<>();
        bm.add(new Bookmark("ReDiGermany.de","https://redigermany.de",this));
        bm.add(new Bookmark("ReDiGermany.de","https://redigermany.de",this));
        bm.add(new Bookmark("Google","https://google.de",this));
        bm.add(new Bookmark("Hof-University","https://Hof-University.de",this));
        bm.add(new Bookmark("Moodle","https://moodle.Hof-University.de",this));
        bm.add(new Bookmark("Java 16 Documentation","https://docs.oracle.com/en/java/javase/16/index.html",this));

        bookmarks.getChildren().addAll(bm);
    }

    private void initUpdateTabInfo(Tab t1){
        ((WebTab)t1).attach(new Observer() {
            @Override
            public void update(String location,String icon,String title) {
                doUpdateTabInfo(location, icon, title);
            }
        });
        String baseInfo = t1.getId();
        if(baseInfo!=null) {
            String[] info = t1.getId().split(":-:");
            doUpdateTabInfo(info[0],info[1],info[2]);
        }
    }
    private void doUpdateTabInfo(String location,String icon,String title){
        if (!icon.equals("")) primaryStage.getIcons().add(new Image(icon));
        primaryStage.setTitle(title);
        urlBar.setText(location);
    }

    private void updateLayoutHeight() {
        tabPane.setMinHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMaxHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMinHeight(primaryStage.getHeight()-89);
        tabPane.setMaxHeight(primaryStage.getHeight()-89);
    }

    private void updateLayoutWidth() {
        urlBar.setMinWidth(primaryStage.getWidth()-(6*menuBtn.getWidth()));
        urlBar.setMaxWidth(primaryStage.getWidth()-(6*menuBtn.getWidth()));
        tabPane.setMinWidth(primaryStage.getWidth()-15);
        tabPane.setMaxWidth(primaryStage.getWidth()-15);
    }
}
