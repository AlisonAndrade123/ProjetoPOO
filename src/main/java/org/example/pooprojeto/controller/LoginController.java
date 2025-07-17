package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.pooprojeto.model.Usuario;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.AppException;
import org.example.pooprojeto.dao.ProdutoDAO;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private Stage primaryStage;
    private AuthService authService;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

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
            Usuario usuarioLogado = authService.login(email, senha);
            showAlert(AlertType.INFORMATION, "Login Bem-sucedido", "Bem-vindo(a), " + usuarioLogado.getNome() + "!");

            // ADICIONADO PARA DEPURAR A PROPRIEDADE isAdmin
            System.out.println("DEBUG - Usuário '" + usuarioLogado.getEmail() + "' (ID: " + usuarioLogado.getId() + ") é Admin? " + usuarioLogado.isAdmin());

            if (usuarioLogado.isAdmin()) {
                loadAdminView(usuarioLogado);
            } else {
                loadProdutosView(usuarioLogado);
            }

        } catch (AppException e) {
            showAlert(AlertType.ERROR, "Falha no Login", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a próxima tela. Detalhes: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateAccountButtonAction(ActionEvent event) {
        try {
            String fxmlPath = "/org/example/pooprojeto/view/CadastroView.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            System.out.println("DEBUG - Tentando carregar CadastroView.fxml...");
            System.out.println("DEBUG - Caminho absoluto para CadastroView.fxml: " + fxmlUrl);

            if (fxmlUrl == null) {
                throw new IOException("Recurso FXML não encontrado: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            CadastroController cadastroController = loader.getController();
            cadastroController.setAuthService(authService);
            cadastroController.setPrimaryStage((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow());

            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Cadastro de Usuário");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de cadastro. Detalhes: " + e.getMessage());
        }
    }

    private void loadProdutosView(Usuario usuarioLogado) throws IOException {
        String fxmlPath = "/org/example/pooprojeto/view/ProdutosView.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        System.out.println("DEBUG - Tentando carregar ProdutosView.fxml...");
        System.out.println("DEBUG - Caminho absoluto para ProdutosView.fxml: " + fxmlUrl);

        if (fxmlUrl == null) {
            throw new IOException("Recurso FXML não encontrado: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        ProdutosController produtosController = loader.getController();
        produtosController.setUsuarioLogado(usuarioLogado);
        produtosController.setProdutoDAO(new ProdutoDAO());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Nossa Loja");
        primaryStage.show();
    }

    private void loadAdminView(Usuario adminLogado) throws IOException {
        String fxmlPath = "/org/example/pooprojeto/view/AdminView.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        System.out.println("DEBUG - Tentando carregar AdminView.fxml...");
        System.out.println("DEBUG - Caminho absoluto para AdminView.fxml: " + fxmlUrl);

        if (fxmlUrl == null) {
            throw new IOException("Recurso FXML não encontrado: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        AdminController adminController = loader.getController();
        adminController.setAdminLogado(adminLogado);
        adminController.setProdutoDAO(new ProdutoDAO());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Administração - Loja Virtual");
        primaryStage.show();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}