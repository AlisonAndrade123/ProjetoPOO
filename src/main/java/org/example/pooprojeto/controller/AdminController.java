package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Window;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.util.CategoriasUtil;
import org.example.pooprojeto.util.NavigationManager;

import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML
    private TextField searchTextField;
    @FXML
    private Button addProductButton;
    @FXML
    private HBox categoryHBox;
    @FXML
    private TilePane productTilePane;

    private Usuario adminLogado;
    private ProdutoDAO produtoDAO;

    @FXML
    public void initialize() {
        this.produtoDAO = new ProdutoDAO();
        this.adminLogado = AuthService.getInstance().getUsuarioLogado();

        if (adminLogado == null || !adminLogado.isAdmin()) {
            // Navega para o login se o acesso for indevido
            NavigationManager.getInstance().navigateToLogin();
            return;
        }

        searchTextField.textProperty().addListener((obs, oldText, newText) -> filterProducts(newText));
        criarBotoesDeCategoria();
        loadAllProducts();
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        Window ownerWindow = productTilePane.getScene().getWindow();
        Object controller = NavigationManager.getInstance().setupModal(
                "/org/example/pooprojeto/view/CadastrarProdutoView.fxml",
                "Cadastro de Novo Produto",
                ownerWindow
        );

        if (controller instanceof CadastrarProdutoController) {
            ((CadastrarProdutoController) controller).getStage().showAndWait();
        }
        loadAllProducts();
    }

    private void handleEditProduct(Produto produto) {
        Window ownerWindow = productTilePane.getScene().getWindow();
        if (ownerWindow == null) return;

        Object controllerObj = NavigationManager.getInstance().setupModal(
                "/org/example/pooprojeto/view/CadastrarProdutoView.fxml",
                "Editar Produto",
                ownerWindow
        );

        if (controllerObj instanceof CadastrarProdutoController controller) {
            controller.carregarDadosParaEdicao(produto);
            controller.getStage().showAndWait();
        }
        loadAllProducts();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(productTilePane.getScene().getWindow());
        alert.showAndWait();
    }

    private void criarBotoesDeCategoria() {
        categoryHBox.getChildren().clear();
        Button todosButton = criarBotaoEstilizado("Todos");
        categoryHBox.getChildren().add(todosButton);
        List<String> categorias = CategoriasUtil.getCategorias();
        for (String nomeCategoria : categorias) {
            Button categoriaButton = criarBotaoEstilizado(nomeCategoria);
            categoryHBox.getChildren().add(categoriaButton);
        }
    }

    private Button criarBotaoEstilizado(String nome) {
        Button button = new Button(nome);
        button.setUserData(nome);
        button.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #00A60E; -fx-border-radius: 20; -fx-border-width: 2; -fx-text-fill: #333333; -fx-padding: 8 20; -fx-cursor: hand;");
        button.setFont(new Font("System", 16.0));
        button.setOnAction(this::handleCategoryFilter);
        return button;
    }

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = (String) clickedButton.getUserData();
        if ("Todos".equals(category)) {
            loadAllProducts();
        } else {
            filterProductsByCategory(category);
        }
    }

    private void handleRemoveProduct(Produto produto) {
        try {
            if (produtoDAO.delete(produto.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Remover Produto", "Produto '" + produto.getNome() + "' removido com sucesso!");
                loadAllProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Remover Produto", "Falha ao remover o produto.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao acessar o banco de dados.");
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Produto produto) {
        VBox card = new VBox(10);
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setPrefHeight(400.0);
        card.setStyle("-fx-background-color: white; -fx-border-color: #00A60E; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 15;");
        ImageView imageView = new ImageView(produto.getImage());
        imageView.setFitHeight(150.0);
        imageView.setFitWidth(180.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        Label nameLabel = new Label(produto.getNome());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5px 0;");
        nameLabel.setWrapText(true);
        Label descriptionLabel = new Label(produto.getDescricao());
        descriptionLabel.setStyle("-fx-font-size: 13px; -fx-padding: 5px 0; -fx-text-fill: #555555;");
        descriptionLabel.setWrapText(true);
        Label priceLabel = new Label("R$ " + String.format("%.2f", produto.getPreco()).replace('.', ','));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00A60E; -fx-padding: 5px 0 15px 0;");
        HBox actionButtons = new HBox(5);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        Button removeButton = new Button("Remover");
        removeButton.setStyle("-fx-background-color: #F43C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-padding: 6px 12px; -fx-cursor: hand;");
        removeButton.setOnAction(e -> handleRemoveProduct(produto));
        Button editButton = new Button("Editar");
        editButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-padding: 6px 12px; -fx-cursor: hand;");
        editButton.setOnAction(e -> handleEditProduct(produto));
        actionButtons.getChildren().addAll(removeButton, editButton);
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, actionButtons);
        return card;
    }

    private void loadAllProducts() {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.findAll();
                displayProducts(produtos);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao carregar produtos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void filterProducts(String searchText) {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.search(searchText);
                displayProducts(produtos);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao pesquisar produtos.");
                e.printStackTrace();
            }
        }
    }

    private void filterProductsByCategory(String category) {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.findByCategory(category);
                displayProducts(produtos);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao filtrar produtos.");
                e.printStackTrace();
            }
        }
    }

    private void displayProducts(List<Produto> produtos) {
        productTilePane.getChildren().clear();
        if (produtos != null && !produtos.isEmpty()) {
            for (Produto p : produtos) {
                VBox productCard = createProductCard(p);
                productTilePane.getChildren().add(productCard);
            }
        } else {
            Label noProductsLabel = new Label("Nenhum produto encontrado.");
            noProductsLabel.setFont(new Font(20));
            productTilePane.getChildren().add(noProductsLabel);
        }
    }
}