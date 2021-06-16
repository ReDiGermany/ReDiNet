package com.github.redigermany.redinet.controller;

import java.time.LocalDateTime;

/**
 * Permission based logger to keep clear console
 * @author Max Kruggel
 */
public class Logger {

    public enum TYPES {
        ALL,
        DEBUG,
        INFO,
        ERROR,
        NONE
    }
    private final TYPES type;
    private final String name;
    private String id;

    /**
     * Sets the prefix id
     * @param id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Checks the type and logs if allowed to console width template >[time,date] [type name] [file name] {[id]} text<
     * @param type the desired permission type
     * @param text the text to print
     */
    private void log(TYPES type,String text){
        if(type.ordinal() >= this.type.ordinal()){
            LocalDateTime now = LocalDateTime.now();
            System.out.printf("[%s %s] [%s] [%s]%s %s%n",now.toLocalTime(),now.toLocalDate(),type.name().toUpperCase(),name,(this.id!=null?" ["+id+"]":""),text);
        }
    }

    /**
     * Logs with debug permission
     * @param text the text to print
     */
    public void debug(String text){
        log(TYPES.DEBUG,text);
    }

    /**
     * Logs with error permission
     * @param text the text to print
     */
    public void error(String text){
        log(TYPES.ERROR,text);
    }

    /**
     * Logs with info permission
     * @param text the text to print
     */
    public void info(String text){
        log(TYPES.INFO,text);
    }

    /**
     * Initializes the logger with desired permission type
     * @param type the desired permission
     */
    public Logger(TYPES type){
        try {
            throw new Exception();
        } catch (Exception e) {
            this.name = e.getStackTrace()[1].getFileName();
            this.type = type;
//            System.out.println("Logger active for "+name+" @"+type.name());
        }
    }

}
