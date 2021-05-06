package com.github.redigermany.redinet.view;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebTab extends Tab {
    private List<Observer> observers = new ArrayList<Observer>();
    public void attach(Observer observer){
        observers.add(observer);
    }

    public void notifyAllObservers(String location,String icon,String title){
        for (Observer observer : observers) {
            observer.update(location,icon,title);
        }
    }

    private String title;
    private String image;
    private String url = "newtab.html";
    private boolean local = false;
    private final WebView webView = new WebView();
    private WebEngine webEngine = webView.getEngine();

    private void updatePageInformation(){
        if(!url.startsWith("http")){
            Path path = Paths.get("src/com/github/redigermany/redinet/view/html/"+url);
            url = "file:/"+path.toAbsolutePath();
            local = true;
        }
        webEngine.load(url);
    }

    private void updatePageInformation2(){
        String location = webEngine.getLocation();
        String icon="";
        String title = webEngine.getTitle();
        NodeList links = webEngine.getDocument().getElementsByTagName("link");
        linkfor: for(int i=0;i<links.getLength();i++){
            Node item = links.item(i);
            NamedNodeMap attr = item.getAttributes();
            for(int j=0;j<attr.getLength();j++){
                if(attr.item(j).getNodeName().equals("rel") && attr.item(j).getNodeValue().contains("icon")){
                    for(int k=0;k<attr.getLength();k++){
                        if(attr.item(k).getNodeName().equals("href")){
                            icon = url+"/"+attr.item(k).getNodeValue();
                            break linkfor;
                        }
                    }
                    break;
                }
            }
        }
        if(!url.startsWith("http")) {
            setId("redinet://newtab:-:"+icon+":-:"+title);
        }else{
            setId(location+":-:"+icon+":-:"+title);
        }
        setText(title);
        System.out.printf("Moved to %s %s%nTitle: %s%n%n",(local?"local":"public"),location,title);
        notifyAllObservers(location,icon,title);
    }



    public WebTab(){
        getStyleClass().add("tabBtn");
        setContent(webView);
        updatePageInformation();
        webEngine.getLoadWorker().stateProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                updatePageInformation2();
            }
        });
    }

    public void setTitle(String title) {
        if(image!=null) {
            setText(title);
            this.title = title;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setImage(String image) {
        if(title!=null && !title.equals("")) setText("");
        setStyle("-fx-background-image: url("+image+")");
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        updatePageInformation();
    }
}
