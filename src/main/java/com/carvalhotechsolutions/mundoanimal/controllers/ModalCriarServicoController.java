package com.carvalhotechsolutions.mundoanimal.controllers;


import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ModalCriarServicoController {
    @FXML
    private TextField create_service_name_field;

    @FXML
    private TextField create_service_price_field;

    @FXML
    private TextArea create_service_description_field;

    private ServicoRepository servicoRepository = new ServicoRepository();

    // Referência para o controlador principal
    private ServicoController servicoController;

    public void setServicoController(ServicoController servicoController) {
        this.servicoController = servicoController;
    }

    @FXML
    public void cadastrarServico() {
        String nome = create_service_name_field.getText();
        String valorStr = create_service_price_field.getText();
        String descricao = create_service_description_field.getText();

        if (nome.isEmpty() || valorStr.isEmpty()) {
            System.err.println("Nome e Valor são obrigatórios!");
            return;
        }

        try {
            double valor = Double.parseDouble(valorStr.replace(",", "."));

            Servico servico = new Servico();
            servico.setNomeServico(nome);
            servico.setValorServico(BigDecimal.valueOf(valor));
            servico.setDescricao(descricao);

            // Persistir no banco de dados
            servicoRepository.save(servico);

            // Atualizar a TableView no controlador principal
            if (servicoController != null) {
                servicoController.atualizarTableView();
            }

            // Fechar modal
            fecharModal();

        } catch (NumberFormatException e) {
            System.err.println("O valor deve ser numérico!");
        }
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) create_service_name_field.getScene().getWindow();
        stage.close();
    }
}
