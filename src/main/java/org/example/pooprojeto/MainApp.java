package org.example.pooprojeto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pooprojeto.controller.LoginController;
import org.example.pooprojeto.dao.UsuarioDAO;
import org.example.pooprojeto.service.AuthService; // >>> CORREÇÃO AQUI: Importe AuthService
import org.example.pooprojeto.util.DatabaseManager; // Para inicializar o DB

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private AuthService authService; // Variável de instância para AuthService
    private UsuarioDAO usuarioDAO;   // Variável de instância para UsuarioDAO

    @Override
    public void init() throws Exception {
        super.init();
        DatabaseManager.initializeDatabase(); // Inicializa BD

        // Instanciação de dependências
        usuarioDAO = new UsuarioDAO();
        authService = new AuthService(usuarioDAO); // Instancia AuthService, passando UsuarioDAO
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        try {
            // >>> ATENÇÃO: Ajuste o caminho do FXML se for diferente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/LoginView.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            // Injeta as dependências no controlador
            loginController.setPrimaryStage(primaryStage);
            loginController.setAuthService(authService); // >>> CORREÇÃO AQUI: Injeta AuthService
            //loginController.setUsuarioDAO(usuarioDAO);   // Injeta UsuarioDAO

            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Gerenciamento");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela de login: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}