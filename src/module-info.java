module ReDiNet {
    exports com.github.redigermany.redinet.view;
    exports com.github.redigermany.redinet.controller;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.web;
    opens com.github.redigermany.redinet.controller;
    exports com.github.redigermany.redinet.model;
    opens com.github.redigermany.redinet.model;
    exports com.github.redigermany.redinet;
}