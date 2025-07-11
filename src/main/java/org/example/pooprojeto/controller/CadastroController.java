package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.AppException;

import java.io.IOException;

public class CadastroController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService;
    private Stage primaryStage; // Para poder mudar a cena principal

    // Métodos para injetar dependências (chamados pelo MainApp ou quem carregar este FXML)
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Lida com a ação de registro quando o botão "Criar conta" é clicado.
     */
    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            // Tenta registrar o novo usuário
            Usuario novoUsuario = authService.register(nome, email, senha, false); // Correção
            showAlert(Alert.AlertType.INFORMATION, "Cadastro Bem-sucedido", "Usuário " + novoUsuario.getNome() + " cadastrado com sucesso!");

            // Após o cadastro, você pode:
            // 1. Redirecionar para a tela de login (mais comum)
            loadLoginView(event);
            // 2. Fazer login automático e ir para a tela de produtos
            // Usuario usuarioLogado = authService.login(email, senha); // Tentar logar o novo usuário
            // loadProdutosView(event);

        } catch (AppException e) {
            showAlert(Alert.AlertType.ERROR, "Falha no Cadastro", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao tentar cadastrar: " + e.getMessage());
        }
    }

    /**
     * Lida com a ação de voltar ao login.
     */
    @FXML
    private void handleBackToLoginButtonAction(ActionEvent event) {
        loadLoginView(event);
    }

    private void loadLoginView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/LoginView.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            // Passa o AuthService e o Stage para o controlador de login
            loginController.setAuthService(authService);
            loginController.setPrimaryStage((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow());
            // loginController.setUsuarioDAO(new UsuarioDAO()); // Se o LoginController ainda precisar do DAO diretamente

            Stage currentStage = (primaryStage != null) ? primaryStage : (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de login.");
        }
    }

    private void loadProdutosView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/ProdutosView.fxml"));
            Parent root = loader.load();

            // Você precisaria de um método setUsuarioLogado no ProdutosController se for passar
            // ProdutosController produtosController = loader.getController();
            // produtosController.setUsuarioLogado(novoUsuario);

            Stage currentStage = (primaryStage != null) ? primaryStage : (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Gerenciamento de Produtos");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela principal.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}