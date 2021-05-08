package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.view.Bookmark;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BookmarkController {
    private final Logger logger = new Logger(Logger.TYPES.ERROR);
    private ArrayList<Bookmark> bm = new ArrayList<>();
    private MainLayout mainLayout;
    private final String fileName = "/bookmarks.list";

    public BookmarkController(MainLayout mainLayout){
        this.mainLayout = mainLayout;
        try {
            InputStream res = mainLayout.getClass().getResourceAsStream(fileName);
            if (res != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(res, StandardCharsets.UTF_8));
                String line;
                while((line=br.readLine())!=null){
                    String[] ln = line.split(":-:");
                    if(ln.length==2) bm.add(new Bookmark(ln[0],ln[1],mainLayout));
                }
            }else{
                logger.error("BookmarkController error! (stream is null)");
            }
        } catch (FileNotFoundException e) {
            logger.error("BookmarkController error! (FNF) "+e.getMessage());
        } catch (IOException e) {
            logger.error("BookmarkController error! (IO) "+e.getMessage());
        }
    }

    public ArrayList<Bookmark> getBookmarks() {
        return bm;
    }

    private int getBookmarkId(String location) {
        for (int i=0;i<bm.size();i++) {
            String checkLocation = bm.get(i).getUrl();
            if(location.endsWith("/") && !checkLocation.endsWith("/")){
                checkLocation+="/";
            }else if(!location.endsWith("/") && checkLocation.endsWith("/")){
                location += "/";
            }
            if (checkLocation.equals(location)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isBookmark(String location){
        return getBookmarkId(location)>=0;
    }

    private void updateList(){
        try (BufferedWriter bf = new BufferedWriter(new FileWriter("res"+fileName))){
            for(Bookmark bookmark:bm){
                bf.write(String.format("%s:-:%s%n",bookmark.getName(),bookmark.getUrl()));
            }
        } catch (IOException e) {
            logger.error("BookmarkController.updateList io error: "+e.getMessage());
        }
        mainLayout.updateBookmarkList();
    }

    public void toggleBookmark(TabInfo info) {
        int id = getBookmarkId(info.getUrl());
        if(id>=0){
            logger.info("Removing bookmark");
            bm.remove(id);
        }else{
            logger.info("Adding bookmark");
            bm.add(new Bookmark(info.getTitle(),info.getUrl(),mainLayout));
        }
        updateList();
    }

    public void removeBookmark(String url) {
        int id = getBookmarkId(url);
        if (id >= 0) {
            logger.info("Removing bookmark");
            bm.remove(id);
            updateList();
        }
    }
}
