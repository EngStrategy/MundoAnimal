package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.StatusAgendamento;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgendamentoRepository {
    public Agendamento save(Agendamento agendamento) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (agendamento.getId() == null) {
                em.persist(agendamento);
            } else {
                em.merge(agendamento);
            }
            em.getTransaction().commit();
            return agendamento;
        }
    }

    public Agendamento findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Agendamento> query = em.createQuery("SELECT a FROM Agendamento a WHERE a.id = :id", Agendamento.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Agendamento> findAll() {
        try(EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT a FROM Agendamento a " +
                    "ORDER BY a.dataAgendamento ASC, a.horarioAgendamento ASC";

            return em.createQuery(jpql, Agendamento.class)
                    .getResultList();
        }
    }

    public void deleteById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            Agendamento agendamento = em.find(Agendamento.class, id);
            if(agendamento != null) {
                em.getTransaction().begin();
                em.remove(agendamento);
                em.getTransaction().commit();
            }
            else {
                throw new IllegalArgumentException("Agendamento not found!");
            }
        }
    }

    public boolean verificarDisponibilidadeHorario(LocalDate data, LocalTime horario) {
        String jpql = "SELECT COUNT(a) FROM Agendamento a " +
                "WHERE a.dataAgendamento = :data " +
                "AND a.horarioAgendamento = :horario " +
                "AND a.status = :statusPendente";

        EntityManager em = JPAutil.getEntityManager();
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("data", data)
                .setParameter("horario", horario)
                .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                .getSingleResult();

        return count == 0;
    }

    public List<Agendamento> buscarAgendamentosPorData(LocalDate data) {
        String jpql = "SELECT a FROM Agendamento a WHERE a.dataAgendamento = :data AND a.status =:statusPendente";
        EntityManager em = JPAutil.getEntityManager();
        return em.createQuery(jpql, Agendamento.class)
                .setParameter("data", data)
                .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                .getResultList();
    }

    public List<Agendamento> findStatusFinalizado() {
        try(EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT a FROM Agendamento a " +
                    "WHERE a.status = :statusFinalizado " +
                    "ORDER BY a.dataAgendamento ASC, a.horarioAgendamento ASC";

            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .getResultList();
        }
    }

    public List<Agendamento> findStatusPendente() {
        try (EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT a FROM Agendamento a " +
                    "WHERE a.status = :statusPendente " +
                    "ORDER BY a.dataAgendamento ASC, a.horarioAgendamento ASC";

            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                    .getResultList();
        }
    }

    public List<Agendamento> findUltimosFinalizados(int quantidade) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT a FROM Agendamento a " +
                    "WHERE a.status = :statusFinalizado " +
                    "ORDER BY a.dataAgendamento DESC, a.horarioAgendamento DESC";

            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .setMaxResults(quantidade)  // Pegamos no máximo "quantidade" registros
                    .getResultList();
        }
    }

    public List<Agendamento> findFinalizadosUltimaSemana() {
        try (EntityManager em = JPAutil.getEntityManager()) {
            LocalDate hoje = LocalDate.now();
            LocalDate semanaPassada = hoje.minusDays(7);

            String jpql = "SELECT a FROM Agendamento a " +
                    "WHERE a.status = :statusFinalizado " +
                    "AND a.dataAgendamento BETWEEN :inicio AND :fim";

            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .setParameter("inicio", semanaPassada)
                    .setParameter("fim", hoje)
                    .getResultList();
        }
    }

    public List<Agendamento> buscarAgendamentosPorIntervalo(LocalDate dataInicio, LocalDate dataFim) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            String jpql = "SELECT a FROM Agendamento a " +
                    "WHERE a.dataAgendamento BETWEEN :inicio AND :fim " +
                    "AND a.status = :statusFinalizado";

            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("inicio", dataInicio)
                    .setParameter("fim", dataFim)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .getResultList();
        }
    }

}
