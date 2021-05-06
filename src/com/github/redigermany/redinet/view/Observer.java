package com.github.redigermany.redinet.view;

public abstract class Observer {
    protected WebTab subject;

    public abstract void update(String location,String icon,String title);
}
