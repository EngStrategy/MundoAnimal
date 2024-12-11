module com.carvalhotechsolutions.mundoanimal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.carvalhotechsolutions.mundoanimal to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal;
    opens com.carvalhotechsolutions.mundoanimal.controllers to javafx.fxml;
    exports com.carvalhotechsolutions.mundoanimal.controllers;
}