package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.ClienteController;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import com.carvalhotechsolutions.mundoanimal.utils.TextFormatterManager;
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

    private final ClienteRepository clienteRepository = new ClienteRepository();

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
            boolean isEdicao = client_id_field.getText() != null && !client_id_field.getText().isEmpty();

            if (isEdicao) {
                Long id = Long.parseLong(client_id_field.getText());
                cliente = clienteRepository.findById(id);
                System.out.println("Editando cliente com ID: " + id); // Log para debug
            } else {
                cliente = new Cliente();
                System.out.println("Criando novo cliente"); // Log para debug
            }

            cliente.setNome(nome);
            cliente.setTelefone(telefone);
            clienteRepository.save(cliente);

            if (clienteController == null) {
                System.out.println("ERRO: clienteController é nulo!"); // Log para debug
                return;
            }

            // Atualizar a TableView e mostrar feedback
            clienteController.atualizarTableView();

            String mensagem = isEdicao ?
                    "Cliente atualizado com sucesso!" :
                    "Cliente cadastrado com sucesso!";

            System.out.println("Exibindo mensagem: " + mensagem); // Log para debug
            clienteController.handleSuccessfulOperation(mensagem);

            fecharModal();
        } catch (Exception e) {
            System.out.println("Erro ao salvar cliente: " + e.getMessage()); // Log para debug
            if (clienteController != null) {
                clienteController.handleError("Erro ao " +
                        (client_id_field.getText().isEmpty() ? "cadastrar" : "atualizar") +
                        " cliente!");
            }
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

        nome = nome.trim();
        telefone = telefone.trim(); // trim() usado para remover espaços desnecessários

        if (nome.isEmpty() || telefone.isEmpty()) {
            mostrarAlerta("Erro", "Campo(s) obrigatório(s) vazio(s)!", Alert.AlertType.ERROR);
            return false;
        }
        String finalTelefone = telefone;
        boolean telefoneJaCadastrado = clienteRepository.findAll().stream()
                .anyMatch(cliente -> cliente.getTelefone().equals(finalTelefone) &&
                        (clienteAtual == null || !cliente.getId().equals(clienteAtual.getId())));
        if (telefoneJaCadastrado){
            mostrarAlerta("Erro", "O telefone informado já está cadastrado no sistema.", Alert.AlertType.ERROR);
            return false;
        }
        String finalNome = nome;
        boolean nomeJaCadastrado = clienteRepository.findAll().stream()
                .anyMatch(cliente -> cliente.getNome().equalsIgnoreCase(finalNome) &&
                        (clienteAtual == null || !cliente.getId().equals(clienteAtual.getId())));

        if (nomeJaCadastrado) {
            mostrarAlerta("Erro", "Já existe um cliente cadastrado com esse nome.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void phoneKeyReleased(){
        TextFormatterManager tfm = new TextFormatterManager();
        tfm.setMask("(##)#####-####");
        tfm.setCaracteresValidos("0123456789");
        tfm.setTf(create_client_phone_field);
        tfm.formatter();
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