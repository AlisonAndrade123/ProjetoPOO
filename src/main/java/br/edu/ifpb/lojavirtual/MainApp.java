package br.edu.ifpb.lojavirtual;

import br.edu.ifpb.lojavirtual.util.DatabaseManager;
import br.edu.ifpb.lojavirtual.util.NavigationManager;
import br.edu.ifpb.lojavirtual.util.PathUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class MainApp extends Application {

    private static Path externalDbPath;

    @Override
    public void start(Stage stage) throws IOException {
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.setPrimaryStage(stage);
        navigationManager.navigateToLogin();
    }

    public static void main(String[] args) {
        // Passo 1: Apenas define ONDE o banco de dados deve estar.
        setupEnvironment();

        // Passo 2: Chama o DatabaseManager, que irá CRIAR e popular o banco
        // de dados nesse local, se ele ainda não existir.
        DatabaseManager.initializeDatabase();

        // Passo 3: Inicia a interface gráfica.
        launch(args);
    }

    /**
     * Define o caminho para o arquivo de banco de dados que será usado pela aplicação.
     * Este caminho será ao lado do arquivo JAR.
     */
    private static void setupEnvironment() {
        Path baseDir = PathUtils.getApplicationBaseDir().toPath();
        externalDbPath = baseDir.resolve("loja_virtual.db");
        System.out.println("O arquivo de banco de dados será usado em: " + externalDbPath.toAbsolutePath());
    }

    /**
     * Método público para que o DatabaseManager saiba qual caminho de conexão usar.
     */
    public static String getDatabaseConnectionString() {
        if (externalDbPath == null) {
            throw new IllegalStateException("O ambiente do banco de dados não foi inicializado.");
        }
        return "jdbc:sqlite:" + externalDbPath.toAbsolutePath().toString();
    }
}