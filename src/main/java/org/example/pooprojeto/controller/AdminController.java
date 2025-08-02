package org.example.pooprojeto.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.CategoriasUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML private TextField searchTextField;
    @FXML private Button addProductButton;
    @FXML private HBox categoryHBox;
    @FXML private TilePane productTilePane;

    private Usuario adminLogado;
    private ProdutoDAO produtoDAO;

    public void setAdminLogado(Usuario admin) { this.adminLogado = admin; }
    public void setProdutoDAO(ProdutoDAO produtoDAO) { this.produtoDAO = produtoDAO; Platform.runLater(this::loadAllProducts); }

    @FXML
    public void initialize() {
        searchTextField.textProperty().addListener((obs, oldText, newText) -> filterProducts(newText));
        criarBotoesDeCategoria();
    }

    private void openModal(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/CadastrarProdutoView.fxml"));
            Parent root = loader.load();
            CadastrarProdutoController controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);
            Window ownerWindow = addProductButton.getScene().getWindow();
            modalStage.initOwner(ownerWindow);
            modalStage.initModality(Modality.APPLICATION_MODAL);

            controller.setProdutoDAO(new ProdutoDAO());
            controller.setCategorias(CategoriasUtil.getCategorias());
            controller.setStage(modalStage);

            if (produto == null) {
                modalStage.setTitle("Cadastro de Novo Produto");
            } else {
                modalStage.setTitle("Editar Produto");
                controller.carregarDadosParaEdicao(produto);
            }

            modalStage.showAndWait();
            loadAllProducts();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar a tela.");
        }
    }

    @FXML private void handleAddProduct(ActionEvent event) { openModal(null); }
    private void handleEditProduct(Produto produto) { openModal(produto); }

    private void handleRemoveProduct(Produto produto) {
        try {
            if (produtoDAO.delete(produto.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto removido com sucesso!");
                loadAllProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao remover o produto.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (addProductButton != null && addProductButton.getScene() != null) {
            alert.initOwner(addProductButton.getScene().getWindow());
        }
        alert.showAndWait();
    }

    // --- MÉTODOS AUXILIARES COM O CORPO RESTAURADO ---
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

    private VBox createProductCard(Produto produto) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
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
        actionButtons.setAlignment(Pos.CENTER);
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

    private void loadAllProducts() { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.findAll(); displayProducts(produtos); } catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage()); e.printStackTrace(); } } }
    private void filterProducts(String searchText) { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.search(searchText); displayProducts(produtos); } catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage()); e.printStackTrace(); } } }
    private void filterProductsByCategory(String category) { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.findByCategory(category); displayProducts(produtos); } catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage()); e.printStackTrace(); } } }

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