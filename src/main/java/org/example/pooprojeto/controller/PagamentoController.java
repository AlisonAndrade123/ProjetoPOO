package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pooprojeto.dao.PedidoDAO;
import org.example.pooprojeto.model.Pedido;
import org.example.pooprojeto.model.PedidoItem;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.pagamento.MetodoPagamento;
import org.example.pooprojeto.pagamento.PagamentoBoleto;
import org.example.pooprojeto.pagamento.PagamentoPix;
import org.example.pooprojeto.util.CarrinhoManager;
import org.example.pooprojeto.util.NavigationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PagamentoController {

    @FXML private VBox paymentOptionsVBox;
    @FXML private VBox resumoVBox;
    @FXML private Label tituloLabel;
    @FXML private Label resumoTituloLabel;
    @FXML private Label totalTituloLabel;
    @FXML private Label totalLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label freteLabel;
    @FXML private Button confirmarPagamentoButton;
    @FXML private HBox opcoesPagamentoHBox;
    @FXML private StackPane painelMetodoPagamento;

    private final CarrinhoManager carrinhoManager = CarrinhoManager.getInstance();
    private ToggleGroup toggleGroup;

    public void inicializar(double valorTotal) {
        String valorFormatado = String.format("R$ %.2f", valorTotal).replace('.', ',');
        totalLabel.setText(valorFormatado);
        subtotalLabel.setText(valorFormatado);
        freteLabel.setText("R$ 0,00");
        confirmarPagamentoButton.setText("Pagar " + valorFormatado);
    }

    @FXML
    public void initialize() {
        aplicarEstilos();
        configurarMetodosPagamento();
    }

    @FXML
    void handleConfirmarPagamento() {
        confirmarPagamentoButton.setDisable(true);

        try {
            Pedido novoPedido = new Pedido();

            // <<< DEBUG: Verificando os dados do pedido >>>
            System.out.println("DEBUG [PagamentoCtrl]: Tentando criar pedido para o usuário ID: " + NavigationManager.getInstance().getUsuarioLogado().getId());
            novoPedido.setUsuarioId(NavigationManager.getInstance().getUsuarioLogado().getId());
            novoPedido.setValorTotal(carrinhoManager.calcularTotal());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            novoPedido.setDataPedido(dtf.format(LocalDateTime.now()));

            List<PedidoItem> itensDoPedido = new ArrayList<>();
            for (Map.Entry<Produto, Integer> entry : carrinhoManager.getItens().entrySet()) {
                PedidoItem item = new PedidoItem();
                item.setProduto(entry.getKey());
                item.setQuantidade(entry.getValue());
                item.setPrecoUnitario(entry.getKey().getPreco());
                itensDoPedido.add(item);
            }
            novoPedido.setItens(itensDoPedido);

            System.out.println("DEBUG [PagamentoCtrl]: Pedido criado com " + novoPedido.getItens().size() + " item(ns). Valor total: " + novoPedido.getValorTotal());

            PedidoDAO pedidoDAO = new PedidoDAO();
            System.out.println("DEBUG [PagamentoCtrl]: Chamando pedidoDAO.salvar()...");
            pedidoDAO.salvar(novoPedido);
            System.out.println("DEBUG [PagamentoCtrl]: pedidoDAO.salvar() executado sem exceção.");

        } catch (Exception e) {
            System.err.println("DEBUG [PagamentoCtrl]: ERRO CRÍTICO ao salvar o pedido!");
            e.printStackTrace();
            Alert alertErro = new Alert(Alert.AlertType.ERROR);
            alertErro.setTitle("Erro no Pedido");
            alertErro.setHeaderText("Não foi possível registrar seu pedido.");
            alertErro.setContentText("Ocorreu um erro ao salvar sua compra. Por favor, tente novamente.");
            alertErro.showAndWait();
            confirmarPagamentoButton.setDisable(false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pagamento Realizado");
        alert.setHeaderText("✅ Pagamento concluído com sucesso!");
        alert.setContentText("Seu pedido foi registrado e será processado em breve.\nObrigado por comprar conosco!");

        Stage ownerStage = (Stage) confirmarPagamentoButton.getScene().getWindow();
        alert.initOwner(ownerStage);

        alert.showAndWait().ifPresent(response -> {
            carrinhoManager.limparCarrinho();
            NavigationManager.getInstance().navigateToProductsView();
        });
    }

    private void configurarMetodosPagamento() {
        List<MetodoPagamento> metodosDisponiveis = List.of(new PagamentoPix(), new PagamentoBoleto());
        toggleGroup = new ToggleGroup();

        for (MetodoPagamento metodo : metodosDisponiveis) {
            ToggleButton tb = new ToggleButton(metodo.getNome());
            tb.setToggleGroup(toggleGroup);
            tb.setUserData(metodo);
            tb.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");
            opcoesPagamentoHBox.getChildren().add(tb);
        }

        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                MetodoPagamento metodoSelecionado = (MetodoPagamento) newToggle.getUserData();
                Node interfaceVisual = metodoSelecionado.gerarComponenteVisual();
                painelMetodoPagamento.getChildren().setAll(interfaceVisual);
            }
        });

        if (!opcoesPagamentoHBox.getChildren().isEmpty()) {
            toggleGroup.selectToggle((Toggle) opcoesPagamentoHBox.getChildren().get(0));
        }
    }

    private void aplicarEstilos() {
        String cardStyle = "-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);";
        paymentOptionsVBox.setStyle(cardStyle);
        resumoVBox.setStyle(cardStyle);
        tituloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        resumoTituloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        totalTituloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00A60E;");
        confirmarPagamentoButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 25; -fx-cursor: hand;");
        confirmarPagamentoButton.setOnMouseEntered(e -> confirmarPagamentoButton.setStyle("-fx-background-color: #008c0c; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 25; -fx-cursor: hand;"));
        confirmarPagamentoButton.setOnMouseExited(e -> confirmarPagamentoButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 25; -fx-cursor: hand;"));
    }
}