package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SecretarioRepository {

    private static final Logger logger = LogManager.getLogger(SecretarioRepository.class);

    public Secretario save(Secretario secretario) {
        logger.info("Salvando secretario");
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (secretario.getId() == null) {
                em.persist(secretario);
                logger.info("Secretario salvo com sucesso");
            } else {
                em.merge(secretario);
                logger.info("Secretario editado com sucesso");
            }
            em.getTransaction().commit();
            return secretario;
        }
    }

    public Secretario findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            logger.info("Consultando secretario");
            TypedQuery<Secretario> query = em.createQuery("SELECT s FROM Secretario s WHERE s.id = :id", Secretario.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Secretario> findAll() {
        logger.info("Buscando todas os secretarios");
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Secretario> query = em.createQuery("SELECT s FROM Secretario s", Secretario.class);
            return query.getResultList();
        }
    }

    public void deleteById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            Secretario secretario = em.find(Secretario.class, id);
            if(secretario != null) {
                em.getTransaction().begin();
                em.remove(secretario);
                em.getTransaction().commit();
                logger.info("Secretario removido com sucesso");
            }
            else {
                logger.info("Secretario nao encontrado");
                throw new IllegalArgumentException("Secretario not found!");
            }
        }
    }

}
