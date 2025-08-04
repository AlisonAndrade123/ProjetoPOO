package org.example.pooprojeto.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.pooprojeto.model.Endereco;
import org.example.pooprojeto.model.NotaFiscal;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.pagamento.MetodoPagamento;
import org.example.pooprojeto.pagamento.PagamentoBoleto;
import org.example.pooprojeto.pagamento.PagamentoPix;
import org.example.pooprojeto.service.AuthService;
import org.example.pooprojeto.service.NotaFiscalService;
import org.example.pooprojeto.util.CarrinhoManager;
import org.example.pooprojeto.util.GeradorNotaFiscalPDF;
import org.example.pooprojeto.util.NavigationManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private VBox processamentoBox;
    @FXML
    private HBox opcoesPagamentoHBox;
    @FXML
    private StackPane painelMetodoPagamento;

    private final CarrinhoManager carrinhoManager = CarrinhoManager.getInstance();
    private ToggleGroup toggleGroup;
    private double valorTotalCompra;

    private final NotaFiscalService notaFiscalService = new NotaFiscalService();

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
        List<Produto> produtosParaNota = new ArrayList<>();
        itensDoCarrinhoMap.forEach((produto, quantidade) -> {
            for (int i = 0; i < quantidade; i++) {
                produtosParaNota.add(produto);
            }
        });

        Endereco endereco = CarrinhoManager.getInstance().getEnderecoEntrega();

        if (usuarioLogado == null) {
            showAlert(Alert.AlertType.ERROR, "Erro de Autenticação", "Usuário não encontrado!");
            confirmarPagamentoButton.setDisable(false);
            return;
        }

        NotaFiscal notaFiscal = notaFiscalService.gerarNotaFiscal(usuarioLogado, produtosParaNota, this.valorTotalCompra, endereco);

        try {
            File arquivoPdf = GeradorNotaFiscalPDF.gerarPdf(notaFiscal);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pagamento Realizado");
            alert.setHeaderText("Pagamento concluído e Nota Fiscal gerada!");
            alert.setContentText("O PDF da sua nota foi salvo em sua pasta 'notas_fiscais'.");

            ButtonType abrirPdfButton = new ButtonType("Abrir PDF da Nota Fiscal");
            ButtonType fecharButton = new ButtonType("Fechar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(abrirPdfButton, fecharButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == abrirPdfButton) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(arquivoPdf);
                } else {
                    showAlert(Alert.AlertType.WARNING, "Aviso", "Não foi possível abrir o arquivo automaticamente.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível gerar ou salvar o PDF da nota fiscal.");
        } finally {
            carrinhoManager.limparCarrinho();
            NavigationManager.getInstance().navigateToProductsView();
        }
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}