
package org.example.proyectocrud.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    // Datos de conexión a PostgreSQL
    private static final String URL =
            "jdbc:postgresql://localhost:5432/bdventas";

    private static final String USER = "postgres";

    private static final String PASSWORD = "XtasXlolomg1";

    // Constructor privado
    private DataBaseConnection() {}

    // Establecer conexión con la base de datos
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}