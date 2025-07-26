package org.example.pooprojeto.pagamento;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert; // <<< IMPORTAÇÃO NECESSÁRIA
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PagamentoBoleto implements MetodoPagamento {

    @Override
    public String getNome() {
        return "Boleto"; // Nome mais curto para o botão
    }

    @Override
    public Node gerarComponenteVisual() {
        VBox layout = new VBox(20.0);
        layout.setPadding(new Insets(20));

        Label instrucao = new Label("Clique no botão abaixo para gerar o seu boleto bancário.");
        instrucao.setWrapText(true);
        instrucao.setStyle("-fx-font-size: 14px;");

        Button gerarBoletoButton = new Button("Gerar e Visualizar Boleto");
        gerarBoletoButton.setStyle("-fx-background-color: #00A60E; -fx-text-fill: white; -fx-cursor: hand;");
        gerarBoletoButton.setFont(new Font("System Bold", 14));

        // <<< MUDANÇA PRINCIPAL: A ação do botão agora mostra um Alerta.
        gerarBoletoButton.setOnAction(e -> {
            // A mensagem no console ainda é útil para debug.
            System.out.println("Boleto gerado com sucesso! (Simulação)");

            // 1. Cria um pop-up (Alerta) do tipo INFORMAÇÃO.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Boleto Gerado");
            alert.setHeaderText("Seu boleto foi gerado com sucesso!");
            alert.setContentText("Em uma aplicação real, o boleto em PDF seria aberto ou disponibilizado para download agora.");

            // 2. Mostra o pop-up e espera o usuário clicar em "OK".
            alert.showAndWait();

            // 3. (Opcional, mas recomendado) Desabilita o botão para evitar que o usuário
            // gere múltiplos boletos para a mesma compra.
            gerarBoletoButton.setDisable(true);
            gerarBoletoButton.setText("Boleto Gerado"); // Muda o texto para indicar que a ação foi concluída
        });

        layout.getChildren().addAll(instrucao, gerarBoletoButton);
        return layout;
    }
}