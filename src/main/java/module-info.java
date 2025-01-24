module com.carvalhotechsolutions.mundoanimal {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jbcrypt;
    requires animatefx;
    requires fontawesomefx;
    requires java.desktop;
    requires org.apache.logging.log4j;

    // Export and open the controllers package

    opens com.carvalhotechsolutions.mundoanimal to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal;

    opens com.carvalhotechsolutions.mundoanimal.model to
            org.hibernate.orm.core,
            jakarta.persistence;
    exports com.carvalhotechsolutions.mundoanimal.model;
    exports com.carvalhotechsolutions.mundoanimal.controllers.autenticacao;
    opens com.carvalhotechsolutions.mundoanimal.controllers.autenticacao to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal.controllers.modals;
    opens com.carvalhotechsolutions.mundoanimal.controllers.modals to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento;
    opens com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal.database;
    opens com.carvalhotechsolutions.mundoanimal.database to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal.utils;
    opens com.carvalhotechsolutions.mundoanimal.utils to javafx.fxml;
}