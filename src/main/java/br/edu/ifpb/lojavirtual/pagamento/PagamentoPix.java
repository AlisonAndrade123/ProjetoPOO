package br.edu.ifpb.lojavirtual.pagamento;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.UUID;

public class PagamentoPix implements MetodoPagamento {
    @Override
    public String getNome() {
        return "PIX";
    }

    @Override
    public Node gerarComponenteVisual() {
        VBox layout = new VBox(15.0);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label instrucao1 = new Label("1. Escaneie o código QR com seu celular");
        ImageView qrCodeImageView = new ImageView();
        try {
            Image qrCode = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/br/edu/ifpb/lojavirtual/imagens/qr.jpeg")));
            qrCodeImageView.setImage(qrCode);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem do QR Code: " + e.getMessage());
        }
        qrCodeImageView.setFitHeight(150.0);
        qrCodeImageView.setFitWidth(150.0);

        Label instrucao2 = new Label("2. Ou copie o código abaixo:");

        TextField pixCodeTextField = new TextField(UUID.randomUUID().toString().replace("-", ""));
        pixCodeTextField.setEditable(false);
        pixCodeTextField.setPrefWidth(300.0);
        pixCodeTextField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-family: 'monospace';");

        Button copiarChaveButton = new Button("Copiar");
        copiarChaveButton.setStyle("-fx-background-color: #e9e9e9; -fx-text-fill: #333; -fx-background-radius: 5; -fx-cursor: hand;");

        copiarChaveButton.setOnAction(event -> {

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();


            content.putString(pixCodeTextField.getText());
            clipboard.setContent(content);


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Chave PIX copiada para a área de transferência!");
            alert.showAndWait();
        });

        HBox copyBox = new HBox(5.0, pixCodeTextField, copiarChaveButton);
        copyBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(instrucao1, qrCodeImageView, instrucao2, copyBox);
        return layout;
    }
}