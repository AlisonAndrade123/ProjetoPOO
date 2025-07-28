package org.example.pooprojeto.service;

import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;

import java.sql.SQLException;
import java.util.List;

public class ProdutoService {

    private ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public List<Produto> getAllProdutos() throws SQLException {
        return produtoDAO.findAll();
    }

    public List<Produto> getProdutosPorCategoria(String categoria) throws SQLException {
        if (categoria == null || categoria.trim().isEmpty() || "Todos".equalsIgnoreCase(categoria.trim())) {
            return produtoDAO.findAll();
        }
        return produtoDAO.findByCategory(categoria);
    }

    public List<Produto> searchProdutos(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return produtoDAO.findAll();
        }
        return produtoDAO.search(searchTerm);
    }

    public void saveProduto(Produto produto) throws SQLException {
        produtoDAO.save(produto);
    }

    public boolean updateProduto(Produto produto) throws SQLException {
        return produtoDAO.update(produto);
    }

    public boolean deleteProduto(int id) throws SQLException {
        return produtoDAO.delete(id);
    }
}