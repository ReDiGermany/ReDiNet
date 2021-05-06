package com.github.redigermany.redinet.view;

import javafx.scene.control.Button;

public class NavigationButton extends Button {
    private String imageUrl;

    public NavigationButton(){
        getStyleClass().add("navigationButton");
    }

    public void setImageUrl(String imageUrl) {
        if(this.imageUrl==null) {
            setStyle("-fx-background-image: url("+imageUrl+")");
            this.imageUrl = imageUrl;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
