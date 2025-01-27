package com.carvalhotechsolutions.mundoanimal;

import animatefx.animation.FadeIn;
import com.carvalhotechsolutions.mundoanimal.database.DatabaseChecker;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

        // Verifica conexão com o banco de dados
        DatabaseChecker.testConnectionAndInitializeAdmin();
        // Inicializa o SceneManager
        ScreenManager sceneManager = new ScreenManager(stage);
        // Inicializa o SceneManagerHolder
        ScreenManagerHolder.initialize(sceneManager);
        // Seleciona a primeira tela da aplicação
        sceneManager.switchTo(ScreenEnum.LOGIN);
        // Exibe a tela
        stage.show();
        // Aplicando FadeIn (AnimateFX)
        new FadeIn(sceneManager.getScreen(ScreenEnum.LOGIN)).play();
    }

    public static void main(String[] args) {
        launch();
    }
}