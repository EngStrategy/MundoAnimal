package com.carvalhotechsolutions.mundoanimal.controllers;


import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ModalCriarServicoController {
    @FXML
    private Label titleLabel;

    @FXML
    private Button actionButton;

    @FXML
    private TextField service_id_field;

    @FXML
    private TextField create_service_name_field;

    @FXML
    private TextField create_service_price_field;

    @FXML
    private TextArea create_service_description_field;

    private ServicoRepository servicoRepository = new ServicoRepository();

    // Referência para o controlador principal
    private ServicoController servicoController;

    private Servico servicoAtual; // Armazena o serviço a ser editado (se edição)


    public void setServicoController(ServicoController servicoController) {
        this.servicoController = servicoController;
    }


    @FXML
    public void cadastrarServico() {
        String nome = create_service_name_field.getText();
        String valorStr = create_service_price_field.getText();
        String descricao = create_service_description_field.getText();

        if (!validarInputs(nome, valorStr)) {
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(valorStr.replace(",", "."));

            Servico servico;

            // Verificar se o serviço já existe (edição)
            if (service_id_field.getText() != null && !service_id_field.getText().isEmpty()) {
                Long id = Long.parseLong(service_id_field.getText());
                servico = servicoRepository.findById(id); // Recupera o serviço existente
            } else {
                servico = new Servico(); // Novo serviço
            }

            servico.setNomeServico(nome);
            servico.setValorServico(valor);
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

    // Configurar o modal para edição
    public void configurarParaEdicao(Servico servico) {
        this.servicoAtual = servico;

        // Atualizar campos
        service_id_field.setText(servico.getId().toString()); // Preencher o ID invisível
        create_service_name_field.setText(servico.getNomeServico());
        create_service_price_field.setText(servico.getValorServico().toString());
        create_service_description_field.setText(servico.getDescricao());

        // Alterar título e botão
        titleLabel.setText("Editar Serviço");
        actionButton.setText("Salvar");
    }

    private boolean validarInputs(String nome, String valor) {
        if (nome.isEmpty() || valor.isEmpty()) {
            mostrarAlerta("Erro", "Campo(s) obrigatório(s) vazio(s)!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) create_service_name_field.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
