package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.view.NavigationButton;
import com.github.redigermany.redinet.view.Observer;
import com.github.redigermany.redinet.view.WebTab;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
    @FXML private NavigationButton goBtn;
    @FXML private TextField urlBar;
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
    private BookmarkController bc = new BookmarkController(this);
    private ContextMenu urlContextMenu = new ContextMenu();
    private int urlContextMenuSelected = 0;
    private WindowState windowState = new WindowState();
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private String initUrl;
    private double urlBarWidth;
    private boolean urlBarChanged = false;

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
        WebTab tab = new WebTab(windowState,primaryStage);
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
        openAbout();
    }

    private void initBaseLayout() {
        initPrevButton();
        initForwButton();
        initReloadButton();
        initGoBtn();
        initHomeBtn();
        initUrlBar();
        initBookmarkButton();
        initMenuButton();
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            try {
                initUpdateTabInfo((WebTab) t1);
            }catch (Exception e){
                if(!e.getMessage().startsWith("class javafx.scene.control.Tab cannot be cast to class com.github.redigermany.redinet.view.WebTab"))
                    e.printStackTrace();
            }
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
    private void initGoBtn(){
        goBtn.setOnAction(e->{
            urlBarChanged = false;
            String url="";
            if(urlContextMenuSelected==0){
                url = "https://www.google.com/search?q="+urlBar.getText();
            }else{
                url = urlContextMenu.getItems().get(urlContextMenuSelected).getText();
            }
            getCurrentTab().setUrl(url);
            urlContextMenu.hide();
        });
    }
    private void initHomeBtn(){
        homeBtn.setOnAction(e->{
            getCurrentTab().setUrl(windowState.getStartPage());
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
            boolean isUrl = urlBar.getText().matches("[a-zA-Z0-9-_\\.]+\\.[a-zA-Z]+");

            if(e.getCode() == KeyCode.ENTER){
                urlBarChanged = false;
                String url="";
                if(urlContextMenuSelected==0 && isUrl) {
                    url = urlBar.getText();
                    if(!url.matches("(http|https)://")){
                        url = "http://"+url;
                    }

                }else if(urlContextMenuSelected==0 && !isUrl || urlContextMenuSelected==1 && isUrl) {
                    url = "https://www.google.com/search?q="+urlBar.getText();
                }else{
                    url = urlContextMenu.getItems().get(urlContextMenuSelected).getText();
                }
                getCurrentTab().setUrl(url);
                urlContextMenu.hide();
            }else{
                urlBarChanged = true;
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

//                    System.out.println(urlBar.getText()+" should be "+(isUrl?"website":"google search")+"; isUrl="+isUrl);
                    if(isUrl){
                        urlContextMenuItems.add(new MenuItem("Go to: "+urlBar.getText()));
                    }
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

    private MenuItem myMenuItem(String title,EventHandler<ActionEvent> e){
        MenuItem item = new MenuItem(title);
        item.setOnAction(e);
        return item;
    }

    private MenuItem myMenuItem(String title){
        return new MenuItem(title);
    }

    private void setStartPage(String url){
        System.out.println("Start Page="+url);
        windowState.setStartPage(url);
    }

    private Label StyledLabel(String text,String style){
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

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
    private void openAbout(){
        setBarStatus(0);
        Tab tab = new Tab();
        tab.setText("ReDiNet :: About");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background:#111;");
        VBox vBox = new VBox();
        ArrayList<Node> list = new ArrayList<>();
        list.add(StyledLabel("About ReDiNet","-fx-font-size: 20;-fx-font-weight: bold"));
        list.add(StyledLabel("ReDiNet is a small Web Browser driven by the default WebView engine of java.",""));
        list.add(StyledLabel("This Web Browser is mainly for showcasing purpose due to the necessity of the OOP2 Course at Hof-University.",""));
        list.add(StyledLabel("It states: Testataufgabe zur Vorlesung Objektorientierte Programmierung 2",""));
        list.add(StyledLabel("Used icons are provided by fontawesome. (See dotmenu -> help -> fontawesome)\n\n",""));
        list.add(StyledLabel("This Software is provided by Max 'ReDiGermany' Kruggel - Computer Science Student at Hof University - 2nd semester - 00381220",""));
        vBox.getChildren().addAll(list);
        scrollPane.setContent(vBox);
        vBox.setOpaqueInsets(new Insets(30));
        tab.setContent(scrollPane);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }
    private void openHistory(){
        setBarStatus(0);
        Tab tab = new Tab();
        tab.setText("ReDiNet :: Browsing History");


        VBox root = new VBox();
        for(String item:HistoryController.getInstance().getList()){
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
            HistoryController.getInstance().clear();
            tabPane.getTabs().remove(tab);
            openHistory();
        });
        vBox.getChildren().addAll(scrollPane,clearButton);
        tab.setContent(vBox);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    private void initBookmarkButton(){
        updateBookmarkList();
        bookmarkBtn.setOnAction(e->{
            bc.toggleBookmark(new TabInfo(tabPane.getSelectionModel().getSelectedItem()));
        });
    }
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
        help.getItems().add(myMenuItem("Github",e->{newTab("https://github.com/ReDiGermany/HOF-OOP2-ReDiNet");}));
        help.getItems().add(myMenuItem("Fontawesome",e->{newTab("http://fontawesome.com/icons/");}));
        items.add(help);

        items.add(myMenuItem("Minimieren",e->{primaryStage.setIconified(true);}));
        items.add(myMenuItem("Maximieren",e->{primaryStage.setMaximized(true);}));
        items.add(myMenuItem("Schließen",e->{primaryStage.hide();}));

        menuContextMenu.getItems().addAll(items);
//        menuContextMenu.add(new MenuItem(historyItem));
        menuBtn.setOnAction(e->{
            menuContextMenu.show(primaryStage,menuBtn.getLayoutX()+primaryStage.getX()-60,menuBtn.getLayoutY()+primaryStage.getY()+60);
        });
        menuBtn.setContextMenu(menuContextMenu);
        menuContextMenu.getStyleClass().add("menuContextMenu");
    }

    private WebTab getCurrentTab(){
        try {
            WebTab tab = (WebTab) tabPane.getSelectionModel().getSelectedItem();
            return tab;
        }catch(Exception e){
            return new WebTab();
        }
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
                "Webseite "+url+" fehler?",
                "Webseite "+url+" wurde nicht gefunden.",
//                "Webseite "+url+" fertig geladen.",
        }[statusId]);
        updateLayoutWidth();
    }

    private void updateLayoutHeight() {
        int offset = bookmarks.getChildren().size()>0?99:74;
        tabPane.setMinHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMaxHeight(primaryStage.getHeight()-(menuBtn.getHeight()*3)-30);
        tabPane.setMinHeight(primaryStage.getHeight()-offset-(infoBar.isVisible()?infoBar.getHeight():0));
        tabPane.setMaxHeight(primaryStage.getHeight()-offset-(infoBar.isVisible()?infoBar.getHeight():0));
    }

    private void updateLayoutWidth() {
        urlBarWidth = primaryStage.getWidth()-(6*menuBtn.getWidth())-20
                -(urlBarChanged?25:0)
        -25
        ;
//        System.out.println("urlBarChanged="+urlBarChanged+"; urlBarWidth="+urlBarWidth);
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

        goBtn.setVisible(urlBarChanged);
        goBtn.setMaxWidth(urlBarChanged?25:0);
        goBtn.setMinWidth(urlBarChanged?25:0);
        goBtn.setPrefWidth(urlBarChanged?25:0);

        urlBar.setMinWidth(urlBarWidth);
        urlBar.setMaxWidth(urlBarWidth);
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
