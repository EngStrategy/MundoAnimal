package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecretarioRepositoryTest {

    private EntityManager em;
    private SecretarioRepository secretarioRepository = new SecretarioRepository();

    @BeforeEach
    void setUp() {
        em = JPAutil.getEntityManager();
        limparBaseDeDados();
    }

    @Test
    void save() {
        Secretario secretario = criarSecretario("user123", "(88) 88888-8888");

        Secretario savedSecretario = secretarioRepository.save(secretario);

        assertNotNull(savedSecretario.getId());
        assertEquals(secretario.getNomeUsuario(), savedSecretario.getNomeUsuario());
        assertEquals(secretario.getTelefone(), savedSecretario.getTelefone());
        assertEquals(secretario.getTipoUsuario(), savedSecretario.getTipoUsuario());
    }

    @Test
    void findById() {
        Secretario secretario = criarSecretario("user123", "(88) 88888-8888");

        em.getTransaction().begin();
        em.persist(secretario);
        em.getTransaction().commit();

        Secretario foundSecretario = secretarioRepository.findById(secretario.getId());

        assertNotNull(foundSecretario);
        assertEquals(secretario.getId(), foundSecretario.getId());
    }

    @Test
    void findAll() {
        Secretario secretario1 = criarSecretario("user123", "(88) 88888-8888");
        Secretario secretario2 = criarSecretario("user321", "(99) 88888-8888");

        em.getTransaction().begin();
        em.persist(secretario1);
        em.persist(secretario2);
        em.getTransaction().commit();

        List<Secretario> secretarios = secretarioRepository.findAll();
        assertEquals(2, secretarios.size());
    }

    @Test
    void deleteById() {
        Secretario secretario = criarSecretario("user123", "(88) 88888-8888");

        em.getTransaction().begin();
        em.persist(secretario);
        em.getTransaction().commit();

        secretarioRepository.deleteById(secretario.getId());

        // Abrir um novo EntityManager para verificar
        EntityManager em2 = JPAutil.getEntityManager();
        Secretario foundSecretario = em2.find(Secretario.class, secretario.getId());
        assertNull(foundSecretario);
        em2.close(); // Fechar o EntityManager após o uso
    }

    private void limparBaseDeDados() {
        // Limpar a base de dados antes de cada teste
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Secretario ").executeUpdate();
        em.getTransaction().commit();
    }

    private Secretario criarSecretario(String username, String telefone) {
        Secretario secretario = new Secretario();
        secretario.setNomeUsuario(username);
        secretario.setTelefone(telefone);
        secretario.setTipoUsuario(TipoUsuario.SECRETARIO);
        secretario.setSenha("senha123");
        return secretario;
    }
}