package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import javafx.application.Platform;
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
    private static final int ROW_HEIGHT = 79; // Altura de cada linha em pixels

    private static final int HEADER_HEIGHT = 79; // Altura do header em pixels

    private IntegerProperty itemsPerPage = new SimpleIntegerProperty();

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

        // Adicionar listener para mudanças na altura da tabela
        tableView.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            calcularItensPorPagina(newHeight.doubleValue());
            tableView.refresh(); // Força a atualização da TableView
            // Reconfigura a paginação quando a altura muda
            Platform.runLater(this::configurarPaginacao);
        });

        // Calcula inicial de itens por página
        calcularItensPorPagina(tableView.getHeight());

        // Adiciona listener para o campo de busca
        filterField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Quando o campo recebe foco
                limparFiltroData();
            }
        });

        configurarColunas();
        configurarBuscaAgendamentos();
        atualizarTableView();
    }

    private void calcularItensPorPagina(double alturaTotal) {
        // Subtrai a altura do header da altura total
        double alturaDisponivel = alturaTotal - HEADER_HEIGHT;
        // Calcula quantas linhas cabem na altura disponível
        int numeroLinhas = Math.max(1, (int) Math.floor(alturaDisponivel / ROW_HEIGHT));
        // Atualiza a propriedade de itens por página
        itemsPerPage.set(numeroLinhas);
    }

    private void configurarPaginacao() {
        if (filteredData.isEmpty()) {
            paginator.setPageCount(1);
            paginator.setCurrentPageIndex(0);
            paginator.setDisable(true);
            tableView.setItems(FXCollections.observableArrayList());
            return;
        }

        // Usa o valor dinâmico de itens por página
        int totalPages = (int) Math.ceil((double) filteredData.size() / itemsPerPage.get());
        paginator.setPageCount(totalPages);

        // Se a página atual é maior que o novo número total de páginas,
        // ajusta para a última página válida
        if (paginator.getCurrentPageIndex() >= totalPages) {
            paginator.setCurrentPageIndex(totalPages - 1);
        }

        paginator.setDisable(false);
        paginator.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            Platform.runLater(() -> {
                atualizarPaginaAtual(newIndex.intValue());
                tableView.refresh(); // Força a atualização da TableView
            });
        });

        // Atualiza a visualização inicial
        atualizarPaginaAtual(paginator.getCurrentPageIndex());
    }

    private void atualizarPaginaAtual(int pageIndex) {
        int startIndex = pageIndex * itemsPerPage.get();
        int endIndex = Math.min(startIndex + itemsPerPage.get(), filteredData.size());

        // Cria uma nova lista com os itens da página atual
        ObservableList<Agendamento> pageItems = FXCollections.observableArrayList(
                filteredData.subList(startIndex, endIndex)
        );

        tableView.setItems(pageItems);
        tableView.refresh();
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
        int totalPages = (int) Math.ceil((double) filteredData.size() / itemsPerPage.get());
        paginator.setPageCount(Math.max(totalPages, 1));

        paginator.setPageFactory(pageIndex -> {
            atualizarPagina(pageIndex);
            return new VBox(); // Retorna um nó vazio
        });

        atualizarPagina(0);
    }

    private void atualizarPagina(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage.get();
        int toIndex = Math.min(fromIndex + itemsPerPage.get(), filteredData.size());

        if (fromIndex <= toIndex) {
            tableView.setItems(FXCollections.observableArrayList(sortedData.subList(fromIndex, toIndex)));
        }
    }

    @FXML
    private void aplicarFiltroData() {
        LocalDate dataInicial = dataInicialPicker.getValue();
        LocalDate dataFinal = dataFinalPicker.getValue();

        if (dataInicial != null && dataFinal != null) {
            filteredData.setPredicate(agendamento -> {
                LocalDate dataAgendamento = agendamento.getDataAgendamento();
                return !dataAgendamento.isBefore(dataInicial) && !dataAgendamento.isAfter(dataFinal);
            });
        } else {
            resetarFiltros();
        }

        atualizarResultados();
    }

    @FXML
    private void limparFiltroData() {
        // Limpa os campos de data
        dataInicialPicker.setValue(null);
        dataFinalPicker.setValue(null);

        // Reseta os filtros e atualiza a tabela
        resetarFiltros();
        atualizarResultados();
    }

    // Método auxiliar para resetar os filtros
    private void resetarFiltros() {
        filteredData.setPredicate(agendamento -> true);
    }

    // Método auxiliar para atualizar contagem e paginação
    private void atualizarResultados() {
        numberOfResults.setText(filteredData.size() + " registro(s) retornado(s)");
        atualizarPaginacao();
    }
}
