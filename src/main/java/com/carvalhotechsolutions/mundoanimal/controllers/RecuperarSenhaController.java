package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class RecuperarSenhaController {
    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/login.fxml", "Login");
    }
}
