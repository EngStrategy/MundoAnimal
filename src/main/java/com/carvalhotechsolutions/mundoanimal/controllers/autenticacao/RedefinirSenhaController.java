package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.utils.PasswordManager;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RedefinirSenhaController {

    @FXML
    private PasswordField reset_confirm_password;

    @FXML
    private PasswordField reset_new_password;

    private static final Logger logger = LogManager.getLogger(RedefinirSenhaController.class);

    @FXML
    private void handleResetPassword() {
        String newPassword = reset_new_password.getText();
        String confirmPassword = reset_confirm_password.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erro", "Por favor, preencha todos os campos.");
            logger.warn("Campos de senha vazios ao tentar redefinir a senha.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erro", "As senhas não coincidem. Tente novamente.");
            logger.warn("As senhas não coincidem: nova senha '{}' e senha de confirmação '{}'.", newPassword, confirmPassword);
            return;
        }

        if (newPassword.length() < 6) {
            showAlert("Erro", "A senha deve ter no mínimo 6 caracteres.");
            logger.warn("Senha com menos de 6 caracteres. Senha fornecida: '{}'", newPassword);
            return;
        }

        try (EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();

            // Obtém o usuário da sessão
            Usuario usuario = SessionManager.getCurrentUser();

            if (usuario == null) {
                showAlert("Erro", "Nenhum usuário autenticado. Tente novamente.");
                logger.error("Nenhum usuário autenticado na sessão.");
                ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);
                return;
            }

            // Atualiza a senha do usuário
            usuario.setSenha(PasswordManager.hashPassword(newPassword)); // Hash da senha
            em.merge(usuario); // Atualiza no banco
            em.getTransaction().commit();

            showAlert("Sucesso", "Senha redefinida com sucesso!");
            logger.info("Senha redefinida com sucesso para o usuário: {}", usuario.getNomeUsuario());
            SessionManager.clearSession();
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);

        } catch (Exception e) {
            showAlert("Erro", "Erro ao redefinir a senha: " + e.getMessage());
            logger.error("Erro ao redefinir a senha: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void backToRecovery() {
        SessionManager.clearSession(); // Se ele desistir de redefinir a senha, deverá ser limpa a sessão
        logger.info("Redefinição de senha cancelada, voltando para a tela de recuperação.");
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.RECUPERAR_SENHA);
    }
}
