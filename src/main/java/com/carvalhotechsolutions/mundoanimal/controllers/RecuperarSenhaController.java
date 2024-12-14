package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RecuperarSenhaController {
    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/autenticacao/login.fxml", "Login");
    }

    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void handleResetBtn(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/autenticacao/redefinir-senha.fxml", "Redefinir senha");
    }
}
