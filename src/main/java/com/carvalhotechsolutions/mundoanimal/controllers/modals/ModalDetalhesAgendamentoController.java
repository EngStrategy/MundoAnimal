package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModalDetalhesAgendamentoController {
    @FXML
    private Text client_name_label;

    @FXML
    private Text service_name_label;

    @FXML
    private Text date_hour_label;

    @FXML
    private Text client_phone_label;

    @FXML
    private Text pet_name_label;

    private Agendamento agendamento;

    public void configurarParaExibicao(Agendamento agendamento) {
        this.agendamento = agendamento;

        client_name_label.setText(agendamento.getCliente().getNome());
        service_name_label.setText(agendamento.getServico().getNomeServico());
        date_hour_label.setText(agendamento.getDataHoraFormatada());
        client_phone_label.setText(agendamento.getCliente().getTelefone());
        pet_name_label.setText(agendamento.getAnimal().getNome());
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) pet_name_label.getScene().getWindow();
        stage.close();
    }
}
