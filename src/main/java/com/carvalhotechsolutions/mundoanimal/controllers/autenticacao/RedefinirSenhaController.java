package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import com.carvalhotechsolutions.mundoanimal.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.security.PasswordUtils;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;


public class RedefinirSenhaController {

    @FXML
    private PasswordField reset_confirm_password;

    @FXML
    private PasswordField reset_new_password;

    @FXML
    private void handleResetPassword() {
        String newPassword = reset_new_password.getText();
        String confirmPassword = reset_confirm_password.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erro", "Por favor, preencha todos os campos.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erro", "As senhas não coincidem. Tente novamente.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert("Erro", "A senha deve ter no mínimo 6 caracteres.");
            return;
        }

        try (EntityManager em = JPAutil.getEntityManager()) {
            em.getTransaction().begin();

            // Obtém o usuário da sessão
            Usuario usuario = SessionManager.getCurrentUser();

            if (usuario == null) {
                showAlert("Erro", "Nenhum usuário autenticado. Tente novamente.");
                ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);
                return;
            }

            // Atualiza a senha do usuário
            usuario.setSenha(PasswordUtils.hashPassword(newPassword)); // Hash da senha
            em.merge(usuario); // Atualiza no banco
            em.getTransaction().commit();

            showAlert("Sucesso", "Senha redefinida com sucesso!");
            SessionManager.clearSession();
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);

        } catch (Exception e) {
            showAlert("Erro", "Erro ao redefinir a senha: " + e.getMessage());
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
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.RECUPERAR_SENHA);
    }
}
