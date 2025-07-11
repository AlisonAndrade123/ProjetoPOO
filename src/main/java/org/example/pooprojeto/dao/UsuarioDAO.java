package org.example.pooprojeto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Importar Statement para obter chaves geradas
import java.util.Optional; // Importar Optional para findByEmail
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.DatabaseManager;
import org.example.pooprojeto.util.AppException; // Assumindo que você tem essa exceção personalizada

public class UsuarioDAO {

    /**
     * Tenta autenticar um usuário com base no e-mail e senha.
     * Este método é mais adequado para ser usado pelo AuthService.
     * @param email O e-mail do usuário.
     * @param senha A senha em texto puro (ATENÇÃO: Em produção, use hashing de senha!).
     * @return Um objeto Usuario se a autenticação for bem-sucedida, caso contrário, null.
     */
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT id, nome, email, senha, is_admin FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setAdmin(rs.getBoolean("is_admin")); // ou rs.getInt("is_admin") == 1
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário no DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca um usuário pelo seu e-mail.
     * @param email O e-mail do usuário a ser buscado.
     * @return Um Optional contendo o Usuario se encontrado, ou um Optional vazio.
     */
    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT id, nome, email, senha, is_admin FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setAdmin(rs.getBoolean("is_admin"));
                return Optional.of(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por e-mail: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Salva um novo usuário no banco de dados.
     * Este método é utilizado para registro (cadastro).
     * @param usuario O objeto Usuario a ser salvo.
     * @return O objeto Usuario salvo, com o ID gerado pelo banco.
     * @throws AppException Se o e-mail já estiver em uso.
     */
    public Usuario save(Usuario usuario) throws AppException {
        // Primeiro, verifique se o email já existe para evitar duplicatas
        if (findByEmail(usuario.getEmail()).isPresent()) {
            throw new AppException("E-mail já cadastrado.");
        }

        String sql = "INSERT INTO usuarios (nome, email, senha, is_admin) VALUES (?, ?, ?, ?)";
        // GET_GENERATED_KEYS é importante para obter o ID que o banco de dados gerou
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setBoolean(4, usuario.isAdmin());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar usuário, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1)); // Define o ID gerado no objeto Usuario
                } else {
                    throw new SQLException("Falha ao criar usuário, nenhum ID gerado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
            throw new AppException("Erro ao cadastrar usuário: " + e.getMessage());
        }
        return usuario; // Retorna o usuário com o ID preenchido
    }

    // Você pode adicionar um método de atualização também, se necessário
    /*
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, is_admin = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setBoolean(4, usuario.isAdmin());
            stmt.setInt(5, usuario.getId());
            stmt.executeUpdate();
        }
    }
    */
}