package com.github.redigermany.redinet.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WindowState {
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private double width = 1280;
    private double height = 720;
    private double posX = 0;
    private double posY = 0;
    private String startPage = "newtab.html";
    private final String fileName = "window.conf";
    private boolean maximized = false;

    private Thread windowConfigThread;
    private void startThread(){
        logger.debug("startThread requested");
        if(windowConfigThread==null || !windowConfigThread.getName().equals("windowConfigThread")){
            logger.debug("starting thread");
            windowConfigThread = new Thread(()->{
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
            },"windowConfigThread");
            windowConfigThread.start();
        }else logger.info("thread already running");
    }
    private void parseLine(String line){
        String[] ln = line.split("=");
        if(ln[0].equals("x")){
            posX = Double.parseDouble(ln[1]);
            logger.info("posX="+posX);
        }
        if(ln[0].equals("y")){
            posY = Double.parseDouble(ln[1]);
            logger.info("posY="+posY);
        }
        if(ln[0].equals("w")){
            width = Double.parseDouble(ln[1]);
            logger.info("width="+width);
        }
        if(ln[0].equals("h")){
            height = Double.parseDouble(ln[1]);
            logger.info("height="+height);
        }
        if(ln[0].equals("ms")){
            maximized = Integer.parseInt(ln[1])==1;
            logger.info("maximized="+maximized);
        }
        if(ln[0].equals("startpage")){
            startPage = ln[1];
            logger.info("startPage="+startPage);
        }
    }

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

    public WindowState(){
        readConfig();
    }

    public double getX() {
        return posX;
    }
    public void setX(double newVal) {
        this.posX = newVal;
        startThread();
    }

    public double getY() {
        return posY;
    }
    public void setY(double newVal) {
        this.posY = newVal;
        startThread();
    }

    public double getWidth() {
        return width;
    }
    public void setWidth(double newVal) {
        this.width = newVal;
        startThread();
    }

    public double getHeight() {
        return height;
    }
    public void setHeight(double newVal) {
        this.height = newVal;
        startThread();
    }

    public boolean getMaximized(){
        return maximized;
    }
    public void setMaximized(Boolean newVal) {
        this.maximized = newVal;
        startThread();
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        if(!startPage.startsWith("http") && !startPage.equals("newtab.html")) startPage = "http://"+startPage;
        this.startPage = startPage;
        startThread();
    }
}
