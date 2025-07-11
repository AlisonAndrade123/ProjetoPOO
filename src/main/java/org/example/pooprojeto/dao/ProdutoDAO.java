package org.example.pooprojeto.dao;

import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.DatabaseManager;
import org.example.pooprojeto.util.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Produto.
 * Responsável por operações de CRUD de produtos no banco de dados.
 */
public class ProdutoDAO {

    /**
     * Salva um novo produto no banco de dados.
     * @param produto O objeto Produto a ser salvo.
     * @return O objeto Produto com o ID gerado pelo banco de dados.
     * @throws AppException Se ocorrer um erro no banco de dados.
     */
    public Produto save(Produto produto) throws AppException {
        String sql = "INSERT INTO produtos (nome, descricao, preco, estoque, categoria, image_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getEstoque());
            pstmt.setString(5, produto.getCategoria());
            pstmt.setString(6, produto.getImageUrl());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new AppException("Falha ao criar produto, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produto.setId(generatedKeys.getInt(1));
                } else {
                    throw new AppException("Não foi possível obter o ID gerado para o produto.");
                }
            }
            return produto;

        } catch (SQLException e) {
            throw new AppException("Erro ao salvar produto no banco de dados: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna uma lista de todos os produtos no banco de dados.
     * @return Uma lista de objetos Produto.
     * @throws AppException Se ocorrer um erro no banco de dados.
     */
    public List<Produto> findAll() throws AppException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, estoque, categoria, image_url FROM produtos";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque"),
                        rs.getString("categoria"),
                        rs.getString("image_url")
                );
                produtos.add(produto);
            }
        } catch (SQLException e) {
            throw new AppException("Erro ao buscar todos os produtos: " + e.getMessage(), e);
        }
        return produtos;
    }

    /**
     * Retorna uma lista de produtos filtrados por categoria.
     * @param categoria A categoria dos produtos a serem buscados.
     * @return Uma lista de objetos Produto que pertencem à categoria.
     * @throws AppException Se ocorrer um erro no banco de dados.
     */
    public List<Produto> findByCategory(String categoria) throws AppException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, estoque, categoria, image_url FROM produtos WHERE categoria = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("estoque"),
                            rs.getString("categoria"),
                            rs.getString("image_url")
                    );
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        }
        return produtos;
    }

    /**
     * Retorna uma lista de produtos filtrados por termo de busca no nome ou descrição.
     * @param searchTerm O termo a ser buscado no nome ou descrição do produto.
     * @return Uma lista de objetos Produto que correspondem ao termo.
     * @throws AppException Se ocorrer um erro no banco de dados.
     */
    public List<Produto> searchProducts(String searchTerm) throws AppException {
        List<Produto> produtos = new ArrayList<>();
        // Usa LIKE para busca parcial e % para curinga
        String sql = "SELECT id, nome, descricao, preco, estoque, categoria, image_url FROM produtos WHERE nome LIKE ? OR descricao LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("estoque"),
                            rs.getString("categoria"),
                            rs.getString("image_url")
                    );
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Erro ao buscar produtos: " + e.getMessage(), e);
        }
        return produtos;
    }

    // Você pode adicionar outros métodos como findById, update, delete se necessário.
}