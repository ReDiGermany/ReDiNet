package com.github.redigermany.redinet.model;

import javafx.scene.control.Tab;

/**
 * Model for Tab Infos.
 * Saving title, url and icon.
 * @author Max Kruggel
 */
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

    /**
     * Returns the url
     * @return string of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the icon
     * @return string of icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the title
     * @return string of title
     */
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
