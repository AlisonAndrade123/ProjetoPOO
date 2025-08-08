package org.example.pooprojeto.model;

import javafx.scene.image.Image;
import java.io.File;
import java.util.Objects;

public class Produto {

    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int quantidade;
    private String categoria;
    private String nomeArquivoImagem;
    private transient Image image;

    public Produto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNomeArquivoImagem() {
        return nomeArquivoImagem;
    }

    public void setNomeArquivoImagem(String nomeArquivoImagem) {
        this.nomeArquivoImagem = nomeArquivoImagem;
        this.image = null;
    }

    public Image getImage() {
        if (image == null) {
            if (this.nomeArquivoImagem != null && !this.nomeArquivoImagem.isEmpty()) {
                try {
                    File arquivoImagem = new File("imagens_produtos" + File.separator + this.nomeArquivoImagem);
                    if (arquivoImagem.exists()) {
                        this.image = new Image(arquivoImagem.toURI().toString());
                    } else {
                        loadPlaceholder();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loadPlaceholder();
                }
            } else {
                loadPlaceholder();
            }
        }
        return image;
    }

    private void loadPlaceholder() {
        try {
            this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/pooprojeto/imagens/placeholder.png")));
        } catch (Exception e) {
            e.printStackTrace();
            this.image = null;
        }
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", quantidade=" + quantidade +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}