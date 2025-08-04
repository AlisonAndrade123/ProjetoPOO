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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof AdminController) {
                ((AdminController) controller).setAdminLogado(usuarioLogado);
                ((AdminController) controller).setProdutoDAO(new ProdutoDAO());
            } else if (controller instanceof ProdutosController) {
                ((ProdutosController) controller).setUsuarioLogado(usuarioLogado);
                ((ProdutosController) controller).setProdutoDAO(new ProdutoDAO());
                ((ProdutosController) controller).setPrimaryStage(this.primaryStage);
            } else if (controller instanceof LoginController) {
                ((LoginController) controller).setAuthService(new AuthService(new UsuarioDAO()));
            } else if (controller instanceof CadastroController) {
                ((CadastroController) controller).setAuthService(new AuthService(new UsuarioDAO()));
            } else if (controller instanceof CarrinhoController) {
                ((CarrinhoController) controller).setUsuarioLogado(usuarioLogado);
                ((CarrinhoController) controller).setPrimaryStage(this.primaryStage);
            } else if (controller instanceof HistoricoController) {
                ((HistoricoController) controller).setUsuarioLogado(usuarioLogado);
                ((HistoricoController) controller).setPrimaryStage(this.primaryStage);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToLogin() { navigateTo("/org/example/pooprojeto/view/LoginView.fxml", "Login"); }
    public void navigateToAdminView() { navigateTo("/org/example/pooprojeto/view/AdminView.fxml", "Administração"); }
    public void navigateToProductsView() { navigateTo("/org/example/pooprojeto/view/ProdutosView.fxml", "Nossa Loja"); }
    public void navigateToCart() { navigateTo("/org/example/pooprojeto/view/CarrinhoView.fxml", "Carrinho"); }
    public void navigateToCadastro() { navigateTo("/org/example/pooprojeto/view/CadastroView.fxml", "Cadastro"); }
    public void navigateToHistory() { navigateTo("/org/example/pooprojeto/view/HistoricoView.fxml", "Histórico de Compras"); }
    public void navigateToPagamento() { navigateTo("/org/example/pooprojeto/view/PagamentoView.fxml", "Pagamento"); }
}