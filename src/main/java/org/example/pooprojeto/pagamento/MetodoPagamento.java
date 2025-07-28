package org.example.pooprojeto.pagamento;
import javafx.scene.Node;

public interface MetodoPagamento {
    String getNome();
    Node gerarComponenteVisual();
}