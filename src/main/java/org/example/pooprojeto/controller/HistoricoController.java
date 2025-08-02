package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator; // <<< Adicionada esta importação
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority; // <<< Adicionada esta importação
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pooprojeto.dao.PedidoDAO;
import org.example.pooprojeto.model.Pedido;
import org.example.pooprojeto.model.PedidoItem;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.NavigationManager;

import java.sql.SQLException;
import java.util.List;

public class HistoricoController {

    @FXML private VBox pedidosContainerVBox;
    @FXML private Label tituloListaLabel;

    private Usuario usuarioLogado;
    private Stage primaryStage;
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarHistorico();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void carregarHistorico() {
        if (usuarioLogado == null) return;
        pedidosContainerVBox.getChildren().clear();
        try {
            List<Pedido> pedidos = pedidoDAO.buscarPorUsuario(usuarioLogado.getId());
            if (pedidos.isEmpty()) {
                tituloListaLabel.setText("Você ainda não fez nenhuma compra.");
            } else {
                tituloListaLabel.setText("Meus Pedidos Anteriores");
                for (Pedido pedido : pedidos) {
                    Node cardPedido = criarCardPedido(pedido);
                    pedidosContainerVBox.getChildren().add(cardPedido);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            tituloListaLabel.setText("Erro ao carregar o histórico.");
        }
    }

    private Node criarCardPedido(Pedido pedido) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        Label idLabel = new Label("Pedido #" + pedido.getId());
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label dataLabel = new Label("Data: " + pedido.getDataPedido());
        dataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        header.getChildren().addAll(idLabel, dataLabel);

        VBox itensVBox = new VBox(5);
        itensVBox.setPadding(new Insets(10, 0, 10, 20));

        try {
            List<PedidoItem> itens = pedidoDAO.buscarItensPorPedido(pedido.getId());
            for (PedidoItem item : itens) {
                HBox itemBox = new HBox(15);
                itemBox.setAlignment(Pos.CENTER_LEFT);
                ImageView imageView = new ImageView(item.getProduto().getImage());
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setPreserveRatio(true);

                String textoItem = String.format("%dx %s", item.getQuantidade(), item.getProduto().getNome());
                Label itemLabel = new Label(textoItem);

                String precoTexto = String.format("R$ %.2f", item.getPrecoUnitario() * item.getQuantidade());
                Label precoLabel = new Label(precoTexto);
                HBox separador = new HBox();
                HBox.setHgrow(separador, Priority.ALWAYS);

                itemBox.getChildren().addAll(imageView, itemLabel, separador, precoLabel);
                itensVBox.getChildren().add(itemBox);
            }
        } catch (SQLException e) {
            itensVBox.getChildren().add(new Label("Erro ao carregar itens."));
            e.printStackTrace();
        }

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabel = new Label("Total do Pedido: " + String.format("R$ %.2f", pedido.getValorTotal()).replace('.', ','));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00A60E;");
        footer.getChildren().add(totalLabel);

        card.getChildren().addAll(header, new Separator(), itensVBox, footer);

        return card;
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        NavigationManager.getInstance().navigateToProductsView();
    }
}