package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoricoController implements Initializable {

    @FXML
    private TableColumn<Agendamento, String> dataColumn;

    @FXML
    private TableColumn<Agendamento, String> donoColumn;

    @FXML
    private TextField filterField;

    @FXML
    private Label numberOfResults;

    @FXML
    private TableColumn<Agendamento, String> petColumn;

    @FXML
    private TableColumn<Agendamento, String> responsavelColumn;

    @FXML
    private TableView<Agendamento> tableView;

    @FXML
    private TableColumn<Agendamento, String> tipoColumn;

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    private ObservableList<Agendamento> agendamentosList = FXCollections.observableArrayList();

    private FilteredList<Agendamento> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("servico"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataAgendamento"));
        petColumn.setCellValueFactory(new PropertyValueFactory<>("animal"));
        responsavelColumn.setCellValueFactory(new PropertyValueFactory<>("responsavelAtendimento"));
        donoColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));

        atualizarTableView();
        configurarBuscaAgendamentos();
    }

    public void atualizarTableView() {
        agendamentosList.setAll(agendamentoRepository.findStatusFinalizado());
        numberOfResults.setText(agendamentosList.size() + " registro(s) retornado(s)");
    }

    private void configurarBuscaAgendamentos() {
        filteredData = new FilteredList<>(agendamentosList, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(agendamento -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;  // Nenhum filtro aplicado, retorna todos
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Verifica se o nome do cliente, nome do animal ou o responsável contém o texto de busca
                boolean matchesCliente = agendamento.getCliente().getNome().toLowerCase().contains(lowerCaseFilter);
                boolean matchesAnimal = agendamento.getAnimal().getNome().toLowerCase().contains(lowerCaseFilter);
                boolean matchesResponsavel = agendamento.getResponsavelAtendimento() != null &&
                        agendamento.getResponsavelAtendimento().toLowerCase().contains(lowerCaseFilter);
                boolean matchesServico = agendamento.getServico().getNomeServico().toLowerCase().contains(lowerCaseFilter);

                return matchesCliente || matchesAnimal || matchesResponsavel || matchesServico;
            });

            // Atualiza o número de registros retornados
            numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");
        });

        // Aplica a lista filtrada na TableView
        tableView.setItems(filteredData);
    }

}
