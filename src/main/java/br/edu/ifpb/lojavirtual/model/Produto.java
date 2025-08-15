package br.edu.ifpb.lojavirtual.model;

import br.edu.ifpb.lojavirtual.MainApp; // Importe o MainApp
import br.edu.ifpb.lojavirtual.util.PathUtils; // Importe o PathUtils
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class Produto {

    // Seus atributos existentes...
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int quantidade;
    private String categoria;
    private String nomeArquivoImagem;
    private transient Image image; // 'transient' é bom para evitar serialização

    // Número de produtos padrão que são inseridos pelo DatabaseManager
    private static final int NUMERO_DE_PRODUTOS_PADRAO = 14;

    public Produto() {
    }

    // Seus getters e setters existentes...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getNomeArquivoImagem() { return nomeArquivoImagem; }

    public void setNomeArquivoImagem(String nomeArquivoImagem) {
        this.nomeArquivoImagem = nomeArquivoImagem;
        this.image = null; // Invalida a imagem em cache para que seja recarregada
    }

    /**
     * Verifica se este produto é um dos produtos padrão da aplicação.
     * @return true se for um produto padrão, false caso contrário.
     */
    public boolean isPadrao() {
        // Assume-se que os produtos inseridos pelo DatabaseManager têm IDs de 1 a 14.
        return this.id > 0 && this.id <= NUMERO_DE_PRODUTOS_PADRAO;
    }

    /**
     * Carrega a imagem do produto de forma inteligente.
     * Se for um produto padrão, carrega de dentro do JAR.
     * Se for um produto do usuário, carrega da pasta externa 'imagens_produtos'.
     * @return O objeto Image do produto ou um placeholder se não for encontrado.
     */
    public Image getImage() {
        if (image == null) {
            if (this.nomeArquivoImagem != null && !this.nomeArquivoImagem.isEmpty()) {
                try {
                    if (isPadrao()) {
                        // ** LÓGICA PARA PRODUTOS PADRÃO (DENTRO DO JAR) **
                        String caminhoRecurso = "/br/edu/ifpb/lojavirtual/imagens_padrao/" + this.nomeArquivoImagem;
                        InputStream stream = MainApp.class.getResourceAsStream(caminhoRecurso);
                        if (stream != null) {
                            this.image = new Image(stream);
                        } else {
                            System.err.println("Imagem padrão não encontrada no JAR: " + caminhoRecurso);
                            loadPlaceholder();
                        }
                    } else {
                        // ** LÓGICA PARA PRODUTOS DO USUÁRIO (FORA DO JAR) **
                        File baseDir = PathUtils.getApplicationBaseDir();
                        File arquivoImagem = new File(baseDir, "imagens_produtos" + File.separator + this.nomeArquivoImagem);
                        if (arquivoImagem.exists()) {
                            this.image = new Image(arquivoImagem.toURI().toString());
                        } else {
                            System.err.println("Imagem do usuário não encontrada no disco: " + arquivoImagem.getAbsolutePath());
                            loadPlaceholder();
                        }
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

    /**
     * Carrega uma imagem de placeholder a partir dos recursos internos.
     */
    private void loadPlaceholder() {
        try {
            // Garanta que você tem um arquivo 'placeholder.png' nesta localização
            String placeholderPath = "/br/edu/ifpb/lojavirtual/imagens/placeholder.png";
            InputStream stream = Objects.requireNonNull(getClass().getResourceAsStream(placeholderPath));
            this.image = new Image(stream);
        } catch (Exception e) {
            System.err.println("Falha ao carregar a imagem placeholder!");
            e.printStackTrace();
            this.image = null; // Retorna nulo se nem o placeholder puder ser carregado
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