package com.carvalhotechsolutions.mundoanimal;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        boolean isConnected = DatabaseChecker.testConnectionAndInitializeAdmin();
        if (!isConnected) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Conexão");
            alert.setHeaderText("Não foi possível conectar ao banco de dados.");
            alert.setContentText("Verifique as configurações do banco e tente novamente.");
            alert.showAndWait();
            Platform.exit(); // Encerra a aplicação
            return;
        }

        // Carrega o arquivo FXML da interface principal
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/autenticacao/login.fxml"));
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