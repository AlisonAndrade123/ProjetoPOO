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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.CategoriasUtil; // MUDANÇA: Adicionada importação da nova classe

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML private TextField searchTextField;
    @FXML private Button addProductButton;
    @FXML private ScrollPane categoryScrollPane;
    @FXML private HBox categoryHBox;
    @FXML private TilePane productTilePane;

    private Usuario adminLogado;
    private ProdutoDAO produtoDAO;

    public void setAdminLogado(Usuario admin) {
        this.adminLogado = admin;
        System.out.println("Administrador logado na AdminView: " + admin.getNome());
    }

    public void setProdutoDAO(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
        Platform.runLater(this::loadAllProducts);
    }

    @FXML
    public void initialize() {
        searchTextField.textProperty().addListener((obs, oldText, newText) -> {
            filterProducts(newText);
        });
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/pooprojeto/view/CadastrarProdutoView.fxml"));
            Parent root = loader.load();

            CadastrarProdutoController cadastrarProdutoController = loader.getController();

            // MUDANÇA: Passa a lista de categorias para o controller da tela de cadastro
            cadastrarProdutoController.setCategorias(CategoriasUtil.getCategorias());

            Stage cadastroStage = new Stage();
            cadastroStage.setTitle("Cadastro de Novo Produto");
            cadastroStage.setScene(new Scene(root));
            cadastroStage.setResizable(false);

            Window ownerWindow = addProductButton.getScene().getWindow();
            cadastroStage.initOwner(ownerWindow);
            cadastroStage.initModality(Modality.APPLICATION_MODAL);

            // Injeta as outras dependências
            cadastrarProdutoController.setProdutoDAO(this.produtoDAO);
            cadastrarProdutoController.setStage(cadastroStage);

            cadastroStage.showAndWait();

            loadAllProducts();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Carregamento", "Falha ao carregar a tela de cadastro. Verifique o console para mais detalhes.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Arquivo Não Encontrado", "Não foi possível encontrar o arquivo FXML. Verifique o caminho e o nome.");
        }
    }

    // ... O resto da classe permanece exatamente igual
    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = (String) clickedButton.getUserData();
        System.out.println("Categoria selecionada: " + category);
        if ("Todos".equals(category)) {
            loadAllProducts();
        } else {
            filterProductsByCategory(category);
        }
    }

    private void handleRemoveProduct(Produto produto) {
        System.out.println("Remover produto: " + produto.getNome());
        try {
            if (produtoDAO.delete(produto.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Remover Produto", "Produto '" + produto.getNome() + "' removido com sucesso!");
                loadAllProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Remover Produto", "Falha ao remover o produto '" + produto.getNome() + "'.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao acessar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditProduct(Produto produto) {
        System.out.println("Editar produto: " + produto.getNome());
        showAlert(Alert.AlertType.INFORMATION, "Editar Produto", "Funcionalidade de editar produto para '" + produto.getNome() + "' será implementada.");
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
        } else {
            System.err.println("ProdutoDAO não foi injetado no AdminController.");
        }
    }

    private void filterProducts(String searchText) {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.search(searchText);
                displayProducts(produtos);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao pesquisar produtos: " + e.getMessage());
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
                showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao filtrar produtos por categoria: " + e.getMessage());
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

        if (produto.getImageUrl() != null && !produto.getImageUrl().isEmpty()) {
            try (InputStream imageStream = getClass().getResourceAsStream("/org/example/pooprojeto/Imagens/" + produto.getImageUrl())) {
                if (imageStream != null) {
                    imageView.setImage(new Image(imageStream));
                } else {
                    System.err.println("Arquivo de imagem não encontrado no caminho: /org/example/pooprojeto/Imagens/" + produto.getImageUrl());
                }
            } catch (Exception e) {
                System.err.println("Falha ao processar a imagem: " + produto.getImageUrl());
            }
        }

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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(addProductButton.getScene().getWindow());
        alert.showAndWait();
    }
}