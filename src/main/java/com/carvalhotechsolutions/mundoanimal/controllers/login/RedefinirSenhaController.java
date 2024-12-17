package com.carvalhotechsolutions.mundoanimal.controllers.login;

import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RedefinirSenhaController {
    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToRecovery(ActionEvent event) throws IOException {
        SessionManager.clearSession(); // Se ele desistir de redefinir a senha, deverá ser limpa a sessão
        NavigationManager.switchScene(event, "/fxml/autenticacao/recuperar-senha.fxml", "Recuperar senha");
    }
}
