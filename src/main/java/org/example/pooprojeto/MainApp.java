package org.example.pooprojeto;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.pooprojeto.util.DatabaseManager;
import org.example.pooprojeto.util.NavigationManager;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        DatabaseManager.initializeDatabase();
    }


    @Override
    public void start(Stage stage) throws IOException {
        NavigationManager navigationManager = NavigationManager.getInstance();

        navigationManager.setPrimaryStage(stage);

        navigationManager.navigateToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}