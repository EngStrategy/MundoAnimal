package com.carvalhotechsolutions.mundoanimal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    private StackPane contentArea;

    @FXML
    private Button inicio_btn;

    @FXML
    private Button clientes_btn;

    @FXML
    private Button agendamentos_btn;

    @FXML
    private Button historico_btn;

    @FXML
    private Button relatorio_btn;

    @FXML
    private Button secretarios_btn;

    @FXML
    private Button servicos_btn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up button actions
        servicos_btn.setOnAction(event -> loadPage("servicos.fxml"));
    }

    private void loadPage(String fxmlFile) {
        try {
            // Load the new fragment into contentArea
            Node page = FXMLLoader.load(getClass().getResource("/fxml/gerenciamento/" + fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace(); // For debugging purposes
        }
    }
}
