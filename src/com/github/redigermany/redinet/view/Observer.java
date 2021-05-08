package com.github.redigermany.redinet.view;

public abstract class Observer {
    protected WebTab subject;

    public abstract void update(WebTab tab);
    public abstract void loadingStatus(int status,WebTab tab);
}
