package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RedefinirSenhaController {
    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToRecovery(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/autenticacao/recuperar-senha.fxml", "Recuperar senha");
    }
}
