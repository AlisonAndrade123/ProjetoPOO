package br.edu.ifpb.lojavirtual.service;

import br.edu.ifpb.lojavirtual.model.Endereco;
import br.edu.ifpb.lojavirtual.model.NotaFiscal;
import br.edu.ifpb.lojavirtual.model.Produto;
import br.edu.ifpb.lojavirtual.model.Usuario;

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
