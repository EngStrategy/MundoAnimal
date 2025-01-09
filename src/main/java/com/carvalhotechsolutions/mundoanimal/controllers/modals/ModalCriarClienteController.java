package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.ClienteController;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalCriarClienteController {
    @FXML
    private Label titleLabel;

    @FXML
    private Button actionButton;

    @FXML
    private TextField client_id_field;

    @FXML
    private TextField create_client_name_field;

    @FXML
    private TextField create_client_phone_field;

    private ClienteRepository clienteRepository = new ClienteRepository();

    // Referência para o controlador principal
    private ClienteController clienteController;

    private Cliente clienteAtual; // Armazena o cliente a ser editado (se edição)


    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    @FXML
    public void cadastrarCliente() {
        String nome = create_client_name_field.getText();
        String telefone = create_client_phone_field.getText();

        if (!validarInputs(nome, telefone)) {
            return;
        }

        try {
            Cliente cliente;

            // Verificar se o cliente já existe (edição)
            if (client_id_field.getText() != null && !client_id_field.getText().isEmpty()) {
                Long id = Long.parseLong(client_id_field.getText());
                cliente = clienteRepository.findById(id); // Recupera o cliente existente
            } else {
                cliente = new Cliente(); // Novo cliente
            }

            cliente.setNome(nome);
            cliente.setTelefone(telefone);

            // Persistir no banco de dados
            clienteRepository.save(cliente);

            // Atualizar a TableView no controlador principal
            if (clienteController != null) {
                clienteController.atualizarTableView();
            }

            // Fechar modal
            fecharModal();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Configurar o modal para edição
    public void configurarParaEdicao(Cliente cliente) {
        this.clienteAtual = cliente;

        // Atualizar campos
        client_id_field.setText(cliente.getId().toString()); // Preencher o campo de ID invisível
        create_client_name_field.setText(cliente.getNome());
        create_client_phone_field.setText(cliente.getTelefone());

        // Alterar título e botão
        titleLabel.setText("Editar Cliente");
        actionButton.setText("Salvar");
    }

    private boolean validarInputs(String nome, String telefone) {
        if (nome.isEmpty() || telefone.isEmpty()) {
            mostrarAlerta("Erro", "Campo(s) obrigatório(s) vazio(s)!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) create_client_name_field.getScene().getWindow();
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