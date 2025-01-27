package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModalConfirmarRemocaoController {
    @FXML
    private Label modalMessage;

    @FXML
    private Label modalTitle;

    @FXML
    private Button cancelarButton;

    @FXML
    private Button deletarButton;

    private static final Logger logger = LogManager.getLogger(ModalConfirmarRemocaoController.class);

    private Long registerId; // Armazena o ID do registro a ser excluído

    private Runnable confirmCallback; // Função para executar após confirmação

    // Define o ID do serviço
    public void setRegisterId(Long registerIdId) {
        this.registerId = registerId;
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
        logger.info("Modal fechado com sucesso");
    }

    public void configurarParaCancelamento() {
        modalTitle.setText("Confirmar Cancelamento");
        modalMessage.setText("Cancelar este agendamento permanentemente?");
        deletarButton.setText("Sim");
        cancelarButton.setText("Não");
    }
}
