package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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
    private Pagination paginator;

    @FXML
    private Button btnFiltro;

    @FXML
    private DatePicker dataFinalPicker;

    @FXML
    private DatePicker dataInicialPicker;


    private final AgendamentoRepository agendamentoRepository = new AgendamentoRepository();
    private ObservableList<Agendamento> agendamentosList = FXCollections.observableArrayList();
    private FilteredList<Agendamento> filteredData;
    private SortedList<Agendamento> sortedData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        filteredData = new FilteredList<>(agendamentosList, p -> true); // Inicializa antes de atualizar a tabela
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        configurarColunas();
        configurarBuscaAgendamentos();
        atualizarTableView();
    }

    public void atualizarTableView() {

        List<Agendamento> novosAgendamentos = agendamentoRepository.findStatusFinalizado();
        agendamentosList.setAll(novosAgendamentos); // Apenas atualiza os dados existentes

        numberOfResults.setText(agendamentosList.size() + " registro(s) retornado(s)");
        atualizarPaginacao();
    }

    private void configurarColunas() {
        tipoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServico().getNomeServico()));
        dataColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataHoraFormatada()));
        petColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAnimal().getNome()));
        responsavelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getResponsavelAtendimento()));
        donoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCliente().getNome()));
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
        paginator.setPageCount(Math.max(totalPages, 1));

        paginator.setPageFactory(pageIndex -> {
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

    @FXML
    private void aplicarFiltroData(ActionEvent event) {
        LocalDate dataInicial = dataInicialPicker.getValue();
        LocalDate dataFinal = dataFinalPicker.getValue();

        if (dataInicial != null && dataFinal != null) {
            filteredData.setPredicate(agendamento -> {
                LocalDate dataAgendamento = agendamento.getDataAgendamento(); // Supondo que a data de agendamento seja do tipo LocalDate

                // Verifica se a data de agendamento está dentro do intervalo selecionado
                boolean dentroDoIntervalo = !dataAgendamento.isBefore(dataInicial) && !dataAgendamento.isAfter(dataFinal);
                return dentroDoIntervalo;
            });
        } else {
            // Se as datas não forem selecionadas, retorna todos os agendamentos
            filteredData.setPredicate(agendamento -> true);
        }

        // Atualiza a tabela com o filtro de data
        numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");
        atualizarPaginacao();
    }

}
