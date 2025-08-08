package org.example.pooprojeto.dao;

import org.example.pooprojeto.model.Pedido;
import org.example.pooprojeto.model.PedidoItem;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    /**
     * Salva um pedido completo (cabeçalho e itens) no banco de dados.
     * Esta operação é transacional.
     */
    public void salvar(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (usuario_id, data_pedido, valor_total) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            // Inicia a transação
            conn.setAutoCommit(false);

            // 1. Insere o cabeçalho do pedido e obtém o ID gerado
            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, pedido.getUsuarioId());
                pstmtPedido.setString(2, pedido.getDataPedido());
                pstmtPedido.setDouble(3, pedido.getValorTotal());
                pstmtPedido.executeUpdate();

                try (ResultSet generatedKeys = pstmtPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedido.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Falha ao criar o pedido, nenhum ID obtido.");
                    }
                }
            }

            // 2. Insere cada item do pedido
            try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                for (PedidoItem item : pedido.getItens()) {
                    pstmtItem.setInt(1, pedido.getId());
                    pstmtItem.setInt(2, item.getProduto().getId());
                    pstmtItem.setInt(3, item.getQuantidade());
                    pstmtItem.setDouble(4, item.getPrecoUnitario());
                    pstmtItem.addBatch(); // Adiciona a inserção ao lote
                }
                pstmtItem.executeBatch(); // Executa todas as inserções de itens de uma vez
            }

            // Se tudo deu certo, efetiva a transação
            conn.commit();

        } catch (SQLException e) {
            // Se algo deu errado, desfaz a transação
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e; // Lança a exceção para o controller saber que falhou
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaura o modo padrão
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Seu método buscarPorUsuario continua aqui...
    public List<Pedido> buscarPorUsuario(int usuarioId) throws SQLException {
        // ... seu código do método buscarPorUsuario ...
        // (Ele já está correto no arquivo que você me mandou)
        Map<Integer, Pedido> pedidosMap = new LinkedHashMap<>();
        String sql = "SELECT ped.id as pedido_id, ped.data_pedido, ped.valor_total, item.id as item_id, item.quantidade as item_quantidade, item.preco_unitario, prod.id as produto_id, prod.nome as produto_nome, prod.descricao as produto_descricao, prod.preco as produto_preco, prod.quantidade as produto_quantidade_estoque, prod.categoria as produto_categoria, prod.nome_arquivo_imagem FROM pedidos ped JOIN pedido_itens item ON ped.id = item.pedido_id JOIN produtos prod ON item.produto_id = prod.id WHERE ped.usuario_id = ? ORDER BY ped.id DESC, item.id ASC";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                int pedidoId = rs.getInt("pedido_id");
                Pedido pedido = pedidosMap.computeIfAbsent(pedidoId, id -> {
                    Pedido p = new Pedido();
                    p.setId(id);
                    p.setUsuarioId(usuarioId);
                    try {
                        p.setDataPedido(rs.getString("data_pedido"));
                        p.setValorTotal(rs.getDouble("valor_total"));
                        p.setItens(new ArrayList<>());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return p;
                });
                Produto produto = new Produto();
                produto.setId(rs.getInt("produto_id"));
                produto.setNome(rs.getString("produto_nome"));
                produto.setDescricao(rs.getString("produto_descricao"));
                produto.setPreco(rs.getDouble("produto_preco"));
                produto.setCategoria(rs.getString("produto_categoria"));
                produto.setQuantidade(rs.getInt("produto_quantidade_estoque"));
                produto.setNomeArquivoImagem(rs.getString("nome_arquivo_imagem"));
                PedidoItem item = new PedidoItem();
                item.setId(rs.getInt("item_id"));
                item.setQuantidade(rs.getInt("item_quantidade"));
                item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                item.setProduto(produto);
                pedido.getItens().add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return new ArrayList<>(pedidosMap.values());
    }
}