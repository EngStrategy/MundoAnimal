package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarClienteController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarPetController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalDetalhesPetController;
import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.AnimalRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AnimalController implements Initializable {
    @FXML
    private TableView<Animal> tableView;

    @FXML
    private TableColumn<Animal, String> nomeColumn;

    @FXML
    private TableColumn<Animal, EspecieAnimal> especieColumn;

    @FXML
    private TableColumn<Animal, Void> acaoColumn;

    private Cliente cliente; // Dono dos pets que serão exibidos na tabela

    private ObservableList<Animal> petsList = FXCollections.observableArrayList();

    private AnimalRepository animalRepository = new AnimalRepository();

    private ClienteRepository clienteRepository = new ClienteRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Define a largura fixa da coluna de ação
        acaoColumn.setPrefWidth(354);
        acaoColumn.setMinWidth(354);
        acaoColumn.setMaxWidth(354);

        // Faz um bind da largura disponível (largura total da tabela menos a largura fixa da coluna de ação)
        DoubleBinding larguraDisponivel = tableView.widthProperty().subtract(354);

        // Configura as outras colunas para se redimensionarem proporcionalmente
        nomeColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.50));  // 50% do espaço restante
        especieColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.50)); // 50% do espaço restante

        // Configurar colunas
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        especieColumn.setCellValueFactory(new PropertyValueFactory<>("especie"));

        // Configurar botões da coluna de ações
        configurarColunaAcao();
    }

    private void configurarColunaAcao() {
        acaoColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editarButton = new Button("Editar");
            private final Button deletarButton = new Button("Deletar");
            private final Button detalhesButton = new Button("Detalhes");

            private final HBox container = new HBox(editarButton, deletarButton, detalhesButton);

            {
                // Estilizando os botões
                editarButton.setStyle(
                        "-fx-background-color: #686AFF; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                deletarButton.setStyle(
                        "-fx-background-color: #FF6F6F; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                detalhesButton.setStyle(
                        "-fx-background-color: #F2F5FA; -fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: 400; -fx-cursor: hand; -fx-min-width: 90px; -fx-border-color: #CCCCCC; -fx-border-radius: 2px;");

                container.setSpacing(16);
                container.setPadding(new Insets(0, 16, 0, 0));
                container.setAlignment(Pos.CENTER);

                // Configurar evento para deletar
                deletarButton.setOnAction(event -> {
                    Animal animal = getTableView().getItems().get(getIndex());
                    abrirModalExcluir(animal.getId());
                });

                // Configurar evento para editar
                editarButton.setOnAction(event -> {
                    Animal animal = getTableView().getItems().get(getIndex());
                    abrirModalEditar(animal.getId());
                });

                detalhesButton.setOnAction(event -> {
                    Animal animal = getTableView().getItems().get(getIndex());
                    abrirModalDetalhes(animal.getId());
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

    private void abrirModalDetalhes(Long animalId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalDetalhesPet.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalDetalhesPetController modalController = loader.getController();

            // Buscar o serviço pelo ID
            Animal animal = animalRepository.findById(animalId);

            // Configurar o modal para edição
            modalController.configurarParaExibicao(animal);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Detalhes do Pet");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirModalEditar(Long animalId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarPet.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalCriarPetController modalController = loader.getController();
            modalController.setAnimalController(this);

            // Buscar o serviço pelo ID
            Animal animal = animalRepository.findById(animalId);

            // Configurar o modal para edição
            modalController.configurarParaEdicao(animal);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Editar Pet");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

            // Recarrega o cliente do banco de dados para ter a lista atualizada
            cliente = clienteRepository.findById(cliente.getId());

            // Atualiza a página de clientes
            ScreenManagerHolder.getInstance().getClienteController().atualizarTableView();
            atualizarTableView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirModalExcluir(Long animalId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalConfirmarRemocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setRegisterId(animalId);
            modalController.setConfirmCallback(() -> {
                animalRepository.deleteById(animalId);

                // Recarrega o cliente do banco de dados para ter a lista atualizada
                cliente = clienteRepository.findById(cliente.getId());

                // Atualiza a página de clientes
                ScreenManagerHolder.getInstance().getClienteController().atualizarTableView();
                atualizarTableView();
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

    public void inicializarTabela() {
        petsList.clear();
        petsList.addAll(cliente.getPets());
        tableView.setItems(petsList);
    }

    public void voltarParaPaginaClientes() {
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.CLIENTES);
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void atualizarTableView() {
        // Limpa a lista atual
        petsList.clear();
        // Adiciona os pets atualizados
        petsList.addAll(cliente.getPets());
        // Força um refresh na TableView
        tableView.refresh();
    }
}

