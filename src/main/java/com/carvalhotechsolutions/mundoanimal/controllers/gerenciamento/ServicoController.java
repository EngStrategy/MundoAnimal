package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalCriarServicoController;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ServicoController implements Initializable {
    @FXML
    private HBox feedbackContainer;

    @FXML
    private Label numberOfResults;

    @FXML
    private TableView<Servico> tableView;

    @FXML
    private TableColumn<Servico, String> nomeColumn;

    @FXML
    private TableColumn<Servico, String> descricaoColumn;

    @FXML
    private TableColumn<Servico, BigDecimal> valorColumn;

    @FXML
    private TableColumn<Servico, Void> acaoColumn;

    @FXML
    private TextField filterField;

    private ServicoRepository servicoRepository = new ServicoRepository();

    private ObservableList<Servico> servicosList = FXCollections.observableArrayList();

    private FilteredList<Servico> filteredData;

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
        nomeColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.25));      // 25% do espaço restante
        descricaoColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.60)); // 60% do espaço restante
        valorColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.15));     // 15% do espaço restante

        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nomeServico"));
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valorServico"));

        configurarColunaValor();
        configurarColunaAcao();
        atualizarTableView();
        configurarBuscaServicos();
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
                    Servico servico = getTableView().getItems().get(getIndex());
                    abrirModalExcluir(servico.getId());
                });

                // Configurar evento para editar
                editarButton.setOnAction(event -> {
                    Servico servico = getTableView().getItems().get(getIndex());
                    abrirModalEditar(servico.getId());
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

    private void configurarColunaValor() {
        valorColumn.setCellFactory(column -> new TableCell<Servico, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Formatar valor para exibição com "R$"
                    setText("R$ " + item.setScale(2, BigDecimal.ROUND_HALF_UP).toString().replace(".", ","));
                }
            }
        });
    }


    @FXML
    public void abrirModalCadastrarServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarServico.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalCriarServicoController modalController = loader.getController();
            modalController.setServicoController(this); // Passa referência do controlador principal

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Cadastrar Serviço");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir o modal: " + e.getMessage());
        }
    }

    private void abrirModalEditar(Long servicoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalCriarServico.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalCriarServicoController modalController = loader.getController();
            modalController.setServicoController(this);

            // Buscar o serviço pelo ID
            Servico servico = servicoRepository.findById(servicoId);

            // Configurar o modal para edição
            modalController.configurarParaEdicao(servico);

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

    private void abrirModalExcluir(Long servicoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalConfirmarRemocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setRegisterId(servicoId);
            modalController.setConfirmCallback(() -> {
                servicoRepository.deleteById(servicoId);
                atualizarTableView(); // Atualizar tabela após exclusão
                handleSuccessfulOperation("Serviço removido com sucesso!");
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

    public void atualizarTableView() {
        servicosList.setAll(servicoRepository.findAll());
        numberOfResults.setText(servicosList.size() + " registro(s) retornado(s)");
    }

    private void configurarBuscaServicos() {
        filteredData = new FilteredList<>(servicosList, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(servico -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Se o campo de busca estiver vazio, mostra todos os serviços
                }
                String lowerCaseFilter = newValue.toLowerCase();

                // Verificando se o nome do serviço, descrição ou preço contém o termo de busca
                boolean matchesNome = servico.getNomeServico().toLowerCase().contains(lowerCaseFilter);
                boolean matchesDescricao = servico.getDescricao() != null && servico.getDescricao().toLowerCase().contains(lowerCaseFilter);
                boolean matchesPreco = servico.getValorServico().toString().contains(newValue); // Comparando com o preço

                // Retorna true se qualquer um dos campos for um match
                return matchesNome || matchesDescricao || matchesPreco;
            });
            numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");
        });
        tableView.setItems(filteredData);
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

