package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.pooprojeto.dao.ProdutoDAO;
import org.example.pooprojeto.model.Produto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CadastrarProdutoController {
    @FXML private TextField nomeTextField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private TextField imagemTextField;
    @FXML private TextField quantidadeTextField;
    @FXML private TextField descricaoTextField;
    @FXML private TextField precoTextField;
    @FXML private Button finalizarCadastroButton;

    // <<< MUDANÇA: A referência à Stage está de volta >>>
    private Stage stage;
    private ProdutoDAO produtoDAO;
    private File imagemSelecionada;
    private Produto produtoParaEditar;

    // <<< MUDANÇA: O método setStage está de volta >>>
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProdutoDAO(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void setCategorias(List<String> categorias) {
        categoriaComboBox.getItems().setAll(categorias);
    }

    // O método onCloseCallback foi removido.

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
    public void initialize() {
        imagemTextField.setEditable(false);
    }

    @FXML
    void handleProcurarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem do Produto");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        // Agora usa a variável 'stage' da classe
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            imagemSelecionada = file;
            imagemTextField.setText(imagemSelecionada.getName());
        }
    }

    @FXML
    void handleFinalizarCadastro(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }
        try {
            String nomeArquivoImagem = null;
            if (imagemSelecionada != null) {
                nomeArquivoImagem = salvarImagemLocalmente(imagemSelecionada);
            }
            if (produtoParaEditar == null) {
                Produto novoProduto = new Produto();
                preencherDadosDoFormulario(novoProduto, nomeArquivoImagem);
                produtoDAO.save(novoProduto);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");
            } else {
                preencherDadosDoFormulario(produtoParaEditar, nomeArquivoImagem);
                produtoDAO.update(produtoParaEditar);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            }

            // <<< MUDANÇA: Agora o controlador se fecha sozinho >>>
            if (stage != null) {
                stage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao salvar: " + e.getMessage());
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
        String errorMessage = "";
        if (nomeTextField.getText().trim().isEmpty()) errorMessage += "Nome é obrigatório.\n";
        if (categoriaComboBox.getValue() == null) errorMessage += "Categoria é obrigatória.\n";
        if (quantidadeTextField.getText().trim().isEmpty()) errorMessage += "Quantidade é obrigatória.\n";
        if (precoTextField.getText().trim().isEmpty()) errorMessage += "Preço é obrigatório.\n";

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }
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

    // <<< MUDANÇA: O método showAlert está de volta >>>
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (this.stage != null) {
            alert.initOwner(this.stage);
        }
        alert.showAndWait();
    }
}