package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.AnimalController;
import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.ClienteController;
import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.AnimalRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ModalCriarPetController implements Initializable {
    @FXML
    private Label titleLabel;

    @FXML
    private Button actionButton;

    @FXML
    private TextField pet_id_field;

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

    private AnimalController animalController;

    private Cliente cliente; // Armazena o dono do pet que está sendo cadastrado

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        create_pet_specie_field.getItems().addAll(EspecieAnimal.values());
        Platform.runLater(() -> create_pet_name_field.requestFocus());
    }

    @FXML
    public void cadastrarPet() {
        String nome = create_pet_name_field.getText();
        EspecieAnimal especie = create_pet_specie_field.getValue();
        String raca = create_pet_race_field.getText();
        String idade = create_pet_age_field.getText();
        String observacoes = create_pet_notes_field.getText();

        if (!validarInputs(nome, especie)) {
            logger.info("Validação de inputs falhou");
            return;
        }

        try {
            Animal animal;
            boolean isEdicao = pet_id_field.getText() != null && !pet_id_field.getText().isEmpty();

            if (isEdicao) {
                Long id = Long.parseLong(pet_id_field.getText());
                animal = animalRepository.findById(id); // Recupera o animal existente
                logger.info("Editando animal com ID: " + id); // Log para debug
            } else {
                animal = new Animal(); // Novo animal
                logger.info("Criando novo animal"); // Log para debug
            }

            animal.setNome(nome);
            animal.setEspecie(especie);
            animal.setRaca(raca.isEmpty() ? null : raca);
            animal.setIdade(idade.isEmpty() ? null : Integer.parseInt(idade));
            animal.setObservacoes(observacoes.isEmpty() ? null : observacoes);
            animal.setDono(this.cliente);
            animalRepository.save(animal);

            if (clienteController != null) {
                clienteController.atualizarTableView();
            }

            String mensagem = isEdicao ?
                    "Pet atualizado com sucesso!" :
                    "Pet cadastrado com sucesso!";


            logger.info("Exibindo mensagem: " + mensagem); // Log para debug

            if(isEdicao) {
                animalController.handleSuccessfulOperation(mensagem);
            }
            else {
                clienteController.handleSuccessfulOperation(mensagem);
            }

            fecharModal();

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Idade deve ser um número válido!", Alert.AlertType.ERROR);
            logger.warn("Exceção NumberFormatException lançada e tratada");
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar pet: " + e.getMessage(), Alert.AlertType.ERROR);
            logger.error("Erro, {}", e.getMessage());
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

    public void configurarParaEdicao(Animal animal) {
        this.cliente = animal.getDono();

        // Atualizar campos
        pet_id_field.setText(animal.getId().toString()); // Preencher o campo de ID invisível
        create_pet_clientName_field.setText(cliente.getNome());
        create_pet_clientPhone_field.setText(cliente.getTelefone());
        create_pet_clientName_field.setEditable(false);
        create_pet_clientPhone_field.setEditable(false);

        // Preenchendo campos referentes ao animal
        create_pet_name_field.setText(animal.getNome());
        create_pet_specie_field.setValue(animal.getEspecie());
        create_pet_race_field.setText(animal.getRaca() == null ? "" : animal.getRaca());
        create_pet_age_field.setText(animal.getIdade() == null ? "" : animal.getIdade().toString());
        create_pet_notes_field.setText(animal.getObservacoes() == null ? "" : animal.getObservacoes());

        // Alterar título e botão
        titleLabel.setText("Editar Pet");
        actionButton.setText("Salvar");
    }

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    public void setAnimalController(AnimalController animalController) {
        this.animalController = animalController;
    }
}
