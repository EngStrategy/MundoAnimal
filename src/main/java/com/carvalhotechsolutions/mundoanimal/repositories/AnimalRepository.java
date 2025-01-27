package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AnimalRepository {

    private static final Logger logger = LogManager.getLogger(AnimalRepository.class);
    public Animal save(Animal animal) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (animal.getId() == null) {
                em.persist(animal);
                logger.info("Novo animal cadastrado: {}", animal);
            } else {
                em.merge(animal);
                logger.info("Animal atualizado {}", animal);
            }
            em.getTransaction().commit();
            return animal;
        } catch (Exception e) {
            logger.error("Erro ao salvar animal: {}", e.getMessage(), e);
            throw e; // Relançar a exceção para que a camada superior possa tratá-la
        }
    }

    public Animal findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            logger.debug("Buscando animal com ID: {}", id);
            TypedQuery<Animal> query = em.createQuery("SELECT a FROM Animal a WHERE a.id = :id", Animal.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Animal> findAll() {
        logger.debug("Buscando todos os animais...");
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Animal> query = em.createQuery("SELECT a FROM Animal a ORDER BY a.nome", Animal.class);
            return query.getResultList();
        }
    }

    public void deleteById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            logger.debug("Tentando excluir animal com ID: {}", id);
            Animal animal = em.find(Animal.class, id);
            if(animal != null) {
                em.getTransaction().begin();

                // Remover o animal da lista de pets do cliente
                Cliente dono = animal.getDono();
                dono.getPets().remove(animal);

                em.remove(animal);

                em.remove(animal);
                em.getTransaction().commit();
                logger.info("Animal removido com sucesso: {}", animal);
            }
            else {
                logger.info("Animal não encontrado");
                throw new IllegalArgumentException("Animal not found!");
            }
        }
    }
}
