package com.github.redigermany.redinet.controller;

import java.time.LocalDateTime;
import java.util.Locale;

public class Logger {
    public enum TYPES {
        ALL,
        DEBUG,
        INFO,
        ERROR,
        NONE
    }
    private TYPES type;
    private final String name;
    private String id;
    public Logger setId(String id){
        this.id = id;
        return this;
    }
    public Logger(TYPES type){
        try {
            throw new Exception();
        } catch (Exception e) {
            this.name = e.getStackTrace()[1].getFileName();
            this.type = type;
//            System.out.println("Logger active for "+name+" @"+type.name());
        }
    }
    private void log(TYPES type,String text){
        if(type.ordinal() >= this.type.ordinal()){
            LocalDateTime now = LocalDateTime.now();
            System.out.printf("[%s %s] [%s] [%s]%s %s%n",now.toLocalTime(),now.toLocalDate(),type.name().toUpperCase(),name,(this.id!=null?" ["+id+"]":""),text);
        }
    }
    public void debug(String text){
        log(TYPES.DEBUG,text);
    }
    public void error(String text){
        log(TYPES.ERROR,text);
    }
    public void info(String text){
        log(TYPES.INFO,text);
    }
}
