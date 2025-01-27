package com.carvalhotechsolutions.mundoanimal.database;

import com.carvalhotechsolutions.mundoanimal.controllers.modals.ModalConfirmarRemocaoController;
import com.carvalhotechsolutions.mundoanimal.model.Administrador;
import com.carvalhotechsolutions.mundoanimal.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.utils.PasswordManager;
import jakarta.persistence.EntityManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseChecker {

    // Construtor privado para evitar instanciação
    private DatabaseChecker() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    private static final Logger logger = LogManager.getLogger(ModalConfirmarRemocaoController.class);

    public static void testConnectionAndInitializeAdmin() {
        try (EntityManager em = JPAutil.getEntityManager()) {

            em.getTransaction().begin();
            // Teste simples, pode ser alterado para algo mais específico
            em.createQuery("SELECT 1").getResultList();
            em.getTransaction().commit();
            logger.info("Conexão bem-sucedida com o banco de dados!");

            // Após verificar a conexão, inicializa o administrador padrão
            initializeAdmin(em);

        } catch (Exception e) {
            logger.error("Erro ao conectar ao banco de dados: {}", e.getMessage(), e);
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
                admin.setSenha(PasswordManager.hashPassword("admin"));
                admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);

                // Persiste o administrador no banco
                em.persist(admin);
                logger.info("Administrador padrão criado com sucesso!");
            } else {
                logger.info("Administrador já existe no banco.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao inicializar o administrador padrão: {}", e.getMessage(), e);
        }
    }

}
