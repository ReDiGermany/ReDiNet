package com.github.redigermany.redinet.controller;

import javafx.scene.control.Tab;

public class TabInfo {
    private String url="";
    private String icon="";
    private String title="";
    public TabInfo(Tab tab) {
        if(tab==null || tab.getId()==null) return;
        String[] info = tab.getId().split(":-:");
        if (info.length != 3) return;
        url = info[0];
        icon = info[1];
        title = info[2];
    }

    public String getUrl() {
        return url;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "TabInfo{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
