module com.carvalhotechsolutions.mundoanimal {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jbcrypt;

    // Export and open the controllers package
    opens com.carvalhotechsolutions.mundoanimal.controllers to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal.controllers;

    opens com.carvalhotechsolutions.mundoanimal to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal;

    opens com.carvalhotechsolutions.mundoanimal.model to
            org.hibernate.orm.core,
            jakarta.persistence;
    exports com.carvalhotechsolutions.mundoanimal.model;
}