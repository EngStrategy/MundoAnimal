package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.StatusAgendamento;
import com.carvalhotechsolutions.mundoanimal.model.Agendamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgendamentoRepository extends GenericRepository<Agendamento> {

    public boolean verificarDisponibilidadeHorario(LocalDate data, LocalTime horario) {
        String jpql = "SELECT COUNT(a) FROM Agendamento a " +
                "WHERE a.dataAgendamento = :data " +
                "AND a.horarioAgendamento = :horario " +
                "AND a.status = :statusPendente";

        try (EntityManager em = JPAutil.getEntityManager()) {
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("data", data)
                    .setParameter("horario", horario)
                    .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                    .getSingleResult();

            return count == 0;
        }
    }

    public List<Agendamento> buscarAgendamentosPorData(LocalDate data) {
        String jpql = "SELECT a FROM Agendamento a WHERE a.dataAgendamento = :data AND a.status =:statusPendente";

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("data", data)
                    .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                    .getResultList();
        }
    }

    public List<Agendamento> findStatusFinalizado() {
        String jpql = "SELECT a FROM Agendamento a " +
                "WHERE a.status = :statusFinalizado " +
                "ORDER BY a.dataAgendamento ASC, a.horarioAgendamento ASC";

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .getResultList();
        }
    }

    public List<Agendamento> findStatusPendente() {
        String jpql = "SELECT a FROM Agendamento a " +
                "WHERE a.status = :statusPendente " +
                "ORDER BY a.dataAgendamento ASC, a.horarioAgendamento ASC";

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusPendente", StatusAgendamento.PENDENTE)
                    .getResultList();
        }
    }

    public List<Agendamento> findUltimosFinalizados(int quantidade) {
        String jpql = "SELECT a FROM Agendamento a " +
                "WHERE a.status = :statusFinalizado " +
                "ORDER BY a.dataAgendamento DESC, a.horarioAgendamento DESC";

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .setMaxResults(quantidade)
                    .getResultList();
        }
    }

    public List<Agendamento> findFinalizadosPorPeriodo(String periodo) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicial;

        switch (periodo) {
            case "Última semana":
                dataInicial = hoje.minusDays(7);
                break;
            case "Último mês":
                dataInicial = hoje.minusMonths(1);
                break;
            case "Últimos 6 meses":
                dataInicial = hoje.minusMonths(6);
                break;
            default:
                dataInicial = LocalDate.of(2000, 1, 1);
                break;
        }

        String jpql = "SELECT a FROM Agendamento a " +
                "WHERE a.status = :statusFinalizado " +
                "AND a.dataAgendamento BETWEEN :inicio AND :fim";

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .setParameter("inicio", dataInicial)
                    .setParameter("fim", hoje)
                    .getResultList();
        }
    }

    public Map<String, Long> getServicosMaisUtilizados(String periodo) {
        List<Agendamento> agendamentos = findFinalizadosPorPeriodo(periodo);

        return agendamentos.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getServico().getNomeServico(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public List<Agendamento> buscarAgendamentosPorIntervalo(LocalDate dataInicio, LocalDate dataFim) {
        String jpql = "SELECT a FROM Agendamento a " +
                "WHERE a.dataAgendamento BETWEEN :inicio AND :fim " +
                "AND a.status = :statusFinalizado";

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(jpql, Agendamento.class)
                    .setParameter("inicio", dataInicio)
                    .setParameter("fim", dataFim)
                    .setParameter("statusFinalizado", StatusAgendamento.FINALIZADO)
                    .getResultList();
        }
    }
}

