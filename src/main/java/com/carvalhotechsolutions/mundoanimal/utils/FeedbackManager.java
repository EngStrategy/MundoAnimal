package com.carvalhotechsolutions.mundoanimal.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;

public class FeedbackManager {
    private static final String POPUP_FXML = "/fxml/popup/feedback.fxml";

    public enum FeedbackType {
        SUCCESS("CHECK_CIRCLE", "#43b554"),
        ERROR("TIMES_CIRCLE", "#FF6F6F"),
        WARNING("EXCLAMATION_CIRCLE", "#FFA500"),
        INFO("INFO_CIRCLE", "#686AFF");

        private final String icon;
        private final String color;

        FeedbackType(String icon, String color) {
            this.icon = icon;
            this.color = color;
        }
    }

    public static void showFeedback(HBox container, String message, FeedbackType type) {
        try {
            FXMLLoader loader = new FXMLLoader(FeedbackManager.class.getResource(POPUP_FXML));
            HBox popupContent = loader.load();

            // Configurar o estilo do popup
            popupContent.getStyleClass().add("popup");

            // Encontrar e configurar os componentes
            FontAwesomeIcon icon = (FontAwesomeIcon) popupContent.lookup("#icon");
            Label messageLabel = (Label) popupContent.lookup("#message");

            // Configurar o conteúdo
            icon.setGlyphName(type.icon);
            icon.setFill(Color.web(type.color));
            messageLabel.setText(message);

            // Limpar popups anteriores
            container.getChildren().removeIf(node -> node.getStyleClass().contains("popup"));

            // Adicionar o novo popup
            container.getChildren().add(popupContent);

            // Animação de entrada
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), popupContent);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Timer para remoção
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), popupContent);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    container.getChildren().remove(popupContent);
                });
                fadeOut.play();
            });
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
