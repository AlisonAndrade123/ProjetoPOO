package org.example.pooprojeto.service;

import org.example.pooprojeto.dao.UsuarioDAO;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.AppException;

import java.util.Optional;

/**
 * Serviço de Autenticação (Singleton).
 * Gerencia a lógica de login/registro e a sessão do usuário logado.
 */
public class AuthService {

    // Instância única (Singleton) para toda a aplicação.
    private static AuthService instance;

    // Campo para armazenar o usuário que está atualmente logado.
    private Usuario usuarioLogado;

    private final UsuarioDAO usuarioDAO;

    // O construtor agora é privado para forçar o uso do getInstance().
    private AuthService() {
        this.usuarioDAO = new UsuarioDAO(); // O DAO é instanciado aqui dentro.
    }

    /**
     * Ponto de acesso global para a instância do AuthService.
     * @return A instância única do serviço.
     */
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
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

        //  Armazena o usuário na sessão após o sucesso do login.
        this.usuarioLogado = usuario;

        System.out.println("Usuário logado com sucesso: " + usuario.getNome() + " (Admin: " + usuario.isAdmin() + ")");
        return usuario;
    }

    public Usuario register(String nome, String email, String senha, boolean isAdmin) throws AppException {
        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.isEmpty()) {
            throw new AppException("Todos os campos são obrigatórios para o registro.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new AppException("Formato de e-mail inválido.");
        }

        Usuario novoUsuario = new Usuario(nome, email, senha, isAdmin);
        return usuarioDAO.save(novoUsuario);
    }

    /**
     *  Método para obter o usuário atualmente logado de qualquer parte do código.
     * @return O usuário logado, ou null se ninguém estiver logado.
     */
    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    /**
     *  Método para fazer logout, limpando a sessão.
     */
    public void logout() {
        System.out.println("Usuário deslogado: " + (usuarioLogado != null ? usuarioLogado.getNome() : "N/A"));
        this.usuarioLogado = null;
    }
}