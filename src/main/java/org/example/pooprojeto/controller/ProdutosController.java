package org.example.pooprojeto.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.CategoriasUtil;

import java.sql.SQLException;
import java.util.List;

public class ProdutosController {

    // ... (outros campos e métodos que já estão corretos) ...
    @FXML private TextField searchTextField;
    @FXML private ScrollPane categoryScrollPane;
    @FXML private HBox categoryHBox;
    @FXML private TilePane productTilePane;
    @FXML private Button profileButton;
    @FXML private Button cartButton;
    private Usuario usuarioLogado;
    private ProdutoDAO produtoDAO;
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }
    public void setUsuarioLogado(Usuario usuario) { this.usuarioLogado = usuario; }
    public void setProdutoDAO(ProdutoDAO produtoDAO) { this.produtoDAO = produtoDAO; Platform.runLater(this::loadAllProducts); }
    @FXML public void initialize() { if (searchTextField != null) { searchTextField.textProperty().addListener((obs, oldText, newText) -> filterProducts(newText)); } criarBotoesDeCategoria(); }
    private void criarBotoesDeCategoria() { categoryHBox.getChildren().clear(); Button todosButton = criarBotaoEstilizado("Todos"); categoryHBox.getChildren().add(todosButton); List<String> categorias = CategoriasUtil.getCategorias(); for (String nomeCategoria : categorias) { Button categoriaButton = criarBotaoEstilizado(nomeCategoria); categoryHBox.getChildren().add(categoriaButton); } }
    private Button criarBotaoEstilizado(String nome) { Button button = new Button(nome); button.setUserData(nome); button.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #00A60E; -fx-border-radius: 20; -fx-border-width: 2; -fx-text-fill: #333333; -fx-padding: 8 20; -fx-cursor: hand;"); button.setFont(new Font("System", 16.0)); button.setOnAction(this::handleCategoryButtonAction); return button; }


    /**
     * <<< ESTE É O MÉTODO QUE GARANTE A FORMATAÇÃO CORRETA
     * Confirme que o seu método está exatamente igual a este.
     */
    // Em ProdutosController.java

    private VBox createProductCard(Produto produto) {
        // ESTE CÓDIGO AGORA É IDÊNTICO AO DO ADMINCONTROLLER
        VBox card = new VBox(10);
        // Esta linha centraliza todos os itens no card
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setPrefHeight(400.0);
        card.setStyle("-fx-background-color: white; -fx-border-color: #00A60E; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 15;");

        ImageView imageView = new ImageView(produto.getImage());
        imageView.setFitHeight(150.0);
        imageView.setFitWidth(180.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        // NOME DO PRODUTO (Estilo exato da tela de Admin)
        Label nameLabel = new Label(produto.getNome());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5px 0;");
        nameLabel.setWrapText(true);

        // DESCRIÇÃO DO PRODUTO (Estilo exato da tela de Admin)
        Label descriptionLabel = new Label(produto.getDescricao());
        descriptionLabel.setStyle("-fx-font-size: 13px; -fx-padding: 5px 0; -fx-text-fill: #555555;");
        descriptionLabel.setWrapText(true);

        // PREÇO DO PRODUTO
        Label priceLabel = new Label("R$ " + String.format("%.2f", produto.getPreco()).replace('.', ','));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00A60E; -fx-padding: 5px 0 15px 0;");

        // BOTÃO DE COMPRA (Única diferença funcional)
        Button buyButton = new Button("Comprar");
        buyButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-padding: 8px 16px; -fx-cursor: hand;");
        buyButton.setOnAction(event -> handleComprarProduto(produto));

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, buyButton);
        return card;
    }

    // ... (resto da classe que já está correto) ...
    @FXML private void handleProfileButtonAction(ActionEvent event) { showAlert(AlertType.INFORMATION, "Funcionalidade", "Funcionalidade de Perfil ainda não implementada."); }
    @FXML private void handleCartButtonAction(ActionEvent event) { showAlert(AlertType.INFORMATION, "Funcionalidade", "Funcionalidade de Carrinho ainda não implementada."); }
    @FXML private void handleCategoryButtonAction(ActionEvent event) { Button clickedButton = (Button) event.getSource(); String category = (String) clickedButton.getUserData(); if ("Todos".equals(category)) { loadAllProducts(); } else { filterProductsByCategory(category); } }
    private void loadAllProducts() { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.findAll(); displayProducts(produtos); } catch (SQLException e) { showAlert(AlertType.ERROR, "Erro de Banco de Dados", "Erro ao carregar produtos: " + e.getMessage()); e.printStackTrace(); } } }
    private void filterProducts(String searchText) { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.search(searchText); displayProducts(produtos); } catch (SQLException e) { showAlert(AlertType.ERROR, "Erro de Banco de Dados", "Erro ao pesquisar produtos: " + e.getMessage()); e.printStackTrace(); } } }
    private void filterProductsByCategory(String category) { if (produtoDAO != null) { try { List<Produto> produtos = produtoDAO.findByCategory(category); displayProducts(produtos); } catch (SQLException e) { showAlert(AlertType.ERROR, "Erro de Banco de Dados", "Erro ao filtrar produtos por categoria: " + e.getMessage()); e.printStackTrace(); } } }
    private void displayProducts(List<Produto> produtos) { productTilePane.getChildren().clear(); if (produtos != null && !produtos.isEmpty()) { for (Produto p : produtos) { VBox productCard = createProductCard(p); productTilePane.getChildren().add(productCard); } } else { Label noProductsLabel = new Label("Nenhum produto encontrado."); noProductsLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #555555;"); productTilePane.getChildren().add(noProductsLabel); } }
    private void handleComprarProduto(Produto produto) { System.out.println("Produto '" + produto.getNome() + "' adicionado ao carrinho ou comprado!"); showAlert(AlertType.INFORMATION, "Compra Realizada", "Você adicionou '" + produto.getNome() + "' ao carrinho!"); }
    private void showAlert(AlertType alertType, String title, String message) { Alert alert = new Alert(alertType); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait(); }
}