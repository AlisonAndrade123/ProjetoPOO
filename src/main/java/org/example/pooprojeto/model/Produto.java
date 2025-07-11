package org.example.pooprojeto.model;

import javafx.scene.image.Image; // Para a imagem do produto na UI

/**
 * Representa um Produto na loja virtual.
 */
public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int estoque;
    private String categoria; // Para o filtro de categorias
    private String imageUrl;  // Caminho da imagem do produto (opcional, pode ser base64 tbm)
    private transient Image image; // Usado para carregar a imagem na UI

    // Construtor completo
    public Produto(int id, String nome, String descricao, double preco, int estoque, String categoria, String imageUrl) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.categoria = categoria;
        this.imageUrl = imageUrl;
    }

    // Construtor para novos produtos (sem ID inicial)
    public Produto(String nome, String descricao, double preco, int estoque, String categoria, String imageUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.categoria = categoria;
        this.imageUrl = imageUrl;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }
    public String getCategoria() { return categoria; }
    public String getImageUrl() { return imageUrl; }
    public Image getImage() {
        if (image == null && imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Tenta carregar a imagem do classpath
                image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/images/" + imageUrl));
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem para produto " + nome + ": " + imageUrl + " - " + e.getMessage());
                // Carrega uma imagem de placeholder se a imagem real não for encontrada
                // Certifique-se de ter uma imagem 'placeholder.png' em 'src/main/resources/org/example/pooprojeto/images/'
                try {
                    image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/images/placeholder.png"));
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar placeholder: " + ex.getMessage());
                    // Se nem o placeholder funcionar, pode ser um Image vazio ou null para indicar a falha
                    image = null; // Ou new Image("file:path/to/default/image.png");
                }
            }
        }
        return image;
    }


    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setEstoque(int estoque) { this.estoque = estoque; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; this.image = null; } // Reseta a imagem carregada

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", estoque=" + estoque +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}