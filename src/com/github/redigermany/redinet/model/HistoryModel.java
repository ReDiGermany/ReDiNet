package com.github.redigermany.redinet.model;

import com.github.redigermany.redinet.controller.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Model for History.
 * History items are saved into `history.log` with every url in it's own file.
 * This class is written with a singleton pattern
 * @author Max Kruggel
 */
public class HistoryModel {
    private final String fileName = "history.log";
    private final Logger logger = new Logger(Logger.TYPES.NONE);
    private final ArrayList<String> history = new ArrayList<>();

    private static HistoryModel INSTANCE;

    /**
     * Gets the current instance. Creates one if none existing.
     * @return HistoryModel instance
     */
    public static HistoryModel getInstance(){
        if(INSTANCE==null) INSTANCE = new HistoryModel();
        return INSTANCE;
    }

    /**
     * Returns the entire history list.
     * @return history list
     */
    public ArrayList<String> getList(){
        return history;
    }

    /**
     * Removes all items from the history list.
     */
    public void clear(){
        history.clear();
        try(FileWriter fw = new FileWriter(fileName,false)){
            fw.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an url to the history.
     * @param url the url to be added
     */
    public void addUrl(String url){
        history.add(url);
        try(FileWriter fw = new FileWriter(fileName,true)){
            fw.append(url).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches the entire history for the given url.
     * @param url the url to be searched for
     * @return the list of found urls matching the input. Max 5 Items.
     */
    public ArrayList<String> search(String url){
        ArrayList<String> ret = new ArrayList<>();

        for(String line:history){
            if(line.contains(url)){

                ret.add(line);
                if(ret.size()==5) return ret;

            }else logger.info(line+" not matching "+url);

        }

        return ret;
    }

    /**
     * Reads the current `history.log`
     */
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

    private HistoryModel(){
        readConfig();
    }

}
