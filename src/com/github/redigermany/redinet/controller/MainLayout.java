package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.model.BookmarkList;
import com.github.redigermany.redinet.model.HistoryModel;
import com.github.redigermany.redinet.model.TabInfo;
import com.github.redigermany.redinet.model.WindowState;
import com.github.redigermany.redinet.view.NavigationButton;
import com.github.redigermany.redinet.model.WebTab;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebHistory;
import javafx.stage.*;

import java.net.URL;
import java.util.ArrayList;


/**
 * Browser main ui logic.
 * @author Max Kruggel
 */
public class MainLayout extends Application {

    @FXML private NavigationButton prevBtn;
    @FXML private NavigationButton forwBtn;
    @FXML private NavigationButton reloadBtn;
    @FXML private NavigationButton goBtn;
    @FXML private UrlBar urlBar;
    @FXML private NavigationButton bookmarkBtn;
    @FXML private NavigationButton menuBtn;
    @FXML private NavigationButton homeBtn;
    @FXML private WebTab newTab;
    @FXML private TabPane tabPane;
    @FXML private HBox bookmarks;
    @FXML private HBox infoBar;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    private Stage primaryStage;
    private final BookmarkList bc = new BookmarkList(this);
    private final ContextMenu urlContextMenu = new ContextMenu();
    private final WindowState windowState = new WindowState();
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private String initUrl;

    public BookmarkList getBookmarkController() {
        return bc;
    }

    /**
     * History ContextMenu
     */
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
        });
    };

    /**
     * Initializes a new Tab with the given url.
     * @param url the url.
     */
    public void newTab(String url){
        setBarStatus(0);
        WebTab tab = new WebTab(windowState,primaryStage);
        if(url!=null) tab.setUrl(url);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    /**
     * Relay for anonymous new tab.
     */
    public void newTab(){
        newTab(null);
    }

    /**
     * Sets the initial url
     * @param url the url
     */
    public void initUrl(String url){
        this.initUrl = url;
    }

    /**
     * Setting window properties.
     */
    private void setWindowProperties(){
        primaryStage.setMaximized(windowState.getMaximized());
        primaryStage.setX(windowState.getX());
        primaryStage.setY(windowState.getY());
    }

    /**
     * Adding window Listeners.
     */
    private void addWindowListeners(){
        primaryStage.xProperty().addListener((obs, oldVal, newVal) -> windowState.setX((double) newVal));
        primaryStage.yProperty().addListener((obs, oldVal, newVal) -> windowState.setY((double) newVal));
        primaryStage.maximizedProperty().addListener((obs,oldVal,newVal)->windowState.setMaximized(newVal));
        primaryStage.widthProperty().addListener((obs,oldVal,newVal)->{
            windowState.setWidth((double) newVal);
            updateLayoutWidth();
        });
        primaryStage.heightProperty().addListener((obs,oldVal,newVal)->{
            windowState.setHeight((double) newVal);
            updateLayoutHeight();
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
            logger.error("File \"MainLayout.fxml\" not found!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(xml);
        loader.setController(this);
        AnchorPane page = loader.load();
        Scene scene = new Scene(page,windowState.getWidth(),windowState.getHeight(), Color.BLACK);

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
        urlBar.setTabPane(tabPane);
        initBaseLayout();
        primaryStage.getIcons().add(new Image(getClass().getResource("/UI/home-solid.png").toExternalForm()));
//        openAbout();
    }

    /**
     * Initializes the whole base layout
     */
    private void initBaseLayout() {
        initPrevButton();
        initForwButton();
        initReloadButton();
        initGoBtn();
        initHomeBtn();
        initBookmarkButton();
        initMenuButton();
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            try {
                initUpdateTabInfo((WebTab) t1);
            }catch (Exception e){
                if(!e.getMessage().startsWith("class javafx.scene.control.Tab cannot be cast to class com.github.redigermany.redinet.model.WebTab"))
                    e.printStackTrace();
            }
        });
        initUpdateTabInfo(getCurrentTab());
        newTab.setOnSelectionChanged(e->newTab());
//        urlBar.currentTab = getCurrentTab();
    }

    /**
     * Initializes the back button
     */
    private void initPrevButton(){
        prevBtn.setDisable(true);
        prevBtn.setOnContextMenuRequested(getHistoryContextMenu);
        prevBtn.setOnAction(e->{
            WebHistory history = getCurrentTab().getHistory();
            if(history.getCurrentIndex()>0)
                history.go(-1);
        });
    }

    /**
     * Initializes the forward button (when u went back)
     */
    private void initForwButton(){
        forwBtn.setDisable(true);
        forwBtn.setOnAction(e->{
            WebHistory history = getCurrentTab().getHistory();
            history.go(1);
        });
        forwBtn.setOnContextMenuRequested(getHistoryContextMenu);
    }

    /**
     * Initializes the reload button
     */
    private void initReloadButton(){
        reloadBtn.setOnAction(e->{
            getCurrentTab().reload();
        });
    }

    /**
     * Initializes the go button
     */
    private void initGoBtn(){
        goBtn.setOnAction(e->urlBar.navigateToUrl());
    }

    /**
     * Initializes the home button
     */
    private void initHomeBtn(){
        homeBtn.setOnAction(e->{
            getCurrentTab().setUrl(windowState.getStartPage());
        });
    }

    /**
     * MenuItem wrapper for simple press actions.
     * @param title the title of the menu item
     * @param e the actionevent listener
     * @return the item
     */
    private MenuItem myMenuItem(String title,EventHandler<ActionEvent> e){
        MenuItem item = new MenuItem(title);
        item.setOnAction(e);
        return item;
    }

    /**
     * Sets the start page to the url
     * @param url new start page url
     */
    private void setStartPage(String url){
        logger.info("Start Page="+url);
        windowState.setStartPage(url);
    }

    /**
     * Opens the Settings tab.
     */
    private void openSettings(){
        String startPage = this.windowState.getStartPage();
        String defaultStartPage = "newtab.html";
        VBox settingsRoot = new VBox();
        settingsRoot.getChildren().add(new Label("Startseite"));
        HBox startPageBox = new HBox();
        TextField url = new TextField(startPage);
        CheckBox def = new CheckBox();
        if(startPage.equals(defaultStartPage)){
            url.setDisable(true);
        }
        def.setSelected(startPage.equals(defaultStartPage));
        def.setOnAction(e->{
            if(def.isSelected()){
                setStartPage(defaultStartPage);
                url.setText(defaultStartPage);
                url.setDisable(true);
            }else{
                url.setDisable(false);
                setStartPage(url.getText());
            }
        });

        url.addEventHandler(KeyEvent.KEY_RELEASED,e->{
            def.setSelected(false);
            setStartPage(url.getText());
        });

        startPageBox.getChildren().addAll(url, def);
        settingsRoot.getChildren().add(startPageBox);
        Button btn = new Button("Speichern");
        Stage stage = new Stage();
        btn.setOnAction(e->{
            stage.hide();
        });
        settingsRoot.getChildren().add(btn);
        settingsRoot.setPadding(new Insets(10,15,10,15));

        Scene scene = new Scene(settingsRoot);
        stage.setScene(scene);
        stage.setTitle("ReDiNet :: Einstellungen");
        stage.show();
    }

    /**
     * Opens the About tab.
     */
    private void openAbout(){
        setBarStatus(0);
        Tab tab = new Tab();
        tab.setText("ReDiNet :: About");
        tab.setContent(new About());
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    /**
     * Opens the History tab.
     */
    private void openHistory(){
        setBarStatus(0);
        Tab tab = new Tab();
        tab.setText("ReDiNet :: Browsing History");


        VBox root = new VBox();
        for(String item: HistoryModel.getInstance().getList()){
            Button btn = new Button(item);
            btn.setPrefWidth(primaryStage.getWidth() - 30);
            btn.setPrefHeight(40);
            btn.getStyleClass().add("historybutton");
            btn.setOnAction(e->{
                newTab(item);
            });
            btn.addEventHandler(MouseEvent.ANY,e->{
                if("MOUSE_ENTERED".equals(e.getEventType().toString())){
                    btn.getStyleClass().add("hoveredbtn");
                }
                else if("MOUSE_EXITED".equals(e.getEventType().toString())){
                    btn.getStyleClass().remove("hoveredbtn");
                }
            });
            root.getChildren().add(btn);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background:#111;");
        scrollPane.setContent(root);
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background:#111;");
        Button clearButton = new Button("Clear History");
        clearButton.setOnAction(e->{
            HistoryModel.getInstance().clear();
            tabPane.getTabs().remove(tab);
            openHistory();
        });
        vBox.getChildren().addAll(scrollPane,clearButton);
        tab.setContent(vBox);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    /**
     * Initializes the bookmark button.
     */
    private void initBookmarkButton(){
        updateBookmarkList();
        bookmarkBtn.setOnAction(e->bc.toggleBookmark(new TabInfo(tabPane.getSelectionModel().getSelectedItem())));
    }

    /**
     * Initializes the menu button and its entries.
     */
    private void initMenuButton(){
//        initHomeBtn();
        ContextMenu menuContextMenu = new ContextMenu();
        ArrayList<MenuItem> items = new ArrayList<>();

        Menu datei = new Menu("Datei");
        datei.getStyleClass().add("menuSubMenu");
        datei.getItems().add(myMenuItem("Neues Tab",e->{newTab();}));
        datei.getItems().add(myMenuItem("Neues Fenster",e->{newWindow("newtab.html");}));
        items.add(datei);

        Menu extras = new Menu("Extras");
        extras.getStyleClass().add("menuSubMenu");
        extras.getItems().add(myMenuItem("Einstellungen",e->{openSettings();}));
        extras.getItems().add(myMenuItem("History",e->{openHistory();}));
        items.add(extras);

        Menu help = new Menu("Hilfe");
        help.getStyleClass().add("menuSubMenu");
        help.getItems().add(myMenuItem("Über",e->{openAbout();}));
        help.getItems().add(myMenuItem("Github",e->{newTab("https://github.com/ReDiGermany/ReDiNet");}));
        help.getItems().add(myMenuItem("Fontawesome",e->{newTab("http://fontawesome.com/icons/");}));
        items.add(help);

        items.add(myMenuItem("Minimieren",e->{primaryStage.setIconified(true);}));
        items.add(myMenuItem("Maximieren",e->{primaryStage.setMaximized(true);}));
        items.add(myMenuItem("Schließen",e->{primaryStage.hide();}));

        menuContextMenu.getItems().addAll(items);
        menuBtn.setOnAction(e->menuContextMenu.show(
                primaryStage,
                menuBtn.getLayoutX()+primaryStage.getX()-60,
                menuBtn.getLayoutY()+primaryStage.getY()+60)
        );
        menuBtn.setContextMenu(menuContextMenu);
        menuContextMenu.getStyleClass().add("menuContextMenu");
    }

    /**
     * Gets the current selected tab
     * @return the tab
     */
    private WebTab getCurrentTab(){
        try {
            WebTab tab = (WebTab) tabPane.getSelectionModel().getSelectedItem();
            return tab;
        }catch(Exception e){
            return new WebTab();
        }
    }

    /**
     * Updates the bookmark list.
     */
    public void updateBookmarkList() {
        bookmarks.getChildren().setAll(bc.getBookmarks());
    }

    /**
     * Updates Browser ui for when tab changing.
     * @param tab
     */
    private void initUpdateTabInfo(WebTab tab){
        logger.debug("tab change?");
//        urlBar.setSecure(tab.getUrl().startsWith("https"));
        tab.attach(new Observer() {
            @Override
            public void update(WebTab tab) {
                if(tab.getImage()!=null){
                    primaryStage.getIcons().set(0,new Image(tab.getImage()));
                }
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
                logger.debug("Tab.loadingStatus(Status "+status+" on "+tab.getUrl()+")");
                setBarStatus(status+1);
            }
        });
        doUpdateTabInfo(tab);
    }

    /**
     * Updates the browser ui with the current tab info.
     * @param tab the new info
     */
    private void doUpdateTabInfo(WebTab tab){
        TabInfo info = new TabInfo(tab);
//        urlBar.setSecure(info.getUrl().startsWith("https"));
        if(bc.isBookmark(info.getUrl())){
            bookmarkBtn.setImageUrl("star-solid.svg");
        }else{
            bookmarkBtn.setImageUrl("star-regular.svg");
        }
        primaryStage.setTitle(info.getTitle());
        if(tab.getImage()!=null){
            primaryStage.getIcons().set(0,new Image(tab.getImage()));
        }
//        primaryStage.setTitle(info.getTitle());
        urlBar.setUrl(info.getUrl());
        if(info.getUrl().startsWith("redinet:")) return;
        if (!info.getIcon().equals("")) primaryStage.getIcons().add(new Image(info.getIcon()));
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

    /**
     * Sets the current tab status.
     * @param statusId
     */
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
                "Webseite "+url+" fehler?",
                "Webseite "+url+" wurde nicht gefunden.",
//                "Webseite "+url+" fertig geladen.",
        }[statusId]);
        updateLayoutWidth();
    }

    /**
     * Updates the content layout height
     */
    private void updateLayoutHeight() {
        int offset = bookmarks.getChildren().size()>0?99:74;
        tabPane.setMinHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMaxHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMinHeight(primaryStage.getHeight()-offset-(infoBar.isVisible()?infoBar.getHeight():0));
        tabPane.setMaxHeight(primaryStage.getHeight()-offset-(infoBar.isVisible()?infoBar.getHeight():0));
    }

    /**
     * Updates the content layout width
     */
    private void updateLayoutWidth() {
        boolean urlBarChanged = false;
        double urlBarWidth = primaryStage.getWidth() - (6 * menuBtn.getWidth()) - 20
//                - (urlBarChanged ? 25 : 0)
                -25
                - 25;

        logger.debug("URLBAR WIDTH WILL BE "+ urlBarWidth);

        tabPane.setMinWidth(primaryStage.getWidth()-15);
        tabPane.setMaxWidth(primaryStage.getWidth()-15);

        urlContextMenu.setMinWidth(50);
        urlContextMenu.setMaxWidth(50);
        urlContextMenu.setWidth(50);
        urlContextMenu.setPrefWidth(50);

        goBtn.setVisible(true);
        goBtn.setMaxWidth(25);
        goBtn.setMinWidth(25);
        goBtn.setPrefWidth(25);

        urlBar.setWidthM(urlBarWidth);
    }

    /**
     * Sets the current tab url
     * @param url new url
     */
    public void setTab(String url) {
        setBarStatus(0);
        getCurrentTab().setUrl(url);
    }

    /**
     * Opens a new window with the given url
     * @param url the start url
     */
    public void newWindow(String url) {
        logger.info("Open "+url+" in new browser window? ");
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
