package com.carvalhotechsolutions.mundoanimal.repositories;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class GenericRepository<T> implements BaseRepository<T> {
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public GenericRepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T save(T entity) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();
            T mergedEntity = em.merge(entity);
            em.getTransaction().commit();
            return mergedEntity;
        }
    }

    @Override
    public T findById(Long id) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = JPAutil.getEntityManager()) {
            TypedQuery<T> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = JPAutil.getEntityManager()) {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.getTransaction().begin();
                em.remove(entity);
                em.getTransaction().commit();
            } else {
                throw new IllegalArgumentException(entityClass.getSimpleName() + " not found!");
            }
        }
    }
}
