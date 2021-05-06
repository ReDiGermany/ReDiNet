package com.github.redigermany.redinet;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private Scene scene;
    private BorderPane root;
    private TabPane tabPane = new TabPane();
    private MenuBar menuBar = new MenuBar();
    private HBox mainNavigation = new HBox();
    private VBox vBox = new VBox();

    private EventHandler<Event> tabSelectionChanged = e->{
        e.consume();
        Tab tab = ((Tab)(e.getTarget()));
    };

    private void newTab(){
        WebView webView = new WebView();
        Tab tab = new Tab("ReDiNet::New Tab", webView);
//        webView.getEngine().load("home.html");
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://redigermany.de");
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if (newValue == Worker.State.SUCCEEDED) {
                            tab.setText(webEngine.getTitle());
                        }
                    }
        });
//        WebHistory history = webEngine.getHistory();
//        ObservableList<WebHistory.Entry> entries = history.getEntries();
//        WebHistory.Entry entry = entries.get(0);
//        String url           = entry.getUrl();
//        String title         = entry.getTitle();
////        tab.setId("redinet://newtab");
//        tab.setText(title);
        tab.setOnSelectionChanged(tabSelectionChanged);
        tabPane.getTabs().add(tabPane.getTabs().size()-1,tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    private Menu addMenuBar(String title){
        Menu menu = new Menu(title);
        menuBar.getMenus().add(menu);
        return menu;
    }

    private MenuItem addMenuItem(Menu menu,String title){
        MenuItem item = new MenuItem(title);
        menu.getItems().add(item);
        return item;
    }

    private void addFileMenu(){
        MenuItem item;
        Menu menu = addMenuBar("File");

        item = addMenuItem(menu,"New Tab");
        item.setOnAction(e->newTab());

        item = addMenuItem(menu,"New Window");
        item.setOnAction(e->openNewWindow());

        item = addMenuItem(menu,"Open");
        item.setOnAction(e->openNewTabWithFile());

//        item = addMenuItem(menu,"Save");
//        item.setOnAction(e->openNewWindow());

        item = addMenuItem(menu,"Close");
        item.setOnAction(e->closeApplication());
    }

    private void closeApplication(){
//        TODO: Implement closeApplication
        System.out.println("not implemented yet");
    }

    private void openNewTabWithFile(){
//        TODO: Implement openNewTabWithFile
        System.out.println("not implemented yet");
    }

    private void openNewWindow(){
//        TODO: Implement openNewWindow
        System.out.println("not implemented yet");
    }

    private void buildMenuBar(){
        addFileMenu();
//        menu = addMenuBar("Edit");
//
//        menu = addMenuBar("View");
//
//        menu = addMenuBar("Favourites");
//
//        menu = addMenuBar("Tools");
//
//        menu = addMenuBar("Help");
    }

    @Override
    public void start(Stage stage)  {
        Tab tab = new Tab("+");
        tab.setOnSelectionChanged(e->{
            newTab();
        });
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        primaryStage = stage;
        root = new BorderPane();
        buildMenuBar();
        menuBar.setMinSize(stage.getWidth(),0);
        Button btnForward = new Button(">");
        Button btnBackward = new Button("<");
        Button btnReload = new Button("R");
        TextField urlBar = new TextField();
        mainNavigation.getChildren().addAll(btnBackward,btnForward,btnReload,urlBar);
        vBox.getChildren().addAll(menuBar,mainNavigation);
        root.setTop(vBox);
        root.setCenter(tabPane);
        scene = new Scene(root,1280,720);
        stage.setTitle("ReDiNet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
