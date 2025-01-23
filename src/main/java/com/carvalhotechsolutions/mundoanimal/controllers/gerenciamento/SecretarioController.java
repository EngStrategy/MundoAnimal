package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarSecretarioController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalEditarSecretarioController;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.repositories.SecretarioRepository;
import com.carvalhotechsolutions.mundoanimal.utils.FeedbackManager;
import javafx.beans.binding.DoubleBinding;
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

public class SecretarioController implements Initializable {
    @FXML
    private HBox feedbackContainer;

    @FXML
    private TableView<Secretario> tableView;

    @FXML
    private TableColumn<Secretario, String> nomeColumn; // Nome de Usuario

    @FXML
    private TableColumn<Secretario, String> phoneColumn; // Telefone

    @FXML
    private TableColumn<Secretario, Void> acaoColumn; // Ação

    @FXML
    private TextField filterField;

    private SecretarioRepository secretarioRepository = new SecretarioRepository();

    private ObservableList<Secretario> secretariosList;

    private FilteredList<Secretario> filteredData;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Define a largura fixa da coluna de ação
        acaoColumn.setPrefWidth(246);
        acaoColumn.setMinWidth(246);
        acaoColumn.setMaxWidth(246);

        // Faz um bind da largura disponível (largura total da tabela menos a largura fixa da coluna de ação)
        DoubleBinding larguraDisponivel = tableView.widthProperty().subtract(246);

        // Configura as outras colunas para se redimensionarem proporcionalmente
        nomeColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.50));  // 50% do espaço restante
        phoneColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.50)); // 50% do espaço restante

        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        configurarColunaAcao();
        atualizarTableView();
        configurarBuscaClientes();
    }

    public void atualizarTableView() {
        secretariosList = FXCollections.observableArrayList(secretarioRepository.findAll());
        filteredData = new FilteredList<>(secretariosList, p -> true);
        tableView.setItems(filteredData);
    }

    private void configurarColunaAcao() {
        acaoColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editarButton = new Button("Editar");
            private final Button deletarButton = new Button("Deletar");
            private final HBox container = new HBox(editarButton, deletarButton);

            {
                // Estilize os botões
                editarButton.setStyle(
                        "-fx-background-color: #686AFF; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");
                deletarButton.setStyle(
                        "-fx-background-color: #FF6F6F; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 90px;");

                container.setSpacing(16);
                container.setPadding(new Insets(0, 16, 0, 0));
                container.setAlignment(Pos.CENTER);

                // Configurar evento para deletar
                deletarButton.setOnAction(event -> {
                    Secretario secretario = getTableView().getItems().get(getIndex());
                    abrirModalExcluir(secretario.getId());
                });

                // Configurar evento para editar
                editarButton.setOnAction(event -> {
                    Secretario secretario = getTableView().getItems().get(getIndex());
                    abrirModalEditar(secretario.getId());
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
    public void abrirModalCadastrarSecretario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarSecretario.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalCriarSecretarioController modalController = loader.getController();
            modalController.setSecretarioController(this); // Passa referência do controlador principal

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Cadastrar Secretario");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir o modal: " + e.getMessage());
        }
    }

    private void abrirModalEditar(Long secretarioId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalEditarSecretario.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalEditarSecretarioController modalController = loader.getController();
            modalController.setSecretarioController(this);

            // Buscar o serviço pelo ID
            Secretario secretario = secretarioRepository.findById(secretarioId);

            // Configurar o modal para edição
            modalController.configurarParaEdicao(secretario);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Editar Secretário(a)");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

            atualizarTableView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirModalExcluir(Long secretarioId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalConfirmarRemocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setRegisterId(secretarioId);
            modalController.setConfirmCallback(() -> {
                secretarioRepository.deleteById(secretarioId);
                atualizarTableView(); // Atualizar tabela após exclusão
                handleSuccessfulOperation("Secretário(a) removido(a) com sucesso!");
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

    private void configurarBuscaClientes() {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(secretario -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return secretario.getNomeUsuario().toLowerCase().contains(lowerCaseFilter)
                        || secretario.getTelefone().toLowerCase().contains(lowerCaseFilter);
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
