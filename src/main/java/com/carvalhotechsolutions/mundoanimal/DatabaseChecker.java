package com.carvalhotechsolutions.mundoanimal;

import com.carvalhotechsolutions.mundoanimal.model.Administrador;
import com.carvalhotechsolutions.mundoanimal.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.security.PasswordUtils;
import jakarta.persistence.EntityManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class DatabaseChecker {

    // Construtor privado para evitar instanciação
    private DatabaseChecker() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    public static void testConnectionAndInitializeAdmin() {
        try (EntityManager em = JPAutil.getEntityManager()) {

            em.getTransaction().begin();
            // Teste simples, pode ser alterado para algo mais específico
            em.createQuery("SELECT 1").getResultList();
            em.getTransaction().commit();
            System.out.println("Conexão bem-sucedida com o banco de dados!");

            // Após verificar a conexão, inicializa o administrador padrão
            initializeAdmin(em);

        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Conexão");
            alert.setHeaderText("Não foi possível conectar ao banco de dados.");
            alert.setContentText("Verifique as configurações do banco e tente novamente.");
            alert.showAndWait();
            Platform.exit(); // Encerra a aplicação
        }
    }

    private static void initializeAdmin(EntityManager em) {
        try {

            em.getTransaction().begin();

            // Verifica se já existe um administrador no banco
            Long adminCount = em.createQuery("SELECT COUNT(a) FROM Administrador a", Long.class).getSingleResult();

            if (adminCount == 0) {
                // Cria um administrador padrão
                Administrador admin = new Administrador();
                admin.setCpf("123.456.789-10");
                admin.setNomeUsuario("admin");
                admin.setSenha(PasswordUtils.hashPassword("admin"));
                admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);

                // Persiste o administrador no banco
                em.persist(admin);
                System.out.println("Administrador padrão criado com sucesso!");
            } else {
                System.out.println("Administrador já existe no banco.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao inicializar o administrador padrão: " + e.getMessage());
        }
    }

}
