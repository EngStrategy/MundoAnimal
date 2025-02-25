package com.carvalhotechsolutions.mundoanimal.utils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertManager {

    @FXML
    private Label modalMessage;

    @FXML
    private Label modalTitle;

    @FXML
    private Button btnModal;

    public void setModalData(String titulo, String mensagem, String textoBotao) {
        modalTitle.setText(titulo);
        modalMessage.setText(mensagem);
        btnModal.setText(textoBotao);
    }

    @FXML
    public void clicarNoBotao() {
        fecharModal();
    }

    private void fecharModal() {
        Stage stage = (Stage) btnModal.getScene().getWindow();
        stage.close();
    }

}
