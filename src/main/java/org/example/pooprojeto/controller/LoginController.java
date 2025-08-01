package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.AppException;
import org.example.pooprojeto.util.NavigationManager;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String email = usernameField.getText();
        String senha = passwordField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            showAlert(AlertType.ERROR, "Erro de Login", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            Usuario usuarioAutenticado = authService.login(email, senha);
            NavigationManager.getInstance().setUsuarioLogado(usuarioAutenticado);

            if (usuarioAutenticado.isAdmin()) {
                NavigationManager.getInstance().navigateToAdminView();
            } else {
                NavigationManager.getInstance().navigateToProductsView();
            }

        } catch (AppException e) {
            showAlert(AlertType.ERROR, "Falha no Login", e.getMessage());
        }
    }

    @FXML
    private void handleCreateAccountButtonAction(ActionEvent event) {
        NavigationManager.getInstance().navigateToCadastro();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}