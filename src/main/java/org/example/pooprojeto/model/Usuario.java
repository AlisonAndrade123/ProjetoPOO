package org.example.pooprojeto.model;

/**
 * Representa um Usuário do sistema, com informações de login e perfil.
 */
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha; // Em produção, armazene hashes de senha, não senhas em texto puro!
    private boolean isAdmin; // true para administrador, false para usuário comum

    // Construtor padrão (adicionado para flexibilidade, especialmente para DAOs)
    public Usuario() {
    }

    // Construtor para criar um novo usuário (sem ID, que será gerado pelo BD)
    public Usuario(String nome, String email, String senha, boolean isAdmin) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.isAdmin = isAdmin;
    }

    // Construtor para reconstruir um objeto Usuario a partir dos dados do banco (com ID)
    public Usuario(int id, String nome, String email, String senha, boolean isAdmin) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.isAdmin = isAdmin;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    } // Acesso à senha deve ser limitado

    public boolean isAdmin() {
        return isAdmin;
    }

    // --- Setters (se necessário para atualizações) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}