package org.example.pooprojeto.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

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

            // <<< MUDANÇA 1: Renomeando 'image_url' para 'nome_arquivo_imagem' para maior clareza.
            String createProductsTable = "CREATE TABLE IF NOT EXISTS produtos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "descricao TEXT," +
                    "preco REAL NOT NULL," +
                    "quantidade INTEGER NOT NULL," +
                    "categoria TEXT NOT NULL," +
                    "nome_arquivo_imagem TEXT)"; // <<< MUDANÇA AQUI
            stmt.execute(createProductsTable);
            System.out.println("Tabela 'produtos' criada/verificada.");

            // Inserir um usuário administrador padrão se não existir
            inserirAdminPadrao(conn);

            // Inserir produtos de exemplo se a tabela de produtos estiver vazia
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM produtos")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Inserindo produtos de exemplo...");
                    // <<< MUDANÇA 1: Atualizando o nome da coluna na inserção dos dados de exemplo
                    stmt.execute("INSERT INTO produtos (nome, descricao, preco, quantidade, categoria, nome_arquivo_imagem) VALUES " + // <<< MUDANÇA AQUI
                            "('Teclado Gamer RGB', 'Teclado mecânico com iluminação RGB personalizável e switches de alta durabilidade.', 279.99, 50, 'Teclado', 'teclado_gamer.png')," +
                            "('Mouse Gamer 16000DPI', 'Mouse óptico de alta precisão com 16000 DPI, ideal para jogos competitivos.', 189.99, 70, 'Mouse', 'mouse_gamer.png')," +
                            "('Monitor 24'' Full HD', 'Monitor de 24 polegadas com resolução Full HD para uma experiência visual imersiva.', 899.99, 30, 'Monitor', 'monitor_fullhd.png')," +
                            "('Processador i7 10700K', 'Processador Intel Core i7 de 10ª geração, ideal para alto desempenho em jogos e tarefas.', 1899.99, 20, 'Processador', 'processador_i7.png')," +
                            "('Placa de Vídeo RTX 3060', 'Placa de vídeo NVIDIA GeForce RTX 3060, oferece gráficos incríveis e Ray Tracing.', 2500.00, 15, 'Placa mãe', 'rtx_3060.png')," +
                            "('Gabinete Gamer RGB', 'Gabinete moderno com painel lateral de vidro temperado e iluminação RGB.', 349.99, 40, 'Gabinete', 'gabinete_gamer.png')," +
                            "('Webcam Full HD', 'Webcam com resolução Full HD 1080p para videochamadas e streaming de alta qualidade.', 199.99, 60, 'Webcam', 'webcam_hd.png')," +
                            "('Headset Gamer 7.1', 'Headset gamer com som surround 7.1 e microfone retrátil para comunicação clara.', 349.99, 55, 'Fones', 'headset_gamer.png')," +
                            "('Fonte 600W 80 Plus', 'Fonte de alimentação de 600W com certificação 80 Plus Bronze para eficiência energética.', 400.00, 25, 'Fonte', 'fonte_600w.png')," +
                            "('Placa Mãe B550', 'Placa mãe AMD B550 compatível com processadores Ryzen, oferece alta performance.', 899.99, 35, 'Placa mãe', 'placa_mae_b550.png')," +
                            "('Memória RAM 16GB', 'Kit de memória RAM DDR4 de 16GB (2x8GB) para melhor desempenho do sistema.', 479.99, 80, 'Memória RAM', 'memoria_ram_16gb.png')," +
                            "('Cooler CPU RGB', 'Cooler para CPU com iluminação RGB e alta capacidade de dissipação de calor.', 150.00, 45, 'Cooler', 'cooler_rgb.png')," +
                            "('SSD 1TB NVMe', 'Armazenamento SSD NVMe de 1TB para velocidades de leitura e gravação ultrarrápidas.', 599.99, 50, 'SSD', 'ssd_nvme.png')," +
                            "('HD Externo 2TB', 'Disco rígido externo portátil de 2TB para backup e armazenamento de dados.', 350.00, 30, 'HD', 'hd_externo.png')," +
                            "('Microfone USB', 'Microfone USB de alta qualidade para gravação de voz e streaming.', 249.99, 65, 'Microfone', 'microfone_usb.png')," +
                            "('Mousepad Gamer XXL', 'Mousepad grande para gamers, otimizado para precisão e conforto.', 79.99, 90, 'Mouse', 'mousepad_gamer.png')," +
                            "('Cadeira Gamer', 'Cadeira ergonômica para gamers, oferece conforto e suporte durante longas sessões.', 1200.00, 20, 'Gabinete', 'cadeira_gamer.png')," +
                            "('Monitor Curvo 27''', 'Monitor curvo de 27 polegadas para uma experiência de jogo imersiva.', 1800.00, 18, 'Monitor', 'monitor_curvo.png')," +
                            "('Water Cooler 240mm', 'Sistema de refrigeração líquida para CPU, mantém temperaturas baixas em cargas intensas.', 450.00, 22, 'Cooler', 'water_cooler.png');"
                    );
                    System.out.println("Produtos de exemplo inseridos.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * <<< MUDANÇA 2: Método auxiliar para inserir o admin usando PreparedStatement para maior segurança.
     */
    private static void inserirAdminPadrao(Connection conn) throws SQLException {
        String adminEmail = "admin@sistema.com";
        String adminPassword = "admin123"; // Em um projeto real, isso deveria ser "hasheado"

        // Primeiro, verifica se o usuário já existe
        String checkUserSql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (PreparedStatement pstmtCheck = conn.prepareStatement(checkUserSql)) {
            pstmtCheck.setString(1, adminEmail);
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Usuário já existe, não faz nada
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
            System.out.println("Usuário administrador padrão criado: " + adminEmail + " / " + adminPassword);
        }
    }
}