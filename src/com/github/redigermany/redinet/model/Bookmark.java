package com.github.redigermany.redinet.model;

import com.github.redigermany.redinet.controller.MainLayout;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

/**
 * Single bookmark link
 * @author Max Kruggel
 */
public class Bookmark extends Pane {
    private String name;
    private String url;
    private MainLayout stage;
    private final Label text = new Label();
    private final ContextMenu contextMenu = new ContextMenu();

    /**
     * Copies the current url to clipboard
     */
    private void copyCurrentLinkToClipboard(){
        Clipboard clipboard = Clipboard.getSystemClipboard();

        Map<DataFormat, Object> content = new HashMap<>();
        content.put(DataFormat.PLAIN_TEXT,url);
        clipboard.setContent(content);
        System.out.println("Copy to clipboard");
    }

    /**
     * Creates the contextmenu and sets up the actions
     */
    private void createContextMenu(){
        MenuItem openBookmark = new MenuItem("Open");
        MenuItem copyAddress = new MenuItem("Copy URL");
        MenuItem openInNewTab = new MenuItem("Open in new Tab");
        MenuItem openInNewWindow = new MenuItem("Open in new Window");
        MenuItem deleteBookmark = new MenuItem("Delete Bookmark");

        contextMenu.getItems().addAll(openBookmark,copyAddress,openInNewTab,openInNewWindow,deleteBookmark);

        openBookmark.setOnAction(e->stage.setTab(url));
        copyAddress.setOnAction(e->copyCurrentLinkToClipboard());
        openInNewTab.setOnAction(e->stage.newTab(url));
        openInNewWindow.setOnAction(e->stage.newWindow(url));
        deleteBookmark.setOnAction(e->stage.getBookmarkController().removeBookmark(url));

        setOnContextMenuRequested(e->{
            e.consume();
            contextMenu.show(this,e.getScreenX(),e.getScreenY());
        });

    }

    public Bookmark(String name,String url,MainLayout stage){
        this();
        setName(name);
        setUrl(url);
        setStage(stage);
    }

    /**
     * Gets the display name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name
     * @param name the new name
     * @return current bookmark
     */
    public Bookmark setName(String name) {
        text.setText(name);
        this.name = name;
        return this;
    }

    /**
     * Sets the url
     * @param url the new url
     * @return current bookmark
     */
    public Bookmark setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Gets the current url
     * @return the current url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the stage
     * @param stage the new stage
     * @return current bookmark
     */
    public Bookmark setStage(MainLayout stage) {
        this.stage = stage;
        return this;
    }

    public Bookmark(){
        getStyleClass().add("bookmark");
        text.getStyleClass().add("bookmark-text");
        getChildren().add(text);

        createContextMenu();

        addEventHandler(MouseEvent.MOUSE_CLICKED,e->{
            if(e.getButton()==MouseButton.SECONDARY) return; // ContextMenu

            boolean inNewTab = e.isControlDown() || e.getButton()== MouseButton.MIDDLE;
            boolean inNewWindow = e.isShiftDown();

            if(inNewTab) stage.newTab(url);
            else if(inNewWindow) stage.newWindow(url);
            else if(e.getButton()==MouseButton.PRIMARY) stage.setTab(url);
        });
    }

}
