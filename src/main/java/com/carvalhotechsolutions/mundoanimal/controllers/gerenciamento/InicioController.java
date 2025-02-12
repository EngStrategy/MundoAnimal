package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InicioController {
    @FXML
    private Label nomeServico1, horarioAgendamento1, nomeCliente1;
    @FXML
    private Label nomeServico2, horarioAgendamento2, nomeCliente2;
    @FXML
    private Label nomeServico3, horarioAgendamento3, nomeCliente3;
    @FXML
    private Label nomeServico4, horarioAgendamento4, nomeCliente4;
    @FXML
    private TableView<Agendamento> tableView;
    @FXML
    private TableColumn<Agendamento, String> clienteColumn, servicoColumn, dataColumn;
    @FXML
    private BarChart barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    @FXML
    public void initialize() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        DoubleBinding larguraDisponivel = tableView.widthProperty().subtract(354);

        clienteColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.35));
        servicoColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.35));
        dataColumn.prefWidthProperty().bind(larguraDisponivel.multiply(0.30));

        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        servicoColumn.setCellValueFactory(new PropertyValueFactory<>("servico"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataFormatada"));

        carregarProximosAgendamentos();
        atualizarAgendamentosFinalizados();
        configurarGrafico();
        atualizarGraficoServicos();
    }

    private void carregarProximosAgendamentos() {
        List<Agendamento> agendamentos = agendamentoRepository.findStatusPendente();

        // Lista de Labels para facilitar a atribuição
        List<Label> servicos = Arrays.asList(nomeServico1, nomeServico2, nomeServico3, nomeServico4);
        List<Label> horarios = Arrays.asList(horarioAgendamento1, horarioAgendamento2, horarioAgendamento3, horarioAgendamento4);
        List<Label> clientes = Arrays.asList(nomeCliente1, nomeCliente2, nomeCliente3, nomeCliente4);

        for (int i = 0; i < 4; i++) {
            if (i < agendamentos.size()) {
                Agendamento agendamento = agendamentos.get(i);
                servicos.get(i).setText(agendamento.getServico().getNomeServico());
                horarios.get(i).setText(agendamento.getDataHoraFormatada());
                clientes.get(i).setText(agendamento.getCliente().getNome());
            } else {
                // Definir mensagens padrão para os HBox vazios
                servicos.get(i).setText("Vago");
                horarios.get(i).setText("-");
                clientes.get(i).setText("-");
            }
        }
    }

    public void atualizarAgendamentosFinalizados() {
        List<Agendamento> ultimosFinalizados = agendamentoRepository.findUltimosFinalizados(5);

        // Atualiza os dados da TableView
        tableView.getItems().setAll(ultimosFinalizados);
    }

    public void atualizarGraficoServicos() {
        // Executar dentro do thread do JavaFX
        Platform.runLater(() -> {
            List<Agendamento> agendamentos = agendamentoRepository.findFinalizadosUltimaSemana();

            // Contar quantas vezes cada serviço foi realizado
            Map<String, Long> contagemServicos = agendamentos.stream()
                    .collect(Collectors.groupingBy(a -> a.getServico().getNomeServico(), Collectors.counting()));

            // Encontrar o maior valor
            long maxValue = contagemServicos.values().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0);

            // Configurar o eixo Y com base no valor máximo
            yAxis = (NumberAxis) barChart.getYAxis();
            // Arredondar para cima para o próximo número inteiro
            int upperBound = (int) Math.ceil(maxValue);
            yAxis.setUpperBound(upperBound);
            yAxis.setLowerBound(0);
            // Definir o número de marcações desejado
            int tickUnit = calculateTickUnit(upperBound);
            yAxis.setTickUnit(tickUnit);
            // Forçar apenas números inteiros
            yAxis.setAutoRanging(false);

            // Criar nova série de dados
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Quantidade");

            // Limpar dados existentes
            barChart.getData().clear();

            // Atualizar categorias do eixo X
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
            xAxis.getCategories().clear();

            // Adicionar novas categorias
            ObservableList<String> categorias = FXCollections.observableArrayList(contagemServicos.keySet());
            xAxis.setCategories(categorias);

            // Adicionar dados à série
            contagemServicos.forEach((servico, quantidade) ->
                    series.getData().add(new XYChart.Data<>(servico, quantidade))
            );

            // Adicionar série ao gráfico
            barChart.getData().add(series);


            // Forçar atualização do layout
            barChart.layout();
        });
    }

    // Método auxiliar para calcular o intervalo de marcação ideal
    private int calculateTickUnit(int maxValue) {
        if (maxValue <= 5) return 1;
        if (maxValue <= 10) return 2;
        if (maxValue <= 20) return 4;
        if (maxValue <= 50) return 5;
        if (maxValue <= 100) return 10;
        return Math.max(1, maxValue / 10); // Para valores maiores, dividir em aproximadamente 10 intervalos
    }


    // Método auxiliar para configuração inicial do gráfico
    public void configurarGrafico() {
        yAxis.setTickUnit(1); // Incremento de 1 em 1
        yAxis.setMinorTickVisible(false);
        barChart.setAnimated(false); // Desabilitar animações para evitar problemas de atualização
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-font-size: 14px;");
    }

    public void atualizarProximosAgendamentos() {
        Platform.runLater(this::carregarProximosAgendamentos);
    }

}
