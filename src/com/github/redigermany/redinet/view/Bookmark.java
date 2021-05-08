package com.github.redigermany.redinet.view;

import com.github.redigermany.redinet.controller.MainLayout;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

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
        ContextMenu contextMenu = new ContextMenu();
        MenuItem openBookmark = new MenuItem("Open");
        MenuItem copyAddress = new MenuItem("Copy URL");
        MenuItem openInNewTab = new MenuItem("Open in new Tab");
        MenuItem openInNewWindow = new MenuItem("Open in new Window");
        MenuItem deleteBookmark = new MenuItem("Delete Bookmark");
        contextMenu.getItems().addAll(openBookmark,copyAddress,openInNewTab,openInNewWindow,deleteBookmark);

        copyAddress.setOnAction(e->{
            Clipboard clipboard = Clipboard.getSystemClipboard();

            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.PLAIN_TEXT,url);
            clipboard.setContent(content);
            System.out.println("Copy to clipboard");
//            stage.setTab(url);
        });

        openBookmark.setOnAction(e->{
            stage.setTab(url);
        });

        openInNewTab.setOnAction(e->{
            stage.newTab(url);
        });

        openInNewWindow.setOnAction(e->{
            stage.newWindow(url);
        });

        deleteBookmark.setOnAction(e->{
            stage.getBookmarkController().removeBookmark(url);
        });
        setOnContextMenuRequested(e->{
            e.consume();
            contextMenu.show(this,e.getScreenX(),e.getScreenY());
        });
        addEventHandler(MouseEvent.MOUSE_CLICKED,e->{
            if(e.getButton()==MouseButton.SECONDARY) return;
            boolean inNewTab = e.isControlDown() || e.getButton()== MouseButton.MIDDLE;
            boolean inNewWindow = e.isShiftDown();
            if(inNewTab){
                stage.newTab(url);
            }else if(inNewWindow){
                stage.newWindow(url);
            }else if(e.getButton()==MouseButton.PRIMARY){
                stage.setTab(url);
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
