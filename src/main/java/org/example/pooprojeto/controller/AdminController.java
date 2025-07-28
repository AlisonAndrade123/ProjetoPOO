package org.example.pooprojeto.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.CategoriasUtil;
import org.example.pooprojeto.util.ModalCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML private TextField searchTextField;
    @FXML private Button addProductButton;
    @FXML private HBox categoryHBox;
    @FXML private TilePane productTilePane;
    @FXML private Pane overlayPane;
    @FXML private StackPane modalPlaceholder;
    @FXML private VBox customAlertBox;
    @FXML private Label customAlertTitle;
    @FXML private Label customAlertMessage;

    private Usuario adminLogado;
    private ProdutoDAO produtoDAO;
    private Node editModalContent = null;

    public void setAdminLogado(Usuario admin) { this.adminLogado = admin; }
    public void setProdutoDAO(ProdutoDAO produtoDAO) { this.produtoDAO = produtoDAO; Platform.runLater(this::loadAllProducts); }

    @FXML
    public void initialize() {
        searchTextField.textProperty().addListener((obs, oldText, newText) -> filterProducts(newText));
        criarBotoesDeCategoria();
        hideCustomAlert();
    }

    @FXML private void handleAddProduct(ActionEvent event) { showEditModal(null); }
    private void handleEditProduct(Produto produto) { showEditModal(produto); }

    private void handleRemoveProduct(Produto produto) {
        try {
            if (produtoDAO.delete(produto.getId())) {
                showCustomAlert("Sucesso", "Produto '" + produto.getNome() + "' removido com sucesso!");
                loadAllProducts();
            } else {
                showCustomAlert("Erro", "Falha ao remover o produto.");
            }
        } catch (SQLException e) {
            showCustomAlert("Erro de Banco de Dados", "Erro ao acessar o banco de dados.");
            e.printStackTrace();
        }
    }

    private void showEditModal(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/CadastrarProdutoView.fxml"));
            Parent modalContent = loader.load();
            CadastrarProdutoController controller = loader.getController();
            controller.setProdutoDAO(this.produtoDAO);
            controller.setCategorias(CategoriasUtil.getCategorias());
            controller.setOnCloseCallback(() -> hideEditModal());
            if (produto != null) {
                controller.carregarDadosParaEdicao(produto);
            }
            this.editModalContent = modalContent;
            overlayPane.setVisible(true);
            modalPlaceholder.getChildren().add(modalContent);
            modalPlaceholder.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            showCustomAlert("Erro de Carregamento", "Falha ao carregar a tela de edição.");
        }
    }

    private void hideEditModal() {
        modalPlaceholder.getChildren().clear();
        modalPlaceholder.setVisible(false);
        overlayPane.setVisible(false);
        editModalContent = null;
        loadAllProducts();
    }

    @FXML private void handleCustomAlertOK(ActionEvent event) { hideCustomAlert(); }

    private void showCustomAlert(String title, String message) {
        customAlertTitle.setText(title);
        customAlertMessage.setText(message);
        overlayPane.setVisible(true);
        customAlertBox.setVisible(true);
    }

    private void hideCustomAlert() {
        overlayPane.setVisible(false);
        customAlertBox.setVisible(false);
    }

    // --- MÉTODOS AUXILIARES COM CORPO COMPLETO ---
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

    @FXML private void handleCategoryFilter(ActionEvent event) { Button clickedButton = (Button) event.getSource(); String category = (String) clickedButton.getUserData(); if ("Todos".equals(category)) { loadAllProducts(); } else { filterProductsByCategory(category); } }
    private void loadAllProducts() { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.findAll(); displayProducts(produtos); } catch (SQLException e) { showCustomAlert("Erro de Banco de Dados", "Erro ao carregar produtos."); e.printStackTrace(); } } }
    private void filterProducts(String searchText) { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.search(searchText); displayProducts(produtos); } catch (SQLException e) { showCustomAlert("Erro de Banco de Dados", "Erro ao pesquisar produtos."); e.printStackTrace(); } } }
    private void filterProductsByCategory(String category) { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.findByCategory(category); displayProducts(produtos); } catch (SQLException e) { showCustomAlert("Erro de Banco de Dados", "Erro ao filtrar produtos."); e.printStackTrace(); } } }

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