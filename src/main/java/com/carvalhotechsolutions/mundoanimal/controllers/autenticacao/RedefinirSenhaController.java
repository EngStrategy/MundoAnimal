package com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;

import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RedefinirSenhaController {
    // Método temporário, apenas para testar a troca de telas, não há lógica alguma aplicada
    @FXML
    private void backToRecovery(ActionEvent event) throws IOException {
        ScreenManagerHolder.getInstance().switchTo(ScreenEnum.RECUPERAR_SENHA);
//        NavigationManager.switchScene(event, "/fxml/autenticacao/recuperarSenha.fxml", "Recuperar senha");
    }
}
