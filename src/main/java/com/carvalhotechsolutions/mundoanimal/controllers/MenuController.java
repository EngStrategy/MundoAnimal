package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.model.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

    @FXML
    private Button sair_btn;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userTypeLabel;

    private Button activeButton; // Botão atualmente ativo

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Usuario usuarioLogado = SessionManager.getCurrentUser();
        if (usuarioLogado.getTipoUsuario() == TipoUsuario.SECRETARIO) {
            servicos_btn.setVisible(false);
            secretarios_btn.setVisible(false);
            userTypeLabel.setText("Secretário");
        }
        userNameLabel.setText(usuarioLogado.getNomeUsuario());
        // Set up button actions
        servicos_btn.setOnAction(event -> loadPage("servicos.fxml"));
        secretarios_btn.setOnAction(event -> loadPage("secretarios.fxml"));
        sair_btn.setOnAction(event -> logout());
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

    private void logout() {
        // Cria um alerta de confirmação
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Você deseja realmente sair?");
        alert.setContentText("Ao sair, você será desconectado do sistema.");

        // Adiciona um ícone personalizado ao alerta
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/assets/images/sad-dog.png"))); // Adicione o caminho para o seu ícone
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);

        alert.setGraphic(imageView);

        // Captura a escolha do usuário
        Optional<ButtonType> result = alert.showAndWait();

        // Verifica a escolha
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage stage = (Stage) contentArea.getScene().getWindow(); // Obtém o estágio atual a partir do contentArea
                Parent loginPage = FXMLLoader.load(getClass().getResource("/fxml/autenticacao/login.fxml")); // Carrega a página de login
                Scene newScene = new Scene(loginPage); // Cria uma nova cena com a página de login carregada
                stage.setScene(newScene); // Define a nova cena no estágio
                stage.show(); // Exibe o estágio atualizado

                SessionManager.setCurrentUser(null); // Limpa o usuário logado
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Usuário cancelou a ação
            alert.close();
        }
    }

}
