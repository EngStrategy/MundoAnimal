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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(MenuController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando MenuController.");
        // Set up button actions
        sair_btn.setOnAction(event -> logout());

        // Configure ações para cada botão
//        configureButton(inicio_btn, "inicio.fxml");
//        configureButton(historico_btn, "historico.fxml");
//        configureButton(relatorio_btn, "relatorio.fxml");
//        configureButton(agendamentos_btn, ScreenEnum.AGENDAMENTOS);
        configureButton(secretarios_btn, ScreenEnum.SECRETARIOS);
        configureButton(servicos_btn, ScreenEnum.SERVICOS);
        configureButton(clientes_btn, ScreenEnum.CLIENTES);
        logger.info("Menu Controller inicializado com sucesso.");
    }

    public void updateUserInterface(Usuario usuario) {
        Platform.runLater(() -> {
            logger.info("Atualizando a interface do usuário para: " + usuario.getNomeUsuario());
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
            logger.info("Tela " + screen + " carregada com sucesso.");
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
        logger.info("Iniciando o processo de logout.");
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
            logger.info("Usuário confirmou o logout.");
            if (activeButton != null) {
                activeButton.getStyleClass().remove("active");
            }
            ScreenManagerHolder.getInstance().switchTo(ScreenEnum.LOGIN);
            SessionManager.setCurrentUser(null); // Limpa o usuário logado
            Node loginScreen = ScreenManagerHolder.getInstance().getScreen(ScreenEnum.LOGIN);
            new FadeIn(loginScreen).play();
            logger.info("Usuário desconectado com sucesso e redirecionado para a tela de login.");
        } else {
            // Usuário cancelou a ação
            logger.info("Usuário cancelou o processo de logout.");
            alert.close();
        }
    }

}
