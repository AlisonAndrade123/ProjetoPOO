module org.example.pooprojeto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    exports org.example.pooprojeto;


    exports org.example.pooprojeto.controller;


    opens org.example.pooprojeto.controller to javafx.fxml;


    exports org.example.pooprojeto.model;
    opens org.example.pooprojeto.model to javafx.fxml;

    exports org.example.pooprojeto.dao;
    opens org.example.pooprojeto.dao to javafx.fxml;

    exports org.example.pooprojeto.service;
    opens org.example.pooprojeto.service to javafx.fxml;

    exports org.example.pooprojeto.util;
    opens org.example.pooprojeto.util to javafx.fxml;
}