package br.edu.ifpb.lojavirtual.pagamento;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PagamentoBoleto implements MetodoPagamento {

    @Override
    public String getNome() {
        return "Boleto";
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

        gerarBoletoButton.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Boleto Gerado");
            alert.setHeaderText("Seu boleto foi gerado com sucesso!");
            alert.setContentText("Em uma aplicação real, o boleto em PDF seria aberto ou disponibilizado para download agora.");

            alert.showAndWait();

            gerarBoletoButton.setDisable(true);
            gerarBoletoButton.setText("Boleto Gerado");
        });

        layout.getChildren().addAll(instrucao, gerarBoletoButton);
        return layout;
    }
}