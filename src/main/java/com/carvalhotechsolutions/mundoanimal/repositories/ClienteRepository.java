package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ClienteRepository extends GenericRepository<Cliente> {
    public boolean clientePossuiAgendamentos(Long clienteId) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT COUNT(a) FROM Agendamento a WHERE a.cliente.id = :clienteId";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("clienteId", clienteId)
                    .getSingleResult();
            return count > 0;
        }
    }
}

