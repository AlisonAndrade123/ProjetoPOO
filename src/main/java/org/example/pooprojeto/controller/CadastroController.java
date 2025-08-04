package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.pooprojeto.service.AuthService; // Import permanece
import org.example.pooprojeto.util.AppException;
import org.example.pooprojeto.util.NavigationManager;

public class CadastroController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;


    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();

        try {
            // ALTERADO: Chamamos diretamente a instância Singleton global do AuthService.
            AuthService.getInstance().register(nome, email, senha, false); // false para usuário padrão

            // Após o sucesso do registro, exibe um alerta e navega para a tela de login
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Cadastro realizado com sucesso! Faça o login para continuar.");
            NavigationManager.getInstance().navigateToLogin();

        } catch (AppException e) {
            // Se o registro falhar (ex: e-mail já existe), mostra a mensagem de erro.
            showAlert(Alert.AlertType.ERROR, "Erro no Cadastro", e.getMessage());
        }
    }

    @FXML
    private void handleBackToLoginButtonAction(ActionEvent event) {
        // Ação para o botão "Voltar", que leva para a tela de login
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
