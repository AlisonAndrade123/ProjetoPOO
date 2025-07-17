package org.example.pooprojeto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pooprojeto.controller.LoginController;
import org.example.pooprojeto.dao.UsuarioDAO;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.DatabaseManager;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private AuthService authService;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws Exception {
        super.init();
        // Inicializa o banco de dados. Isso garantirá que as tabelas e o usuário admin sejam criados.
        DatabaseManager.initializeDatabase();

        // Instanciação de dependências
        usuarioDAO = new UsuarioDAO();
        // AuthService precisa de UsuarioDAO para realizar a autenticação
        authService = new AuthService(usuarioDAO);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        try {
            // Carrega a tela de login como a primeira tela da aplicação
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/LoginView.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            // Injeta as dependências no LoginController
            // O LoginController precisará do Stage para poder trocar de tela
            loginController.setPrimaryStage(primaryStage);
            // O LoginController precisará do AuthService para autenticar o usuário
            loginController.setAuthService(authService);

            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Gerenciamento - Login"); // Título mais descritivo para a tela de login
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de login: " + e.getMessage());
            e.printStackTrace();
            // Poderia adicionar um Alert para o usuário aqui, se for crítico
        }
    }

    public static void main(String[] args) {
        launch();
    }
}