package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class AgendamentoRepositoryTest {

    private AgendamentoRepository agendamentoRepository = new AgendamentoRepository();
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = JPAutil.getEntityManager();
        limparBaseDeDados();
    }

    @Test
    void save() {
        // Arrange
        Agendamento agendamento = criarAgendamentoCompleto();

        // Act
        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        // Assert
        assertNotNull(savedAgendamento.getId());
        assertEquals(agendamento.getDataAgendamento(), savedAgendamento.getDataAgendamento());
        assertEquals(agendamento.getHorarioAgendamento(), savedAgendamento.getHorarioAgendamento());
    }

    @Test
    void update() {
        // Arrange
        Agendamento agendamento = criarAgendamentoCompleto();
        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        LocalTime novoHorario = LocalTime.of(15, 0);
        savedAgendamento.setHorarioAgendamento(novoHorario);

        // Act
        Agendamento updatedAgendamento = agendamentoRepository.save(savedAgendamento);

        // Assert
        assertEquals(novoHorario, updatedAgendamento.getHorarioAgendamento());
        assertEquals(savedAgendamento.getId(), updatedAgendamento.getId());
    }

    @Test
    void findById() {
        // Arrange
        Agendamento agendamento = criarAgendamentoCompleto();
        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        // Act
        Agendamento foundAgendamento = agendamentoRepository.findById(savedAgendamento.getId());

        // Assert
        assertNotNull(foundAgendamento);
        assertEquals(savedAgendamento.getId(), foundAgendamento.getId());
    }

    @Test
    void findAll() {
        // Arrange
        Agendamento agendamento1 = criarAgendamento(LocalDate.now(), LocalTime.of(14, 0));
        Agendamento agendamento2 = criarAgendamento(LocalDate.now(), LocalTime.of(9, 0));
        Agendamento agendamento3 = criarAgendamento(LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        agendamentoRepository.save(agendamento1);
        agendamentoRepository.save(agendamento2);
        agendamentoRepository.save(agendamento3);

        // Act
        List<Agendamento> agendamentos = agendamentoRepository.findAll();

        // Assert
        assertEquals(3, agendamentos.size());
        assertTrue(agendamentos.get(0).getHorarioAgendamento().isBefore(agendamentos.get(1).getHorarioAgendamento()));
        assertTrue(agendamentos.get(0).getDataAgendamento().isBefore(agendamentos.get(2).getDataAgendamento()) ||
                agendamentos.get(0).getDataAgendamento().isEqual(agendamentos.get(2).getDataAgendamento()));
    }

    @Test
    void deleteById() {
        // Arrange
        Agendamento agendamento = criarAgendamentoCompleto();
        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        // Act
        agendamentoRepository.deleteById(savedAgendamento.getId());

        // Assert
        assertThrows(NoResultException.class, () -> {
            agendamentoRepository.findById(savedAgendamento.getId());
        });
    }

    @Test
    void verificarDisponibilidadeHorario() {
        // Arrange
        LocalDate data = LocalDate.now();
        LocalTime horario = LocalTime.of(14, 0);
        Agendamento agendamento = criarAgendamento(data, horario);
        agendamentoRepository.save(agendamento);

        // Act & Assert
        assertFalse(agendamentoRepository.verificarDisponibilidadeHorario(data, horario));
        assertTrue(agendamentoRepository.verificarDisponibilidadeHorario(data, horario.plusHours(1)));
    }

    @Test
    void buscarAgendamentosPorData() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);

        Agendamento agendamento1 = criarAgendamento(hoje, LocalTime.of(9, 0));
        Agendamento agendamento2 = criarAgendamento(hoje, LocalTime.of(14, 0));
        Agendamento agendamento3 = criarAgendamento(amanha, LocalTime.of(10, 0));

        agendamentoRepository.save(agendamento1);
        agendamentoRepository.save(agendamento2);
        agendamentoRepository.save(agendamento3);

        // Act
        List<Agendamento> agendamentosHoje = agendamentoRepository.buscarAgendamentosPorData(hoje);
        List<Agendamento> agendamentosAmanha = agendamentoRepository.buscarAgendamentosPorData(amanha);

        // Assert
        assertEquals(2, agendamentosHoje.size());
        assertEquals(1, agendamentosAmanha.size());
    }

    private Agendamento criarAgendamentoCompleto() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste " + System.nanoTime());
        cliente.setTelefone(gerarNumero11Digitos());

        Animal animal = new Animal();
        animal.setNome("Pet Teste");
        animal.setEspecie(EspecieAnimal.CACHORRO);
        animal.setDono(cliente);

        Servico servico = new Servico();
        servico.setNomeServico("Banho");
        servico.setDescricao("Banho completo");
        servico.setValorServico(BigDecimal.valueOf(50.0));

        em.getTransaction().begin();
        em.persist(cliente);
        em.persist(animal);
        em.persist(servico);
        em.getTransaction().commit();

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setAnimal(animal);
        agendamento.setServico(servico);
        agendamento.setDataAgendamento(LocalDate.now());
        agendamento.setHorarioAgendamento(LocalTime.of(14, 0));
        agendamento.setResponsavelAtendimento("Funcionário Teste");

        return agendamento;
    }

    private Agendamento criarAgendamento(LocalDate data, LocalTime horario) {
        Agendamento agendamento = criarAgendamentoCompleto();
        agendamento.setDataAgendamento(data);
        agendamento.setHorarioAgendamento(horario);
        return agendamento;
    }

    private void limparBaseDeDados() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Agendamento").executeUpdate();
        em.createQuery("DELETE FROM Animal").executeUpdate();
        em.createQuery("DELETE FROM Cliente").executeUpdate();
        em.createQuery("DELETE FROM Servico").executeUpdate();
        em.getTransaction().commit();
    }

    private String gerarNumero11Digitos() {
        Random random = new Random();

        // Gera um número entre 10000000000L e 99999999999L (11 dígitos)
        long minimo = 10000000000L;
        long maximo = 99999999999L;

        // Calcula um número aleatório dentro do intervalo
        long numeroAleatorio = minimo + ((long)(random.nextDouble() * (maximo - minimo)));

        return String.valueOf(numeroAleatorio);
    }
}