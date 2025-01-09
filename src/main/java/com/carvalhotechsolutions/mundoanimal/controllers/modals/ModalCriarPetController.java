package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.ClienteController;
import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.AnimalRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ModalCriarPetController implements Initializable {
    @FXML
    private Label titleLabel;

    @FXML
    private Button actionButton;

    @FXML
    private TextField create_pet_clientName_field;

    @FXML
    private TextField create_pet_clientPhone_field;

    @FXML
    private TextField create_pet_name_field;

    @FXML
    private ComboBox<EspecieAnimal> create_pet_specie_field;

    @FXML
    private TextField create_pet_race_field;

    @FXML
    private TextField create_pet_age_field;

    @FXML
    private TextArea create_pet_notes_field;

    private AnimalRepository animalRepository = new AnimalRepository();

    private ClienteRepository clienteRepository = new ClienteRepository();

    private ClienteController clienteController;

    private Cliente cliente; // Armazena o dono do pet que está sendo cadastrado

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        create_pet_specie_field.getItems().addAll(EspecieAnimal.values());
    }

    @FXML
    public void cadastrarPet() {
        String nome = create_pet_name_field.getText();
        EspecieAnimal especie = create_pet_specie_field.getValue();
        String raca = create_pet_race_field.getText();
        String idade = create_pet_age_field.getText();
        String observacoes = create_pet_notes_field.getText();

        if (!validarInputs(nome, especie)) {
            return;
        }

        try {
            Animal animal = new Animal();
            animal.setNome(nome);
            animal.setEspecie(especie);
            animal.setRaca(raca.isEmpty() ? "Não informada" : raca);
            animal.setIdade(idade.isEmpty() ? 0 : Integer.parseInt(idade));
            animal.setObservacoes(observacoes.isEmpty() ? "Sem observações" : observacoes);
            animal.setDono(this.cliente);

            // Persistir no banco de dados
            animalRepository.save(animal);

            // Atualizar a TableView no controlador principal
            if (clienteController != null) {
                clienteController.atualizarTableView();
            }

            // Fechar modal
            fecharModal();

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Idade deve ser um número válido!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar pet: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validarInputs(String nome, EspecieAnimal especie) {
        if (nome.isEmpty() || especie == null) {
            mostrarAlerta("Erro", "Nome do pet e espécie são obrigatórios!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public void configurarParaCadastro(Long clientId) {
        this.cliente = clienteRepository.findById(clientId);

        create_pet_clientName_field.setText(cliente.getNome());
        create_pet_clientPhone_field.setText(cliente.getTelefone());
        create_pet_clientName_field.setEditable(false);
        create_pet_clientPhone_field.setEditable(false);
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) create_pet_name_field.getScene().getWindow();
        stage.close();
    }
}
