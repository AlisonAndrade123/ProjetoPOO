package org.example.pooprojeto.model;

import javafx.scene.image.Image;

/**
 * Representa um Produto na loja virtual.
 */
public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int quantidade; // <<< MUDANÇA: Renomeado de 'estoque' para 'quantidade'
    private String categoria;
    private String imageUrl;
    private transient Image image;

    // <<< MUDANÇA: Adicionado construtor vazio, exigido pelo controller
    public Produto() {
    }

    // Construtor completo (atualizado para usar 'quantidade')
    public Produto(int id, String nome, String descricao, double preco, int quantidade, String categoria, String imageUrl) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade; // <<< MUDANÇA
        this.categoria = categoria;
        this.imageUrl = imageUrl;
    }

    // Construtor para novos produtos (atualizado para usar 'quantidade')
    public Produto(String nome, String descricao, double preco, int quantidade, String categoria, String imageUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade; // <<< MUDANÇA
        this.categoria = categoria;
        this.imageUrl = imageUrl;
    }

    // --- Getters (atualizado para usar 'quantidade') ---
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; } // <<< MUDANÇA: Renomeado de 'getEstoque'
    public String getCategoria() { return categoria; }
    public String getImageUrl() { return imageUrl; }
    public Image getImage() {
        if (image == null && imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Ajuste para o caminho correto se necessário
                image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/Imagens/" + imageUrl));
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem para produto " + nome + ": " + imageUrl + " - " + e.getMessage());
                try {
                    image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/Imagens/placeholder.png"));
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar placeholder: " + ex.getMessage());
                    image = null;
                }
            }
        }
        return image;
    }

    // --- Setters (atualizado para usar 'quantidade') ---
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; } // <<< MUDANÇA: Renomeado de 'setEstoque'
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; this.image = null; }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", quantidade=" + quantidade + // <<< MUDANÇA
                ", categoria='" + categoria + '\'' +
                '}';
    }
}