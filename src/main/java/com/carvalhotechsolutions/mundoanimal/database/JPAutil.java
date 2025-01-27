package com.carvalhotechsolutions.mundoanimal.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAutil {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("database");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
