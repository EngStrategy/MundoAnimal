package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalDetalhesAgendamentoController;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InicioController {
    @FXML
    private TextField agendamento1_id, agendamento2_id, agendamento3_id, agendamento4_id;
    @FXML
    private Label nomeServico1, horarioAgendamento1, nomeCliente1;
    @FXML
    private Label nomeServico2, horarioAgendamento2, nomeCliente2;
    @FXML
    private Label nomeServico3, horarioAgendamento3, nomeCliente3;
    @FXML
    private Label nomeServico4, horarioAgendamento4, nomeCliente4;
    @FXML
    private ChoiceBox<String> periodoChoiceBox1;
    @FXML
    private Label first_client, second_client, third_client, fourth_client, fifth_client;
    @FXML
    private Label first_client_services, second_client_services, third_client_services, fourth_client_services, fifth_client_services;
    @FXML
    private ChoiceBox<String> periodoChoiceBox2;
    @FXML
    private PieChart pieChart;
    @FXML
    private VBox pieChartContainer, emptyStateContainer;

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    @FXML
    public void initialize() {
        List<ChoiceBox<String>> choiceBoxes = Arrays.asList(periodoChoiceBox1, periodoChoiceBox2);
        choiceBoxes.forEach(
                x -> x.getItems().addAll(
                        "Total",
                        "Última semana",
                        "Último mês",
                        "Últimos 6 meses"
                )
        );
        choiceBoxes.forEach(x -> x.setValue("Total"));

        // Listener para atualizar os clientes frequentes quando mudar o período
        periodoChoiceBox1.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> atualizarClientesFrequentes()
        );

        // Listener para atualizar o gráfico quando mudar a seleção
        periodoChoiceBox2.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> atualizarGraficoServicos()
        );

        carregarProximosAgendamentos();
        carregarClientesFrequentes();
        setupEmptyState();
        atualizarGraficoServicos();
    }

    @FXML
    private void handleHBoxClick(MouseEvent event) {
        HBox clickedHBox = (HBox) event.getSource();
        // Find the VBox within the clicked HBox
        VBox vbox = (VBox) clickedHBox.getChildren().get(0);
        // Find the hidden TextField within the VBox
        TextField idField = (TextField) vbox.getChildren().stream()
                .filter(node -> node instanceof TextField)
                .findFirst()
                .orElse(null);

        if (idField != null && !idField.getText().isEmpty()) {
            Long agendamentoId = Long.parseLong(idField.getText());
            abrirModalDetalhes(agendamentoId);
        }
    }

    private void carregarProximosAgendamentos() {
        List<Agendamento> agendamentos = agendamentoRepository.findStatusPendente();

        // Lista de Labels para facilitar a atribuição
        List<TextField> ids = Arrays.asList(agendamento1_id, agendamento2_id, agendamento3_id, agendamento4_id);
        List<Label> servicos = Arrays.asList(nomeServico1, nomeServico2, nomeServico3, nomeServico4);
        List<Label> horarios = Arrays.asList(horarioAgendamento1, horarioAgendamento2, horarioAgendamento3, horarioAgendamento4);
        List<Label> clientes = Arrays.asList(nomeCliente1, nomeCliente2, nomeCliente3, nomeCliente4);

        for (int i = 0; i < 4; i++) {
            // Get the parent HBox of the TextField
            HBox currentHBox = (HBox) ids.get(i).getParent().getParent();

            if (i < agendamentos.size()) {
                Agendamento agendamento = agendamentos.get(i);
                ids.get(i).setText(agendamento.getId().toString());
                servicos.get(i).setText(agendamento.getServico().getNomeServico());
                horarios.get(i).setText(agendamento.getDataHoraFormatada());
                clientes.get(i).setText(agendamento.getCliente().getNome());

                // Enable click handling for HBoxes with appointments
                currentHBox.setDisable(false);
            } else {
                // Definir mensagens padrão para os HBox vazios
                ids.get(i).setText("");
                servicos.get(i).setText("Vago");
                horarios.get(i).setText("-");
                clientes.get(i).setText("-");

                // Disable click handling for empty HBoxes
                currentHBox.setDisable(true);
            }
        }
    }

    private void carregarClientesFrequentes() {
        String periodoSelecionado = periodoChoiceBox1.getValue();
        LocalDate dataInicio = null;
        LocalDate dataFim = LocalDate.now();

        // Define o período com base na seleção
        switch (periodoSelecionado) {
            case "Última semana":
                dataInicio = dataFim.minusWeeks(1);
                break;
            case "Último mês":
                dataInicio = dataFim.minusMonths(1);
                break;
            case "Últimos 6 meses":
                dataInicio = dataFim.minusMonths(6);
                break;
            case "Total":
                dataInicio = LocalDate.of(2000, 1, 1); // Data bem antiga para pegar todos
                break;
        }

        // Busca os agendamentos do período
        List<Agendamento> agendamentos = agendamentoRepository.buscarAgendamentosPorIntervalo(dataInicio, dataFim);

        // Agrupa por cliente e conta os serviços
        Map<Cliente, Long> clientesContagem = agendamentos.stream()
                .collect(Collectors.groupingBy(
                        Agendamento::getCliente,
                        Collectors.counting()
                ));

        // Ordena os clientes por número de serviços (decrescente) e nome (crescente) para desempate
        List<Map.Entry<Cliente, Long>> clientesOrdenados = clientesContagem.entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    int compareByCount = e2.getValue().compareTo(e1.getValue());
                    if (compareByCount == 0) {
                        // Se tiver mesmo número de serviços, desempata por ordem alfabética
                        return e1.getKey().getNome().compareTo(e2.getKey().getNome());
                    }
                    return compareByCount;
                })
                .limit(5)
                .toList();

        // Lista de labels para facilitar a atualização
        List<Label> clienteLabels = Arrays.asList(
                first_client, second_client, third_client, fourth_client, fifth_client
        );
        List<Label> servicosLabels = Arrays.asList(
                first_client_services, second_client_services, third_client_services,
                fourth_client_services, fifth_client_services
        );

        // Atualiza os labels com os dados dos clientes
        for (int i = 0; i < 5; i++) {
            if (i < clientesOrdenados.size()) {
                // Tem cliente para essa posição
                Map.Entry<Cliente, Long> entry = clientesOrdenados.get(i);
                Cliente cliente = entry.getKey();
                Long quantidade = entry.getValue();

                // Atualiza o nome do cliente
                clienteLabels.get(i).setText(cliente.getNome());
                // Atualiza a quantidade de serviços
                servicosLabels.get(i).setText(quantidade.toString());
            } else {
                // Não tem cliente para essa posição
                clienteLabels.get(i).setText("Posição não ocupada");
                servicosLabels.get(i).setText("-");
            }
        }

        // Adiciona estilo diferente para posições não ocupadas
        for (int i = 0; i < 5; i++) {
            if (i >= clientesOrdenados.size()) {
                clienteLabels.get(i).setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
                servicosLabels.get(i).setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
            } else {
                clienteLabels.get(i).setStyle("");
                servicosLabels.get(i).setStyle("");
            }
        }
    }

    private void setupEmptyState() {
        emptyStateContainer = new VBox();
        emptyStateContainer.setAlignment(Pos.BOTTOM_CENTER);
        emptyStateContainer.setSpacing(10);
        emptyStateContainer.setPrefHeight(100); // Mesma altura aproximada do PieChart
        emptyStateContainer.setStyle("-fx-background-color: white;");

        // Ícone de gráfico
        Label iconLabel = new Label("📊");
        iconLabel.setStyle("-fx-font-size: 48px;");

        // Mensagem principal
        Label messageLabel = new Label("Nenhum serviço realizado no período");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #666666;");

        // Mensagem secundária
        Label subMessageLabel = new Label("Os serviços realizados aparecerão aqui após o primeiro atendimento");
        subMessageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");

        emptyStateContainer.getChildren().addAll(iconLabel, messageLabel, subMessageLabel);
    }

    private void atualizarGraficoServicos() {
        pieChart.setVisible(true);
        pieChart.setManaged(true);

        String periodoSelecionado = periodoChoiceBox2.getValue();
        Map<String, Long> servicosCounts = agendamentoRepository.getServicosMaisUtilizados(periodoSelecionado);

        // Remover qualquer visualização anterior
        pieChartContainer.getChildren().remove(emptyStateContainer);
        pieChart.setVisible(true);

        // Verificar se há dados
        if (servicosCounts.isEmpty()) {
            // Mostrar estado vazio
            pieChart.setVisible(false);
            pieChart.setManaged(false);
            if (!pieChartContainer.getChildren().contains(emptyStateContainer)) {
                pieChartContainer.getChildren().add(emptyStateContainer);
            }
            return;
        }

        // Limpar dados anteriores
        pieChart.getData().clear();
        pieChart.setLabelsVisible(false);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(Side.RIGHT);

        // Adicionar novos dados
        servicosCounts.forEach((servico, quantidade) -> {
            PieChart.Data slice = new PieChart.Data(
                    servico + " - " + quantidade,
                    quantidade.doubleValue()
            );
            pieChart.getData().add(slice);
        });

        // Adicionar tooltips e animações
        pieChart.getData().forEach(data -> {
            double percentagem = (data.getPieValue() / servicosCounts.values().stream()
                    .mapToDouble(Long::doubleValue).sum() * 100);
            String nomeServico = data.getName().split(" - ")[0];
            String texto = String.format("%s: %.1f%%", nomeServico, percentagem);

            Tooltip tooltip = new Tooltip(texto);
            tooltip.setShowDelay(Duration.ZERO);
            Tooltip.install(data.getNode(), tooltip);

            data.getNode().setOnMouseEntered(event ->
                    data.getNode().setStyle("-fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
            data.getNode().setOnMouseExited(event ->
                    data.getNode().setStyle("-fx-scale-x: 1; -fx-scale-y: 1;"));
        });
    }

    private void abrirModalDetalhes(Long agendamentoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modals/modalDetalhesAgendamento.fxml"));
            Parent modalContent = loader.load();

            // Obter o controlador do modal
            ModalDetalhesAgendamentoController modalController = loader.getController();

            // Buscar o serviço pelo ID
            Agendamento agendamento = agendamentoRepository.findById(agendamentoId);

            // Configurar o modal para edição
            modalController.configurarParaExibicao(agendamento);

            // Configurar o Stage do modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Detalhes do Agendamento");
            modalStage.setScene(new Scene(modalContent));
            modalStage.setResizable(false);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void atualizarServicosUtilizados() {
        Platform.runLater((this::atualizarGraficoServicos));
    }

    public void atualizarClientesFrequentes() {
        Platform.runLater((this::carregarClientesFrequentes));
    }

    public void atualizarProximosAgendamentos() {
        Platform.runLater(this::carregarProximosAgendamentos);
    }
}
