package com.carvalhotechsolutions.mundoanimal.utils;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.AnimalController;
import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.ClienteController;
import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.MenuController;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ScreenManager {
    private final Stage stage;
    private final BorderPane masterLayout;
    private final Map<ScreenEnum, Node> screens;
    private Map<ScreenEnum, Object> controllers = new HashMap<>();
    private ScreenEnum currentScreen;
    private BorderPane menuTemplate;
    private StackPane contentArea;

    public ScreenManager(Stage stage) {
        this.stage = stage;
        this.masterLayout = new BorderPane();
        this.screens = new EnumMap<>(ScreenEnum.class);

        // Configuração inicial do stage
        this.stage.setTitle("Sistema PetShop Mundo Animal");
        this.stage.setMinWidth(1400);
        this.stage.setMinHeight(820);
        this.stage.setWidth(1400);
        this.stage.setHeight(820);

        // Aplicar o CSS global ao masterLayout
        masterLayout.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // Criar a cena principal com o layout mestre
        Scene masterScene = new Scene(masterLayout);
        this.stage.setScene(masterScene);

        // Carregar todas as telas
        loadScreens();
    }

    private void loadScreens() {
        for (ScreenEnum screen : ScreenEnum.values()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
                Node loadedNode = loader.load();

                if (screen.getType() == ScreenEnum.ScreenType.TEMPLATE) {
                    // Se for o template do menu, guarda referência especial
                    if (loadedNode instanceof BorderPane) {
                        menuTemplate = (BorderPane) loadedNode;
                        // Guarda referência da área de conteúdo
                        contentArea = (StackPane) menuTemplate.getCenter();
                    }
                }

                // Configurar visibilidade inicial
                loadedNode.setVisible(false);
                loadedNode.setManaged(false);

                // Adicionar aos mapas de telas e controladores
                screens.put(screen, loadedNode);
                controllers.put(screen, loader.getController());

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erro ao carregar a tela: " + screen.getFxmlPath());
            }
        }
    }

    public void switchTo(ScreenEnum screen) {
        switch (screen.getType()) {
            case FULL:
                switchToFullScreen(screen);
                break;
            case TEMPLATE:
                switchToTemplate(screen);
                break;
            case CONTENT:
                switchToContent(screen);
                break;
        }
        currentScreen = screen;
    }

    private void switchToFullScreen(ScreenEnum screen) {
        Node newScreen = screens.get(screen);
        if (newScreen != null) {
            contentArea.getChildren().clear();
            masterLayout.getChildren().clear();
            masterLayout.setCenter(newScreen);
            newScreen.setVisible(true);
            newScreen.setManaged(true);
        }
    }

    private void switchToTemplate(ScreenEnum screen) {
        Node template = screens.get(screen);
        if (template != null) {
            masterLayout.getChildren().clear();
            masterLayout.setCenter(template);
            template.setVisible(true);
            template.setManaged(true);
        }
    }

    private void switchToContent(ScreenEnum screen) {
        // Garante que o template do menu está visível
        if (!menuTemplate.isVisible()) {
            masterLayout.getChildren().clear();
            masterLayout.setCenter(menuTemplate);
            menuTemplate.setVisible(true);
            menuTemplate.setManaged(true);
        }

        // Carrega o novo conteúdo na área de conteúdo
        Node content = screens.get(screen);
        if (content != null && contentArea != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            content.setVisible(true);
            content.setManaged(true);
        }
    }

    public Stage getStage() {
        return this.stage;
    }


    public Node getScreen(ScreenEnum screen) {
        return screens.get(screen);
    }

    public MenuController getMenuController() {
        return (MenuController) controllers.get(ScreenEnum.MENU);
    }

    public AnimalController getAnimalController() {
        return (AnimalController) controllers.get(ScreenEnum.PETS);
    }

    public ClienteController getClienteController() {
        return (ClienteController) controllers.get(ScreenEnum.CLIENTES);
    }
}