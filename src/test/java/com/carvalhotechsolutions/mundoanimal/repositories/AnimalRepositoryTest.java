package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.EspecieAnimal;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnimalRepository animalRepository = new AnimalRepository();

    @BeforeEach
    void setUp() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("test"); // Definido em persistence.xml
        }
        em = JPAutil.getEntityManager();

        limparBaseDeDados();
    }

    @Test
    void save() {
        Animal animal = criarAnimal();
        Cliente cliente = animal.getDono();

        em.getTransaction().begin();
        em.persist(animal);
        em.getTransaction().commit();

        assertNotNull(animal.getId());
        assertEquals("Scooby", animal.getNome());
        assertEquals(EspecieAnimal.CACHORRO, animal.getEspecie());
        assertEquals(cliente, animal.getDono());
    }

    @Test
    void findById() {
        Animal animal = criarAnimal();

        em.getTransaction().begin();
        em.persist(animal);
        em.getTransaction().commit();

        Animal foundAnimal = animalRepository.findById(animal.getId());

        assertNotNull(foundAnimal);
        assertEquals(animal.getId(), foundAnimal.getId());
    }

    @Test
    void findAll() {
        Animal animal1 = criarAnimal();
        Animal animal2 = criarAnimal();

        em.getTransaction().begin();
        em.persist(animal1);
        em.persist(animal2);
        em.getTransaction().commit();

        List<Animal> animais = animalRepository.findAll();
        assertEquals(2, animais.size());
    }

    @Test
    void deleteById() {
        Animal animal = criarAnimal();
        Cliente cliente = animal.getDono();

        em.getTransaction().begin();
        em.persist(animal);
        em.getTransaction().commit();

        animalRepository.deleteById(animal.getId());

        // Abrir um novo EntityManager para verificar
        EntityManager em2 = emf.createEntityManager();
        Animal foundAnimal = em2.find(Animal.class, cliente.getId());
        assertNull(foundAnimal);
        em2.close(); // Fechar o EntityManager após o uso
    }

    private void limparBaseDeDados() {
        // Limpar a base de dados antes de cada teste
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Agendamento").executeUpdate();
        em.createQuery("DELETE FROM Animal").executeUpdate();
        em.createQuery("DELETE FROM Cliente").executeUpdate();
        em.createQuery("DELETE FROM Servico").executeUpdate();
        em.getTransaction().commit();
    }

    private Animal criarAnimal() {
        Cliente cliente = criarCliente("João Silva " + System.nanoTime(), "12345" + System.currentTimeMillis());
        Animal animal = new Animal();
        animal.setNome("Scooby");
        animal.setEspecie(EspecieAnimal.CACHORRO);
        animal.setDono(cliente);
        return animal;
    }

    private Cliente criarCliente(String nome, String telefone) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();
        return cliente;
    }
}