package com.carvalhotechsolutions.mundoanimal.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ModalManager {

    public static void mostrarModal(String titulo, String mensagem, String textoBotao) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalManager.class.getResource("/fxml/popup/genericAlert.fxml"));
            Parent root = loader.load();

            AlertManager controller = loader.getController();
            controller.setModalData(titulo, mensagem, textoBotao);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
