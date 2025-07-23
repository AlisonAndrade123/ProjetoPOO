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
import java.sql.SQLException;
import java.util.List;

public class CadastrarProdutoController {

    // ==== Elementos FXML ====
    @FXML private TextField nomeTextField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private TextField imagemTextField;
    @FXML private TextField quantidadeTextField;
    @FXML private TextField descricaoTextField;
    @FXML private TextField precoTextField;
    @FXML private Button finalizarCadastroButton;

    // ==== Atributos de Suporte ====
    private Stage stage;
    private ProdutoDAO produtoDAO;
    private File imagemSelecionada;
    private Produto produtoParaEditar;

    // ==== Métodos Injetores ====
    public void setStage(Stage stage) { this.stage = stage; }
    public void setProdutoDAO(ProdutoDAO produtoDAO) { this.produtoDAO = produtoDAO; }
    public void setCategorias(List<String> categorias) { categoriaComboBox.getItems().setAll(categorias); }
    public Stage getStage() { return this.stage; }

    public void carregarDadosParaEdicao(Produto produto) {
        // <<< DEBUG: VERIFICANDO A RECEPÇÃO DOS DADOS
        System.out.println("DEBUG: [CadastrarProdutoController] -> carregarDadosParaEdicao CHAMADO para: " + produto.getNome());
        this.produtoParaEditar = produto;
        nomeTextField.setText(produto.getNome());
        descricaoTextField.setText(produto.getDescricao());
        quantidadeTextField.setText(String.valueOf(produto.getQuantidade()));
        precoTextField.setText(String.format("%.2f", produto.getPreco()).replace('.', ','));
        categoriaComboBox.setValue(produto.getCategoria());
        if (produto.getNomeArquivoImagem() != null) {
            imagemTextField.setText(produto.getNomeArquivoImagem());
        }
        if (stage != null) {
            stage.setTitle("Editar Produto");
        }
        finalizarCadastroButton.setText("Salvar Alterações");
    }

    @FXML
    public void initialize() {
        // <<< DEBUG: VERIFICANDO SE O CONTROLADOR É INICIALIZADO
        System.out.println("DEBUG: [CadastrarProdutoController] -> initialize() CHAMADO. A tela foi carregada.");
        imagemTextField.setEditable(false);
    }

    @FXML
    void handleProcurarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem do Produto");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            imagemSelecionada = file;
            imagemTextField.setText(imagemSelecionada.getName());
        }
    }

    @FXML
    void handleFinalizarCadastro(ActionEvent event) {
        // <<< DEBUG PASSO 3: VERIFICANDO O CLIQUE NO BOTÃO FINAL
        System.out.println("DEBUG: [CadastrarProdutoController] -> PASSO 3 -> Clicou no botão 'Finalizar/Salvar Alterações'.");

        if (!validarCampos()) {
            System.out.println("DEBUG: [CadastrarProdutoController] -> ERRO -> Campos inválidos.");
            return;
        }

        try {
            String nomeArquivoImagem = null;
            if (imagemSelecionada != null) {
                nomeArquivoImagem = salvarImagemLocalmente(imagemSelecionada);
            }
            if (produtoParaEditar == null) {
                // MODO CADASTRO
                System.out.println("DEBUG: [CadastrarProdutoController] -> PASSO 4 -> Entrou no modo CADASTRO.");
                Produto novoProduto = new Produto();
                preencherDadosDoFormulario(novoProduto, nomeArquivoImagem);
                System.out.println("DEBUG: [CadastrarProdutoController] -> PASSO 5 -> Chamando produtoDAO.save()...");
                produtoDAO.save(novoProduto); // Lembre-se de ter prints de debug dentro do seu DAO também!
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");
            } else {
                // MODO EDIÇÃO
                System.out.println("DEBUG: [CadastrarProdutoController] -> PASSO 4 -> Entrou no modo EDIÇÃO para o produto ID: " + produtoParaEditar.getId());
                preencherDadosDoFormulario(produtoParaEditar, nomeArquivoImagem);
                System.out.println("DEBUG: [CadastrarProdutoController] -> PASSO 5 -> Chamando produtoDAO.update()...");
                produtoDAO.update(produtoParaEditar); // Lembre-se de ter prints de debug dentro do seu DAO também!
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            }

            System.out.println("DEBUG: [CadastrarProdutoController] -> PASSO 6 -> Ação no banco de dados concluída. Fechando a janela.");
            stage.close();

        } catch (Exception e) {
            System.err.println("DEBUG: [CadastrarProdutoController] -> ERRO CRÍTICO no handleFinalizarCadastro!");
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
        String errorMessage = "";
        if (nomeTextField.getText() == null || nomeTextField.getText().trim().isEmpty()) { errorMessage += "O campo 'Nome' é obrigatório.\n"; }
        if (categoriaComboBox.getValue() == null || categoriaComboBox.getValue().isEmpty()) { errorMessage += "É obrigatório selecionar uma 'Categoria'.\n"; }
        if (quantidadeTextField.getText() == null || quantidadeTextField.getText().trim().isEmpty()) { errorMessage += "O campo 'Quantidade' é obrigatório.\n"; }
        if (precoTextField.getText() == null || precoTextField.getText().trim().isEmpty()) { errorMessage += "O campo 'Preço' é obrigatório.\n"; }
        if (errorMessage.length() == 0) { return true; }
        else { showAlert(Alert.AlertType.ERROR, "Campos Inválidos", errorMessage); return false; }
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}