// src/main/java/org/example/pooprojeto/service/ProdutoService.java
package org.example.pooprojeto.service;

import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.AppException;

import java.util.List;

/**
 * Serviço para operações relacionadas a Produtos.
 * Atua como uma camada intermediária entre o Controller e o DAO,
 * encapsulando a lógica de negócio dos produtos.
 */
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    /**
     * Busca e retorna todos os produtos disponíveis.
     * @return Uma lista de todos os produtos.
     * @throws AppException Se ocorrer um erro ao acessar os dados.
     */
    public List<Produto> getAllProdutos() throws AppException {
        return produtoDAO.findAll();
    }

    /**
     * Busca e retorna produtos filtrados por uma categoria específica.
     * @param categoria A categoria a ser filtrada.
     * @return Uma lista de produtos da categoria especificada.
     * @throws AppException Se ocorrer um erro ao acessar os dados.
     */
    public List<Produto> getProdutosByCategory(String categoria) throws AppException {
        if (categoria == null || categoria.trim().isEmpty() || categoria.equalsIgnoreCase("Todos")) {
            return produtoDAO.findAll(); // Se for "Todos" ou vazio, retorna todos os produtos
        }
        return produtoDAO.findByCategory(categoria);
    }

    /**
     * Busca e retorna produtos que correspondem a um termo de busca.
     * @param searchTerm O termo a ser buscado no nome ou descrição dos produtos.
     * @return Uma lista de produtos que correspondem ao termo de busca.
     * @throws AppException Se ocorrer um erro ao acessar os dados.
     */
    public List<Produto> searchProdutos(String searchTerm) throws AppException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return produtoDAO.findAll(); // Se o termo de busca for vazio, retorna todos
        }
        return produtoDAO.searchProducts(searchTerm);
    }

    // Você pode adicionar outros métodos de lógica de negócio aqui,
    // como adicionar ao carrinho, verificar estoque, etc.
}