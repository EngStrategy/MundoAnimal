package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ServicoRepository {

    private static final Logger logger = LogManager.getLogger(ServicoRepository.class);

    public Servico save(Servico servico) {
        logger.info("Iniciando salvamento de servico");
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (servico.getId() == null) {
                em.persist(servico);
                logger.info("Serviço salvo com sucesso");
            } else {
                em.merge(servico);
                logger.info("Serviço atualizado com sucesso");
            }
            em.getTransaction().commit();
            return servico;
        }
    }

    public Servico findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            logger.info("Buscando serviço por ID");
            TypedQuery<Servico> query = em.createQuery("SELECT s FROM Servico s WHERE s.id = :id", Servico.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Servico> findAll() {
        logger.info("Buscando todas os servicos");
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Servico> query = em.createQuery("SELECT s FROM Servico s", Servico.class);
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
                logger.info("Serviço Deletado com sucesso");
            }
            else {
                logger.info("Serviço não encontrado");
                throw new IllegalArgumentException("Service not found!");
            }
        }
    }
}
