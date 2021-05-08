package com.github.redigermany.redinet.view;

import javafx.scene.control.Button;

import java.io.File;
import java.net.URL;

public class NavigationButton extends Button {
    private String imageUrl;

    public NavigationButton(){
        getStyleClass().add("navigationButton");
    }

    public void setImageUrl(String imageUrl) {
        URL url = getClass().getResource("/UI/" + imageUrl);
        if(url!=null){
            setStyle("-fx-background-image: url("+url+")");
            this.imageUrl = imageUrl;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
