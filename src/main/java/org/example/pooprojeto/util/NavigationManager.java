package org.example.pooprojeto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pooprojeto.controller.*;
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

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }
    public void setUsuarioLogado(Usuario usuario) { this.usuarioLogado = usuario; }

    public Object navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();

            // Lógica de injeção de dependência para as telas principais
            if (controller instanceof AdminController) {
                ((AdminController) controller).setAdminLogado(usuarioLogado);
                ((AdminController) controller).setProdutoDAO(new ProdutoDAO());
            } else if (controller instanceof ProdutosController) {
                ((ProdutosController) controller).setUsuarioLogado(usuarioLogado);
                ((ProdutosController) controller).setProdutoDAO(new ProdutoDAO());
                ((ProdutosController) controller).setPrimaryStage(this.primaryStage);
            } else if (controller instanceof LoginController) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                AuthService authService = new AuthService(usuarioDAO);
                ((LoginController) controller).setAuthService(authService);
            } else if (controller instanceof CadastroController) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                AuthService authService = new AuthService(usuarioDAO);
                ((CadastroController) controller).setAuthService(authService);
            } else if (controller instanceof CarrinhoController) {
                ((CarrinhoController) controller).setUsuarioLogado(usuarioLogado);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // <<< MUDANÇA: O método setupModal foi REMOVIDO >>>
    // A lógica de criar o modal de "Adicionar/Editar Produto" agora é uma
    // responsabilidade exclusiva do AdminController, para evitar o bug de renderização.

    // --- Métodos de atalho ---
    public void navigateToLogin() { navigateTo("/org/example/pooprojeto/view/LoginView.fxml", "Sistema de Gerenciamento - Login"); }
    public void navigateToAdminView() { navigateTo("/org/example/pooprojeto/view/AdminView.fxml", "Administração - Loja Virtual"); }
    public void navigateToProductsView() { navigateTo("/org/example/pooprojeto/view/ProdutosView.fxml", "Nossa Loja"); }
    public void navigateToCart() { navigateTo("/org/example/pooprojeto/view/CarrinhoView.fxml", "Meu Carrinho de Compras"); }
    public void navigateToCadastro() { navigateTo("/org/example/pooprojeto/view/CadastroView.fxml", "Cadastro de Novo Usuário"); }

    public PagamentoController navigateToPagamento() {
        Object controller = navigateTo("/org/example/pooprojeto/view/PagamentoView.fxml", "Finalizar Pagamento");
        return (controller instanceof PagamentoController) ? (PagamentoController) controller : null;
    }
}