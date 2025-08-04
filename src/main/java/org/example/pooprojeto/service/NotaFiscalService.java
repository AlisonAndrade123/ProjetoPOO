package org.example.pooprojeto.service;


import org.example.pooprojeto.model.Endereco;
import org.example.pooprojeto.model.NotaFiscal;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.model.Usuario;

import java.util.List;

public class NotaFiscalService {
    public NotaFiscal gerarNotaFiscal(Usuario cliente, List<Produto> produtos, double valorTotal, Endereco endereco) {
        if (cliente == null || produtos == null || produtos.isEmpty() || endereco == null) {
            throw new IllegalArgumentException("Dados insuficientes para gerar a nota fiscal.");
        }

        NotaFiscal notaFiscal = new NotaFiscal(valorTotal, cliente.getNome(), produtos, endereco);
        return notaFiscal;
    }

}
