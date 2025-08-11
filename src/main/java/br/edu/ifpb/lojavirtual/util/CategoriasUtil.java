package br.edu.ifpb.lojavirtual.util;

import java.util.Arrays;
import java.util.List;

public class CategoriasUtil {

    // Lista central de todas as categorias disponíveis na loja
    private static final List<String> CATEGORIAS = Arrays.asList(
            "Teclado",
            "Mouse",
            "Monitor",
            "Processador",
            "GPU",
            "Gabinete",
            "Webcam",
            "Fones",
            "Fonte",
            "Placa mãe",
            "Memória RAM",
            "Cooler",
            "SSD",
            "HD",
            "Microfone",
            "Mousepad",
            "Cadeira"
    );

    public static List<String> getCategorias() {
        return CATEGORIAS;
    }
}