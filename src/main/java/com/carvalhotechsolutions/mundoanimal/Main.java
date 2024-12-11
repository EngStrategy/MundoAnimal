package com.carvalhotechsolutions.mundoanimal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML da interface principal
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();

        // Define a cena com o layout carregado
        Scene scene = new Scene(root);
        // Aplica o CSS à cena
        String css = this.getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Configura o palco principal
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        // Exibe a interface
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}