package org.example.pooprojeto.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.example.pooprojeto.model.Endereco;
import org.example.pooprojeto.model.NotaFiscal;
import org.example.pooprojeto.model.Produto;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.awt.Desktop;

public class GeradorNotaFiscalPDF {

    private static final Font FONTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
    private static final Font FONTE_CABECALHO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font FONTE_CORPO = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font FONTE_TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

    public static File gerarPdf(NotaFiscal notaFiscal) throws IOException {
        Path diretorioPath = Paths.get(System.getProperty("user.home"), "Desktop", "notas_fiscais");
        Files.createDirectories(diretorioPath);

        String nomeArquivo = "nota_fiscal_" + notaFiscal.getNumero() + ".pdf";
        File arquivoPdf = new File(diretorioPath.toFile(), nomeArquivo);
        
        Document documento = new Document(PageSize.A4);
        try {
            // 1. Cria o escritor de PDF associado ao arquivo e ao documento
            PdfWriter.getInstance(documento, new FileOutputStream(arquivoPdf));

            // 2. Abre o documento para começar a escrever
            documento.open();

            // 3. Adiciona todo o conteúdo ao documento
            // Informação cliente
            Paragraph titulo = new Paragraph("NOTA FISCAL", FONTE_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(Chunk.NEWLINE);

            Endereco endereco = notaFiscal.getEnderecoEntrega();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            documento.add(new Paragraph("Número: " + notaFiscal.getNumero(), FONTE_CABECALHO));
            documento.add(new Paragraph("Data de Emissão: " + notaFiscal.getDataEmissao().format(formatter), FONTE_CABECALHO));
            documento.add(new Paragraph("Cliente: " + notaFiscal.getClienteNome(), FONTE_CABECALHO));
            documento.add(Chunk.NEWLINE);
            documento.add(new com.lowagie.text.pdf.draw.LineSeparator());
            documento.add(Chunk.NEWLINE);

            if (endereco != null) {
                Paragraph tituloEndereco = new Paragraph("ENDEREÇO DE ENTREGA", FONTE_CABECALHO);
                tituloEndereco.setSpacingBefore(10);
                documento.add(tituloEndereco);
                documento.add(new Paragraph(endereco.toString(), FONTE_CORPO));
                documento.add(Chunk.NEWLINE);
            }

            documento.add(criarTabelaProdutos(notaFiscal.getProdutos()));
            documento.add(Chunk.NEWLINE);

            Paragraph total = new Paragraph(String.format("VALOR TOTAL: R$ %.2f", notaFiscal.getValorTotal()), FONTE_TOTAL);
            total.setAlignment(Element.ALIGN_RIGHT);
            documento.add(total);

        } catch (DocumentException | IOException e) {
            throw new IOException("Erro ao gerar documento PDF", e);
        } finally {
            // 4. Fecha o documento. Isso é CRUCIAL e finaliza a escrita no arquivo.
            if (documento.isOpen()) {
                documento.close();
            }
        }

        return arquivoPdf;
    }

    private static PdfPTable criarTabelaProdutos(List<Produto> produtos) {
        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);

        String[] cabecalhos = {"PRODUTO", "QTD.", "PREÇO UNIT.", "SUBTOTAL"};
        for (String cabecalho : cabecalhos) {
            PdfPCell cell = new PdfPCell(new Phrase(cabecalho, FONTE_CABECALHO));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            tabela.addCell(cell);
        }

        Map<Produto, Long> produtosAgrupados = produtos.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        for (Map.Entry<Produto, Long> entry : produtosAgrupados.entrySet()) {
            Produto p = entry.getKey();
            long quantidade = entry.getValue();
            tabela.addCell(new Phrase(p.getNome(), FONTE_CORPO));
            tabela.addCell(new Phrase(String.valueOf(quantidade), FONTE_CORPO));
            tabela.addCell(new Phrase(String.format("R$ %.2f", p.getPreco()), FONTE_CORPO));
            tabela.addCell(new Phrase(String.format("R$ %.2f", p.getPreco() * quantidade), FONTE_CORPO));
        }
        return tabela;
    }
    public static void abrirPdf(File arquivoPdf) {
        new Thread(() -> {
            try {
                if (!Desktop.isDesktopSupported()) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "Recurso Indisponível", "A abertura automática de arquivos não é suportada neste sistema."));
                    return;
                }
                Desktop.getDesktop().open(arquivoPdf);
            } catch (IOException ex) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erro ao Abrir PDF", "Não foi possível abrir o arquivo.\nVerifique se você tem um leitor de PDF instalado e configurado como padrão."));
                ex.printStackTrace();
            }
        }).start();
    }
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        // Assegura que o alerta seja criado e exibido na thread da UI
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}