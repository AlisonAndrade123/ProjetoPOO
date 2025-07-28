package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;
import org.example.pooprojeto.util.ModalCallback; // <<< USA NOSSA NOVA INTERFACE

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
// A importação de 'java.util.function.Runnable' foi REMOVIDA

public class CadastrarProdutoController {

    @FXML private TextField nomeTextField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private TextField imagemTextField;
    @FXML private TextField quantidadeTextField;
    @FXML private TextField descricaoTextField;
    @FXML private TextField precoTextField;
    @FXML private Button finalizarCadastroButton;

    private ProdutoDAO produtoDAO;
    private File imagemSelecionada;
    private Produto produtoParaEditar;

    // <<< MUDANÇA 1: O tipo do campo agora é a nossa interface ModalCallback.
    private ModalCallback onCloseCallback;

    public void setProdutoDAO(ProdutoDAO produtoDAO) { this.produtoDAO = produtoDAO; }
    public void setCategorias(List<String> categorias) { categoriaComboBox.getItems().setAll(categorias); }

    // <<< MUDANÇA 2: O método agora aceita um ModalCallback.
    public void setOnCloseCallback(ModalCallback callback) {
        this.onCloseCallback = callback;
    }

    public void carregarDadosParaEdicao(Produto produto) {
        this.produtoParaEditar = produto;
        nomeTextField.setText(produto.getNome());
        descricaoTextField.setText(produto.getDescricao());
        quantidadeTextField.setText(String.valueOf(produto.getQuantidade()));
        precoTextField.setText(String.format("%.2f", produto.getPreco()).replace('.', ','));
        categoriaComboBox.setValue(produto.getCategoria());
        if (produto.getNomeArquivoImagem() != null) {
            imagemTextField.setText(produto.getNomeArquivoImagem());
        }
        finalizarCadastroButton.setText("Salvar Alterações");
    }

    @FXML
    public void initialize() { imagemTextField.setEditable(false); }

    @FXML
    void handleProcurarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem do Produto");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(nomeTextField.getScene().getWindow());
        if (file != null) {
            imagemSelecionada = file;
            imagemTextField.setText(imagemSelecionada.getName());
        }
    }

    @FXML
    void handleFinalizarCadastro(ActionEvent event) {
        if (!validarCampos()) { return; }
        try {
            String nomeArquivoImagem = null;
            if (imagemSelecionada != null) {
                nomeArquivoImagem = salvarImagemLocalmente(imagemSelecionada);
            }
            if (produtoParaEditar == null) {
                Produto novoProduto = new Produto();
                preencherDadosDoFormulario(novoProduto, nomeArquivoImagem);
                produtoDAO.save(novoProduto);
            } else {
                preencherDadosDoFormulario(produtoParaEditar, nomeArquivoImagem);
                produtoDAO.update(produtoParaEditar);
            }
            if (onCloseCallback != null) {
                // <<< MUDANÇA 3: Chamamos o método da nossa interface.
                onCloseCallback.onModalClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void preencherDadosDoFormulario(Produto produto, String nomeArquivoImagem) {
        produto.setNome(nomeTextField.getText());
        produto.setCategoria(categoriaComboBox.getValue());
        produto.setDescricao(descricaoTextField.getText());
        produto.setQuantidade(Integer.parseInt(quantidadeTextField.getText()));
        produto.setPreco(Double.parseDouble(precoTextField.getText().replace(',', '.')));
        if (nomeArquivoImagem != null) {
            produto.setNomeArquivoImagem(nomeArquivoImagem);
        }
    }

    private boolean validarCampos() {
        // Esta validação é simples, mas funcional.
        // Em um sistema real, seria bom mostrar um alerta.
        return !nomeTextField.getText().trim().isEmpty() &&
                categoriaComboBox.getValue() != null &&
                !quantidadeTextField.getText().trim().isEmpty() &&
                !precoTextField.getText().trim().isEmpty();
    }

    private String salvarImagemLocalmente(File arquivoImagem) throws IOException {
        Path diretorioDestino = Paths.get("imagens_produtos");
        if (!Files.exists(diretorioDestino)) {
            Files.createDirectories(diretorioDestino);
        }
        String nomeUnico = System.currentTimeMillis() + "_" + arquivoImagem.getName();
        Path arquivoDestino = diretorioDestino.resolve(nomeUnico);
        Files.copy(arquivoImagem.toPath(), arquivoDestino, StandardCopyOption.REPLACE_EXISTING);
        return nomeUnico;
    }
}