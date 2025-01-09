package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SecretarioRepository {

    public Secretario save(Secretario secretario) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            if (secretario.getId() == null) {
                em.persist(secretario);
            } else {
                em.merge(secretario);
            }
            em.getTransaction().commit();
            return secretario;
        }
    }

    public Secretario findById(Long id) {
        try(EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<Secretario> query = em.createQuery("SELECT s FROM Secretario s WHERE s.id = :id", Secretario.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    public List<Secretario> findAll() {
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
            }
            else {
                throw new IllegalArgumentException("Secretario not found!");
            }
        }
    }

}
