package org.example.pooprojeto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
// <<< MUDANÇA: Imports adicionados para os novos controladores e serviços
import org.example.pooprojeto.controller.AdminController;
import org.example.pooprojeto.controller.CadastroController;
import org.example.pooprojeto.controller.LoginController;
import org.example.pooprojeto.controller.ProdutosController;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.dao.UsuarioDAO;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.service.AuthService;

import java.io.IOException;

public class NavigationManager {

    private static NavigationManager instance;
    private Stage primaryStage;
    private Usuario usuarioLogado;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Lógica para injetar dependências comuns nos controladores
            Object controller = loader.getController();

            if (controller instanceof AdminController) {
                ((AdminController) controller).setAdminLogado(usuarioLogado);
                ((AdminController) controller).setProdutoDAO(new ProdutoDAO());
            } else if (controller instanceof ProdutosController) {
                ((ProdutosController) controller).setUsuarioLogado(usuarioLogado);
                ((ProdutosController) controller).setProdutoDAO(new ProdutoDAO());
            } else if (controller instanceof LoginController) {
                // <<< MUDANÇA: Bloco adicionado para configurar a tela de Login
                // Isso resolve o NullPointerException no AuthService.
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                AuthService authService = new AuthService(usuarioDAO);
                ((LoginController) controller).setAuthService(authService);
            } else if (controller instanceof CadastroController) {
                // <<< MUDANÇA: Bloco adicionado para configurar a tela de Cadastro
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                AuthService authService = new AuthService(usuarioDAO);
                ((CadastroController) controller).setAuthService(authService);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Adicione um alerta de erro aqui
        }
    }

    // --- Métodos de atalho ---
    public void navigateToLogin() {
        navigateTo("/org/example/pooprojeto/view/LoginView.fxml", "Sistema de Gerenciamento - Login");
    }

    public void navigateToAdminView() {
        navigateTo("/org/example/pooprojeto/view/AdminView.fxml", "Administração - Loja Virtual");
    }

    public void navigateToProductsView() {
        navigateTo("/org/example/pooprojeto/view/ProdutosView.fxml", "Nossa Loja");
    }

    public void navigateToCart() {
        navigateTo("/org/example/pooprojeto/view/CarrinhoView.fxml", "Meu Carrinho de Compras");
    }

    /**
     * <<< MUDANÇA: Novo método de atalho adicionado para a tela de Cadastro.
     */
    public void navigateToCadastro() {
        navigateTo("/org/example/pooprojeto/view/CadastroView.fxml", "Cadastro de Novo Usuário");
    }
}