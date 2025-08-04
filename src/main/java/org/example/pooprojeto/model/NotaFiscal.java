package org.example.pooprojeto.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class NotaFiscal {
    private static final AtomicLong contadorId = new AtomicLong(1);
    private final long numero;
    private final LocalDateTime dataEmissao;
    private final double valorTotal;
    private final String clienteNome;
    private final List<Produto> produtos;
    private final Endereco enderecoEntrega;

    public NotaFiscal(double valorTotal, String clienteNome, List<Produto> produtos, Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
        this.numero = contadorId.getAndIncrement();
        this.dataEmissao = LocalDateTime.now();
        this.valorTotal = valorTotal;
        this.clienteNome = clienteNome;
        this.produtos = produtos;
    }

    public long getNumero() {
        return numero;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }
}