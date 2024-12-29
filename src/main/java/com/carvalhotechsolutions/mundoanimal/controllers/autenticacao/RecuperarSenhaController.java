package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RecuperarSenhaController {
    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);
//        NavigationManager.switchScene(event, "/fxml/autenticacao/login.fxml", "Login");
    }

    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void handleResetBtn(ActionEvent event) throws IOException {
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.REDEFINIR_SENHA);
//        NavigationManager.switchScene(event, "/fxml/autenticacao/redefinirSenha.fxml", "Redefinir senha");
    }
}
