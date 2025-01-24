package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import animatefx.animation.FadeIn;
import com.carvalhotechsolutions.mundoanimal.database.JPAutil;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.utils.PasswordManager;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import jakarta.persistence.EntityManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController {
    @FXML
    private TextField login_user_field;

    @FXML
    private PasswordField login_password_field;

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = login_user_field.getText();
        String password = login_password_field.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erro", "Por favor, preencha todos os campos.");
            logger.warn("Tentativa de login com campos vazios.");
            return;
        }

        try (EntityManager em = JPAutil.getEntityManager()) {

            // Verifica se o usuário é Administrador
            Usuario usuario = em.createQuery(
                            "SELECT u FROM Administrador u WHERE u.nomeUsuario = :username", Usuario.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            // Se não for Administrador, verifica se é Secretário
            if (usuario == null) {
                usuario = em.createQuery(
                                "SELECT u FROM Secretario u WHERE u.nomeUsuario = :username", Usuario.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
            }

            if (usuario == null) {
                showAlert("Erro", "Usuário não encontrado.");
                logger.info("Usuário não encontrado");
                return;
            }

            // Verifica a senha
            if (!PasswordManager.checkPassword(password, usuario.getSenha())) {
                showAlert("Erro", "Senha incorreta.");
                logger.info("Senha incorreta");
                return;
            }

            // Login bem-sucedido
            SessionManager.setCurrentUser(usuario);
            logger.info("Login bem-sucedido para o usuário: {}", username);

//            PopupManager.showLoginSuccessPopup(ScreenManagerHolder.getInstance().getStage());

            // Atualiza a interface do menu através do ScreenManagerHolder
            ScreenManagerHolder.getInstance().getMenuController().updateUserInterface(usuario);

            // Chamando tela de menu e aplicando FadeIn
            Node menuScreen = ScreenManagerHolder.getInstance().getScreen(ScreenEnum.MENU);
            new FadeIn(menuScreen).play();
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.MENU);
            logger.info("Tela de menu carregada com sucesso.");

        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao verificar as credenciais: " + e.getMessage());
            logger.error("Erro ao verificar as credenciais: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.RECUPERAR_SENHA);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}