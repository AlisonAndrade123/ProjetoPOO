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

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Tenta autenticar um usuário no sistema.
     *
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

        if (userOptional.isEmpty()) {
            throw new AppException("E-mail ou senha inválidos.");
        }

        Usuario usuario = userOptional.get();

        if (!usuario.getSenha().equals(senha)) {
            throw new AppException("E-mail ou senha inválidos.");
        }

        System.out.println("Usuário logado com sucesso: " + usuario.getNome() + " (Admin: " + usuario.isAdmin() + ")");
        return usuario;
    }

    /**
     * Registra um novo usuário no sistema.
     *
     * @param nome    O nome completo do novo usuário.
     * @param email   O e-mail do novo usuário (deve ser único).
     * @param senha   A senha do novo usuário.
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

        // Validação básica de formato de e-mail
        if (!email.contains("@") || !email.contains(".")) {
            throw new AppException("Formato de e-mail inválido.");
        }

        Usuario novoUsuario = new Usuario(nome, email, senha, isAdmin);
        return usuarioDAO.save(novoUsuario);
    }
}