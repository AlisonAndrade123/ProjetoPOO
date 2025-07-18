package org.example.pooprojeto.dao;

import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    /**
     * <<< MUDANÇA 1: ATUALIZADO para salvar 'nome_arquivo_imagem'.
     * Salva um novo produto no banco de dados.
     */
    public void save(Produto produto) throws SQLException {
        // A query SQL foi atualizada para usar a coluna correta.
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getQuantidade());
            pstmt.setString(5, produto.getCategoria());
            // O método getNomeArquivoImagem() é chamado em vez de getImageUrl().
            pstmt.setString(6, produto.getNomeArquivoImagem());

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

    /**
     * <<< MUDANÇA 2: ATUALIZADO para buscar 'nome_arquivo_imagem' e usar setters.
     * Busca todos os produtos no banco de dados.
     */
    public List<Produto> findAll() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        // A query SQL foi atualizada para buscar a coluna correta.
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem FROM produtos";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Usando o construtor padrão e setters para criar o objeto.
                // Isso torna o código mais legível e menos dependente do construtor.
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setCategoria(rs.getString("categoria"));
                produto.setNomeArquivoImagem(rs.getString("nome_arquivo_imagem"));
                produtos.add(produto);
            }
        }
        return produtos;
    }

    /**
     * <<< MUDANÇA 3: ATUALIZADO para buscar 'nome_arquivo_imagem'.
     * Encontra produtos por categoria.
     */
    public List<Produto> findByCategory(String categoria) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem FROM produtos WHERE categoria = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setQuantidade(rs.getInt("quantidade"));
                    produto.setCategoria(rs.getString("categoria"));
                    produto.setNomeArquivoImagem(rs.getString("nome_arquivo_imagem"));
                    produtos.add(produto);
                }
            }
        }
        return produtos;
    }

    /**
     * <<< MUDANÇA 4: ATUALIZADO para buscar 'nome_arquivo_imagem'.
     * Busca produtos por nome ou descrição.
     */
    public List<Produto> search(String searchTerm) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem FROM produtos WHERE nome LIKE ? OR descricao LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setQuantidade(rs.getInt("quantidade"));
                    produto.setCategoria(rs.getString("categoria"));
                    produto.setNomeArquivoImagem(rs.getString("nome_arquivo_imagem"));
                    produtos.add(produto);
                }
            }
        }
        return produtos;
    }

    /**
     * <<< MUDANÇA 5: ATUALIZADO para atualizar 'nome_arquivo_imagem'.
     * Atualiza um produto existente.
     */
    public boolean update(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade = ?, categoria = ?, nome_arquivo_imagem = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getQuantidade());
            pstmt.setString(5, produto.getCategoria());
            pstmt.setString(6, produto.getNomeArquivoImagem()); // Usa o novo getter
            pstmt.setInt(7, produto.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deleta um produto pelo ID. Nenhuma mudança necessária aqui.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * <<< MUDANÇA 6: ATUALIZADO para buscar 'nome_arquivo_imagem'.
     * Encontra um produto pelo seu ID.
     */
    public Produto findById(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem FROM produtos WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Produto produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setQuantidade(rs.getInt("quantidade"));
                    produto.setCategoria(rs.getString("categoria"));
                    produto.setNomeArquivoImagem(rs.getString("nome_arquivo_imagem"));
                    return produto;
                }
            }
        }
        return null;
    }
}