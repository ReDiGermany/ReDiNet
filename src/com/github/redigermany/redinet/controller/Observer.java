package com.github.redigermany.redinet.controller;

import com.github.redigermany.redinet.model.WebTab;

/**
 * Simple observer.
 * @author Max Kruggel
 */
public abstract class Observer {
    protected WebTab subject;

    public abstract void update(WebTab tab);
    public abstract void loadingStatus(int status,WebTab tab);
}
