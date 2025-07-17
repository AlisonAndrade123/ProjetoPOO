package org.example.pooprojeto.dao;

import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.DatabaseManager; // Verifique se este é o nome correto da sua classe de conexão

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // <<< MUDANÇA: Renomeado de 'add' para 'save' e ajustado para usar 'quantidade'
    public void save(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade, categoria, image_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getQuantidade()); // <<< MUDANÇA
            pstmt.setString(5, produto.getCategoria());
            pstmt.setString(6, produto.getImageUrl());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        produto.setId(rs.getInt(1));
                    }
                }
            }
        }
    }

    // Método para encontrar todos os produtos
    public List<Produto> findAll() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, image_url FROM produtos"; // <<< MUDANÇA
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produtos.add(new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade"), // <<< MUDANÇA
                        rs.getString("categoria"),
                        rs.getString("image_url")
                ));
            }
        }
        return produtos;
    }

    // Método para encontrar produtos por categoria
    public List<Produto> findByCategory(String categoria) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, image_url FROM produtos WHERE categoria = ?"; // <<< MUDANÇA
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade"), // <<< MUDANÇA
                            rs.getString("categoria"),
                            rs.getString("image_url")
                    ));
                }
            }
        }
        return produtos;
    }

    // Método para buscar produtos por nome ou descrição
    public List<Produto> search(String searchTerm) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, image_url FROM produtos WHERE nome LIKE ? OR descricao LIKE ?"; // <<< MUDANÇA
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade"), // <<< MUDANÇA
                            rs.getString("categoria"),
                            rs.getString("image_url")
                    ));
                }
            }
        }
        return produtos;
    }

    // Método para atualizar um produto
    public boolean update(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade = ?, categoria = ?, image_url = ? WHERE id = ?"; // <<< MUDANÇA
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getQuantidade()); // <<< MUDANÇA
            pstmt.setString(5, produto.getCategoria());
            pstmt.setString(6, produto.getImageUrl());
            pstmt.setInt(7, produto.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Método para deletar um produto pelo ID
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Método para encontrar um produto pelo ID
    public Produto findById(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, image_url FROM produtos WHERE id = ?"; // <<< MUDANÇA
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade"), // <<< MUDANÇA
                            rs.getString("categoria"),
                            rs.getString("image_url")
                    );
                }
            }
        }
        return null;
    }
}