package org.example.pooprojeto.dao;

import org.example.pooprojeto.model.Pedido;
import org.example.pooprojeto.model.PedidoItem;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void salvar(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (usuario_id, data_pedido, valor_total) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, pedido.getUsuarioId());
                pstmtPedido.setString(2, pedido.getDataPedido());
                pstmtPedido.setDouble(3, pedido.getValorTotal());
                pstmtPedido.executeUpdate();
                try (ResultSet generatedKeys = pstmtPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedido.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Falha ao obter o ID do pedido.");
                    }
                }
            }

            try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                for (PedidoItem item : pedido.getItens()) {
                    pstmtItem.setInt(1, pedido.getId());
                    pstmtItem.setInt(2, item.getProduto().getId());
                    pstmtItem.setInt(3, item.getQuantidade());
                    pstmtItem.setDouble(4, item.getPrecoUnitario());
                    pstmtItem.addBatch();
                }
                pstmtItem.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("### ERRO no PedidoDAO.salvar ### Ocorreu um erro durante a transação. Executando rollback...");
            e.printStackTrace(); // <<< Adicionado para detalhar o erro
            if (conn != null) { conn.rollback(); }
            throw e;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); }
        }
    }

    public List<Pedido> buscarPorUsuario(int usuarioId) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE usuario_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setUsuarioId(rs.getInt("usuario_id"));
                pedido.setDataPedido(rs.getString("data_pedido"));
                pedido.setValorTotal(rs.getDouble("valor_total"));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.err.println("### ERRO no PedidoDAO.buscarPorUsuario ### Falha ao buscar pedidos.");
            e.printStackTrace(); // <<< Adicionado para detalhar o erro
            throw e; // Re-lança a exceção para que o controller saiba que algo deu errado
        }
        return pedidos;
    }

    public List<PedidoItem> buscarItensPorPedido(int pedidoId) throws SQLException {
        List<PedidoItem> itens = new ArrayList<>();
        String sql = "SELECT pi.quantidade, pi.preco_unitario, p.id as produto_id, p.nome as produto_nome " +
                "FROM pedido_itens pi JOIN produtos p ON pi.produto_id = p.id " +
                "WHERE pi.pedido_id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("produto_id"));
                produto.setNome(rs.getString("produto_nome"));
                PedidoItem item = new PedidoItem();
                item.setProduto(produto);
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                itens.add(item);
            }
        } catch (SQLException e) {
            System.err.println("### ERRO no PedidoDAO.buscarItensPorPedido ### Falha ao buscar itens do pedido.");
            e.printStackTrace(); // <<< Adicionado para detalhar o erro
            throw e; // Re-lança a exceção
        }
        return itens;
    }
}