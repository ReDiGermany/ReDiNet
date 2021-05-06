package com.github.redigermany.redinet.view;

import com.github.redigermany.redinet.controller.MainLayout;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Bookmark extends Pane {
    private String name;
    private String url;
    private MainLayout stage;
    private Label text = new Label();

    public static Object getController(Node node) {
        Object controller = null;
        do {
            controller = node.getUserData();
            node = node.getParent();
        } while (controller == null && node != null);
        return controller;
    }

    public Bookmark(){
        getStyleClass().add("bookmark");
        text.getStyleClass().add("bookmark-text");
        getChildren().add(text);
        setOnMouseClicked(e->{
            boolean inNewTab = e.isControlDown() || e.getButton()== MouseButton.MIDDLE;
            boolean inNewWindow = e.isShiftDown();
            System.out.printf("Opening %s in %s%n",url,inNewTab?"new tab":(inNewWindow?"new window":"same tab"));
            if(inNewTab){
                stage.newTab(url);
            }else if(inNewWindow){
                //TODO: Open in new Window
            }else{
                //TODO: Open in current Tab
            }
        });
//        setOnAction(e->{
//            stage.newTab(url);
//        });
    }

    public Bookmark(String name,String url,MainLayout stage){
        this();
        setName(name);
        setUrl(url);
        setStage(stage);
    }

    public String getName() {
        return name;
    }

    public Bookmark setName(String name) {
        text.setText(name);
        this.name = name;
        return this;
    }

    public Bookmark setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Bookmark setStage(MainLayout stage) {
        this.stage = stage;
        return this;
    }

    public MainLayout getStage() {
        return stage;
    }
}
