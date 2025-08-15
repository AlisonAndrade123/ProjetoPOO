package br.edu.ifpb.lojavirtual.util;

import br.edu.ifpb.lojavirtual.MainApp;

import java.io.File;
import java.net.URISyntaxException;

public class PathUtils {
    public static File getApplicationBaseDir() {
        try {
            File source = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (source.isFile() && source.getName().endsWith(".jar")) {
                return source.getParentFile();
            } else {
                return new File(System.getProperty("user.dir"));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return new File(".");
        }
    }
}