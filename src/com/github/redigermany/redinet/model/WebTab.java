package com.github.redigermany.redinet.model;

import com.github.redigermany.redinet.controller.Observer;
import com.github.redigermany.redinet.model.HistoryModel;
import com.github.redigermany.redinet.controller.Logger;
import com.github.redigermany.redinet.model.WindowState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper Class for Browser Tabs.
 * @author Max Kruggel
 */
public class WebTab extends Tab {
    private final Logger logger = new Logger(Logger.TYPES.ERROR);
    private final List<Observer> observers = new ArrayList<>();
    private String title="new Tab";
    private String image;
    private String url = "notfound.html";
    private boolean local = false;
    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();
    private Stage primaryStage;

    /**
     * Updates current URL to new Value. Adds the url to History. Updates Page Information.
     */
    private final ChangeListener<? super String> historyUpdateListener = (obs, ol, nw)->{
        url = nw;
        HistoryModel.getInstance().addUrl(nw);
        logger.info("new url "+nw);
        updatePageInformation();
    };

    /**
     * Checks current state. Updates to all observer.
     */
    private final ChangeListener<? super Worker.State> stateUpdateListener = (ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
        logger.info("New state! "+newValue+" ("+observable.getValue().name()+" | "+observable.getValue().ordinal()+")");

        if(observable.getValue().ordinal()==5) setUrl("notfound.html");

        notifyAllObserversStatus(observable.getValue().ordinal());
        updatePageInformation();
    };

    /**
     * Parses the incoming alerts to new window.
     */
    private final EventHandler<WebEvent<String>> alertListener = e->{
        Stage alertStage = new Stage();
        StackPane root = new StackPane();
        root.getChildren().add(new Label(e.getData()));
        Scene scene = new Scene(root,300,100);
        alertStage.setScene(scene);
        alertStage.setTitle(title);
        alertStage.initOwner(primaryStage);
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.show();
    };

    /**
     * Sets url to null.
     */
    private final EventHandler<Event> closedListener = e->{
        logger.debug("Resetting webview due to closing event");
        webEngine.load(null);
    };

    /**
     * Initializes the Tab. Loads current URL, Adds listeners.
     */
    private void initWebTab(){
        logger.info("Init tab");

        getStyleClass().add("tabBtn");
        setContent(webView);

        loadCurrentUrl();

        webEngine.locationProperty().addListener(historyUpdateListener);
        webEngine.getLoadWorker().stateProperty().addListener(stateUpdateListener);
        webEngine.setOnAlert(alertListener);
        setOnClosed(closedListener);
        idProperty().addListener((obs,ol,nw)->logger.setId(nw));
    }

    /**
     * Loads current URL. Checks if url is local.
     */
    private void loadCurrentUrl(){
        if(!url.startsWith("http")){
            Path path = Paths.get("src/com/github/redigermany/redinet/view/html/"+url);
            url = "file:/"+path.toAbsolutePath();
            local = true;
        }
        logger.info("Loading "+(local?"local":"remote")+" url "+url);
        webEngine.load(url);
    }

    /**
     * Gets the current favicon.
     * @deprecated Unuseable due to tab restrictions.
     * @return url if favicon. empty string if not found.
     */
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

    /**
     * Updates the page info. Getting page url, favicon, title, parsing local urls.
     */
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
        notifyAllObservers();
    }

    /**
     * Gets the current page title
     * @return page title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the current page url
     * @return page url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the current page url
     * @param url new url
     */
    public void setUrl(String url) {
        logger.info("Setting url to "+url);
        this.url = url;
        loadCurrentUrl();
    }

    /**
     * Reloads the tab
     */
    public void reload() {
        webEngine.reload();
    }

    /**
     * Attaches an observer to the notification list
     * @param observer the observer to be attached
     */
    public void attach(Observer observer){
        observers.add(observer);
    }

    /**
     * Notifies all observers with the current tab information
     */
    private void notifyAllObservers(){
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Notifies all observers for the current loading state
     * @param status the current state
     */
    private void notifyAllObserversStatus(int status){
        for (Observer observer : observers) {
            observer.loadingStatus(status,this);
        }
    }

    /**
     * Gets the current engine history
     */
    public WebHistory getHistory() {
        return webEngine.getHistory();
    }

    /**
     * Sets the tab image url
     * @param image url
     */
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

    /**
     * Gets the tab image
     * @return url
     */
    public String getImage() {
        return image;
    }

    public WebTab(WindowState ws,Stage primaryStage){
        this.url = ws.getStartPage();
        this.primaryStage = primaryStage;
        initWebTab();
    }

    public WebTab(){
        initWebTab();
    }
}
