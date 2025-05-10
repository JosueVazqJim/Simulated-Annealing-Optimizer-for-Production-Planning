module org.vazquezj.proyecto.recocido_simulado {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
	requires exp4j;
	requires static lombok;

	opens org.vazquezj.proyecto.recocido_simulado to javafx.fxml;
    exports org.vazquezj.proyecto.recocido_simulado;
}