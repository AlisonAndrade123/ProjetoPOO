// Em util/CarrinhoManager.java
package org.example.pooprojeto.util;

import org.example.pooprojeto.model.Produto;
import java.util.LinkedHashMap;
import java.util.Map;

public class CarrinhoManager {

    private static CarrinhoManager instance;
    private final Map<Produto, Integer> itens;

    private CarrinhoManager() {
        this.itens = new LinkedHashMap<>();
    }

    public static CarrinhoManager getInstance() {
        if (instance == null) {
            instance = new CarrinhoManager();
        }
        return instance;
    }

    public void adicionarProduto(Produto produto) {
        this.itens.put(produto, this.itens.getOrDefault(produto, 0) + 1);
    }

    // <<< NOVO MÉTODO
    public void incrementarQuantidade(Produto produto) {
        this.adicionarProduto(produto); // A lógica é a mesma
    }

    // <<< NOVO MÉTODO
    public void decrementarQuantidade(Produto produto) {
        int quantidadeAtual = this.itens.getOrDefault(produto, 0);
        if (quantidadeAtual > 1) {
            this.itens.put(produto, quantidadeAtual - 1);
        } else {
            // Se a quantidade for 1 ou menos, remove o produto
            this.removerProduto(produto);
        }
    }

    public void removerProduto(Produto produto) {
        this.itens.remove(produto);
    }

    public Map<Produto, Integer> getItens() {
        return this.itens;
    }

    public double calcularTotal() {
        double total = 0.0;
        for (Map.Entry<Produto, Integer> entry : itens.entrySet()) {
            total += entry.getKey().getPreco() * entry.getValue();
        }
        return total;
    }

    public void limparCarrinho() {
        this.itens.clear();
    }
}