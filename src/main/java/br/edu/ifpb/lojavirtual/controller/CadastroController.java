package br.edu.ifpb.lojavirtual.controller;

import br.edu.ifpb.lojavirtual.service.AuthService;
import br.edu.ifpb.lojavirtual.util.AppException;
import br.edu.ifpb.lojavirtual.util.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            AuthService.getInstance().register(nome, email, senha, false);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Cadastro realizado com sucesso! Faça o login para continuar.");
            NavigationManager.getInstance().navigateToLogin();

        } catch (AppException e) {
            showAlert(Alert.AlertType.ERROR, "Erro no Cadastro", e.getMessage());
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
