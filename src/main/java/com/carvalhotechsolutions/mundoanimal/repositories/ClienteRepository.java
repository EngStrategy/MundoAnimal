package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ClienteRepository {
    public Cliente save(Cliente cliente) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (cliente.getId() == null) {
                em.persist(cliente);
            } else {
                em.merge(cliente);
            }
            em.getTransaction().commit();
            return cliente;
        }
    }

    public Cliente findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c WHERE c.id = :id", Cliente.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Cliente> findAll() {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c ORDER BY c.nome", Cliente.class);
            return query.getResultList();
        }
    }

    public void deleteById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            Cliente cliente = em.find(Cliente.class, id);
            if(cliente != null) {
                em.getTransaction().begin();
                em.remove(cliente);
                em.getTransaction().commit();
            }
            else {
                throw new IllegalArgumentException("Cliente not found!");
            }
        }
    }

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
