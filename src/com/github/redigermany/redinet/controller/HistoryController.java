package com.github.redigermany.redinet.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryController {
    private static HistoryController INSTANCE;
    public static HistoryController getInstance(){
        if(INSTANCE==null){
            INSTANCE = new HistoryController();
        }
        return INSTANCE;
    }
    private final String fileName = "history.log";
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private ArrayList<String> history = new ArrayList<>();

    public ArrayList<String> getList(){
        return history;
    }

    public void clear(){
        history.clear();
        try(FileWriter fw = new FileWriter(fileName,false)){
            fw.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HistoryController(){
        readConfig();
    }

    private void readConfig(){
        try(BufferedReader fr = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line=fr.readLine())!=null){
                history.add(line);
            }
        }catch(IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void addUrl(String url){
        history.add(url);
        try(FileWriter fw = new FileWriter(fileName,true)){
            fw.append(url+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> search(String text){
        ArrayList<String> ret = new ArrayList<>();
        for(String line:history){
            if(line.contains(text)){
                ret.add(line);
                if(ret.size()==5) return ret;
            }else{
                logger.info(line+" not matching "+text);
            }
        }
        return ret;
    }
}
