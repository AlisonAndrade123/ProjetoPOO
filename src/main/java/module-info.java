// src/main/java/module-info.java
module org.example.pooprojeto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc; // >>> ADICIONE ESTA LINHA SE ESTIVER USANDO SQLite JDBC! <<<

    // Exporta o pacote principal da aplicação, onde está o MainApp
    exports org.example.pooprojeto;

    // >>> CORREÇÃO CRÍTICA: Exporta e abre os pacotes para que JavaFX possa acessá-los <<<

    // Exporta o pacote controller para que outros módulos (incluindo javafx.fxml indiretamente)
    // possam ver as classes públicas.
    exports org.example.pooprojeto.controller;

    // 'opens' permite que o módulo javafx.fxml use reflexão profunda
    // para acessar campos @FXML e métodos @FXML dentro dos seus controladores.
    // Isso é ABSOLUTAMENTE necessário para o FXMLLoader funcionar.
    opens org.example.pooprojeto.controller to javafx.fxml;

    // Também é uma boa prática exportar/abrir outros pacotes que podem ser acessados
    // por outras partes do sistema ou via FXML.
    exports org.example.pooprojeto.model;
    opens org.example.pooprojeto.model to javafx.fxml; // Se você referenciar modelos diretamente no FXML

    exports org.example.pooprojeto.dao;
    opens org.example.pooprojeto.dao to javafx.fxml; // Se você referenciar DAOs diretamente no FXML

    exports org.example.pooprojeto.service;
    opens org.example.pooprojeto.service to javafx.fxml; // Se você referenciar serviços diretamente no FXML

    exports org.example.pooprojeto.util;
    opens org.example.pooprojeto.util to javafx.fxml; // Se você referenciar utilitários (como AppException) no FXML
}