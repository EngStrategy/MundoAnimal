package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoricoController implements Initializable {

    private static final int ROWS_PER_PAGE = 8;

    @FXML
    private TableColumn<Agendamento, String> dataColumn, donoColumn, petColumn, responsavelColumn, tipoColumn;

    @FXML
    private TextField filterField;

    @FXML
    private Label numberOfResults;

    @FXML
    private TableView<Agendamento> tableView;

    @FXML
    private Pagination pagination;

    private final AgendamentoRepository agendamentoRepository = new AgendamentoRepository();
    private ObservableList<Agendamento> agendamentosList = FXCollections.observableArrayList();
    private FilteredList<Agendamento> filteredData;
    private SortedList<Agendamento> sortedData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("servico"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataAgendamento"));
        petColumn.setCellValueFactory(new PropertyValueFactory<>("animal"));
        responsavelColumn.setCellValueFactory(new PropertyValueFactory<>("responsavelAtendimento"));
        donoColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));

        filteredData = new FilteredList<>(agendamentosList, p -> true); // Inicializa antes de atualizar a tabela
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        configurarBuscaAgendamentos();
        atualizarTableView();
    }

    public void atualizarTableView() {
        agendamentosList.setAll(agendamentoRepository.findStatusFinalizado());

        // Atualiza a lista filtrada
        filteredData = new FilteredList<>(agendamentosList, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");
        atualizarPaginacao();
    }

    private void configurarBuscaAgendamentos() {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(agendamento -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                boolean matchesCliente = agendamento.getCliente().getNome().toLowerCase().contains(lowerCaseFilter);
                boolean matchesAnimal = agendamento.getAnimal().getNome().toLowerCase().contains(lowerCaseFilter);
                boolean matchesResponsavel = agendamento.getResponsavelAtendimento() != null &&
                        agendamento.getResponsavelAtendimento().toLowerCase().contains(lowerCaseFilter);
                boolean matchesServico = agendamento.getServico().getNomeServico().toLowerCase().contains(lowerCaseFilter);

                return matchesCliente || matchesAnimal || matchesResponsavel || matchesServico;
            });

            numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");
            atualizarPaginacao();
        });
    }

    private void atualizarPaginacao() {
        int totalPages = (int) Math.ceil((double) filteredData.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(totalPages, 1));

        pagination.setPageFactory(pageIndex -> {
            atualizarPagina(pageIndex);
            return new VBox(); // Retorna um nó vazio
        });

        atualizarPagina(0);
    }

    private void atualizarPagina(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());

        if (fromIndex <= toIndex) {
            tableView.setItems(FXCollections.observableArrayList(sortedData.subList(fromIndex, toIndex)));
        }
    }
}

