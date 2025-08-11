// CORREÇÃO 1: O nome do módulo deve ser o mesmo do build.gradle.kts
module br.edu.ifpb.lojavirtual {
    // Módulos do JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Dependências externas
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires com.github.librepdf.openpdf;
    requires java.desktop; // Usado para operações como Desktop.getDesktop().open()

    // Exponha o pacote principal para que o lançador do Gradle funcione
    exports br.edu.ifpb.lojavirtual;

    // Abra os pacotes que contêm os Controllers para a biblioteca JavaFX FXML
    // Apenas os pacotes com Controllers precisam ser abertos para o FXML.
    opens br.edu.ifpb.lojavirtual.controller to javafx.fxml;

    // Não é necessário abrir ou exportar pacotes de modelo, DAO ou serviço
    // a menos que outra biblioteca precise acessá-los via reflexão.
    // Para o código da sua própria aplicação, o `requires` já dá o acesso necessário.
    exports br.edu.ifpb.lojavirtual.model;
    exports br.edu.ifpb.lojavirtual.dao;
}