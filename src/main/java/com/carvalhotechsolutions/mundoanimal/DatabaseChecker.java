package com.carvalhotechsolutions.mundoanimal;

import jakarta.persistence.EntityManager;

public class DatabaseChecker {

    public static boolean testConnection() {
        EntityManager em = null;
        try {
            em = JPAutil.getEntityManager();
            em.getTransaction().begin();
            // Teste simples, pode ser alterado para algo mais específico
            em.createQuery("SELECT 1").getResultList();
            em.getTransaction().commit();
            System.out.println("Conexão bem-sucedida com o banco de dados!");
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
