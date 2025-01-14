package com.carvalhotechsolutions.mundoanimal.utils;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class PopupManager {

    public static void showLoginSuccessPopup(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(PopupManager.class.getResource("/fxml/modals/modalLoginSucesso.fxml"));
            AnchorPane popupContent = loader.load();

            // Criar um Stage para o pop-up
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initOwner(primaryStage);
            popupStage.setAlwaysOnTop(true);

            // Configurar cena
            Scene popupScene = new Scene(popupContent);
            popupStage.setScene(popupScene);

            // Posição no canto superior direito do primaryStage
            popupStage.setX(primaryStage.getX() + primaryStage.getWidth() - popupContent.getPrefWidth() - 20);
            popupStage.setY(primaryStage.getY() + 50);

            // Exibir com animação
            new FadeIn(popupContent).play();
            popupStage.show();

            // Configurar para fechar automaticamente após 3 segundos
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                // Animação de fade-out suave
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), popupContent);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setCycleCount(1);
                fadeOut.setInterpolator(Interpolator.EASE_OUT); // Usando uma interpolação suave
                fadeOut.play();

                fadeOut.setOnFinished(fadeEvent -> {
                    // Remover o popup após o fade-out
                    popupStage.close();
                });
            });
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




