package com.github.redigermany.redinet.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * Image based navigation button
 * @author Max Kruggel
 */
public class NavigationButton extends Button {
    private String imageUrl;
    private int imageWidth = 15;
    private int imageHeight = 15;

    public NavigationButton(){
        getStyleClass().add("navigationButton");
    }

    public String getSvgImage(String filePath, String color){
        // Checking URL
        URL url = getClass().getResource(filePath);
        if(url==null) return "";

        // Reading Content from URL
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(url.getFile()))) {
            String fr = bufferedReader.readLine();

            // Searching for data path (<svg><path d={svgPath}></path></svg>)
            String svgPath = "";
            String[] rp = fr.split("\"");
            for(int i=0;i<rp.length-1;i++){
                if(rp[i].endsWith("d=") && svgPath.equals("")){
                    svgPath = rp[i+1];
                }
            }

            // Setting SVG Path as fx shape
            return String.format("-fx-background-color:%s; -fx-shape: \"%s\"",color,svgPath);

        } catch (IOException e) {
            // Possible fallback image?
        }
        return "";
    }

    /**
     * Sets the image url
     * @param imageUrl the new url
     */
    public void setImageUrl(String imageUrl) {
        int d = 25;
        setPrefSize(d,d);
        setMinSize(d,d);
        setMaxSize(d,d);
        String style = "";
        if(imageUrl.endsWith("svg")){
            String s = getSvgImage("/UI/" + imageUrl,"#fff");
            Pane pane = new Pane();
            pane.setStyle(s);
//            style = "-fx-background-color: #4ae53a";
            pane.setMinSize(imageWidth,imageHeight);
            pane.setMaxSize(imageWidth,imageHeight);
            pane.setPrefSize(imageWidth,imageHeight);
            setGraphic(pane);
        }else{
            URL url = getClass().getResource("/UI/" + imageUrl);
            style = "-fx-background-image: url("+url +")";
        }
        setStyle(style);
    }

    /**
     * Gets the current image url
     * @return image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageWidth(int imageWidth){
        this.imageWidth = imageWidth;
    }
    public int getImageWidth(){return imageWidth;}
    public void setImageHeight(int imageHeight){
        this.imageHeight = imageHeight;
    }
    public int getImageHeight(){return imageHeight;}
}
