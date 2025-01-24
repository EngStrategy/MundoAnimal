package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.AgendamentoController;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.repositories.AgendamentoRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.AnimalRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ClienteRepository;
import com.carvalhotechsolutions.mundoanimal.repositories.ServicoRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ModalCriarAgendamentoController implements Initializable {
    @FXML
    private TextField agendamento_id_field;

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

    private AgendamentoController agendamentoController;

    private ServicoRepository servicoRepository = new ServicoRepository();

    private ClienteRepository clienteRepository = new ClienteRepository();

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarCampoData();
        configurarHorariosDisponiveis();
        carregarServicos();
        carregarClientes();
    }

    @FXML
    public void cadastrarAgendamento() {

        boolean isEdicao = agendamento_id_field.getText() != null && !agendamento_id_field.getText().isEmpty();

        // Validações
        if (!validarCampos()) {
            return;
        }

        // Criar novo agendamento
        Agendamento novoAgendamento = new Agendamento();
        novoAgendamento.setDataAgendamento(create_agendamento_date_field.getValue());
        novoAgendamento.setHorarioAgendamento(
                LocalTime.parse(create_agendamento_time_field.getValue(),
                        DateTimeFormatter.ofPattern("HH:mm"))
        );
        novoAgendamento.setServico(create_agendamento_servico_field.getValue());
        novoAgendamento.setCliente(create_agendamento_client_field.getValue());
        novoAgendamento.setAnimal(create_agendamento_pet_field.getValue());

        if (agendamentoController == null) {
            System.out.println("ERRO: agendamentoController é nulo!"); // Log para debug
            return;
        }

        try {
            agendamentoController.salvarAgendamento(novoAgendamento);
            agendamentoController.atualizarTableView();

            String mensagem = isEdicao ?
                    "Agendamento atualizado com sucesso!" :
                    "Agendamento cadastrado com sucesso!";

            System.out.println("Exibindo mensagem: " + mensagem); // Log para debug
            agendamentoController.handleSuccessfulOperation(mensagem);

            fecharModal();
        } catch (Exception e) {
            // Tratar erro (pode ser um alert)
            e.printStackTrace();
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

    private List<String> gerarHorariosDisponiveis(LocalDate data, List<Agendamento> agendamentosExistentes) {
        List<String> horariosDisponiveis = new ArrayList<>();
        LocalTime inicio = LocalTime.of(6, 0);
        LocalTime fim = LocalTime.of(20, 0);

        while (inicio.isBefore(fim.plusMinutes(1))) {
            String horarioStr = inicio.format(DateTimeFormatter.ofPattern("HH:mm"));

            // Verificar se o horário já está ocupado
            LocalTime finalInicio = inicio;
            boolean horarioOcupado = agendamentosExistentes.stream()
                    .anyMatch(a -> a.getHorarioAgendamento().equals(finalInicio));

            if (!horarioOcupado) {
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

    private void mostrarErro(String mensagem) {
        // Implementar exibição de erro (Alert, por exemplo)
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void setAgendamentoController(AgendamentoController agendamentoController) {
        this.agendamentoController = agendamentoController;
    }
}
