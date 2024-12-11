package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.utils.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class LoginController {
    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {
        NavigationManager.switchScene(event, "/fxml/recuperar-senha.fxml", "Recuperar senha");
    }
}