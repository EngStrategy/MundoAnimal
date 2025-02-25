package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClienteRepositoryTest {

    private EntityManager em;
    private ClienteRepository clienteRepository = new ClienteRepository();

    @BeforeEach
    void setUp() {
        em = JPAutil.getEntityManager();
        limparBaseDeDados();
    }

    @Test
    void save() {
        Cliente cliente = criarCliente("João", "(88) 88888-8888");

        Cliente savedCliente = clienteRepository.save(cliente);

        assertNotNull(savedCliente.getId());
        assertEquals(cliente.getNome(), savedCliente.getNome());
        assertEquals(cliente.getTelefone(), savedCliente.getTelefone());
    }

    @Test
    void findById() {
        Cliente cliente = criarCliente("João", "(88) 88888-8888");

        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();

        Cliente foundCliente = clienteRepository.findById(cliente.getId());

        assertNotNull(foundCliente);
        assertEquals(cliente.getId(), foundCliente.getId());
    }

    @Test
    void findAll() {
        Cliente cliente1 = criarCliente("João", "(88) 88888-8888");
        Cliente cliente2 = criarCliente("Maria", "(99) 99999-9999");

        em.getTransaction().begin();
        em.persist(cliente1);
        em.persist(cliente2);
        em.getTransaction().commit();

        List<Cliente> clientes = clienteRepository.findAll();
        assertEquals(2, clientes.size());
    }

    @Test
    void deleteById() {
        Cliente cliente = criarCliente("João", "(88) 88888-8888");

        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();

        clienteRepository.deleteById(cliente.getId());

        // Abrir um novo EntityManager para verificar
        EntityManager em2 = JPAutil.getEntityManager();
        Cliente foundCliente = em2.find(Cliente.class, cliente.getId());
        assertNull(foundCliente);
        em2.close(); // Fechar o EntityManager após o uso
    }

    @Test
    public void deveRetornarTrueQuandoClientePossuiAgendamentos() {
        Cliente cliente = criarCliente("João", "(88) 88888-8888");

        em.getTransaction().begin();
        em.persist(cliente);

        Servico servico = new Servico();
        servico.setNomeServico("Banho");
        servico.setValorServico(BigDecimal.valueOf(100));
        servico.setDescricao("Banho com água e sabão");
        em.persist(servico);

        Animal animal = new Animal();
        animal.setNome("Scooby");
        animal.setEspecie(EspecieAnimal.CACHORRO);
        animal.setDono(cliente);
        em.persist(animal);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setAnimal(animal);
        agendamento.setDataAgendamento(LocalDate.now());
        agendamento.setHorarioAgendamento(LocalTime.of(14, 0));
        em.persist(agendamento);

        em.getTransaction().commit();

        boolean resultado = clienteRepository.clientePossuiAgendamentos(cliente.getId());
        assertTrue(resultado);
    }

    @Test
    public void deveRetornarFalseQuandoClienteNaoPossuiAgendamentos() {
        Cliente cliente = criarCliente("João Silva", "(88) 88888-8888");

        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();

        boolean resultado = clienteRepository.clientePossuiAgendamentos(cliente.getId());
        assertFalse(resultado);
    }

    private void limparBaseDeDados() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Agendamento").executeUpdate();
        em.createQuery("DELETE FROM Animal").executeUpdate();
        em.createQuery("DELETE FROM Cliente").executeUpdate();
        em.createQuery("DELETE FROM Servico").executeUpdate();
        em.getTransaction().commit();
    }

    private Cliente criarCliente(String nome, String telefone) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        return cliente;
    }
}