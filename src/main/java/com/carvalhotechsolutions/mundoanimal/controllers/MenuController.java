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

    private Button activeButton; // Botão atualmente ativo

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure ações para cada botão
        configureButton(inicio_btn, "inicio.fxml");
        configureButton(clientes_btn, "clientes.fxml");
        configureButton(agendamentos_btn, "agendamentos.fxml");
        configureButton(historico_btn, "historico.fxml");
        configureButton(relatorio_btn, "relatorio.fxml");
        configureButton(secretarios_btn, "secretarios.fxml");
        configureButton(servicos_btn, "servicos.fxml");
    }

    private void configureButton(Button button, String fxmlFile) {
        button.setOnAction(event -> {
            // Gerenciar o botão ativo
            setActiveButton(button);

            // Carregar a página no contentArea
            loadPage(fxmlFile);
        });
    }

    private void setActiveButton(Button button) {
        // Remover a classe 'active' do botão anteriormente ativo, se houver
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        // Adicionar a classe 'active' ao botão atual
        button.getStyleClass().add("active");

        // Atualizar o botão atualmente ativo
        activeButton = button;
    }

    private void loadPage(String fxmlFile) {
        try {
            // Carregar a nova página dentro do contentArea
            Node page = FXMLLoader.load(getClass().getResource("/fxml/gerenciamento/" + fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
