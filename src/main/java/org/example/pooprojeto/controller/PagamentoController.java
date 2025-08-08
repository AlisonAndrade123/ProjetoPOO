package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.pooprojeto.dao.PedidoDAO;
import org.example.pooprojeto.model.*;
import org.example.pooprojeto.pagamento.MetodoPagamento;
import org.example.pooprojeto.pagamento.PagamentoBoleto;
import org.example.pooprojeto.pagamento.PagamentoPix;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.service.NotaFiscalService;
import org.example.pooprojeto.util.CarrinhoManager;
import org.example.pooprojeto.util.GeradorNotaFiscalPDF; // Mantém este import
import org.example.pooprojeto.util.NavigationManager;

import java.io.File; // Mantém este import
import java.io.IOException; // Mantém este import
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PagamentoController {

    // ... Seus @FXML e declarações de variáveis permanecem os mesmos ...
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


    // O método inicializar continua igual
    public void inicializar(double valorTotal) {
        this.valorTotalCompra = valorTotal;
        String valorFormatado = String.format("R$ %.2f", valorTotal).replace('.', ',');
        totalLabel.setText(valorFormatado);
        subtotalLabel.setText(valorFormatado);
        freteLabel.setText("R$ 0,00");
        confirmarPagamentoButton.setText("Pagar " + valorFormatado);
    }

    // O método initialize continua igual
    @FXML
    public void initialize() {
        aplicarEstilos();
        configurarMetodosPagamento();
    }


    // --- MÉTODO handleConfirmarPagamento SIMPLIFICADO ---
    @FXML
    void handleConfirmarPagamento() {
        confirmarPagamentoButton.setDisable(true);

        // --- PREPARAÇÃO DOS DADOS ---
        Usuario usuarioLogado = AuthService.getInstance().getUsuarioLogado();
        Map<Produto, Integer> itensDoCarrinhoMap = carrinhoManager.getItens();
        Endereco endereco = carrinhoManager.getEnderecoEntrega();

        if (usuarioLogado == null || endereco == null) {
            showAlert(Alert.AlertType.ERROR, "Erro Crítico", "Dados do usuário ou endereço não encontrados.");
            confirmarPagamentoButton.setDisable(false);
            return;
        }

        // --- INÍCIO DA LÓGICA RESTAURADA ---
        // 1. Criar o objeto Pedido
        Pedido novoPedido = new Pedido();
        novoPedido.setUsuarioId(usuarioLogado.getId());
        novoPedido.setValorTotal(this.valorTotalCompra);
        novoPedido.setDataPedido(LocalDateTime.now().toString());

        // 2. Converter os itens do carrinho para o formato PedidoItem
        List<PedidoItem> itensDoPedido = itensDoCarrinhoMap.entrySet().stream()
                .map(entry -> {
                    Produto produto = entry.getKey();
                    int quantidade = entry.getValue();
                    PedidoItem item = new PedidoItem();

                    // --- CORREÇÃO PRINCIPAL AQUI ---
                    // Em vez de setar apenas o ID, setamos o objeto Produto inteiro.
                    item.setProduto(produto); // Em vez de item.setProdutoId(produto.getId());

                    item.setQuantidade(quantidade);
                    item.setPrecoUnitario(produto.getPreco());
                    return item;
                })
                .collect(Collectors.toList());
        novoPedido.setItens(itensDoPedido);

        // 3. Tentar salvar o Pedido no Banco de Dados
        try {
            pedidoDAO.salvar(novoPedido);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao registrar o pedido no sistema. A compra foi cancelada.");
            confirmarPagamentoButton.setDisable(false);
            return; // Interrompe a execução se o pedido não puder ser salvo
        }
        // --- FIM DA LÓGICA RESTAURADA ---

        // 4. Se o pedido foi salvo, prosseguir com a Nota Fiscal
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

    // ... os outros métodos (showAlert, configurarMetodosPagamento, aplicarEstilos) permanecem os mesmos ...
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