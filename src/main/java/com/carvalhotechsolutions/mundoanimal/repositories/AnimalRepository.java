package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.StatusAgendamento;
import com.carvalhotechsolutions.mundoanimal.model.Animal;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AnimalRepository extends GenericRepository<Animal> {
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
