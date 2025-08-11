package br.edu.ifpb.lojavirtual.util;

import br.edu.ifpb.lojavirtual.model.Endereco;
import br.edu.ifpb.lojavirtual.model.Produto;

import java.util.LinkedHashMap;
import java.util.Map;
public class CarrinhoManager {

    private static CarrinhoManager instance;
    private final Map<Produto, Integer> itens;
    private Endereco enderecoEntrega;

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

    public void incrementarQuantidade(Produto produto) {
        this.adicionarProduto(produto);
    }


    public void decrementarQuantidade(Produto produto) {
        int quantidadeAtual = this.itens.getOrDefault(produto, 0);
        if (quantidadeAtual > 1) {
            this.itens.put(produto, quantidadeAtual - 1);
        } else {
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
    public void setEnderecoEntrega(Endereco endereco) {
        this.enderecoEntrega = endereco;
    }

    public Endereco getEnderecoEntrega() {
        return this.enderecoEntrega;
    }

    public void limparCarrinho() {
        this.itens.clear();
        this.enderecoEntrega = null; 
    }
}