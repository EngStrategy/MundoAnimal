package com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;

import animatefx.animation.FadeIn;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.model.Usuario;
import com.carvalhotechsolutions.mundoanimal.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
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
        // Set up button actions
        sair_btn.setOnAction(event -> logout());

        // Configure ações para cada botão
        configureButton(inicio_btn, ScreenEnum.INICIO);
        configureButton(historico_btn, ScreenEnum.HISTORICO);
        configureButton(relatorio_btn, ScreenEnum.RELATORIO);
        configureButton(agendamentos_btn, ScreenEnum.AGENDAMENTOS);
        configureButton(secretarios_btn, ScreenEnum.SECRETARIOS);
        configureButton(servicos_btn, ScreenEnum.SERVICOS);
        configureButton(clientes_btn, ScreenEnum.CLIENTES);
    }

    public void activateInitialScreen() {
        // Simula o clique no botão de início
        inicio_btn.fire();
    }

    public void updateUserInterface(Usuario usuario) {
        Platform.runLater(() -> {
            userNameLabel.setText(usuario.getNomeUsuario());
            userTypeLabel.setText(usuario.getTipoUsuario() == TipoUsuario.SECRETARIO ? "Secretário(a)" : "Administrador(a)");

            boolean isSecretario = usuario.getTipoUsuario() == TipoUsuario.SECRETARIO;
            servicos_btn.setVisible(!isSecretario);
            secretarios_btn.setVisible(!isSecretario);
        });
    }

    private void configureButton(Button button, ScreenEnum screen) {
        button.setOnAction(event -> {
            // Gerenciar o botão ativo
            setActiveButton(button);

            // Carregar a página no contentArea
            Node animatedScreen = ScreenManagerHolder.getInstance().getScreen(screen);
            new FadeIn(animatedScreen).play();
            ScreenManagerHolder.getInstance().switchTo(screen);
        });
    }

    public Button getActiveButton() {
        return activeButton;
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
            if (activeButton != null) {
                activeButton.getStyleClass().remove("active");
            }
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);
            SessionManager.setCurrentUser(null); // Limpa o usuário logado
            Node loginScreen = ScreenManagerHolder.getInstance().getScreen(ScreenEnum.LOGIN);
            new FadeIn(loginScreen).play();
        } else {
            // Usuário cancelou a ação
            alert.close();
        }
    }

}
