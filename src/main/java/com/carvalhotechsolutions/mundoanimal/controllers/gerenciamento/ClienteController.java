package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarClienteController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarPetController;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {
    @FXML
    private TableView<Cliente> tableView;

    @FXML
    private TableColumn<Cliente, String> nomeColumn;

    @FXML
    private TableColumn<Cliente, String> telefoneColumn;

    @FXML
    private TableColumn<Cliente, String> petsColumn;

    @FXML
    private TableColumn<Cliente, Void> acaoColumn;

    private ClienteRepository clienteRepository = new ClienteRepository();

    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Define a largura fixa da coluna de ação
        acaoColumn.setPrefWidth(462);
        acaoColumn.setMinWidth(462);
        acaoColumn.setMaxWidth(462);

        // Faz um bind da largura disponível (largura total da tabela menos a largura fixa da coluna de ação)
        DoubleBinding larguraDisponivel = tableView.widthProperty().subtract(462);

        // Configura as outras colunas para se redimensionarem proporcionalmente
        nomeColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.33));      // 33% do espaço restante
        telefoneColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.33)); // 33% do espaço restante
        petsColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.34));     // 34% do espaço restante

        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        telefoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        petsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPetsFormatados())
        );

        configurarColunaAcao();
        atualizarTableView();
    }

    private void configurarColunaAcao() {
        acaoColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editarButton = new Button("Editar");
            private final Button deletarButton = new Button("Deletar");
            private final Button novoPetButton = new Button("Novo pet");
            private final Button verPetsButton = new Button("Ver pets");

            private final HBox container = new HBox(editarButton, deletarButton, novoPetButton, verPetsButton);

            {
                // Estilizando os botões
                editarButton.setStyle(
                        "-fx-background-color: #686AFF; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                deletarButton.setStyle(
                        "-fx-background-color: #FF6F6F; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                novoPetButton.setStyle(
                        "-fx-background-color: #2cc428; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                verPetsButton.setStyle(
                        "-fx-background-color: #F2F5FA; -fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: 400; -fx-cursor: hand; -fx-min-width: 90px; -fx-border-color: #CCCCCC; -fx-border-radius: 2px;");

                container.setSpacing(16);
                container.setPadding(new Insets(0, 16, 0, 0));
                container.setAlignment(Pos.CENTER);

                // Configurar evento para deletar
                deletarButton.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    abrirModalExcluir(cliente.getId());
                });

                // Configurar evento para editar
                editarButton.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    abrirModalEditar(cliente.getId());
                });

                novoPetButton.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    abrirModalCadastrarPet(cliente.getId());
                });

                verPetsButton.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    abrirPaginaVerPets(cliente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    public void abrirModalCadastrarCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarCliente.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalCriarClienteController modalController = loader.getController();
            modalController.setClienteController(this); // Passa referência do controlador principal

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Cadastrar Cliente");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir o modal: " + e.getMessage());
        }
    }

    private void abrirModalEditar(Long clienteId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarCliente.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalCriarClienteController modalController = loader.getController();

            // Buscar o serviço pelo ID
            Cliente cliente = clienteRepository.findById(clienteId);

            // Configurar o modal para edição
            modalController.configurarParaEdicao(cliente);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Editar Serviço");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

            atualizarTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirModalExcluir(Long clienteId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalConfirmarRemocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setRegisterId(clienteId);
            modalController.setConfirmCallback(() -> {
                clienteRepository.deleteById(clienteId);
                atualizarTableView(); // Atualizar tabela após exclusão
            });

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Confirmar Exclusão");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void abrirModalCadastrarPet(Long clienteId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarPet.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalCriarPetController modalController = loader.getController();
            modalController.setClienteController(this); // Passa referência do controlador principal

            // Configurar campos do cliente no modal
            modalController.configurarParaCadastro(clienteId);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Cadastrar Pet");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir o modal: " + e.getMessage());
        }
    }

    private void abrirPaginaVerPets(Cliente cliente) {
        if (cliente.getPets().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação");
            alert.setHeaderText(null);
            alert.setContentText("Este cliente não possui pets cadastrados.");
            alert.showAndWait();
            return;
        }

        // Configurar o controlador do modal
        AnimalController animalController = ScreenManagerHolder.getInstance().getAnimalController();
        animalController.setCliente(cliente);
        animalController.inicializarTabela();

        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.PETS);
    }

    public void atualizarTableView() {
        clientesList = FXCollections.observableArrayList(clienteRepository.findAll());
        tableView.setItems(clientesList);
    }
}