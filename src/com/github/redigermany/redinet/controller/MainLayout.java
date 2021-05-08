package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.view.NavigationButton;
import com.github.redigermany.redinet.view.Observer;
import com.github.redigermany.redinet.view.WebTab;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebHistory;
import javafx.stage.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class MainLayout extends Application {

    @FXML private NavigationButton prevBtn;
    @FXML private NavigationButton forwBtn;
    @FXML private NavigationButton reloadBtn;
    @FXML private TextField urlBar;
    @FXML private NavigationButton bookmarkBtn;
    @FXML private NavigationButton menuBtn;
    @FXML private WebTab newTab;
    @FXML private TabPane tabPane;
    @FXML private HBox bookmarks;
    @FXML private HBox infoBar;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    private Stage primaryStage;
    private BookmarkController bc = new BookmarkController(this);
    private ContextMenu urlContextMenu = new ContextMenu();
    private int urlContextMenuSelected = 0;
    private WindowState windowState = new WindowState();
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private String initUrl;
    private double urlBarWidth;

    public BookmarkController getBookmarkController() {
        return bc;
    }
    private final EventHandler<? super ContextMenuEvent> getHistoryContextMenu = e ->{

        ObservableList<WebHistory.Entry> history = getCurrentTab().getHistory().getEntries();
        ContextMenu contextMenu = new ContextMenu();
        int maxItems = 10;
        if(history.size()<maxItems) maxItems=history.size()-1;
        ArrayList<MenuItem> historyList = new ArrayList<>();
        for(int i=0;i<maxItems;i++){
            WebHistory.Entry item = history.get(i);
            historyList.add(new MenuItem(item.getTitle()));
        }
        contextMenu.getItems().addAll(historyList);
        contextMenu.show(primaryStage,e.getScreenX(),e.getScreenY());
        contextMenu.setOnAction(e1->{
            ObservableList<MenuItem> items = ((ContextMenu) (e1.getSource())).getItems();
            for(int i=0;i<items.size();i++){
                if(items.get(i)==e1.getTarget()){
                    WebHistory temphistory = getCurrentTab().getHistory();
                    temphistory.go(i-temphistory.getCurrentIndex());
                    return;
                }
            }
//            System.err.println("History item not found?");
        });
    };

    public void newTab(String url){
        setBarStatus(0);
        WebTab tab = new WebTab(primaryStage);
        if(url!=null) tab.setUrl(url);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }
    public void newTab(){
        newTab(null);
    }

    public void initUrl(String url){
        this.initUrl = url;
    }

    private void setWindowProperties(){
        primaryStage.setMaximized(windowState.getMaximized());
        primaryStage.setX(windowState.getX());
        primaryStage.setY(windowState.getY());
    }
    private void addWindowListeners(){
        primaryStage.xProperty().addListener((obs, oldVal, newVal) -> {
            windowState.setX((double) newVal);
        });
        primaryStage.yProperty().addListener((obs, oldVal, newVal) -> {
            windowState.setY((double) newVal);
        });
        primaryStage.widthProperty().addListener((obs,oldVal,newVal)->{
            windowState.setWidth((double) newVal);
            updateLayoutWidth();
        });
        primaryStage.heightProperty().addListener((obs,oldVal,newVal)->{
            windowState.setHeight((double) newVal);
            updateLayoutHeight();
        });
        primaryStage.maximizedProperty().addListener((obs,oldVal,newVal)->{
            windowState.setMaximized(newVal);
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(e->{
            // TODO: Save tabs?
            logger.info("Close request");
        });
        primaryStage = stage;
        URL xml = getClass().getResource("/com/github/redigermany/redinet/view/MainLayout.fxml");
        if(xml==null){
            System.out.println("File \"MainLayout.fxml\" not found!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(xml);
        loader.setController(this);
        AnchorPane page = loader.load();
        Scene scene = new Scene(page,windowState.getWidth(),windowState.getHeight());
        setWindowProperties();
        addWindowListeners();
        stage.setTitle("ReDiNet");
        stage.setScene(scene);
        stage.show();
        updateLayoutWidth();
        updateLayoutHeight();

        if(initUrl!=null) newTab(initUrl);
        else {
            newTab();
            // TODO: Get last tabs
        }

        initBaseLayout();
    }

    private void initBaseLayout() {
        initPrevButton();
        initForwButton();
        initReloadButton();
        initUrlBar();
        initBookmarkButton();
        initMenuButton();
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            initUpdateTabInfo((WebTab) t1);
        });
        initUpdateTabInfo(getCurrentTab());
        newTab.setOnSelectionChanged(e->{
            newTab();
        });
    }

    private void initPrevButton(){
        prevBtn.setDisable(true);
        prevBtn.setOnContextMenuRequested(getHistoryContextMenu);
        prevBtn.setOnAction(e->{
            WebHistory history = getCurrentTab().getHistory();
            if(history.getCurrentIndex()>0)
                history.go(-1);
        });
    }
    private void initForwButton(){
        forwBtn.setDisable(true);
        forwBtn.setOnAction(e->{
            WebHistory history = getCurrentTab().getHistory();
            history.go(1);
        });
        forwBtn.setOnContextMenuRequested(getHistoryContextMenu);
    }
    private void initReloadButton(){
        reloadBtn.setOnAction(e->{
            getCurrentTab().reload();
        });
    }

    private void showUrlBarAutocomplete(){
        urlContextMenu.show(primaryStage,primaryStage.getX()+urlBar.getLayoutX()+35,primaryStage.getY()+urlBar.getLayoutY()+60);
        updateLayoutWidth();
    }

    private void initUrlBar(){
        urlBar.setContextMenu(urlContextMenu);
        urlBar.addEventFilter(MouseEvent.MOUSE_CLICKED, e->{
            urlBar.selectAll();
            showUrlBarAutocomplete();
        });
        urlBar.addEventFilter(KeyEvent.KEY_RELEASED,e->{
            if(e.getCode() == KeyCode.ENTER){
                String url="";
                if(urlContextMenuSelected==0){
                    url = "https://www.google.com/search?q="+urlBar.getText();
                }else{
                    url = urlContextMenu.getItems().get(urlContextMenuSelected).getText();
                }
                getCurrentTab().setUrl(url);
                urlContextMenu.hide();
            }else{
                if(e.getCode()==KeyCode.DOWN){
                    urlContextMenuSelected++;
                    if(urlContextMenuSelected==urlContextMenu.getItems().size()){
                        urlContextMenuSelected = 0;
                    }
                    Set<Node> items = urlContextMenu.getSkin().getNode().lookupAll(".menu-item");
                    Iterator<Node> it = items.iterator();
                    Node item;
                    int n=0;
                    while(it.hasNext()){
                        item = it.next();
                        if(n==urlContextMenuSelected){
                            item.requestFocus();
                            break;
                        }
                        n++;
                    }
                }
                else if(e.getCode()==KeyCode.UP){
                    urlContextMenuSelected--;
                    if(urlContextMenuSelected<0){
                        urlContextMenuSelected = urlContextMenu.getItems().size()-1;
                    }
                    Set<Node> items = urlContextMenu.getSkin().getNode().lookupAll(".menu-item");
                    Iterator<Node> it = items.iterator();
                    Node item;
                    int n=0;
                    while(it.hasNext()){
                        item = it.next();
                        if(n==urlContextMenuSelected){
                            item.requestFocus();
                            break;
                        }
                        n++;
                    }
                }
                else {
                    ArrayList<String> historyLog = HistoryController.getInstance().search(urlBar.getText());
                    ArrayList<MenuItem> urlContextMenuItems = new ArrayList<>();
                    urlContextMenuItems.add(new MenuItem("Google search: "+urlBar.getText()));
                    for(String historyItem:historyLog){
                        urlContextMenuItems.add(new MenuItem(historyItem));
                    }
                    urlContextMenu.getItems().setAll(urlContextMenuItems);
                    showUrlBarAutocomplete();
                }
            }
        });
        urlContextMenu.getStyleClass().add("urlContextMenu");
    }
    private void initBookmarkButton(){
        updateBookmarkList();
        bookmarkBtn.setOnAction(e->{
            bc.toggleBookmark(new TabInfo(tabPane.getSelectionModel().getSelectedItem()));
        });
    }
    private void initMenuButton(){}

    private WebTab getCurrentTab(){
        return (WebTab) tabPane.getSelectionModel().getSelectedItem();
    }

    public void updateBookmarkList() {
        bookmarks.getChildren().setAll(bc.getBookmarks());
    }

    private void initUpdateTabInfo(WebTab tab){
        tab.attach(new Observer() {
            @Override
            public void update(WebTab tab) {
                if(!tab.getTitle().equals("Loading...")){
                    logger.info("Tab.update(done "+tab.getTitle()+")");
                    infoBar.setVisible(false);
                }else{
                    logger.info("Tab.update(wip)");
                    infoBar.setVisible(true);
                    infoBar.setMaxHeight(25);
                    infoBar.setMinHeight(25);
                }
                updateLayoutHeight();
                doUpdateTabInfo(tab);
            }
            @Override
            public void loadingStatus(int status,WebTab tab) {
                System.out.println("Tab.loadingStatus(Status "+status+" on "+tab.getUrl()+")");
                setBarStatus(status+1);
            }
        });
        doUpdateTabInfo(tab);
    }
    private void doUpdateTabInfo(WebTab tab){
        TabInfo info = new TabInfo(tab);
        if(info.getUrl().startsWith("https")){
            urlBar.getStyleClass().setAll("urlBar","secure");
        }else{
            urlBar.getStyleClass().setAll("urlBar");
        }
        if(bc.isBookmark(info.getUrl())){
            bookmarkBtn.setImageUrl("star-full-solid.png");
        }else{
            bookmarkBtn.setImageUrl("star-empty-solid.png");
        }
        if (!info.getIcon().equals("")) primaryStage.getIcons().add(new Image(info.getIcon()));
        primaryStage.setTitle(info.getTitle());
        urlBar.setText(info.getUrl());
        WebHistory history = tab.getHistory();
        int entryLength = history.getEntries().size()-1;
        int currentEntry = history.getCurrentIndex();
//        System.out.println("Max History="+entryLength+"; current="+currentEntry);
        if(currentEntry==entryLength){
            forwBtn.setDisable(true);
            if(entryLength>0){
                prevBtn.setDisable(false);
            }
        }else{
            if(entryLength>0){
                forwBtn.setDisable(false);
            }
            if(currentEntry==0){
                prevBtn.setDisable(true);
            }
        }
    }

    public void setBarStatus(int statusId){
        infoBar.setVisible(statusId!=0);
        infoBar.setMaxHeight(25);
        infoBar.setMinHeight(25);
        updateLayoutHeight();
        updateLayoutHeight();
        progressBar.setProgress(statusId/4.0);
        String url = getCurrentTab().getUrl();
        progressLabel.setText(new String[]{
                "Laden wird vorbereitet.",
                "Website "+url+" bereit.",
                "Ladevorgang gestartet.",
                "Lädt "+url,
                "Webseite "+url+" fertig geladen.",
        }[statusId]);
    }

    private void updateLayoutHeight() {
        int offset = bookmarks.getChildren().size()>0?99:74;
        tabPane.setMinHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMaxHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMinHeight(primaryStage.getHeight()-offset-(infoBar.isVisible()?infoBar.getHeight():0));
        tabPane.setMaxHeight(primaryStage.getHeight()-offset-(infoBar.isVisible()?infoBar.getHeight():0));
    }

    private void updateLayoutWidth() {
        urlBarWidth = primaryStage.getWidth()-(6*menuBtn.getWidth())-20;
        urlBar.setMinWidth(urlBarWidth);
        urlBar.setMaxWidth(urlBarWidth);
        tabPane.setMinWidth(primaryStage.getWidth()-15);
        tabPane.setMaxWidth(primaryStage.getWidth()-15);
//        Popup p = new Popup();
//        ArrayList<Label> test = new ArrayList<>();
//        for(int i=0;i<6;i++){
//            Label label = new Label("Test "+i);
//            label.getStyleClass().add("urlpopupitem");
//            label.setMaxWidth(urlBarWidth);
//            label.setMinWidth(urlBarWidth);
//            label.setPrefWidth(urlBarWidth);
//            label.setTranslateY(i*25);
//            test.add(label);
//        }
//        p.getContent().setAll(test);
//        p.show(primaryStage);
//        p.setAnchorX(primaryStage.getX() + urlBar.getLayoutX() + 25);
//        p.setAnchorY(primaryStage.getY() + urlBar.getLayoutY() + 50);
        urlContextMenu.setMinWidth(50);
        urlContextMenu.setMaxWidth(50);
        urlContextMenu.setWidth(50);
        urlContextMenu.setPrefWidth(50);
    }
    public void setTab(String url) {
        setBarStatus(0);
        getCurrentTab().setUrl(url);
    }

    public void newWindow(String url) {
        System.out.println("Open "+url+" in new browser window? ");
        try {
            Stage newStage = new Stage();
            final MainLayout mainStage = new MainLayout();
            mainStage.initUrl(url);
            mainStage.start(newStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
