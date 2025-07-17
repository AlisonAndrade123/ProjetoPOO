package org.example.pooprojeto.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox; // MUDANÇA: Importado ComboBox
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
import java.util.List; // MUDANÇA: Importado List

public class CadastrarProdutoController {

    // ==== Elementos FXML ====
    @FXML private TextField nomeTextField;
    @FXML private ComboBox<String> categoriaComboBox; // MUDANÇA: TextField trocado por ComboBox
    @FXML private TextField imagemTextField;
    @FXML private TextField quantidadeTextField;
    @FXML private TextField descricaoTextField;
    @FXML private TextField precoTextField;
    @FXML private Button procurarImagemButton;
    @FXML private Button finalizarCadastroButton;

    // ==== Atributos de Suporte ====
    private Stage stage;
    private ProdutoDAO produtoDAO;
    private File imagemSelecionada;

    // ==== Métodos Injetores ====
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProdutoDAO(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    // MUDANÇA: Novo método para receber e popular a lista de categorias
    public void setCategorias(List<String> categorias) {
        categoriaComboBox.getItems().setAll(categorias);
    }

    // ==== Método de Inicialização ====
    @FXML
    public void initialize() {
        imagemTextField.setEditable(false);
    }

    // ==== Métodos de Eventos FXML (onAction) ====
    @FXML
    void handleProcurarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem do Produto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(stage);
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
            Produto novoProduto = new Produto();
            novoProduto.setNome(nomeTextField.getText());
            novoProduto.setCategoria(categoriaComboBox.getValue()); // MUDANÇA: Obtém valor do ComboBox
            novoProduto.setDescricao(descricaoTextField.getText());
            novoProduto.setQuantidade(Integer.parseInt(quantidadeTextField.getText()));
            novoProduto.setPreco(Double.parseDouble(precoTextField.getText().replace(',', '.')));

            if (imagemSelecionada != null) {
                String nomeDoArquivo = salvarImagemLocalmente(imagemSelecionada);
                novoProduto.setImageUrl(nomeDoArquivo);
            } else {
                novoProduto.setImageUrl(null);
            }

            produtoDAO.save(novoProduto);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Por favor, insira um número válido para Quantidade e Preço.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao salvar o produto no banco de dados.\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==== Métodos Auxiliares ====
    private boolean validarCampos() {
        String errorMessage = "";

        if (nomeTextField.getText() == null || nomeTextField.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Nome' é obrigatório.\n";
        }
        // MUDANÇA: Validação ajustada para o ComboBox
        if (categoriaComboBox.getValue() == null || categoriaComboBox.getValue().isEmpty()) {
            errorMessage += "É obrigatório selecionar uma 'Categoria'.\n";
        }
        if (quantidadeTextField.getText() == null || quantidadeTextField.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Quantidade' é obrigatório.\n";
        }
        if (precoTextField.getText() == null || precoTextField.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Preço' é obrigatório.\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }
    }

    private String salvarImagemLocalmente(File arquivoImagem) throws IOException {
        Path destinoDir = Paths.get("src/main/resources/org/example/pooprojeto/Imagens");
        if (!Files.exists(destinoDir)) {
            Files.createDirectories(destinoDir);
        }
        String nomeArquivo = arquivoImagem.getName();
        Path destinoArquivo = destinoDir.resolve(nomeArquivo);
        Files.copy(arquivoImagem.toPath(), destinoArquivo, StandardCopyOption.REPLACE_EXISTING);
        return nomeArquivo;
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