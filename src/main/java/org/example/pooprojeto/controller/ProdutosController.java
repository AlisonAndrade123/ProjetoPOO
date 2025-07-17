package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent; // Importe ActionEvent

// Adicione estes imports se for navegar para outras telas
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button; // <--- Importe Button

public class ProdutosController {

    @FXML
    private TextField searchTextField;
    @FXML
    private ScrollPane categoryScrollPane;
    @FXML
    private HBox categoryHBox;
    @FXML
    private TilePane productTilePane;
    @FXML
    private Button profileButton; // Precisa ser um campo @FXML se você usar fx:id no FXML
    @FXML
    private Button cartButton; // Precisa ser um campo @FXML se você usar fx:id no FXML


    private Usuario usuarioLogado;
    private ProdutoDAO produtoDAO;
    private Stage primaryStage; // Adicione para navegação de volta ou para novas telas

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        System.out.println("Usuário logado na ProdutosView: " + usuario.getNome());
    }

    public void setProdutoDAO(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
        Platform.runLater(this::loadAllProducts);
    }

    @FXML
    public void initialize() {
        if (searchTextField != null) {
            searchTextField.textProperty().addListener((obs, oldText, newText) -> {
                filterProducts(newText);
            });
        }
        // Você pode carregar as categorias dinamicamente aqui se tiver um DAO de Categoria
        // loadCategories(); // Exemplo
    }

    // ==== MÉTODOS ADICIONADOS/CORRIGIDOS PARA RESOLVER O ERRO ====

    @FXML
    private void handleProfileButtonAction(ActionEvent event) {
        System.out.println("Botão de Perfil clicado!");
        // Exemplo: Navegar para a tela de perfil
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/PerfilView.fxml"));
            Parent root = loader.load();

            PerfilController perfilController = loader.getController();
            perfilController.setUsuarioLogado(usuarioLogado); // Passe o usuário logado

            // Se você injetar o primaryStage no ProdutosController, pode usá-lo aqui:
            // Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            // stage.setScene(new Scene(root));
            // stage.setTitle("Meu Perfil");
            // stage.show();

            // Ou, se o primaryStage for injetado no ProdutosController:
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Meu Perfil");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de perfil.");
        }
        */
        showAlert(AlertType.INFORMATION, "Funcionalidade", "Funcionalidade de Perfil ainda não implementada.");
    }

    @FXML
    private void handleCartButtonAction(ActionEvent event) {
        System.out.println("Botão de Carrinho clicado!");
        // Exemplo: Navegar para a tela do carrinho
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/CarrinhoView.fxml"));
            Parent root = loader.load();

            CarrinhoController carrinhoController = loader.getController();
            carrinhoController.setUsuarioLogado(usuarioLogado); // Passe o usuário logado

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Meu Carrinho");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela do carrinho.");
        }
        */
        showAlert(AlertType.INFORMATION, "Funcionalidade", "Funcionalidade de Carrinho ainda não implementada.");
    }

    @FXML
    private void handleCategoryButtonAction(ActionEvent event) {
        // Este método é chamado quando qualquer botão de categoria é clicado.
        // O `userData` do botão contém o nome da categoria.
        Button clickedButton = (Button) event.getSource();
        String category = (String) clickedButton.getUserData();
        System.out.println("Categoria clicada: " + category);

        if ("Todos".equals(category)) {
            loadAllProducts();
        } else {
            filterProductsByCategory(category);
        }
    }

    // ==== Lógica de Carregamento e Exibição de Produtos ====

    private void loadAllProducts() {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.findAll();
                displayProducts(produtos);
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erro de Banco de Dados", "Erro ao carregar produtos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("ProdutoDAO não foi injetado no ProdutosController.");
        }
    }

    private void filterProducts(String searchText) {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.search(searchText);
                displayProducts(produtos);
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erro de Banco de Dados", "Erro ao pesquisar produtos: " + e.getMessage());
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
                showAlert(AlertType.ERROR, "Erro de Banco de Dados", "Erro ao filtrar produtos por categoria: " + e.getMessage());
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
            noProductsLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #555555;");
            productTilePane.getChildren().add(noProductsLabel);
        }
    }

    private VBox createProductCard(Produto produto) {
        VBox card = new VBox(10);
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setPrefHeight(400.0);
        card.setStyle("-fx-background-color: white; -fx-border-color: #00A60E; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 15;");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150.0);
        imageView.setFitWidth(180.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        try {
            Image image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/images/" + produto.getImageUrl()));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem para o produto " + produto.getNome() + ": " + produto.getImageUrl() + ". Erro: " + e.getMessage());
            // Opcional: define uma imagem placeholder se a original falhar
            // try {
            //     imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pooprojeto/images/placeholder.png")));
            // } catch (Exception ex) {
            //     System.err.println("Não foi possível carregar a imagem placeholder. Erro: " + ex.getMessage());
            // }
        }

        Label nameLabel = new Label(produto.getNome());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5px 0;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        Label descriptionLabel = new Label(produto.getDescricao());
        descriptionLabel.setStyle("-fx-font-size: 13px; -fx-padding: 5px 0; -fx-text-fill: #555555;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);

        Label priceLabel = new Label("R$ " + String.format("%.2f", produto.getPreco()).replace('.', ','));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00A60E; -fx-padding: 5px 0 15px 0;");

        // --- Adição do botão "Comprar" ---
        Button buyButton = new Button("Comprar");
        buyButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-padding: 8px 16px; -fx-cursor: hand;");
        buyButton.setOnAction(event -> handleComprarProduto(produto)); // Adiciona o evento de compra

        // Adiciona todos os elementos ao card
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel, buyButton); // <--- Adicionado buyButton
        return card;
    }

    // --- Novo método para lidar com a ação de compra ---
    private void handleComprarProduto(Produto produto) {
        System.out.println("Produto '" + produto.getNome() + "' adicionado ao carrinho ou comprado!");
        showAlert(AlertType.INFORMATION, "Compra Realizada", "Você adicionou '" + produto.getNome() + "' ao carrinho!");
        // Aqui você implementaria a lógica real:
        // 1. Adicionar o produto a um carrinho (uma lista de produtos no controlador ou em um serviço de carrinho).
        // 2. Se for uma compra direta, iniciar o processo de checkout.
        // Exemplo: carrinho.adicionarItem(produto);
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}