package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import com.carvalhotechsolutions.mundoanimal.utils.ModalManager;
import com.carvalhotechsolutions.mundoanimal.utils.RelatorioManager;
import com.itextpdf.text.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class RelatorioController {

    @FXML
    private Button actionButton;
    @FXML
    private DatePicker dataInicialPicker, dataFinalPicker;
    private final AgendamentoRepository agendamentoRepository = new AgendamentoRepository();
    private final RelatorioManager relatorioManager = new RelatorioManager();


    @FXML
    private void gerarRelatorio() {

        LocalDate inicio = dataInicialPicker.getValue();
        LocalDate fim = dataFinalPicker.getValue();

        dataInicialPicker.getEditor().clear();
        dataFinalPicker.getEditor().clear();

        if (inicio == null || fim == null) {
            mostrarAlerta("Erro", "Selecione um intervalo de datas válido.");
            return;
        }

        if (inicio.isAfter(fim)) {
            mostrarAlerta("Erro", "A data inicial não pode ser maior que a data final.");
            return;
        }

        List<Agendamento> agendamentos = agendamentoRepository.buscarAgendamentosPorIntervalo(inicio, fim);

        if (agendamentos.isEmpty()) {
            mostrarAlerta("Aviso", "Nenhum agendamento encontrado neste período.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Relatorio_" + inicio + "_" + fim + ".pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // Usuário cancelou
        }

        try {
            relatorioManager.gerarRelatorioPDF(agendamentos, inicio, fim, file);
            mostrarAlerta("Sucesso", "Relatório gerado com sucesso!");
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao gerar o relatório.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        ModalManager.mostrarModal(titulo, mensagem, "OK");
    }

}
