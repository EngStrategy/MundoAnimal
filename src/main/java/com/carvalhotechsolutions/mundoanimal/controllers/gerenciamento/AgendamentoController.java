package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarAgendamentoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarPetController;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import com.carvalhotechsolutions.mundoanimal.utils.FeedbackManager;
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

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    private ObservableList<Agendamento> agendamentosList = FXCollections.observableArrayList();

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

        configurarColunaAcao();
        atualizarTableView();
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
//                editarButton.setOnAction(event -> {
//                    Agendamento agendamento = getTableView().getItems().get(getIndex());
//                    abrirModalEditar(agendamento.getId());
//                });

//                finalizarButton.setOnAction(event -> {
//                    Agendamento agendamento = getTableView().getItems().get(getIndex());
//                    abrirModalCadastrarPet(agendamento.getId());
//                });
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

    public void abrirModalCadastrarAgendamento() {
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
        agendamentosList = FXCollections.observableArrayList(agendamentoRepository.findAll());
        tableView.setItems(agendamentosList);
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

        if (!horarioDisponivel) {
            throw new RuntimeException("Horário já está ocupado");
        }

        agendamentoRepository.save(agendamento);
    }

    private boolean verificarDisponibilidadeHorario(LocalDate data, LocalTime horario) {
        // Lógica para verificar se já existe agendamento no mesmo horário
        return agendamentoRepository.verificarDisponibilidadeHorario(data, horario);
    }
}
