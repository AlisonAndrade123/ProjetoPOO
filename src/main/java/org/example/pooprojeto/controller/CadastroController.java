package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.AppException;
import org.example.pooprojeto.util.NavigationManager;

public class CadastroController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

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
            Usuario novoUsuario = authService.register(nome, email, senha, false);
            showAlert(Alert.AlertType.INFORMATION, "Cadastro Bem-sucedido", "Usuário " + novoUsuario.getNome() + " cadastrado com sucesso!");

            NavigationManager.getInstance().navigateToLogin();

        } catch (AppException e) {
            showAlert(Alert.AlertType.ERROR, "Falha no Cadastro", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao tentar cadastrar.");
        }
    }

    @FXML
    private void handleBackToLoginButtonAction(ActionEvent event) {
        NavigationManager.getInstance().navigateToLogin();
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}