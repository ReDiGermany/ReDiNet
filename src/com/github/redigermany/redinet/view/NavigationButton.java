package com.github.redigermany.redinet.view;

import javafx.scene.control.Button;
import java.net.URL;

/**
 * Image based navigation button
 * @author Max Kruggel
 */
public class NavigationButton extends Button {
    private String imageUrl;

    public NavigationButton(){
        getStyleClass().add("navigationButton");
    }

    /**
     * Sets the image url
     * @param imageUrl the new url
     */
    public void setImageUrl(String imageUrl) {
        URL url = getClass().getResource("/UI/" + imageUrl);
        if(url!=null){
            setStyle("-fx-background-image: url("+url+")");
            this.imageUrl = imageUrl;
        }
    }

    /**
     * Gets the current image url
     * @return image url
     */
    public String getImageUrl() {
        return imageUrl;
    }
}
