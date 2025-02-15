package com.carvalhotechsolutions.mundoanimal.enums;

public enum ScreenEnum {
    LOGIN("/fxml/autenticacao/login.fxml", "Login", ScreenType.FULL),
    RECUPERAR_SENHA("/fxml/autenticacao/recuperarSenha.fxml", "Recuperar senha", ScreenType.FULL),
    REDEFINIR_SENHA("/fxml/autenticacao/redefinirSenha.fxml", "Redefinir Senha", ScreenType.FULL),
    MENU("/fxml/gerenciamento/menu.fxml", "Menu Principal", ScreenType.TEMPLATE),
    SECRETARIOS("/fxml/gerenciamento/secretarios.fxml", "Secretários", ScreenType.CONTENT),
    SERVICOS("/fxml/gerenciamento/servicos.fxml", "Serviços", ScreenType.CONTENT),
    CLIENTES("/fxml/gerenciamento/clientes.fxml", "Clientes", ScreenType.CONTENT),
    PETS("/fxml/gerenciamento/pets.fxml", "Pets", ScreenType.CONTENT),
    AGENDAMENTOS("/fxml/gerenciamento/agendamentos.fxml", "Agendamentos", ScreenType.CONTENT),
    HISTORICO("/fxml/gerenciamento/historico.fxml", "Historico", ScreenType.CONTENT),
    INICIO("/fxml/gerenciamento/inicio.fxml", "Inicio", ScreenType.CONTENT),
    RELATORIO("/fxml/gerenciamento/relatorio.fxml", "Relatorio", ScreenType.CONTENT);

    private final String fxmlPath;
    private final String title;
    private final ScreenType type;

    ScreenEnum(String fxmlPath, String title, ScreenType type) {
        this.fxmlPath = fxmlPath;
        this.title = title;
        this.type = type;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }

    public ScreenType getType() {
        return type;
    }

    // Enum para definir o tipo de tela
    public enum ScreenType {
        FULL,      // Tela completa (como login)
        TEMPLATE,  // Template com menu (como menu.fxml)
        CONTENT    // Conteúdo para ser carregado no contentArea
    }
}
