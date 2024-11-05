module org.solotyan.simulationmodelqmo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    opens org.solotyan.simulationmodelqmo to javafx.fxml;
    exports org.solotyan.simulationmodelqmo;
}