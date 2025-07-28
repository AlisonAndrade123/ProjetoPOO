package org.example.pooprojeto;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.pooprojeto.util.DatabaseManager;
import org.example.pooprojeto.util.NavigationManager;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        // Garante que o banco de dados e as tabelas sejam criados antes de qualquer coisa.
        DatabaseManager.initializeDatabase();
    }


    @Override
    public void start(Stage stage) throws IOException {
        // 1. Pega a instância única do nosso gerenciador de navegação.
        NavigationManager navigationManager = NavigationManager.getInstance();

        // 2. "Apresenta" a janela principal (Stage) para o gerenciador.
        // A partir de agora, o NavigationManager saberá em qual janela deve trocar as telas.
        navigationManager.setPrimaryStage(stage);

        // 3. Inicia a aplicação mandando o gerenciador navegar para a tela de login.
        navigationManager.navigateToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}