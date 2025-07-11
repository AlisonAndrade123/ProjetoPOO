package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.pooprojeto.dao.UsuarioDAO; // Pode ser removido se AuthService for suficiente
import org.example.pooprojeto.model.Usuario;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.AppException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Stage primaryStage;
    private AuthService authService;
    // Removendo UsuarioDAO daqui se ele não for usado diretamente,
    // já que o AuthService encapsula as operações de DAO.
    // private UsuarioDAO usuarioDAO;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    // Método setUsuarioDAO pode ser removido se o AuthService já estiver lidando com isso
    /*
    public void setUsuarioDAO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }
    */

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Erro de Login", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            Usuario usuarioLogado = authService.login(username, password);
            showAlert(AlertType.INFORMATION, "Login Bem-sucedido", "Bem-vindo(a), " + usuarioLogado.getNome() + "!");

            loadProdutosView(event, usuarioLogado); // Passe o usuário logado

        } catch (AppException e) {
            showAlert(AlertType.ERROR, "Falha no Login", e.getMessage());
        }
    }

    @FXML
    private void handleCreateAccountButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/CadastroView.fxml"));
            Parent root = loader.load();

            CadastroController cadastroController = loader.getController();
            cadastroController.setAuthService(authService);
            // Passa o primaryStage para o controlador de cadastro para que ele possa voltar
            cadastroController.setPrimaryStage((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow());

            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Cadastro de Usuário");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de cadastro.");
        }
    }

    private void loadProdutosView(ActionEvent event, Usuario usuarioLogado) { // Recebe usuarioLogado
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/ProdutosView.fxml"));
            Parent root = loader.load();

            // Certifique-se de que ProdutosController tem um método setUsuarioLogado
            ProdutosController produtosController = loader.getController();
            produtosController.setUsuarioLogado(usuarioLogado); // Passe o usuário logado

            Stage currentStage = (primaryStage != null) ? primaryStage : (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Gerenciamento de Produtos");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela principal.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}