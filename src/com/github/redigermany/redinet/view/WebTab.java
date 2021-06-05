package com.github.redigermany.redinet.view;

import com.github.redigermany.redinet.controller.HistoryController;
import com.github.redigermany.redinet.controller.Logger;
import com.github.redigermany.redinet.controller.MainLayout;
import com.github.redigermany.redinet.controller.WindowState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.MouseEvent;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WebTab extends Tab {
    private final Logger logger = new Logger(Logger.TYPES.ERROR);
    private final List<Observer> observers = new ArrayList<Observer>();
    private String title="new Tab";
    private String image;
    private String url = "notfound.html";
    private boolean local = false;
    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();
    private Stage primaryStage;

    public WebTab(){
        initWebTab();
    }
    private void initWebTab(){
        logger.info("Init tab");

        getStyleClass().add("tabBtn");
        setContent(webView);
        loadCurrentUrl();
        webEngine.locationProperty().addListener((obs,ol,nw)->{
            url = nw;
            HistoryController.getInstance().addUrl(nw);
            logger.info("new url "+nw);
            updatePageInformation();
        });
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                System.out.println("New state! "+newValue+" ("+observable.getValue().name()+" | "+observable.getValue().ordinal()+")");
                if(observable.getValue().ordinal()==5){
                    setUrl("notfound.html");
                }
                notifyAllObserversStatus(observable.getValue().ordinal());
                updatePageInformation();
            }
        });
        webEngine.setOnAlert(e->{
            Stage alertStage = new Stage();
            StackPane root = new StackPane();
            root.getChildren().add(new Label(e.getData()));
            Scene scene = new Scene(root,300,100);
            alertStage.setScene(scene);
            alertStage.setTitle(title);
            alertStage.initOwner(primaryStage);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            alertStage.show();
        });
        setOnClosed(e->{
            logger.debug("Resetting webview due to closing event");
            webEngine.load(null);
        });
        idProperty().addListener((obs,ol,nw)->{
            logger.setId(nw);
        });
    }
    public WebTab(WindowState ws,Stage primaryStage){
        this.url = ws.getStartPage();
        this.primaryStage = primaryStage;
        initWebTab();
    }

    private void loadCurrentUrl(){
        if(!url.startsWith("http")){
            Path path = Paths.get("src/com/github/redigermany/redinet/view/html/"+url);
            url = "file:/"+path.toAbsolutePath();
            local = true;
        }
        logger.info("Loading "+(local?"local":"remote")+" url "+url);
        webEngine.load(url);
    }

    private String getPageIcon(){
        logger.info("[PI] title found. checking icon.");
        NodeList links = webEngine.getDocument().getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            Node item = links.item(i);
            NamedNodeMap attr = item.getAttributes();
            for (int j = 0; j < attr.getLength(); j++) {
                if (attr.item(j).getNodeName().equals("rel") && attr.item(j).getNodeValue().contains("icon")) {
                    for (int k = 0; k < attr.getLength(); k++) {
                        if (attr.item(k).getNodeName().equals("href")) {
                            return url + "/" + attr.item(k).getNodeValue();
                        }
                    }
                }
            }
        }
        return "";
    }

    private void updatePageInformation(){
        logger.info("[PI] Updating Page information!");
        String location = webEngine.getLocation();
        logger.info("[PI] location="+location);
        String icon="";
        title = webEngine.getTitle();
        logger.info("[PI] title="+title);
        if(title!=null) {
            icon = getPageIcon();
            if(!url.startsWith("http")) {
                logger.info("local file - setting visible url to redinet://newtab");
                setId("redinet://newtab:-:"+icon+":-:"+title);
            }else{
                logger.info("remote url");
                setId(location+":-:"+icon+":-:"+title);
            }
        }else{
            logger.info("Title not found. Seems like we still loading...");
            title="Loading...";
        }
        if(image==null) setText(title);
        logger.info(String.format("Moved to %s %s%nTitle: %s%n%n",(local?"local":"public"),location,title));
        notifyAllObservers(this);
    }



    public void setTitle(String title) {
        if(image!=null) {
            logger.info("Setting title to "+title);
            setText(title);
            this.title = title;
        }else logger.info("Not updating title. Image is set!");
    }

    public String getTitle() {
        return title;
    }

    public void setImage(String image) {
        logger.info("Setting image to "+image);
        setText("");
        URL url = getClass().getResource("/UI/" + image);
        if(url!=null){
            setStyle("-fx-background-image: url("+url+")");
            this.image = image;
        }else{
            logger.info("image not found");
        }
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        logger.info("Setting url to "+url);
        this.url = url;
        loadCurrentUrl();
    }

    public void reload() {
        webEngine.reload();
    }

    public void attach(Observer observer){
        observers.add(observer);
    }

    public void notifyAllObservers(WebTab tab){
        for (Observer observer : observers) {
            observer.update(tab);
        }
    }

    public void notifyAllObserversStatus(int status){
        for (Observer observer : observers) {
            observer.loadingStatus(status,this);
        }
    }

    public WebHistory getHistory() {
        return webEngine.getHistory();
    }
}
