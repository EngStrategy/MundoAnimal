package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.AgendamentoController;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ModalCriarAgendamentoController implements Initializable {
    @FXML
    private TextField agendamento_id_field;

    @FXML
    private TextField finish;

    @FXML
    private DatePicker create_agendamento_date_field;

    @FXML
    private ComboBox<String> create_agendamento_time_field;

    @FXML
    private ComboBox<Servico> create_agendamento_servico_field;

    @FXML
    private ComboBox<Cliente> create_agendamento_client_field;

    @FXML
    private ComboBox<Animal> create_agendamento_pet_field;

    @FXML
    private TextField create_agendamento_responsavel_field;

    @FXML
    private ComboBox<String> create_agendamento_depTime_field;

    @FXML Button actionButton;

    @FXML
    private HBox endServiceContainer;

    @FXML
    private HBox lastRegisterContainer;

    private AgendamentoController agendamentoController;

    private ServicoRepository servicoRepository = new ServicoRepository();

    private ClienteRepository clienteRepository = new ClienteRepository();

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    private Agendamento agendamentoAtual; // em caso de ser edição ou finalizacao

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarCampoData();
        configurarHorariosDisponiveis();
        carregarServicos();
        carregarClientes();
        gerarHorarios();
    }

    @FXML
    public void cadastrarAgendamento() {
        // Validações
        if (!validarCampos()) {
            return;
        }

        if (agendamentoController == null) {
            System.out.println("ERRO: agendamentoController é nulo!"); // Log para debug
            return;
        }

        if (finish.getText() != null && !finish.getText().isEmpty()) {
            if (!validarCamposFinalizacao()) {
                return;
            }

            String responsavel = create_agendamento_responsavel_field.getText(); // Pegando o responsável
            if (responsavel == null || responsavel.isBlank()) {
                agendamentoController.handleError("O responsável deve ser informado!");
                return;
            }

            String horarioSaidaStr = create_agendamento_depTime_field.getValue();
            agendamentoController.finalizarAgendamento(agendamentoAtual.getId(), responsavel, horarioSaidaStr); // Passando o responsável
            fecharModal();
            return;
        }

        try {
            Agendamento agendamento;
            boolean isEdicao = agendamento_id_field.getText() != null && !agendamento_id_field.getText().isEmpty();

            if(isEdicao) {
                Long id = Long.parseLong(agendamento_id_field.getText());
                agendamento = agendamentoRepository.findById(id);
                System.out.println("Editando cliente com ID: " + id);
            } else {
                agendamento = new Agendamento();
                System.out.println("Criando novo cliente");
            }

            // Criar novo agendamento
            agendamento.setDataAgendamento(create_agendamento_date_field.getValue());
            agendamento.setHorarioAgendamento(
                    LocalTime.parse(create_agendamento_time_field.getValue(),
                            DateTimeFormatter.ofPattern("HH:mm"))
            );
            agendamento.setServico(create_agendamento_servico_field.getValue());
            agendamento.setCliente(create_agendamento_client_field.getValue());
            agendamento.setAnimal(create_agendamento_pet_field.getValue());

            agendamentoController.salvarAgendamento(agendamento);
            agendamentoController.atualizarTableView();

            String mensagem = isEdicao ?
                    "Agendamento atualizado com sucesso!" :
                    "Agendamento cadastrado com sucesso!";

            System.out.println("Exibindo mensagem: " + mensagem); // Log para debug
            agendamentoController.handleSuccessfulOperation(mensagem);

            fecharModal();
        } catch (Exception e) {
            e.printStackTrace();
            agendamentoController.handleError("Ocorreu um erro inesperado!");
        }
    }

    private void configurarHorariosDisponiveis() {
        // Adicionar listener para atualizar horários disponíveis quando a data for selecionada
        create_agendamento_date_field.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                atualizarHorariosDisponiveis(newValue);
            }
        });
    }

    private void atualizarHorariosDisponiveis(LocalDate data) {
        // Limpar horários anteriores
        create_agendamento_time_field.getItems().clear();

        // Buscar agendamentos para a data selecionada
        List<Agendamento> agendamentosNaData = agendamentoRepository.buscarAgendamentosPorData(data);

        // Gerar lista de horários
        List<String> horariosDisponiveis = gerarHorariosDisponiveis(data, agendamentosNaData);

        create_agendamento_time_field.getItems().addAll(horariosDisponiveis);
    }

    private void gerarHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime inicio = LocalTime.of(6, 0);
        LocalTime fim = LocalTime.of(20, 0);

        while (inicio.isBefore(fim.plusMinutes(1))) {
            horarios.add(inicio.format(DateTimeFormatter.ofPattern("HH:mm")));
            inicio = inicio.plusMinutes(15);
        }

        create_agendamento_depTime_field.getItems().addAll(horarios);
    }

    private List<String> gerarHorariosDisponiveis(LocalDate data, List<Agendamento> agendamentosExistentes) {
        List<String> horariosDisponiveis = new ArrayList<>();
        LocalTime inicio = LocalTime.of(6, 0);
        LocalTime fim = LocalTime.of(20, 0);

        // Obter a data e hora atual
        LocalDateTime agora = LocalDateTime.now();

        while (inicio.isBefore(fim.plusMinutes(1))) {
            String horarioStr = inicio.format(DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime finalInicio = inicio;

            // Verificar se o horário já está ocupado
            boolean horarioOcupado = agendamentosExistentes.stream()
                    .anyMatch(a -> a.getHorarioAgendamento().equals(finalInicio));

            // Verificar se o horário já passou (apenas para a data atual)
            boolean horarioPassou = data.equals(agora.toLocalDate()) && inicio.isBefore(agora.toLocalTime());

            // Adicionar horário apenas se não estiver ocupado e não tiver passado
            if (!horarioOcupado && !horarioPassou) {
                horariosDisponiveis.add(horarioStr);
            }

            inicio = inicio.plusMinutes(15);
        }

        return horariosDisponiveis;
    }

    private void configurarCampoData() {
        // Definir formato de data brasileiro
        create_agendamento_date_field.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ?
                        LocalDate.parse(string, dateFormatter) : null;
            }
        });

        // Impedir seleção de datas anteriores
        create_agendamento_date_field.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void carregarServicos() {
        List<Servico> servicos = servicoRepository.findAll();
        create_agendamento_servico_field.getItems().addAll(servicos);
    }

    private void carregarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        create_agendamento_client_field.getItems().addAll(clientes);

        // Adicionar listener para filtrar pets quando cliente for selecionado
        create_agendamento_client_field.setOnAction(event -> {
            Cliente clienteSelecionado = create_agendamento_client_field.getValue();
            if (clienteSelecionado != null) {
                carregarPetsPorCliente(clienteSelecionado);

                // Limpar o valor selecionado no ComboBox de pets
                create_agendamento_pet_field.setValue(null);
            }
        });
    }

    private void carregarPetsPorCliente(Cliente cliente) {
        // Limpar pets anteriores
        create_agendamento_pet_field.getItems().clear();

        // Carregar apenas pets do cliente selecionado
        create_agendamento_pet_field.getItems().addAll(cliente.getPets());
    }

    private void fecharModal() {
        // Obter a janela atual e fechá-la
        Stage stage = (Stage) create_agendamento_date_field.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        if (create_agendamento_date_field.getValue() == null) {
            mostrarErro("Selecione uma data");
            return false;
        }
        if (create_agendamento_time_field.getValue() == null) {
            mostrarErro("Selecione um horário");
            return false;
        }
        if (create_agendamento_servico_field.getValue() == null) {
            mostrarErro("Selecione um serviço");
            return false;
        }
        if (create_agendamento_client_field.getValue() == null) {
            mostrarErro("Selecione um cliente");
            return false;
        }
        if (create_agendamento_pet_field.getValue() == null) {
            mostrarErro("Selecione um pet");
            return false;
        }
        return true;
    }

    private boolean validarCamposFinalizacao() {
        String responsavel = create_agendamento_responsavel_field.getText();
        Object depTime = create_agendamento_depTime_field.getValue();

        // Verifica se o campo "Responsável pelo Atendimento" está vazio
        if (responsavel == null || responsavel.trim().isEmpty()) {
            mostrarErro("Insira o nome do responsável pelo atendimento");
            return false;
        }

        // Verifica se o campo "Horário de Saída" não foi selecionado
        if (depTime == null) {
            mostrarErro("Selecione um horário de saída");
            return false;
        }

        return true; // Todos os campos foram preenchidos
    }

    private void mostrarErro(String mensagem) {
        // Implementar exibição de erro (Alert, por exemplo)
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void configurarParaCadastro() {
        endServiceContainer.setVisible(false);
        endServiceContainer.setManaged(false);
        lastRegisterContainer.setStyle("-fx-border-color: transparent transparent #cccccc transparent;");
        lastRegisterContainer.setPadding(new Insets(0, 30, 20, 30));
        actionButton.setText("Cadastrar");
    }

    public void setAgendamentoController(AgendamentoController agendamentoController) {
        this.agendamentoController = agendamentoController;
    }

    public void configurarParaEdicao(Agendamento agendamento) {
        this.agendamentoAtual = agendamento;
        agendamento_id_field.setText(agendamento.getId().toString()); // Preencher o campo de ID invisível

        configurarParaCadastro();
        actionButton.setText("Atualizar");

        // populando campos do formulario com dados atuais do agendamento
        create_agendamento_date_field.setValue(agendamento.getDataAgendamento());
        create_agendamento_time_field.setValue(
                agendamento.getHorarioAgendamento().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        create_agendamento_servico_field.setValue(agendamento.getServico());

        // Populando os ComboBoxes de cliente e pet
        create_agendamento_client_field.setValue(agendamento.getCliente());
        carregarPetsPorCliente(agendamento.getCliente()); // Carregar pets do cliente atual
        create_agendamento_pet_field.setValue(agendamento.getAnimal());
    }

    public void configurarParaFinalizar(Agendamento agendamento) {
        this.agendamentoAtual = agendamento;
        finish.setText("true");
        actionButton.setText("Finalizar");

        create_agendamento_date_field.setValue(agendamento.getDataAgendamento());
        create_agendamento_date_field.setDisable(true);

        create_agendamento_time_field.setValue(
                agendamento.getHorarioAgendamento().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        create_agendamento_time_field.setDisable(true);

        create_agendamento_servico_field.setValue(agendamento.getServico());
        create_agendamento_servico_field.setDisable(true);

        create_agendamento_client_field.setValue(agendamento.getCliente());
        create_agendamento_client_field.setDisable(true);

        create_agendamento_pet_field.setValue(agendamento.getAnimal());
        create_agendamento_pet_field.setDisable(true);
    }
}
