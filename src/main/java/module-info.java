
module org.example.proyectocrud {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens org.example.proyectocrud to javafx.fxml;
    opens org.example.proyectocrud.controller to javafx.fxml;

    exports org.example.proyectocrud;
}