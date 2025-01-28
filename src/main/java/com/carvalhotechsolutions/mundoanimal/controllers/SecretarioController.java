package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.SecretarioRepository;
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
import java.net.URL;
import java.util.ResourceBundle;

public class SecretarioController implements Initializable {
    @FXML
    private TableView<Secretario> tableViewSecretarios;

    @FXML
    private TableColumn<Secretario, String> nomeColumn; // Nome de Usuario

    @FXML
    private TableColumn<Secretario, String> phoneColumn; // Telefone

    @FXML
    private TableColumn<Secretario, Void> acaoColumn; // Ação

    private SecretarioRepository secretarioRepository = new SecretarioRepository();

    private ObservableList<Secretario> secretariosList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        configurarColunaAcao();

        atualizarTableView();
    }

    public void atualizarTableView() {
        secretariosList = FXCollections.observableArrayList(secretarioRepository.findAll());
        tableViewSecretarios.setItems(secretariosList);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modal-novo-secretario.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modal-editar-secretario.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalEditarSecretarioController modalController = loader.getController();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modal-confirmar-remocao.fxml"));
            Parent modalContent = loader.load();

            // Configurar o controlador do modal
            ModalConfirmarRemocaoController modalController = loader.getController();
            modalController.setServicoId(secretarioId);
            modalController.setConfirmCallback(() -> {
                secretarioRepository.deleteById(secretarioId);
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

}
