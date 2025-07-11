// src/main/java/org/example/pooprojeto/controller/ProdutosController.java
package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.example.pooprojeto.model.Usuario; // Certifique-se que o modelo Usuario está correto

import javafx.event.ActionEvent; // Importar para os métodos onAction

// Imports adicionais que você pode precisar dependendo da lógica (ex: ProdutoDAO, Produto, FXMLLoader para detalhes)
// import org.example.pooprojeto.dao.ProdutoDAO;
// import org.example.pooprojeto.model.Produto;


public class ProdutosController {

    // ==== Elementos FXML ====
    @FXML
    private Label welcomeLabel; // Este fx:id não está no FXML que você me enviou AGORA, mas estava no anterior.
    // Se você removeu, remova daqui também ou adicione um Label no FXML.

    @FXML
    private Button profileButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button cartButton;

    @FXML
    private ScrollPane categoryScrollPane;

    @FXML
    private HBox categoryHBox;

    @FXML
    private TilePane productTilePane;


    // ==== Atributos de Suporte ====
    private Usuario usuarioLogado;
    // private ProdutoDAO produtoDAO; // Você provavelmente vai precisar de um DAO para produtos

    // ==== Métodos Injetores (chamados pelo FXMLLoader) ====
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        // Se você não tem mais um 'welcomeLabel' no FXML, esta linha vai dar NullPointerException.
        // Se tiver, descomente e use.
        // if (welcomeLabel != null) {
        //     welcomeLabel.setText("Bem-vindo(a), " + usuario.getNome() + "!");
        // }
        System.out.println("Usuário logado na ProdutosView: " + usuario.getNome()); // Para depuração
        // Aqui você pode carregar produtos baseados no usuário, etc.
        // Ex: if (usuario.isAdmin()) { showAdminButtons(); }
    }

    // public void setProdutoDAO(ProdutoDAO produtoDAO) {
    //     this.produtoDAO = produtoDAO;
    // }

    // ==== Método de Inicialização (chamado automaticamente após o FXML ser carregado) ====
    @FXML
    public void initialize() {
        // Este método é chamado automaticamente após o FXML ser carregado e os @FXML serem injetados.
        // É um bom lugar para carregar os produtos iniciais, configurar listeners, etc.

        // Exemplo: Adicionar um listener para a barra de pesquisa
        // searchTextField.textProperty().addListener((obs, oldText, newText) -> {
        //     // filterProducts(newText);
        // });

        // Carregar todos os produtos no início (você precisará de um método para isso)
        // loadAllProducts();

        // Se o welcomeLabel foi removido do FXML, você deve remover a referência aqui também.
        // Ou adicione o welcomeLabel de volta ao FXML se quiser exibi-lo.
        // Se você precisa exibir o nome do usuário, faça isso em setUsuarioLogado(),
        // pois initialize() é chamado antes de setUsuarioLogado().
    }


    // ==== Métodos dos Eventos FXML (onAction) ====

    @FXML
    private void handleProfileButtonAction(ActionEvent event) {
        System.out.println("Botão Perfil clicado!");
        // Implementar lógica para ir para a tela de perfil
        // Ex: loadProfileView();
    }

    @FXML
    private void handleCartButtonAction(ActionEvent event) {
        System.out.println("Botão Carrinho clicado!");
        // Implementar lógica para ir para a tela do carrinho
        // Ex: loadCartView();
    }

    @FXML
    private void handleCategoryButtonAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String category = (String) clickedButton.getUserData(); // Pega o userData que você definiu no FXML
        System.out.println("Categoria clicada: " + category);
        // Implementar lógica para filtrar produtos por categoria
        // Ex: filterProductsByCategory(category);
    }

    // ==== Outros métodos para a lógica de produtos ====

    // Exemplo de método para carregar produtos (você precisará da sua classe Produto e ProdutoDAO)
    /*
    private void loadAllProducts() {
        if (produtoDAO != null) {
            try {
                List<Produto> produtos = produtoDAO.findAll();
                displayProducts(produtos);
            } catch (SQLException e) {
                System.err.println("Erro ao carregar produtos: " + e.getMessage());
                // Exibir alerta ao usuário
            }
        }
    }

    private void displayProducts(List<Produto> produtos) {
        productTilePane.getChildren().clear(); // Limpa produtos anteriores
        for (Produto p : produtos) {
            // Crie um VBox ou outro layout para representar cada produto
            VBox productCard = createProductCard(p);
            productTilePane.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Produto produto) {
        // Lógica para criar o visual de um único produto (imagem, nome, preço, botões)
        // Isso seria mais complexo e pode envolver outro FXML para o "cartão do produto"
        VBox card = new VBox(10); // Espaçamento de 10px
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 10; -fx-padding: 10; -fx-background-color: white;");
        ImageView imageView = new ImageView(new Image(produto.getImageUrl())); // Supondo que Produto tenha getImageUrl()
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        Label nameLabel = new Label(produto.getNome());
        nameLabel.setFont(new Font("System Bold", 16));
        Label priceLabel = new Label("R$ " + String.format("%.2f", produto.getPreco()));
        priceLabel.setFont(new Font("System Bold", 18));
        priceLabel.setStyle("-fx-text-fill: #00A60E;");
        Button comprarButton = new Button("Comprar");
        comprarButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-background-radius: 5;");
        comprarButton.setOnAction(e -> handleComprarAction(produto));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, comprarButton);
        return card;
    }

    private void handleComprarAction(Produto produto) {
        System.out.println("Comprar: " + produto.getNome());
        // Lógica para adicionar ao carrinho ou comprar diretamente
    }
    */

    // Métodos para exibir alertas (se você precisar deles aqui)
    /*
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    */
}