package com.github.redigermany.redinet.view;

import com.github.redigermany.redinet.model.HistoryModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class UrlBar extends ComboBox<String> {

    public WebTab getCurrentTab;
    private final EventHandler<ActionEvent> action = e->{
        e.consume();
        String url = getCurrentUrl();
        System.out.println("Navigating to url "+url);
        getCurrentTab.setUrl(url);
    };
    private final EventHandler<? super KeyEvent> keyReleased = e->{
        if(e.getCode().isArrowKey()) return;
        boolean isUrl = getEditor().getText().matches("[a-zA-Z0-9-_\\.]+\\.[a-zA-Z]+");

        ArrayList<String> historyLog = HistoryModel.getInstance().search(getEditor().getText());
        ArrayList<String> urlContextMenuItems = new ArrayList<>();

        if(isUrl){
            urlContextMenuItems.add("Go to: "+getEditor().getText());
        }
        urlContextMenuItems.add("Google search: "+getEditor().getText());
        urlContextMenuItems.addAll(historyLog);
        getItems().setAll(urlContextMenuItems);
        show();
    };

    public void setSecure(boolean secure){
        getEditor().getStyleClass().setAll("urlBar",secure?"secure":"");
    }

    public UrlBar(){
        getStyleClass().add("urlBarOuter");
        getEditor().getStyleClass().add("urlBar");
        setEditable(true);
        addEventFilter(MouseEvent.MOUSE_CLICKED, e->getEditor().selectAll());
        addEventHandler(KeyEvent.KEY_RELEASED, keyReleased);
        setOnAction(action);
    }

    public void setUrl(String url){
        getEditor().setText(url);
    }

    public void setWidthM(double urlBarWidth){
        getEditor().setMinWidth(urlBarWidth);
        getEditor().setMaxWidth(urlBarWidth);
        setMinWidth(urlBarWidth);
        setMaxWidth(urlBarWidth);
    }

    private String parseURL(String url){
        if(url.startsWith("Go to: ")){
            if(!url.matches("(http|https)://")){
                url = "http://"+url.replace("Go to: ","");
            }
        }
        else if(url.startsWith("Google search: ")){
            url = "https://www.google.com/search?q="+url.replace("Google search: ","");
        }

        return url;
    }

    private String getCurrentUrl(){
        return parseURL(getEditor().getText());
    }

    public void navigate(){
        getCurrentTab.setUrl(getCurrentUrl());
    }

    public void navigateTo(String url){
        getCurrentTab.setUrl(parseURL(url));
    }
}
