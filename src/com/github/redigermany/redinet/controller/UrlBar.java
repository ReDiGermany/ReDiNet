package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.model.HistoryModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import com.github.redigermany.redinet.model.WebTab;

/**
 * URL Bar
 * @author Max Kruggel
 */
public class UrlBar extends ComboBox<String> {

    public TabPane currentTab;

    int caret = 0;

    private WebTab getCurrentTab(){
        Tab tab = currentTab.getSelectionModel().getSelectedItem();
        if(tab instanceof WebTab){
            return (WebTab) tab;
        }
        return null;
    }

    private void setNewUrl(String url){
        WebTab tab = getCurrentTab();
        if(tab!=null) tab.setUrl(url);
    }

    /**
     * EventHandler for bar action. Getting URL and navigating to it.
     */
    private final EventHandler<ActionEvent> action = e->{
        e.consume();
        String url = getCurrentUrl();
        System.out.println("Navigating to url "+url);
        setNewUrl(url);
    };

    public void navigateToUrl(){
        if(autocompleteBox!=null) {
            String url = parseURL(autocompleteBox.get(0));
            System.out.println("navigateToUrl "+url);
            setNewUrl(url);
        }
    }

    ArrayList<String> autocompleteBox;
    /**
     * EventHandler for pressed key.
     * Checks URL, loads history and fills context menu.
     */
    private final EventHandler<? super KeyEvent> keyReleased = e->{
        String text = getEditor().getText();
        if(e.getCode() == KeyCode.SPACE){
            e.consume();
            text = text.replaceAll(" Google search: ","");
            getEditor().setText(text);
            getEditor().positionCaret(text.length()+1);
            return;
        }
        if(e.getCode() == KeyCode.ENTER){
            String url = getCurrentUrl();
            System.out.println("Navigating to url (2) "+url);
            setNewUrl(url);
            return;
        }
        if(e.getCode().isArrowKey()) return;
        boolean isUrl = text.matches("[a-zA-Z0-9-_\\.]+\\.[a-zA-Z]+");

        ArrayList<String> historyLog = HistoryModel.getInstance().search(text);
        ArrayList<String> autocompleteBox = new ArrayList<>();

        if(isUrl) autocompleteBox.add("Go to: "+text);

        autocompleteBox.add(text);
        autocompleteBox.add("Google search: "+text);
        autocompleteBox.addAll(historyLog);
        getItems().setAll(autocompleteBox);
        getEditor().setText(text);
        getEditor().positionCaret(caret);
        if(!isShowing()) show();
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
            url = url.replace("Go to: ","");
        } else if(url.startsWith("Google search: "))
            url = "https://www.google.com/search?q="+url.replace("Google search: ","");

        if(!url.startsWith("http")) {
            url = "http://" + url;
        }

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
        setNewUrl(getCurrentUrl());
    }

    public UrlBar(){
        getStyleClass().add("urlBarOuter");
        getEditor().getStyleClass().add("urlBar");
        setEditable(true);
        addEventFilter(MouseEvent.MOUSE_CLICKED, e->getEditor().selectAll());
        setOnKeyReleased(keyReleased);
        getEditor().caretPositionProperty().addListener(e->{
            if(getEditor().getCaretPosition()!=0) caret = getEditor().getCaretPosition();
        });
    }
    public void setTabPane(TabPane tab){
        this.currentTab = tab;
    }
}
