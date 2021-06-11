package com.github.redigermany.redinet.model;

import com.github.redigermany.redinet.controller.Logger;
import com.github.redigermany.redinet.controller.MainLayout;
import com.github.redigermany.redinet.view.Bookmark;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Model for Bookmarks.
 * Bookmarks are saved into `bookmarks.list` as a csv styles file but with ":-:" as default splitter
 * @author Max Kruggel
 */
public class BookmarkModel {
    private final Logger logger = new Logger(Logger.TYPES.ERROR);
    private final ArrayList<Bookmark> bookmarkList = new ArrayList<>();
    private final MainLayout mainLayout;
    private final String fileName = "/bookmarks.list";

    public BookmarkModel(MainLayout mainLayout){
        this.mainLayout = mainLayout;
        readBookmarks();
    }

    /**
     * Returns a Bookmark List
     * @return ArrayList\<Bookmark\>
     */
    public ArrayList<Bookmark> getBookmarks() {
        return bookmarkList;
    }

    /**
     * Checks if the url is in the bookmark list
     * @param url url string
     * @return true if is bookmark
     */
    public boolean isBookmark(String url){
        return getBookmarkIndex(url)>=0;
    }

    /**
     * Toogles the bookmark state by the tab info.
     * Adds the current tab url+title as a bookmark if doesn't exist.
     * Removes the bookmark if url is already a bookmark.
     * @param info TabInfo containing title and url
     */
    public void toggleBookmark(TabInfo info) {
        int id = getBookmarkIndex(info.getUrl());
        boolean existing = id>0;
        logger.info(String.format("%s bookmark",existing?"Removing":"Adding"));

        if(existing) bookmarkList.remove(id);
        else bookmarkList.add(new Bookmark(info.getTitle(),info.getUrl(),mainLayout));

        updateList();
    }

    /**
     * Removes a bookmark by it's given url
     * @param url the url to be deleted
     */
    public void removeBookmark(String url) {
        int id = getBookmarkIndex(url);
        if (id >= 0) {
            logger.info("Removing bookmark");
            bookmarkList.remove(id);

            updateList();
        }
    }

    /**
     * Loads all bookmarks from the file.
     * Logs to error when file was not found or there was an other io error existing.
     */
    private void readBookmarks(){
        InputStream res = getInputStream();
        if(res==null) return;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(res, StandardCharsets.UTF_8));
            String line;

            while((line=br.readLine())!=null) addLine(line);

        } catch (FileNotFoundException e) {
            logger.error("BookmarkModel error! (FNF) "+e.getMessage());
        } catch (IOException e) {
            logger.error("BookmarkModel error! (IO) "+e.getMessage());
        }
    }

    /**
     * Adds the current line as a bookmark. Must contain ":-:" as a splitter
     * @param line the input string with {url}:-:{title}
     */
    private void addLine(String line){
        String[] ln = line.split(":-:");
        if(ln.length==2) {
            bookmarkList.add(new Bookmark(ln[0],ln[1],mainLayout));
        }
    }

    /**
     * Returns the file InputStream.
     * Logs to error when not existing.
     * @return null | InputStream
     */
    private InputStream getInputStream(){
        InputStream res = mainLayout.getClass().getResourceAsStream(fileName);
        if (res == null) logger.error("BookmarkModel error! (stream is null)");

        return res;
    }

    /**
     * Compares 2 urls for tailing slashes & adds it or removes it.
     * @param url input url to modify
     * @param checkUrl input url to compare to
     * @return corrected url
     */
    private String compareLocations(String url,String checkUrl){
        if(url.endsWith("/") && !checkUrl.endsWith("/"))
            url = url.trim();
        else if(!url.endsWith("/") && checkUrl.endsWith("/"))
            url += "/";

        return url;
    }

    /**
     * Gets a bookmark by its given url
     * @param url the url to search
     * @return -1 if not found | index if found
     */
    private int getBookmarkIndex(String url) {
        for (int i = 0; i< bookmarkList.size(); i++) {

            String checkUrl = bookmarkList.get(i).getUrl();
            url = compareLocations(url,checkUrl);

            if (checkUrl.equals(url)) return i;

        }

        return -1;
    }

    /**
     * Writes all bookmarks to the file.
     * Logs to error when not existing.
     */
    private void updateList(){
        try (BufferedWriter bf = new BufferedWriter(new FileWriter("res"+fileName))){
            for(Bookmark bookmark: bookmarkList){
                bf.write(String.format("%s:-:%s%n",bookmark.getName(),bookmark.getUrl()));
            }
        } catch (IOException e) {
            logger.error("BookmarkModel.updateList io error: "+e.getMessage());
        }
        mainLayout.updateBookmarkList();
    }

}
