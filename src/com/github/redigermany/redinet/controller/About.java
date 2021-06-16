package com.github.redigermany.redinet.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;

/**
 * Controller for About page.
 * @author Max Kruggel
 */
public class About extends Pane {
    public About(){
        URL xml = getClass().getResource("/com/github/redigermany/redinet/view/About.fxml");
        if(xml==null){
            System.out.println("File \"About.fxml\" not found!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(xml);
        loader.setController(this);
        try {
            AnchorPane pane = loader.load();
            this.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
