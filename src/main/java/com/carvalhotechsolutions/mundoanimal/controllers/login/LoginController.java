package com.carvalhotechsolutions.mundoanimal.controllers.login;

import com.carvalhotechsolutions.mundoanimal.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.security.PasswordUtils;
import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import jakarta.persistence.EntityManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

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
            if (!PasswordUtils.checkPassword(password, usuario.getSenha())) {
                showAlert("Erro", "Senha incorreta.");
                return;
            }

            // Login bem-sucedido - Direciona para o menu principal
            SessionManager.setCurrentUser(usuario);
            NavigationManager.switchScene(event, "/fxml/gerenciamento/menu.fxml", "Pet Shop Mundo Animal");

        } catch (Exception e) {
            showAlert("Erro", "Ocorreu um erro ao verificar as credenciais: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/autenticacao/recuperar-senha.fxml", "Recuperar senha");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}