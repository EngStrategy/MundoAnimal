package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Administrador;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RecuperarSenhaController {

    @FXML
    private TextField recovery_cpf_field;

    @FXML
    private TextField recovery_username_field;

    private static final Logger logger = LogManager.getLogger(RecuperarSenhaController.class);

    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToLogin() throws IOException {
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);
    }

    @FXML
    private void handleResetBtn() throws IOException {

        String cpf = recovery_cpf_field.getText();
        String username = recovery_username_field.getText();

        if (cpf.isEmpty() || username.isEmpty()) {
            showAlert("Erro.", "Por favor preencha todos os campos.");
            logger.warn("Campos vazios ao tentar recuperar senha: CPF ou nome de usuário.");
            return;
        }

        try (EntityManager em = JPAutil.getEntityManager()) {
            Usuario usuario = null;

            // Busca o CPF do administrador padrão
            Administrador adminPadrao = em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.nomeUsuario = 'admin'", Administrador.class)
                    .getSingleResult();

            if (adminPadrao == null) {
                showAlert("Erro", "Administrador padrão não encontrado no sistema.");
                logger.info("Administrador padrão não encontrado no sistema.");
                return;
            }

            // Busca o usuário pelo nome de usuário
            Administrador admin = em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.nomeUsuario = :username", Administrador.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (admin != null) {
                // Se for administrador, valida o CPF com o próprio admin padrão
                if (!cpf.equals(admin.getCpf())) {
                    showAlert("Erro", "CPF inválido para o administrador.");
                    logger.info("CPF inválido para o administrador {}. CPF informado: {}", username, cpf);
                    return;
                }
                usuario = admin;
            } else {
                // Se não for admin, busca em Secretário
                Secretario secretario = em.createQuery(
                                "SELECT s FROM Secretario s WHERE s.nomeUsuario = :username", Secretario.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (secretario == null) {
                    showAlert("Erro", "Usuário não encontrado.");
                    logger.info("Usuário não encontrado para o nome de usuário: {}", username);
                    return;
                }

                // Valida o CPF do secretário com o CPF do administrador padrão
                if (!cpf.equals(adminPadrao.getCpf())) {
                    showAlert("Erro", "CPF inválido para recuperação de senha.");
                    logger.info("CPF inválido para o secretário {}. CPF informado: {}", username, cpf);
                    return;
                }
                usuario = secretario;
            }

            // Se passou todas as validações
            SessionManager.setCurrentUser(usuario);
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.REDEFINIR_SENHA);

        } catch (Exception e) {
            showAlert("Erro", "Erro ao buscar usuário: " + e.getMessage());
            logger.error("Erro ao buscar usuário: {}", e.getMessage());
            e.printStackTrace();
        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
