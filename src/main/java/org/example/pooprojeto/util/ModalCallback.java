// Em src/main/java/org/example/pooprojeto/util/ModalCallback.java

package org.example.pooprojeto.util;

/**
 * Uma interface de callback simples para sinalizar que uma operação modal foi concluída.
 * Isso evita a necessidade de importar java.util.function.Runnable, contornando
 * possíveis problemas de configuração do classpath ou do módulo.
 */
public interface ModalCallback {
    void onModalClose();
}