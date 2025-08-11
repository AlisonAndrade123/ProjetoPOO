package br.edu.ifpb.lojavirtual.controller;

import br.edu.ifpb.lojavirtual.dao.PedidoDAO;
import br.edu.ifpb.lojavirtual.model.*;
import br.edu.ifpb.lojavirtual.pagamento.MetodoPagamento;
import br.edu.ifpb.lojavirtual.pagamento.PagamentoBoleto;
import br.edu.ifpb.lojavirtual.pagamento.PagamentoPix;
import br.edu.ifpb.lojavirtual.service.AuthService;
import br.edu.ifpb.lojavirtual.service.NotaFiscalService;
import br.edu.ifpb.lojavirtual.util.CarrinhoManager;
import br.edu.ifpb.lojavirtual.util.GeradorNotaFiscalPDF;
import br.edu.ifpb.lojavirtual.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PagamentoController {

    @FXML
    private VBox paymentOptionsVBox;
    @FXML
    private VBox resumoVBox;
    @FXML
    private Label tituloLabel;
    @FXML
    private Label resumoTituloLabel;
    @FXML
    private Label totalTituloLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label freteLabel;
    @FXML
    private Button confirmarPagamentoButton;
    @FXML
    private HBox opcoesPagamentoHBox;
    @FXML
    private StackPane painelMetodoPagamento;

    private final CarrinhoManager carrinhoManager = CarrinhoManager.getInstance();
    private final NotaFiscalService notaFiscalService = new NotaFiscalService();
    private ToggleGroup toggleGroup;
    private double valorTotalCompra;
    private final PedidoDAO pedidoDAO = new PedidoDAO();


    public void inicializar(double valorTotal) {
        this.valorTotalCompra = valorTotal;
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

        Usuario usuarioLogado = AuthService.getInstance().getUsuarioLogado();
        Map<Produto, Integer> itensDoCarrinhoMap = carrinhoManager.getItens();
        Endereco endereco = carrinhoManager.getEnderecoEntrega();

        if (usuarioLogado == null || endereco == null) {
            showAlert(Alert.AlertType.ERROR, "Erro Crítico", "Dados do usuário ou endereço não encontrados.");
            confirmarPagamentoButton.setDisable(false);
            return;
        }

        Pedido novoPedido = new Pedido();
        novoPedido.setUsuarioId(usuarioLogado.getId());
        novoPedido.setValorTotal(this.valorTotalCompra);
        novoPedido.setDataPedido(LocalDateTime.now().toString());

        List<PedidoItem> itensDoPedido = itensDoCarrinhoMap.entrySet().stream()
                .map(entry -> {
                    Produto produto = entry.getKey();
                    int quantidade = entry.getValue();
                    PedidoItem item = new PedidoItem();
                    item.setProduto(produto);
                    item.setQuantidade(quantidade);
                    item.setPrecoUnitario(produto.getPreco());
                    return item;
                })
                .collect(Collectors.toList());
        novoPedido.setItens(itensDoPedido);

        try {
            pedidoDAO.salvar(novoPedido);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao registrar o pedido no sistema. A compra foi cancelada.");
            confirmarPagamentoButton.setDisable(false);
            return;
        }

        List<Produto> produtosParaNota = new ArrayList<>();
        itensDoCarrinhoMap.forEach((p, q) -> { for (int i = 0; i < q; i++) produtosParaNota.add(p); });

        NotaFiscal notaFiscal = notaFiscalService.gerarNotaFiscal(usuarioLogado, produtosParaNota, this.valorTotalCompra, endereco);

        try {
            File arquivoPdf = GeradorNotaFiscalPDF.gerarPdf(notaFiscal);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pagamento Realizado");
            alert.setHeaderText("Compra finalizada e Nota Fiscal gerada!");
            alert.setContentText("O PDF da sua nota foi salvo em sua pasta 'notas_fiscais'.");

            ButtonType abrirPdfButton = new ButtonType("Abrir PDF da Nota Fiscal");
            ButtonType fecharButton = new ButtonType("Fechar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(abrirPdfButton, fecharButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == abrirPdfButton) {
                GeradorNotaFiscalPDF.abrirPdf(arquivoPdf);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível gerar ou salvar o PDF da nota fiscal.");
        } finally {
            carrinhoManager.limparCarrinho();
            NavigationManager.getInstance().navigateToProductsView();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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