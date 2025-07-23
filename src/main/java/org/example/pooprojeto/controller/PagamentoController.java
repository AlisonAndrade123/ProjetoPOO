package org.example.pooprojeto.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.example.pooprojeto.util.NavigationManager;
import java.util.UUID;
import org.example.pooprojeto.util.CarrinhoManager;

public class PagamentoController {

    // Componentes FXML
    @FXML private VBox paymentOptionsVBox;
    @FXML private VBox resumoVBox;
    @FXML private Label tituloLabel;
    @FXML private VBox pixPane;
    @FXML private ImageView qrCodeImageView;
    @FXML private TextField pixCodeTextField;
    @FXML private Button copiarChaveButton;
    @FXML private Label resumoTituloLabel;
    @FXML private Label totalTituloLabel;
    @FXML private Label totalLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label freteLabel;
    @FXML private Button confirmarPagamentoButton;
    @FXML private VBox processamentoBox;

    public void inicializar(double valorTotal) {
        String valorFormatado = String.format("R$ %.2f", valorTotal).replace('.', ',');
        totalLabel.setText(valorFormatado);
        subtotalLabel.setText(valorFormatado);
        freteLabel.setText("R$ 0,00");
        confirmarPagamentoButton.setText("Pagar " + valorFormatado);
    }

    @FXML
    public void initialize() {
        // Aplica os estilos a todos os componentes
        aplicarEstilos();
        gerarDadosPixSimulados();
    }

    private void aplicarEstilos() {
        // Estilo dos "Cards"
        String cardStyle = "-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);";
        paymentOptionsVBox.setStyle(cardStyle);
        resumoVBox.setStyle(cardStyle);

        // Estilo dos Títulos
        tituloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        resumoTituloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Estilo do Total
        totalTituloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00A60E;");

        // Estilo do campo de código Pix
        pixCodeTextField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-family: 'monospace';");

        // Estilo dos Botões
        copiarChaveButton.setStyle("-fx-background-color: #e9e9e9; -fx-text-fill: #333; -fx-background-radius: 5; -fx-cursor: hand;");
        confirmarPagamentoButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 25; -fx-cursor: hand;");

        // Efeito Hover para os botões (um pouco mais complexo, mas dá um toque final)
        confirmarPagamentoButton.setOnMouseEntered(e -> confirmarPagamentoButton.setStyle("-fx-background-color: #008c0c; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 25; -fx-cursor: hand;"));
        confirmarPagamentoButton.setOnMouseExited(e -> confirmarPagamentoButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 25; -fx-cursor: hand;"));
    }

    private void gerarDadosPixSimulados() {
        try {
            Image qrCode = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/Imagens/fake_qr_code.png"));
            qrCodeImageView.setImage(qrCode);
        } catch (Exception e) {
            System.err.println("Erro ao carregar QR Code falso: " + e.getMessage());
        }
        pixCodeTextField.setText(UUID.randomUUID().toString().replace("-", ""));
    }

    @FXML
    void handleCopiarChave() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(pixCodeTextField.getText());
        clipboard.setContent(content);
        showAlert(Alert.AlertType.INFORMATION, "Copiado!", "A chave Pix foi copiada para a área de transferência.");
    }

    @FXML
    void handleConfirmarPagamento() {
        confirmarPagamentoButton.setVisible(false);
        confirmarPagamentoButton.setManaged(false);
        processamentoBox.setVisible(true);
        processamentoBox.setManaged(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
        delay.setOnFinished(event -> {
            showAlert(Alert.AlertType.INFORMATION, "Pagamento Aprovado!", "Obrigado pela sua compra! Seu pedido está a caminho.");
            carrinhoManager.limparCarrinho(); // <<< Limpa o carrinho após a compra
            NavigationManager.getInstance().navigateToProductsView();
        });
        delay.play();
    }

    // <<< Adicionei a referência ao CarrinhoManager aqui
    private final CarrinhoManager carrinhoManager = CarrinhoManager.getInstance();

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}