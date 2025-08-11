package br.edu.ifpb.lojavirtual.pagamento;
import javafx.scene.Node;
public interface MetodoPagamento {
    String getNome();
    Node gerarComponenteVisual();
}