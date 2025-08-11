package br.edu.ifpb.lojavirtual.util;

import java.sql.*;

/**
 * Gerencia a conexão com o banco de dados SQLite e a criação inicial das tabelas.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:loja_virtual.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Inicializa a estrutura do banco de dados e insere dados padrão se necessário.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "senha TEXT NOT NULL," +
                    "is_admin INTEGER DEFAULT 0)";
            stmt.execute(createUsersTable);

            String createProductsTable = "CREATE TABLE IF NOT EXISTS produtos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "descricao TEXT," +
                    "preco REAL NOT NULL," +
                    "quantidade INTEGER NOT NULL," +
                    "categoria TEXT NOT NULL," +
                    "nome_arquivo_imagem TEXT)";
            stmt.execute(createProductsTable);

            String createPedidosTable = "CREATE TABLE IF NOT EXISTS pedidos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER NOT NULL," +
                    "data_pedido TEXT NOT NULL," +
                    "valor_total REAL NOT NULL," +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios(id))";
            stmt.execute(createPedidosTable);

            String createPedidoItensTable = "CREATE TABLE IF NOT EXISTS pedido_itens (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pedido_id INTEGER NOT NULL," +
                    "produto_id INTEGER NOT NULL," +
                    "quantidade INTEGER NOT NULL," +
                    "preco_unitario REAL NOT NULL," +
                    "FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (produto_id) REFERENCES produtos(id))";
            stmt.execute(createPedidoItensTable);

            // Inserir um usuário administrador padrão se não existir
            inserirAdminPadrao(conn);

            // Inserir produtos padrão se a tabela estiver vazia
            inserirProdutosPadrao(conn);

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inserirAdminPadrao(Connection conn) throws SQLException {
        String adminEmail = "admin@sistema.com";
        String adminPassword = "admin123";

        String checkUserSql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (PreparedStatement pstmtCheck = conn.prepareStatement(checkUserSql)) {
            pstmtCheck.setString(1, adminEmail);
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String insertUserSql = "INSERT INTO usuarios (nome, email, senha, is_admin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtInsert = conn.prepareStatement(insertUserSql)) {
            pstmtInsert.setString(1, "Administrador");
            pstmtInsert.setString(2, adminEmail);
            pstmtInsert.setString(3, adminPassword);
            pstmtInsert.setInt(4, 1);
            pstmtInsert.executeUpdate();
        }
    }

    /**
     * Insere uma lista de produtos padrão na tabela 'produtos' se ela estiver vazia.
     *
     * @param conn A conexão com o banco de dados.
     * @throws SQLException Se ocorrer um erro de SQL.
     */
    private static void inserirProdutosPadrao(Connection conn) throws SQLException {
        String checkProductsSql = "SELECT COUNT(*) FROM produtos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkProductsSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Se já houver produtos, não faz nada
            }
        }

        String insertProductSql = "INSERT INTO produtos (nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertProductSql)) {

            Object[][] produtos = {
                    {"Teclado com fio", "Confiável e pronto para usar, este teclado com fio oferece uma conexão estável e digitação precisa para todas as suas tarefas.", 25.0, 10, "Teclado", "teclado.png"},
                    {"Mouse com fio", "Este mouse com fio oferece precisão e confiabilidade para todas as suas tarefas.", 10.0, 10, "Mouse", "mouse.png"},
                    {"Monitor 22 Polegadas Full HD", "Desfrute de imagens nítidas e vibrantes com este monitor Full HD de 22 polegadas. Ideal para trabalho, estudo e entretenimento.", 350.0, 10, "Monitor", "monitor_22.png"},
                    {"Intel Core i5 3330", "O Intel Core i5-3330 é um processador quad-core da 3ª geração, ideal para computadores de mesa, oferecendo desempenho sólido para tarefas do dia a dia e multitarefas.", 150.0, 10, "Processador", "intel_i5_3330.png"},
                    {"AMD FX-4300", "O AMD FX-4300 é um processador quad-core de 3.8GHz, ideal para PCs mais antigos, oferecendo desempenho básico para uso diário e jogos que não exigem muito.", 160.0, 10, "Processador", "amd_fx.png"},
                    {"Placa de vídeo - MSI ATI Radeon HD 5450", "A MSI ATI Radeon HD 5450 é uma placa de vídeo de baixo perfil projetada para oferecer desempenho gráfico básico e funcionalidade de vídeo de alta definição.", 150.0, 10, "GPU", "msi_5450.png"},
                    {"Placa de vídeo RX 550 4gb", "A RX 550 é uma placa de vídeo que pode rodar diversos jogos, mas com configurações e resoluções mais baixas, especialmente em jogos mais recentes e exigentes.", 400.0, 10, "GPU", "rx550.png"},
                    {"Placa de vídeo - MSI GTX 1650", "A GTX 1650 é uma placa de vídeo de entrada, adequada para jogos em Full HD (1080p) com configurações gráficas médias a baixas, sendo uma boa opção para quem busca um bom custo-benefício.", 1200.0, 10, "GPU", "msi_1650.png"},
                    {"Gabinete Gamer", "Gabinete com led.", 350.0, 10, "Gabinete", "gabinete_games.png"},
                    {"Gabinete", "Gabinete comum.", 70.0, 10, "Gabinete", "gabinete.png"},
                    {"Placa mãe lga 1155 b75", "Placa mãe para processadores Intel soquete lga 1155 com chipset b75.", 300.0, 10, "Placa mãe", "placa_mae_1155_b75.png"},
                    {"Placa mãe Socket AM3+", "Placa mãe para processadores AMD soquete AM3+.", 300.0, 10, "Placa mãe", "placa_mae_am3+.png"},
                    {"Memória ram 8gb DDR3", "Memória ram 8gb DDR3 com frequência de 1333 MHz.", 60.0, 10, "Memória RAM", "ram_ddr3.png"},
                    {"Memória ram 8gb DDR4", "Memória ram 8gb DDR4 com frequência de 3200 MHz.", 150.0, 10, "Memória RAM", "ram_ddr4.png"}
            };

            for (Object[] produto : produtos) {
                pstmt.setString(1, (String) produto[0]);
                pstmt.setString(2, (String) produto[1]);
                pstmt.setDouble(3, (Double) produto[2]);
                pstmt.setInt(4, (Integer) produto[3]);
                pstmt.setString(5, (String) produto[4]);
                pstmt.setString(6, (String) produto[5]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}