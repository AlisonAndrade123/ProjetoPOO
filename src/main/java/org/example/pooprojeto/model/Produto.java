package org.example.pooprojeto.model;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Representa um Produto na loja virtual.
 */
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

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNomeArquivoImagem() {
        return nomeArquivoImagem;
    }


    public Image getImage() {
        // Carrega a imagem apenas na primeira vez que for solicitada (Lazy Loading)
        if (image == null && this.nomeArquivoImagem != null && !this.nomeArquivoImagem.isEmpty()) {
            try {
                // Constrói o caminho para o arquivo na pasta externa
                File arquivoImagem = new File("imagens_produtos" + File.separator + this.nomeArquivoImagem);

                if (arquivoImagem.exists()) {
                    image = new Image(arquivoImagem.toURI().toString());
                } else {
                    System.err.println("Arquivo de imagem não encontrado: " + arquivoImagem.getPath());
                    loadPlaceholder();
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem " + this.nomeArquivoImagem + ": " + e.getMessage());
                loadPlaceholder();
            }
        } else if (image == null) {
            loadPlaceholder();
        }
        return image;
    }

    private void loadPlaceholder() {
        try {
            this.image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/imagens/placeholder.png"));
        } catch (Exception e) {

            this.image = null;
        }
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Define o nome do arquivo da imagem do produto.
     * Este método também força o recarregamento da imagem na próxima chamada de getImage().
     *
     * @param nomeArquivoImagem O nome do arquivo da imagem.
     */
    public void setNomeArquivoImagem(String nomeArquivoImagem) {
        this.nomeArquivoImagem = nomeArquivoImagem;
        this.image = null;
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