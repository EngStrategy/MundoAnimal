package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarClienteController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarPetController;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import com.carvalhotechsolutions.mundoanimal.utils.FeedbackManager;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    private static final int ROW_HEIGHT = 79; // Altura de cada linha em pixels

    private static final int HEADER_HEIGHT = 79; // Altura do header em pixels

    private IntegerProperty itemsPerPage = new SimpleIntegerProperty();

    @FXML
    private HBox feedbackContainer;

    @FXML
    private TableView<Cliente> tableView;

    @FXML
    private TableColumn<Cliente, String> telefoneColumn, petsColumn, nomeColumn;

    @FXML
    private TableColumn<Cliente, Void> acaoColumn;

    @FXML
    private Label numberOfResults;

    @FXML
    private TextField filterField;

    @FXML
    private Pagination paginator;

    private ClienteRepository clienteRepository = new ClienteRepository();

    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();

    private FilteredList<Cliente> filteredData = new FilteredList<>(clientesList);

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

        // Adicionar listener para mudanças na altura da tabela
        tableView.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            calcularItensPorPagina(newHeight.doubleValue());
            tableView.refresh(); // Força a atualização da TableView
            // Reconfigura a paginação quando a altura muda
            Platform.runLater(this::configurarPaginacao);
        });

        // Calcula inicial de itens por página
        calcularItensPorPagina(tableView.getHeight());

        configurarColunaAcao();
        atualizarTableView();
        configurarBuscaClientes(); // apos atualizarTableView()
    }

    private void calcularItensPorPagina(double alturaTotal) {
        // Subtrai a altura do header da altura total
        double alturaDisponivel = alturaTotal - HEADER_HEIGHT;
        // Calcula quantas linhas cabem na altura disponível
        int numeroLinhas = Math.max(1, (int) Math.floor(alturaDisponivel / ROW_HEIGHT));
        // Atualiza a propriedade de itens por página
        itemsPerPage.set(numeroLinhas);
    }

    private void configurarPaginacao() {
        if (filteredData.isEmpty()) {
            paginator.setPageCount(1);
            paginator.setCurrentPageIndex(0);
            paginator.setDisable(true);
            tableView.setItems(FXCollections.observableArrayList());
            return;
        }

        // Usa o valor dinâmico de itens por página
        int totalPages = (int) Math.ceil((double) filteredData.size() / itemsPerPage.get());
        paginator.setPageCount(totalPages);

        // Se a página atual é maior que o novo número total de páginas,
        // ajusta para a última página válida
        if (paginator.getCurrentPageIndex() >= totalPages) {
            paginator.setCurrentPageIndex(totalPages - 1);
        }

        paginator.setDisable(false);
        paginator.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            Platform.runLater(() -> {
                atualizarPaginaAtual(newIndex.intValue());
                tableView.refresh(); // Força a atualização da TableView
            });
        });

        // Atualiza a visualização inicial
        atualizarPaginaAtual(paginator.getCurrentPageIndex());
    }

    private void atualizarPaginaAtual(int pageIndex) {
        int startIndex = pageIndex * itemsPerPage.get();
        int endIndex = Math.min(startIndex + itemsPerPage.get(), filteredData.size());

        // Cria uma nova lista com os itens da página atual
        ObservableList<Cliente> pageItems = FXCollections.observableArrayList(
            filteredData.subList(startIndex, endIndex)
        );

        tableView.setItems(pageItems);
        tableView.refresh();
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
        filterField.clear();

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
            modalController.setClienteController(this); // Passa referência do controlador principal

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
        if (clienteRepository.clientePossuiAgendamentos(clienteId)) {
            handleError("Este cliente possui agendamento(s) pendente(s)");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalConfirmarRemocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setRegisterId(clienteId);
            modalController.setConfirmCallback(() -> {
                clienteRepository.deleteById(clienteId);
                atualizarTableView(); // Atualizar tabela após exclusão
                handleSuccessfulOperation("Cliente removido com sucesso!");
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
            handleError("Este cliente não possui pets cadastrados!");
            return;
        }

        // Configurar o controlador do modal
        AnimalController animalController = ScreenManagerHolder.getInstance().getAnimalController();
        animalController.setCliente(cliente);
        animalController.atualizarTableView();

        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.PETS);
    }

    public void atualizarTableView() {
        clientesList.setAll(clienteRepository.findAll());
        numberOfResults.setText(clientesList.size() + " registro(s) retornado(s)");
        configurarPaginacao(); // O método atualizado será chamado aqui também
        tableView.refresh();
    }

    private void configurarBuscaClientes() {
        filteredData = new FilteredList<>(clientesList, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cliente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                boolean matchesCliente = cliente.getNome().toLowerCase().contains(lowerCaseFilter) ||
                        cliente.getTelefone().toLowerCase().contains(lowerCaseFilter);
                boolean matchesPet = cliente.getPets().stream()
                        .anyMatch(pet -> pet.getNome().toLowerCase().contains(lowerCaseFilter));
                return matchesCliente || matchesPet;
            });

            Platform.runLater(() -> {
                // Atualiza o número de resultados
                numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");

                // Se não houver resultados, limpa a tabela
                if (filteredData.isEmpty()) {
                    tableView.setItems(FXCollections.observableArrayList());
                }

                // Reconfigura a paginação com os dados filtrados
                configurarPaginacao();
            });
        });
    }

    public void handleSuccessfulOperation(String message) {
        FeedbackManager.showFeedback(
                feedbackContainer,
                message,
                FeedbackManager.FeedbackType.SUCCESS
        );
    }

    public void handleError(String message) {
        FeedbackManager.showFeedback(
                feedbackContainer,
                message,
                FeedbackManager.FeedbackType.ERROR
        );
    }
}