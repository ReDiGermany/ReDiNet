package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.model.HistoryModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import com.github.redigermany.redinet.model.WebTab;

/**
 * URL Bar
 * @author Max Kruggel
 */
public class UrlBar extends ComboBox<String> {

    public WebTab currentTab;

    /**
     * EventHandler for bar action. Getting URL and navigating to it.
     */
    private final EventHandler<ActionEvent> action = e->{
        e.consume();
        String url = getCurrentUrl();
        System.out.println("Navigating to url "+url);
        currentTab.setUrl(url);
    };

    /**
     * EventHandler for pressed key.
     * Checks URL, loads history and fills context menu.
     */
    private final EventHandler<? super KeyEvent> keyReleased = e->{
        if(e.getCode().isArrowKey()) return;
        boolean isUrl = getEditor().getText().matches("[a-zA-Z0-9-_\\.]+\\.[a-zA-Z]+");

        ArrayList<String> historyLog = HistoryModel.getInstance().search(getEditor().getText());
        ArrayList<String> urlContextMenuItems = new ArrayList<>();

        if(isUrl) urlContextMenuItems.add("Go to: "+getEditor().getText());

        urlContextMenuItems.add("Google search: "+getEditor().getText());
        urlContextMenuItems.addAll(historyLog);
        getItems().setAll(urlContextMenuItems);
        show();
    };

    /**
     * Sets the secure flag shield
     * @param secure true if is secure
     */
    private void setSecure(boolean secure){
        getEditor().getStyleClass().setAll("urlBar",secure?"secure":"");
    }

    /**
     * Sets the current URL
     * @param url new url
     */
    public void setUrl(String url){
        setSecure(url.startsWith("https"));
        getEditor().setText(url);
    }

    /**
     * Sets the current bar width
     * @param urlBarWidth new width
     */
    public void setWidthM(double urlBarWidth){
        getEditor().setMinWidth(urlBarWidth);
        getEditor().setMaxWidth(urlBarWidth);
        setMinWidth(urlBarWidth);
        setMaxWidth(urlBarWidth);
    }

    /**
     * Checks url for protocol, uses google search otherwhise
     * @param url url to check
     * @return parsed url
     */
    private String parseURL(String url){
        if(url.startsWith("Go to: ")){
            if(!url.matches("(http|https)://"))
                url = "http://"+url.replace("Go to: ","");
        } else if(url.startsWith("Google search: "))
            url = "https://www.google.com/search?q="+url.replace("Google search: ","");

        return url;
    }

    /**
     * Returns the current url
     * @return url
     */
    private String getCurrentUrl(){
        return parseURL(getEditor().getText());
    }

    /**
     * Navigates to the current url
     */
    public void navigate(){
        currentTab.setUrl(getCurrentUrl());
    }

    public UrlBar(){
        getStyleClass().add("urlBarOuter");
        getEditor().getStyleClass().add("urlBar");
        setEditable(true);
        addEventFilter(MouseEvent.MOUSE_CLICKED, e->getEditor().selectAll());
        addEventHandler(KeyEvent.KEY_RELEASED, keyReleased);
        setOnAction(action);
    }
}
