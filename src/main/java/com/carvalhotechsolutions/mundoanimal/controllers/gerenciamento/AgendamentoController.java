package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarAgendamentoController;
import com.carvalhotechsolutions.mundoanimal.enums.StatusAgendamento;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import com.carvalhotechsolutions.mundoanimal.utils.FeedbackManager;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AgendamentoController implements Initializable {
    private static final int ROW_HEIGHT = 79; // Altura de cada linha em pixels

    private static final int HEADER_HEIGHT = 79; // Altura do header em pixels

    private IntegerProperty itemsPerPage = new SimpleIntegerProperty();

    @FXML
    private HBox feedbackContainer;

    @FXML
    private TableView<Agendamento> tableView;

    @FXML
    private TableColumn<Agendamento, String> servicoColumn;

    @FXML
    private TableColumn<Agendamento, String> horarioColumn;

    @FXML
    private TableColumn<Agendamento, String> petColumn;

    @FXML
    private TableColumn<Agendamento, String> clienteColumn;

    @FXML
    private TableColumn<Agendamento, Void> acaoColumn;

    @FXML
    private Label numberOfResults;

    @FXML
    private TextField filterField;

    @FXML
    private Pagination paginator;

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    private ObservableList<Agendamento> agendamentosList = FXCollections.observableArrayList();

    private FilteredList<Agendamento> filteredData = new FilteredList<>(agendamentosList);

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
        servicoColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.22));      // 22% do espaço restante
        horarioColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.34)); // 34% do espaço restante
        petColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.22));     // 22% do espaço restante
        clienteColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.22));     // 22% do espaço restante

        servicoColumn.setCellValueFactory(new PropertyValueFactory<>("servico"));
        horarioColumn.setCellValueFactory(new PropertyValueFactory<>("dataHoraFormatada"));
        petColumn.setCellValueFactory(new PropertyValueFactory<>("animal"));
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));

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
        configurarBuscaAgendamentos();
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
        ObservableList<Agendamento> pageItems = FXCollections.observableArrayList(
                filteredData.subList(startIndex, endIndex)
        );

        tableView.setItems(pageItems);
        tableView.refresh();
    }

    private void configurarColunaAcao() {
        acaoColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editarButton = new Button("Editar");
            private final Button cancelarButton = new Button("Cancelar");
            private final Button finalizarButton = new Button("Finalizar");

            private final HBox container = new HBox(editarButton, cancelarButton, finalizarButton);

            {
                // Estilizando os botões
                editarButton.setStyle(
                        "-fx-background-color: #686AFF; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                cancelarButton.setStyle(
                        "-fx-background-color: #FF6F6F; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                finalizarButton.setStyle(
                        "-fx-background-color: #F2F5FA; -fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: 400; -fx-cursor: hand; -fx-min-width: 90px; -fx-border-color: #CCCCCC; -fx-border-radius: 2px;");

                container.setSpacing(16);
                container.setPadding(new Insets(0, 16, 0, 0));
                container.setAlignment(Pos.CENTER);

                // Configurar evento para deletar
                cancelarButton.setOnAction(event -> {
                    Agendamento agendamento = getTableView().getItems().get(getIndex());
                    abrirModalCancelar(agendamento.getId());
                });

                // Configurar evento para editar
                editarButton.setOnAction(event -> {
                    Agendamento agendamento = getTableView().getItems().get(getIndex());
                    abrirModalEditar(agendamento.getId());
                });

                // Configurar evento para finalizar
                finalizarButton.setOnAction(event -> {
                    Agendamento agendamento = getTableView().getItems().get(getIndex());
                    abrirModalFinalizar(agendamento.getId());
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

    private void abrirModalEditar(Long agendamentoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarAgendamento.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalCriarAgendamentoController modalController = loader.getController();
            modalController.setAgendamentoController(this); // Passa referência do controlador principal

            // Buscar o serviço pelo ID
            Agendamento agendamento = agendamentoRepository.findById(agendamentoId);

            // Configurar o modal para edição
            modalController.configurarParaEdicao(agendamento);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Editar Agendamento");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

            atualizarTableView();

            Platform.runLater(() -> ScreenManagerHolder.getInstance().getInicioController().atualizarProximosAgendamentos());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirModalCancelar(Long agendamentoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalConfirmarRemocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setRegisterId(agendamentoId);
            modalController.configurarParaCancelamento();
            modalController.setConfirmCallback(() -> {
                agendamentoRepository.deleteById(agendamentoId);
                atualizarTableView(); // Atualizar tabela após exclusão
                handleSuccessfulOperation("Agendamento cancelado com sucesso!");

                Platform.runLater(() -> ScreenManagerHolder.getInstance().getInicioController().atualizarProximosAgendamentos());
            });

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Confirmar Cancelamento");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirModalFinalizar(Long agendamentoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarAgendamento.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalCriarAgendamentoController modalController = loader.getController();
            modalController.setAgendamentoController(this); // Passa referência do controlador principal

            // Buscar o serviço pelo ID
            Agendamento agendamento = agendamentoRepository.findById(agendamentoId);

            // Configurar o modal para edição
            modalController.configurarParaFinalizar(agendamento);

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

    public void abrirModalCadastrarAgendamento() {
        filterField.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarAgendamento.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalCriarAgendamentoController modalController = loader.getController();
            modalController.setAgendamentoController(this); // Passa referência do controlador principal

            modalController.configurarParaCadastro();

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Cadastrar Agendamento");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir o modal: " + e.getMessage());
        }
    }

    public void atualizarTableView() {
        agendamentosList.clear();
        agendamentosList.addAll(agendamentoRepository.findStatusPendente());
        numberOfResults.setText(agendamentosList.size() + " registro(s) retornado(s)");
        configurarPaginacao(); // O método atualizado será chamado aqui também
        tableView.refresh();
    }

    public void configurarBuscaAgendamentos() {
        filteredData = new FilteredList<>(agendamentosList, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(agendamento -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                boolean matchesCliente = agendamento.getCliente().getNome().toLowerCase().contains(lowerCaseFilter);
                boolean matchesAnimal = agendamento.getAnimal().getNome().toLowerCase().contains(lowerCaseFilter);
                boolean matchesServico = agendamento.getServico().getNomeServico().toLowerCase().contains(lowerCaseFilter);
                boolean matchesData = agendamento.getDataHoraFormatada().toLowerCase().contains(lowerCaseFilter);
                boolean matchesStatus = agendamento.getStatus().toString().toLowerCase().contains(lowerCaseFilter);
                boolean matchesResponsavel = agendamento.getResponsavelAtendimento() != null &&
                        agendamento.getResponsavelAtendimento().toLowerCase().contains(lowerCaseFilter);

                return matchesCliente || matchesAnimal || matchesServico ||
                        matchesData || matchesStatus || matchesResponsavel;
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

    public void salvarAgendamento(Agendamento agendamento) {
        boolean horarioDisponivel = verificarDisponibilidadeHorario(
                agendamento.getDataAgendamento(),
                agendamento.getHorarioAgendamento()
        );

        if (!horarioDisponivel && agendamento.getId() == null) {
            throw new RuntimeException("Horário já está ocupado");
        }

        agendamentoRepository.save(agendamento);

        Platform.runLater(() -> ScreenManagerHolder.getInstance().getInicioController().atualizarProximosAgendamentos());
    }

    private boolean verificarDisponibilidadeHorario(LocalDate data, LocalTime horario) {
        // Lógica para verificar se já existe agendamento no mesmo horário
        return agendamentoRepository.verificarDisponibilidadeHorario(data, horario);
    }

    public void finalizarAgendamento(Long id, String responsavel, String horarioSaida) {
        Agendamento agendamentoFinalizado = agendamentoRepository.findById(id);
        agendamentoFinalizado.setStatus(StatusAgendamento.FINALIZADO);
        agendamentoFinalizado.setHorarioSaida(LocalTime.parse(horarioSaida));
        agendamentoFinalizado.setResponsavelAtendimento(responsavel);
        agendamentoRepository.save(agendamentoFinalizado);
        handleSuccessfulOperation("Agendamento finalizado com sucesso!");

        Platform.runLater(() -> ScreenManagerHolder.getInstance().getInicioController().atualizarProximosAgendamentos());
        Platform.runLater(() -> ScreenManagerHolder.getInstance().getInicioController().atualizarClientesFrequentes());
        Platform.runLater(() -> ScreenManagerHolder.getInstance().getInicioController().atualizarServicosUtilizados());
    }

}
