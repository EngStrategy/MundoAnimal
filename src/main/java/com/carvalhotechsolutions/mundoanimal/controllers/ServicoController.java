package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    private TableColumn<Servico, Double> valorColumn;

    private ServicoRepository servicoRepository = new ServicoRepository();

    private ObservableList<Servico> servicosList;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nomeServico"));
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valorServico"));

        atualizarTableView();
    }

    public void atualizarTableView() {
        servicosList = FXCollections.observableArrayList(servicoRepository.findAll());
        tableView.setItems(servicosList);
    }
}
