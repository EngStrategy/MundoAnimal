package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.StatusAgendamento;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AnimalRepository {
    public Animal save(Animal animal) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (animal.getId() == null) {
                em.persist(animal);
            } else {
                em.merge(animal);
            }
            em.getTransaction().commit();
            return animal;
        }
    }

    public Animal findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Animal> query = em.createQuery("SELECT a FROM Animal a WHERE a.id = :id", Animal.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Animal> findAll() {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Animal> query = em.createQuery("SELECT a FROM Animal a ORDER BY a.nome", Animal.class);
            return query.getResultList();
        }
    }

    public void deleteById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            Animal animal = em.find(Animal.class, id);
            if(animal != null) {
                em.getTransaction().begin();

                // Remover o animal da lista de pets do cliente
                Cliente dono = animal.getDono();
                dono.getPets().remove(animal);

                em.remove(animal);

                em.remove(animal);
                em.getTransaction().commit();
            }
            else {
                throw new IllegalArgumentException("Animal not found!");
            }
        }
    }

    public boolean petPossuiAgendamentos(Long animalId) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Agendamento a WHERE a.animal.id = :animalId AND a.status = :statusPendente";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("animalId", animalId)
                    .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                    .getSingleResult();
            return count > 0;
        }
    }
}
