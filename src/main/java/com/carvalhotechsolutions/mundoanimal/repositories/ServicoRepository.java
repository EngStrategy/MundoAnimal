package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ServicoRepository {

    public Servico save(Servico servico) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (servico.getId() == null) {
                em.persist(servico);
            } else {
                em.merge(servico);
            }
            em.getTransaction().commit();
            return servico;
        }
    }

    public Servico findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Servico> query = em.createQuery("SELECT s FROM Servico s WHERE s.id = :id", Servico.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Servico> findAll() {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Servico> query = em.createQuery("SELECT s FROM Servico s ORDER BY s.nomeServico", Servico.class);
            return query.getResultList();
        }
    }

    public void deleteById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            Servico servico = em.find(Servico.class, id);
            if(servico != null) {
                em.getTransaction().begin();
                em.remove(servico);
                em.getTransaction().commit();
            }
            else {
                throw new IllegalArgumentException("Service not found!");
            }
        }
    }

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
