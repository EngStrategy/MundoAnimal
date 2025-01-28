package com.carvalhotechsolutions.mundoanimal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ModalConfirmarRemocaoController {
    @FXML
    private Button cancelarButton;

    @FXML
    private Button deletarButton;

    private Long servicoId; // Armazena o ID do serviço a ser excluído

    private Runnable confirmCallback; // Função para executar após confirmação

    // Define o ID do serviço
    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    // Define a ação a ser executada após confirmação
    public void setConfirmCallback(Runnable confirmCallback) {
        this.confirmCallback = confirmCallback;
    }

    @FXML
    public void cancelar() {
        fecharModal();
    }

    @FXML
    public void confirmar() {
        if (confirmCallback != null) {
            confirmCallback.run(); // Executa a função de exclusão
        }
        fecharModal();
    }

    private void fecharModal() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
}
