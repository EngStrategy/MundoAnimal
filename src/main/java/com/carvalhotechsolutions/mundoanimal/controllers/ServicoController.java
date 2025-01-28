package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ServicoController implements Initializable {
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

    private ServicoRepository servicoRepository = new ServicoRepository();

    private ObservableList<Servico> servicosList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nomeServico"));
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valorServico"));

        configurarColunaValor();
        configurarColunaAcao();
        atualizarTableView();
    }

    private void configurarColunaAcao() {
        acaoColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editarButton = new Button("Editar");
            private final Button deletarButton = new Button("Deletar");
            private final HBox container = new HBox(editarButton, deletarButton);

            {
                // Estilize os botões
                editarButton.setStyle("-fx-background-color: #2E86C1; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand;");
                deletarButton.setStyle("-fx-background-color: #C0392B; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand;");
                container.setSpacing(18);
                container.setPadding(new Insets(10, 24, 10, 24));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modal-novo-servico.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modal-novo-servico.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalCriarServicoController modalController = loader.getController();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modal-confirmar-remocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setServicoId(servicoId);
            modalController.setConfirmCallback(() -> {
                servicoRepository.deleteById(servicoId);
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

    public void atualizarTableView() {
        servicosList = FXCollections.observableArrayList(servicoRepository.findAll());
        tableView.setItems(servicosList);
    }

}

