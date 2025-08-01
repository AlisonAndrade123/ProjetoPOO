package org.example.pooprojeto.util;

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

            // SQL para criar a tabela de usuários (sem alterações)
            String createUsersTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "senha TEXT NOT NULL," +
                    "is_admin INTEGER DEFAULT 0)";
            stmt.execute(createUsersTable);
            System.out.println("Tabela 'usuarios' criada/verificada.");

            String createProductsTable = "CREATE TABLE IF NOT EXISTS produtos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "descricao TEXT," +
                    "preco REAL NOT NULL," +
                    "quantidade INTEGER NOT NULL," +
                    "categoria TEXT NOT NULL," +
                    "nome_arquivo_imagem TEXT)";
            stmt.execute(createProductsTable);
            System.out.println("Tabela 'produtos' criada/verificada.");

            // Inserir um usuário administrador padrão se não existir
            inserirAdminPadrao(conn);

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inserirAdminPadrao(Connection conn) throws SQLException {
        String adminEmail = "admin@sistema.com";
        String adminPassword = "admin123";

        // Primeiro, verifica se o usuário já existe
        String checkUserSql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (PreparedStatement pstmtCheck = conn.prepareStatement(checkUserSql)) {
            pstmtCheck.setString(1, adminEmail);
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        // Se não existir, insere o novo usuário administrador
        String insertUserSql = "INSERT INTO usuarios (nome, email, senha, is_admin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtInsert = conn.prepareStatement(insertUserSql)) {
            pstmtInsert.setString(1, "Administrador");
            pstmtInsert.setString(2, adminEmail);
            pstmtInsert.setString(3, adminPassword);
            pstmtInsert.setInt(4, 1);
            pstmtInsert.executeUpdate();
        }
    }
}