package org.example.pooprojeto.service;

import org.example.pooprojeto.dao.UsuarioDAO;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.AppException;

import java.util.Optional;

/**
 * Serviço de Autenticação. Contém a lógica de negócio para login e registro de usuários.
 * Interage com a camada DAO para persistência de dados.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO;

    // Injeção de dependência via construtor
    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Tenta autenticar um usuário no sistema.
     * @param email O e-mail fornecido pelo usuário.
     * @param senha A senha fornecida pelo usuário.
     * @return O objeto Usuario logado, se as credenciais forem válidas.
     * @throws AppException Se o login falhar (usuário não encontrado, senha incorreta, etc.).
     */
    public Usuario login(String email, String senha) throws AppException {
        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            throw new AppException("E-mail e senha são obrigatórios.");
        }

        Optional<Usuario> userOptional = usuarioDAO.findByEmail(email);

        // Verifica se o usuário com o e-mail fornecido existe
        if (userOptional.isEmpty()) {
            throw new AppException("E-mail ou senha inválidos."); // Mensagem genérica por segurança
        }

        Usuario usuario = userOptional.get(); // Obtém o objeto Usuario do Optional

        // Em um sistema real, aqui você usaria um método para comparar hashes de senha (ex: BCrypt.checkpw)
        if (!usuario.getSenha().equals(senha)) { // Comparação de senha em texto puro (APENAS PARA EXEMPLO)
            throw new AppException("E-mail ou senha inválidos.");
        }

        System.out.println("Usuário logado com sucesso: " + usuario.getNome() + " (Admin: " + usuario.isAdmin() + ")");
        return usuario;
    }

    /**
     * Registra um novo usuário no sistema.
     * @param nome O nome completo do novo usuário.
     * @param email O e-mail do novo usuário (deve ser único).
     * @param senha A senha do novo usuário.
     * @param isAdmin Indica se o usuário é um administrador (geralmente false para registros via interface).
     * @return O objeto Usuario recém-criado com o ID atribuído.
     * @throws AppException Se o registro falhar (e-mail já em uso, campos vazios, erro no BD).
     */
    public Usuario register(String nome, String email, String senha, boolean isAdmin) throws AppException {
        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.isEmpty()) {
            throw new AppException("Todos os campos são obrigatórios para o registro.");
        }

        // Validação básica de formato de e-mail (pode ser mais robusta com regex)
        if (!email.contains("@") || !email.contains(".")) {
            throw new AppException("Formato de e-mail inválido.");
        }

        Usuario novoUsuario = new Usuario(nome, email, senha, isAdmin);
        return usuarioDAO.save(novoUsuario); // O DAO já lida com a verificação de e-mail duplicado
    }
}