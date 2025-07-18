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
    @FXML private Button finalizarCadastroButton; // <<< ADICIONADO: @FXML para podermos alterar o texto do botão

    // ==== Atributos de Suporte ====
    private Stage stage;
    private ProdutoDAO produtoDAO;
    private File imagemSelecionada;

    // <<< MUDANÇA 1: Variável para controlar o modo (Cadastro vs. Edição)
    // Se for nulo, estamos cadastrando. Se tiver um objeto, estamos editando.
    private Produto produtoParaEditar;

    // ==== Métodos Injetores ====
    public void setStage(Stage stage) { this.stage = stage; }
    public void setProdutoDAO(ProdutoDAO produtoDAO) { this.produtoDAO = produtoDAO; }
    public void setCategorias(List<String> categorias) { categoriaComboBox.getItems().setAll(categorias); }

    /**
     * <<< MUDANÇA 2: NOVO MÉTODO FUNDAMENTAL
     * Prepara a tela para o modo de edição.
     * @param produto O produto cujos dados serão carregados na tela.
     */
    public void carregarDadosParaEdicao(Produto produto) {
        this.produtoParaEditar = produto;

        // Preenche os campos da tela com os dados do produto existente
        nomeTextField.setText(produto.getNome());
        descricaoTextField.setText(produto.getDescricao());
        quantidadeTextField.setText(String.valueOf(produto.getQuantidade()));
        precoTextField.setText(String.format("%.2f", produto.getPreco()).replace('.', ','));
        categoriaComboBox.setValue(produto.getCategoria());

        if (produto.getNomeArquivoImagem() != null) {
            imagemTextField.setText(produto.getNomeArquivoImagem());
        }

        // Altera o título da janela e o texto do botão para indicar que estamos editando
        stage.setTitle("Editar Produto");
        finalizarCadastroButton.setText("Salvar Alterações");
    }

    // ==== Método de Inicialização (sem alterações) ====
    @FXML
    public void initialize() {
        imagemTextField.setEditable(false);
    }

    // ==== Métodos de Eventos FXML ====
    @FXML
    void handleProcurarImagem(ActionEvent event) {
        // Nenhuma mudança necessária aqui
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem do Produto");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            imagemSelecionada = file;
            imagemTextField.setText(imagemSelecionada.getName());
        }
    }

    /**
     * <<< MUDANÇA 3: Lógica principal atualizada para decidir entre SAVE e UPDATE
     */
    @FXML
    void handleFinalizarCadastro(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        try {
            // Verifica se uma nova imagem foi selecionada
            String nomeArquivoImagem = null;
            if (imagemSelecionada != null) {
                nomeArquivoImagem = salvarImagemLocalmente(imagemSelecionada);
            }

            // Decide se vai criar um novo produto ou atualizar um existente
            if (produtoParaEditar == null) {
                // MODO CADASTRO
                Produto novoProduto = new Produto();
                preencherDadosDoFormulario(novoProduto, nomeArquivoImagem);
                produtoDAO.save(novoProduto);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");
            } else {
                // MODO EDIÇÃO
                preencherDadosDoFormulario(produtoParaEditar, nomeArquivoImagem);
                produtoDAO.update(produtoParaEditar);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            }

            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Por favor, insira um número válido para Quantidade e Preço.");
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao salvar as informações.\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * <<< MUDANÇA 4: NOVO MÉTODO auxiliar para evitar repetição de código
     * @param produto O objeto produto (novo ou existente) a ser preenchido.
     * @param nomeArquivoImagem O nome da nova imagem (ou nulo se nenhuma foi selecionada).
     */
    private void preencherDadosDoFormulario(Produto produto, String nomeArquivoImagem) {
        produto.setNome(nomeTextField.getText());
        produto.setCategoria(categoriaComboBox.getValue());
        produto.setDescricao(descricaoTextField.getText());
        produto.setQuantidade(Integer.parseInt(quantidadeTextField.getText()));
        produto.setPreco(Double.parseDouble(precoTextField.getText().replace(',', '.')));

        // Só atualiza o nome do arquivo da imagem se uma NOVA imagem foi selecionada
        if (nomeArquivoImagem != null) {
            produto.setNomeArquivoImagem(nomeArquivoImagem);
        }
    }


    // ==== Métodos Auxiliares (sem alterações) ====
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