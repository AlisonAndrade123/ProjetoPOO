package org.example.pooprojeto.service;

import org.example.pooprojeto.dao.UsuarioDAO;
import org.example.pooprojeto.model.Usuario;
import org.example.pooprojeto.util.AppException;

import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

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
}