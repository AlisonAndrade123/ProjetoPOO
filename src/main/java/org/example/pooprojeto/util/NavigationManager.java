package org.example.pooprojeto.util;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pooprojeto.controller.CadastrarProdutoController;
import org.example.pooprojeto.controller.PagamentoController;
import org.example.pooprojeto.dao.ProdutoDAO;
import java.io.IOException;
public class NavigationManager {
    private static NavigationManager instance;
    private Stage primaryStage;

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

    public Object navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();

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
     * CORREÇÃO: Este método agora se chama 'setupModal' e apenas PREPARA o modal.
     * Ele não o exibe mais com showAndWait().
     * Ele retorna o CONTROLLER para que quem o chamou possa interagir com ele.
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

            // A lógica de configuração continua aqui, e é crucial.
            if (controller instanceof CadastrarProdutoController) {
                ((CadastrarProdutoController) controller).setStage(modalStage); // Passa a janela para o controller poder se fechar.
                ((CadastrarProdutoController) controller).setProdutoDAO(new ProdutoDAO());
                ((CadastrarProdutoController) controller).setCategorias(CategoriasUtil.getCategorias());
            }

            return controller; // Retorna o controller JÁ CONFIGURADO.
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // O resto dos métodos de navegação permanece o mesmo
    public void navigateToLogin() {
        navigateTo("/org/example/pooprojeto/view/LoginView.fxml", "Sistema de Gerenciamento - Login");
    }

    public void navigateToAdminView() {
        navigateTo("/org/example/pooprojeto/view/AdminView.fxml", "Administração - Loja Virtual");
    }

// ... todos os seus outros métodos navigateTo ...

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
        return (controller instanceof PagamentoController) ? (PagamentoController) controller : null;
    }

    public void navigateToHistory() {
        navigateTo("/org/example/pooprojeto/view/HistoricoView.fxml", "Meu Histórico de Compras");
    }
}