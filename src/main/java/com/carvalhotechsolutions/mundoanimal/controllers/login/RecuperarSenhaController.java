package com.carvalhotechsolutions.mundoanimal.controllers.login;

import com.carvalhotechsolutions.mundoanimal.JPAutil;
import com.carvalhotechsolutions.mundoanimal.model.Administrador;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import jakarta.persistence.EntityManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RecuperarSenhaController {

    @FXML
    private Button recovery_btn;

    @FXML
    private TextField recovery_cpf_field;

    @FXML
    private TextField recovery_username_field;

    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/autenticacao/login.fxml", "Login");
    }

    @FXML
    private void handleResetBtn(ActionEvent event) throws IOException {

        String cpf = recovery_cpf_field.getText();
        String username = recovery_username_field.getText();

        if (cpf.isEmpty() || username.isEmpty()) {
            showAlert("Erro.", "Por favor preencha todos os campos.");
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
                    return;
                }

                // Valida o CPF do secretário com o CPF do administrador padrão
                if (!cpf.equals(adminPadrao.getCpf())) {
                    showAlert("Erro", "CPF inválido para recuperação de senha.");
                    return;
                }
                usuario = secretario;
            }

            // Se passou todas as validações
            SessionManager.setCurrentUser(usuario);
            NavigationManager.switchScene(event, "/fxml/autenticacao/redefinir-senha.fxml", "Redefinir senha");

        } catch (Exception e) {
            showAlert("Erro", "Erro ao buscar usuário: " + e.getMessage());
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
