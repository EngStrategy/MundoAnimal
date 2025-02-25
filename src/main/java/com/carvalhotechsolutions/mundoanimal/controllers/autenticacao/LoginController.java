package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import animatefx.animation.FadeIn;
import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.MenuController;
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

public class LoginController {
    @FXML
    private TextField login_user_field;

    @FXML
    private PasswordField login_password_field;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = login_user_field.getText();
        String password = login_password_field.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erro", "Por favor, preencha todos os campos.");
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
                return;
            }

            // Verifica a senha
            if (!PasswordManager.checkPassword(password, usuario.getSenha())) {
                showAlert("Erro", "Senha incorreta.");
                return;
            }

            // Login bem-sucedido
            SessionManager.setCurrentUser(usuario);

            // Atualiza a interface do menu através do ScreenManagerHolder
            ScreenManagerHolder.getInstance().getMenuController().updateUserInterface(usuario);

            // Chamando tela de menu e aplicando FadeIn
            Node menuScreen = ScreenManagerHolder.getInstance().getScreen(ScreenEnum.MENU);
            new FadeIn(menuScreen).play();
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.MENU);

            // Obtém a referência do MenuController
            MenuController menuController = (MenuController) ScreenManagerHolder.getInstance()
                    .getMenuController();

            // Chama o método para ativar a tela inicial
            menuController.activateInitialScreen();

        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao verificar as credenciais: " + e.getMessage());
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