package org.example.pooprojeto.model;

import javafx.scene.image.Image;
import java.io.File;

/**
 * Representa um Produto na loja virtual.
 * ATUALIZADO para carregar imagens de uma pasta externa.
 */
public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int quantidade;
    private String categoria;

    // <<< MUDANÇA 1: Renomeado de 'imageUrl' para 'nomeArquivoImagem' para maior clareza.
    private String nomeArquivoImagem;

    // Campo 'transient' para guardar a imagem carregada em memória, sem tentar salvá-la no banco.
    private transient Image image;

    // Construtor vazio para facilitar a criação de objetos.
    public Produto() {
    }

    // Construtores não são estritamente necessários, mas podem ser mantidos se você os usa em algum lugar.
    // Lembre-se de atualizar os parâmetros se os mantiver.

    // --- Getters ---
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public String getCategoria() { return categoria; }

    // <<< MUDANÇA 2: Getter renomeado para corresponder ao novo nome do campo.
    public String getNomeArquivoImagem() { return nomeArquivoImagem; }

    /**
     * <<< MUDANÇA 3: Lógica de carregamento de imagem TOTALMENTE ATUALIZADA.
     * Agora, ela carrega a imagem da pasta externa "imagens_produtos".
     * Se falhar, carrega uma imagem "placeholder" dos recursos internos.
     */
    public Image getImage() {
        // Carrega a imagem apenas na primeira vez que for solicitada (Lazy Loading)
        if (image == null && this.nomeArquivoImagem != null && !this.nomeArquivoImagem.isEmpty()) {
            try {
                // Constrói o caminho para o arquivo na pasta externa
                File arquivoImagem = new File("imagens_produtos" + File.separator + this.nomeArquivoImagem);

                if (arquivoImagem.exists()) {
                    // Se o arquivo existir, carrega-o
                    image = new Image(arquivoImagem.toURI().toString());
                } else {
                    // Se não existir, registra o erro e carrega a imagem padrão
                    System.err.println("Arquivo de imagem não encontrado: " + arquivoImagem.getPath());
                    loadPlaceholder();
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem " + this.nomeArquivoImagem + ": " + e.getMessage());
                loadPlaceholder(); // Carrega a imagem padrão em caso de qualquer outro erro
            }
        } else if (image == null) {
            // Se o produto não tiver um nome de arquivo de imagem, carrega a imagem padrão
            loadPlaceholder();
        }
        return image;
    }

    // Método auxiliar para não repetir o código do placeholder
    private void loadPlaceholder() {
        try {
            this.image = new Image(getClass().getResourceAsStream("/org/example/pooprojeto/Imagens/placeholder.png"));
        } catch (Exception e) {
            System.err.println("CRÍTICO: Não foi possível carregar a imagem placeholder! " + e.getMessage());
            this.image = null; // Deixa nulo se até o placeholder falhar
        }
    }


    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    /**
     * <<< MUDANÇA 4: Setter renomeado.
     * Ao definir um novo nome de arquivo, invalida a imagem antiga em cache.
     */
    public void setNomeArquivoImagem(String nomeArquivoImagem) {
        this.nomeArquivoImagem = nomeArquivoImagem;
        this.image = null; // Força o recarregamento na próxima chamada de getImage()
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