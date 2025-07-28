package org.example.pooprojeto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pooprojeto.controller.*; // Importa todos os controladores do pacote
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

    /**
     * <<< MUDANÇA 1: O método agora retorna o controlador da cena carregada.
     * Isso permite que a tela anterior passe dados para a nova tela.
     */
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
            } else if (controller instanceof LoginController) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                AuthService authService = new AuthService(usuarioDAO);
                ((LoginController) controller).setAuthService(authService);
            } else if (controller instanceof CadastroController) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                AuthService authService = new AuthService(usuarioDAO);
                ((CadastroController) controller).setAuthService(authService);
            } else if (controller instanceof CarrinhoController) {
                // Exemplo de como você poderia configurar o CarrinhoController
                ((CarrinhoController) controller).setUsuarioLogado(usuarioLogado);
            }
            // O PagamentoController não precisa de injeção aqui, pois é configurado
            // pela tela que o chama (CarrinhoController).

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();

            return controller; // <<< Retorna o controlador criado

        } catch (IOException e) {
            e.printStackTrace();
            return null; // Retorna nulo em caso de erro
        }
    }

    // O método setupModal continua o mesmo, está perfeito para sua finalidade.
    public Object setupModal(String fxmlPath, String title, Window ownerWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("ERRO CRÍTICO! -> FXML NÃO ENCONTRADO EM: " + fxmlPath);
                return null;
            }
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller == null) {
                System.err.println("ERRO CRÍTICO! -> O controlador do FXML é NULO.");
                return null;
            }
            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);
            modalStage.initOwner(ownerWindow);
            modalStage.initModality(Modality.APPLICATION_MODAL);
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


    // --- Métodos de atalho (precisam ser atualizados para não quebrar o código existente) ---
    // Mantemos as versões 'void' para compatibilidade com o código que não precisa do retorno.
    public void navigateToLogin() { navigateTo("/org/example/pooprojeto/view/LoginView.fxml", "Sistema de Gerenciamento - Login"); }
    public void navigateToAdminView() { navigateTo("/org/example/pooprojeto/view/AdminView.fxml", "Administração - Loja Virtual"); }
    public void navigateToProductsView() { navigateTo("/org/example/pooprojeto/view/ProdutosView.fxml", "Nossa Loja"); }
    public void navigateToCart() { navigateTo("/org/example/pooprojeto/view/CarrinhoView.fxml", "Meu Carrinho de Compras"); }
    public void navigateToCadastro() { navigateTo("/org/example/pooprojeto/view/CadastroView.fxml", "Cadastro de Novo Usuário"); }

    /**
     * <<< NOVO MÉTODO DE ATALHO PARA A TELA DE PAGAMENTO
     * Este método usa o navigateTo e retorna o controlador, como precisamos.
     */
    public PagamentoController navigateToPagamento() {
        Object controller = navigateTo("/org/example/pooprojeto/view/PagamentoView.fxml", "Finalizar Pagamento");
        if (controller instanceof PagamentoController) {
            return (PagamentoController) controller;
        }
        return null;
    }
}