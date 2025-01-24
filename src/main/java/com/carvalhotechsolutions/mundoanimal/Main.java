package com.carvalhotechsolutions.mundoanimal;

import animatefx.animation.FadeIn;
import com.carvalhotechsolutions.mundoanimal.database.DatabaseChecker;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage stage) {

        logger.info("Iniciando aplicação JavaFX...");

        // Verifica conexão com o banco de dados
        DatabaseChecker.testConnectionAndInitializeAdmin();
        logger.info("Banco de dados verificado.");

        // Inicializa o SceneManager
        ScreenManager sceneManager = new ScreenManager(stage);
        // Inicializa o SceneManagerHolder
        ScreenManagerHolder.initialize(sceneManager);
        // Seleciona a primeira tela da aplicação
        sceneManager.switchTo(ScreenEnum.LOGIN);
        logger.info("Tela de login carregada.");
        // Exibe a tela
        stage.show();
        logger.info("Stage exibido.");
        // Aplicando FadeIn (AnimateFX)
        new FadeIn(sceneManager.getScreen(ScreenEnum.LOGIN)).play();
    }

    public static void main(String[] args) {
        launch();
    }
}