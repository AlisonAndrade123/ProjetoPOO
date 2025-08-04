// Local: src/main/java/org/example/pooprojeto/util/NavigationManager.java
package org.example.pooprojeto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pooprojeto.controller.CadastrarProdutoController;
import org.example.pooprojeto.controller.PagamentoController;
import org.example.pooprojeto.dao.ProdutoDAO; // MANTIDO - Ainda é útil para o modal

import java.io.IOException;

public class NavigationManager {

    private static NavigationManager instance;
    private Stage primaryStage;

    // REMOVIDO: O NavigationManager não precisa mais saber quem está logado.
    // private Usuario usuarioLogado;

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

    // REMOVIDO: Não precisamos mais deste método.
    // public void setUsuarioLogado(Usuario usuario) { ... }


    /**
     * Navega para uma nova cena principal.
     * A lógica de injeção de dependência foi removida daqui e movida
     * para dentro dos próprios controllers, que agora usam o AuthService.
     */
    public Object navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();

            // --- LÓGICA DE INJEÇÃO REMOVIDA ---
            // O código que verificava "instanceof LoginController", "instanceof AdminController", etc.
            // foi completamente removido. Os controllers agora são independentes.

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

    /**
     * Configura um modal. Esta lógica pode ser mantida, pois é específica.
     */
    public Object setupModal(String fxmlPath, String title, Window ownerWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);
            modalStage.initOwner(ownerWindow);
            modalStage.initModality(Modality.APPLICATION_MODAL);

            // A injeção para o modal de cadastro de produto ainda é útil, podemos manter.
            if (controller instanceof CadastrarProdutoController) {
                ((CadastrarProdutoController) controller).setProdutoDAO(new ProdutoDAO());
                ((CadastrarProdutoController) controller).setCategorias(CategoriasUtil.getCategorias());
                ((CadastrarProdutoController) controller).setStage(modalStage);
            }
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Métodos de navegação (sem alterações)
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

    public void navigateToCadastro() {
        navigateTo("/org/example/pooprojeto/view/CadastroView.fxml", "Cadastro de Novo Usuário");
    }

    public PagamentoController navigateToPagamento() {
        Object controller = navigateTo("/org/example/pooprojeto/view/PagamentoView.fxml", "Finalizar Pagamento");
        if (controller instanceof PagamentoController) {
            return (PagamentoController) controller;
        }
        return null;
    }
}