package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ServicoRepository extends GenericRepository<Servico> {

    public boolean servicoPossuiAgendamentos(Long servicoId) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Agendamento a WHERE a.servico.id = :servicoId";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("servicoId", servicoId)
                    .getSingleResult();
            return count > 0;
        }
    }
}
