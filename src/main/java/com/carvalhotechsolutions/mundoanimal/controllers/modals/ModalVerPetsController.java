package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ModalVerPetsController implements Initializable {
    @FXML
    private Label titleLabel;

    @FXML
    private TableView<Animal> tableView;

    @FXML
    private TableColumn<Animal, String> nomePetColumn;

    @FXML
    private TableColumn<Animal, Void> acaoColumn;

    @FXML
    private Button actionButton;

    private Cliente cliente; // Dono dos pets que serão exibidos na tabela

    private ObservableList<Animal> petsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
    }

    private void configurarColunas() {
        // Configurar coluna do nome do pet
        nomePetColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        // Configurar coluna de ações
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
                        "-fx-background-color: #686AFF; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 86px;");
                deletarButton.setStyle(
                        "-fx-background-color: #FF6F6F; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: 800; -fx-cursor: hand; -fx-min-width: 86px;");
                detalhesButton.setStyle(
                        "-fx-background-color: #F2F5FA; -fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: 400; -fx-cursor: hand; -fx-min-width: 86px; -fx-border-color: #CCCCCC; -fx-border-radius: 2px;");

                container.setSpacing(16);
                container.setPadding(new Insets(0, 16, 0, 0));
                container.setAlignment(Pos.CENTER);

                // Configurar evento para deletar
                deletarButton.setOnAction(event -> {

                });

                // Configurar evento para editar
                editarButton.setOnAction(event -> {

                });

                detalhesButton.setOnAction(event -> {

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
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        titleLabel.setText("Pets de " + cliente.getNome());
    }

    public void inicializarTabela() {
        petsList.clear();
        petsList.addAll(cliente.getPets());
        tableView.setItems(petsList);
    }

    @FXML
    public void sair() {
        Stage stage = (Stage) actionButton.getScene().getWindow();
        stage.close();
    }
}
