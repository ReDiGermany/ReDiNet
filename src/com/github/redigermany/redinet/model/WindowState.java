package com.github.redigermany.redinet.model;

import com.github.redigermany.redinet.controller.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Model for Window State + config.
 * Saving title, url and icon.
 * @author Max Kruggel
 */
public class WindowState {
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private double width = 1280;
    private double height = 720;
    private double posX = 0;
    private double posY = 0;
    private String startPage = "newtab.html";
    private final String fileName = "window.conf";
    private boolean maximized = false;

    public WindowState(){
        readConfig();
    }

    /**
     * Gets the global window X position
     * @return position X
     */
    public double getX() {
        return posX;
    }

    /**
     * Sets the global window X position and updates the config
     * @param newVal new position X
     */
    public void setX(double newVal) {
        if(posX == newVal) return;
        posX = newVal;
        startThread();
    }

    /**
     * Gets the global window Y position
     * @return position Y
     */
    public double getY() {
        return posY;
    }

    /**
     * Sets the global window Y position and updates the config
     * @param newVal new position Y
     */
    public void setY(double newVal) {
        if(posY == newVal) return;
        posY = newVal;
        startThread();
    }

    /**
     * Gets the global window width
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the global window width and updates the config
     * @param newVal new width
     */
    public void setWidth(double newVal) {
        if(width == newVal) return;
        width = newVal;
        startThread();
    }

    /**
     * Gets the global window height
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the global window height and updates the config
     * @param newVal new height
     */
    public void setHeight(double newVal) {
        if(height == newVal) return;
        height = newVal;
        startThread();
    }

    /**
     * Gets the window maximize state
     * @return maximize state
     */
    public boolean getMaximized(){
        return maximized;
    }

    /**
     * Sets the window maximize state
     * @param newVal new maximize state
     */
    public void setMaximized(Boolean newVal) {
        if(maximized == newVal) return;
        maximized = newVal;
        startThread();
    }

    /**
     * Gets the start page
     * @return start page
     */
    public String getStartPage() {
        return startPage;
    }

    /**
     * Checks if url not starting with http and not matching newtab.html
     * @param newVal url to check
     * @return corrected url
     */
    private String validateStartPage(String newVal){
        if(!newVal.startsWith("http") && !newVal.equals("newtab.html"))
            newVal = "http://"+newVal;
        return newVal;
    }

    /**
     * Sets the start page and updates the config
     * @param newVal new start page
     */
    public void setStartPage(String newVal) {
        newVal = validateStartPage(newVal);
        if(startPage.equals(validateStartPage(newVal))) return;
        startPage = validateStartPage(newVal);
        startThread();
    }


    private Thread windowConfigThread;
    /**
     * Thread for config changes
     */
    private final Runnable newThread = ()->{
        logger.debug("thread running");
        try {
            Thread.sleep(1000);
            logger.debug("thread delayed");
            try(FileWriter fw = new FileWriter(fileName)) {
                String newConf = String.join("\n",new String[]{
                        "x=" + posX,
                        "y=" + posY,
                        "w=" + width,
                        "h=" + height,
                        "startpage=" + startPage,
                        "ms=" + (maximized?1:0),
                });
                logger.info("Writing window config to:\n"+newConf);
                fw.write(newConf);
            }catch(IOException ignored){
            }
            windowConfigThread.interrupt();
            windowConfigThread = null;
        } catch (InterruptedException ignored) {
        }
    };

    /**
     * Checks if no updateThread is running and starts it.
     */
    private void startThread(){
        logger.debug("startThread requested");
        if(windowConfigThread==null || !windowConfigThread.getName().equals("windowConfigThread")){
            logger.debug("starting thread");
            windowConfigThread = new Thread(newThread,"windowConfigThread");
            windowConfigThread.start();
        }else logger.info("thread already running");
    }

    /**
     * Updates the current config with the line.
     * must be one of the following:
     *      x
     *      y
     *      w
     *      h
     *      ms
     *      startpage
     * must have the following syntax: {name}={value}
     * @param line the line to update the url to.
     */
    private void parseLine(String line){
        String[] ln = line.split("=");
        if(ln[0].equals("x"))           posX        = Double.parseDouble(ln[1]);
        if(ln[0].equals("y"))           posY        = Double.parseDouble(ln[1]);
        if(ln[0].equals("w"))           width       = Double.parseDouble(ln[1]);
        if(ln[0].equals("h"))           height      = Double.parseDouble(ln[1]);
        if(ln[0].equals("ms"))          maximized   = Integer.parseInt(ln[1])==1;
        if(ln[0].equals("startpage"))   startPage   = ln[1];
        logger.info(ln[0]+"="+startPage);
    }

    /**
     * Reads the entire config line by line
     */
    private void readConfig(){
        try(BufferedReader fr = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line=fr.readLine())!=null){
                parseLine(line);
            }
        }catch(IOException e) {
            logger.error(e.getMessage());
        }
    }

}
