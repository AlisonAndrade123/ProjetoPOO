package org.example.pooprojeto.pagamento;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert; // <<< IMPORTAÇÃO NECESSÁRIA
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image; // <<< IMPORTAÇÃO NECESSÁRIA
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard; // <<< IMPORTAÇÃO NECESSÁRIA
import javafx.scene.input.ClipboardContent; // <<< IMPORTAÇÃO NECESSÁRIA
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.UUID; // <<< IMPORTAÇÃO NECESSÁRIA

public class PagamentoPix implements MetodoPagamento {
    @Override
    public String getNome() { return "PIX"; }

    @Override
    public Node gerarComponenteVisual() {
        VBox layout = new VBox(15.0);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER); // Centraliza todo o conteúdo do painel

        Label instrucao1 = new Label("1. Escaneie o código QR com seu celular");
        ImageView qrCodeImageView = new ImageView();
        try {
            // Carrega a imagem do QR Code a partir dos recursos do projeto
            Image qrCode = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/Imagens/qr.jpeg"));
            qrCodeImageView.setImage(qrCode);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem do QR Code: " + e.getMessage());
        }
        qrCodeImageView.setFitHeight(150.0);
        qrCodeImageView.setFitWidth(150.0);

        Label instrucao2 = new Label("2. Ou copie o código abaixo:");

        // Gera uma chave PIX aleatória toda vez que a interface é criada
        TextField pixCodeTextField = new TextField(UUID.randomUUID().toString().replace("-", ""));
        pixCodeTextField.setEditable(false);
        pixCodeTextField.setPrefWidth(300.0);
        pixCodeTextField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-family: 'monospace';");

        Button copiarChaveButton = new Button("Copiar");
        copiarChaveButton.setStyle("-fx-background-color: #e9e9e9; -fx-text-fill: #333; -fx-background-radius: 5; -fx-cursor: hand;");

        // <<< AQUI ESTÁ A FUNCIONALIDADE ADICIONADA >>>
        copiarChaveButton.setOnAction(event -> {
            // 1. Acessa a área de transferência do sistema.
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            // 2. Coloca o texto da chave PIX na área de transferência.
            content.putString(pixCodeTextField.getText());
            clipboard.setContent(content);

            // 3. Cria e exibe um alerta de sucesso para o usuário.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null); // Remove o cabeçalho para um visual mais limpo
            alert.setContentText("Chave PIX copiada para a área de transferência!");
            alert.showAndWait();
        });

        HBox copyBox = new HBox(5.0, pixCodeTextField, copiarChaveButton);
        copyBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(instrucao1, qrCodeImageView, instrucao2, copyBox);
        return layout;
    }
}