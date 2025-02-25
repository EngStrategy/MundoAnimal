package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicoRepositoryTest {

    private EntityManager em;
    private ServicoRepository servicoRepository = new ServicoRepository();

    @BeforeEach
    void setUp() {
        em = JPAutil.getEntityManager();
        limparBaseDeDados();
    }

    @Test
    void save() {
        Servico servico = criarServico();

        Servico savedServico = servicoRepository.save(servico);

        assertNotNull(savedServico.getId());
        assertEquals(servico.getNomeServico(), savedServico.getNomeServico());
        assertEquals(servico.getValorServico(), savedServico.getValorServico());
        assertEquals(servico.getDescricao(), savedServico.getDescricao());
    }

    @Test
    void findById() {
        Servico servico = criarServico();

        em.getTransaction().begin();
        em.persist(servico);
        em.getTransaction().commit();

        Servico foundServico = servicoRepository.findById(servico.getId());
        assertNotNull(foundServico);
        assertEquals(servico.getId(), foundServico.getId());
    }

    @Test
    void findAll() {
        Servico servico1 = criarServico();
        Servico servico2 = criarServico();

        em.getTransaction().begin();
        em.persist(servico1);
        em.persist(servico2);
        em.getTransaction().commit();

        List<Servico> servicos = servicoRepository.findAll();
        assertEquals(2, servicos.size());
    }

    @Test
    void deleteById() {
        Servico servico = criarServico();

        em.getTransaction().begin();
        em.persist(servico);
        em.getTransaction().commit();

        servicoRepository.deleteById(servico.getId());

        // Abrir um novo EntityManager para verificar
        EntityManager em2 = JPAutil.getEntityManager();
        Servico foundService = em2.find(Servico.class, servico.getId());
        assertNull(foundService);
        em2.close(); // Fechar o EntityManager após o uso
    }

    private void limparBaseDeDados() {
        // Limpar a base de dados antes de cada teste
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Agendamento").executeUpdate();
        em.createQuery("DELETE FROM Servico").executeUpdate();
        em.getTransaction().commit();
    }

    private Servico criarServico() {
        Servico servico = new Servico();
        servico.setNomeServico("Banho");
        servico.setValorServico(BigDecimal.valueOf(100));
        servico.setDescricao("Banho com água e sabão");
        return servico;
    }
}