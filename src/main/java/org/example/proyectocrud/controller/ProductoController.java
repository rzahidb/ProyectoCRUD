package org.example.proyectocrud.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.proyectocrud.connection.DataBaseConnection;
import org.example.proyectocrud.model.Categoria;
import org.example.proyectocrud.model.Marca;
import org.example.proyectocrud.model.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    // Componentes del formulario de productos

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;

    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<Marca> cmbMarca;
    @FXML private DatePicker dpFecha;


    // Componentes de búsqueda y filtros

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    @FXML private TextField txtPrecioMinimo;
    @FXML private TextField txtPrecioMaximo;

    @FXML private CheckBox chkConExistencias;
    @FXML private ComboBox<String> cmbOrdenarPor;


    // Tabla de productos

    @FXML private TableView<Producto> tblProductos;

    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colMarca;

    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, LocalDate> colFechaCreacion;


    // Listas observables

    private final ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> listaCategorias =
            FXCollections.observableArrayList();

    private final ObservableList<Marca> listaMarcas =
            FXCollections.observableArrayList();


    // Inicializar los componentes y cargar los datos

    @FXML
    private void initialize() {

        configurarTableView();
        configurarSeleccionTableView();

        cargarCategorias();
        cargarMarcas();

        cargarOpcionesOrden();

        dpFecha.setValue(LocalDate.now());

        cargarProductos();
    }


    // Configurar las columnas del TableView

    private void configurarTableView() {

        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodigo()
                )
        );

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategoria()
                )
        );

        colMarca.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getMarca()
                )
        );

        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getPrecio()
                ).asObject()
        );

        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getStock()
                ).asObject()
        );

        colFechaCreacion.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getFechaCreacion()
                )
        );

        tblProductos.setItems(listaProductos);
    }


    // Detectar la selección de un producto en la tabla

    private void configurarSeleccionTableView() {

        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue != null) {
                        cargarProductoEnFormulario(newValue);
                    }

                });
    }


    // Mostrar los datos del producto seleccionado en el formulario

    private void cargarProductoEnFormulario(Producto p) {

        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());

        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));

        dpFecha.setValue(p.getFechaCreacion());

        cmbCategoria.setValue(null);

        for (Categoria categoria : listaCategorias) {

            if (categoria.getIdCategoria() == p.getIdCategoria()) {

                cmbCategoria.setValue(categoria);
                break;
            }
        }

        cmbMarca.setValue(null);

        for (Marca marca : listaMarcas) {

            if (marca.getIdMarca() == p.getIdMarca()) {

                cmbMarca.setValue(marca);
                break;
            }
        }
    }


    // Cargar las categorías activas desde PostgreSQL

    private void cargarCategorias() {

        listaCategorias.clear();

        String sql = """
                SELECT
                    id_categoria,
                    nombre
                FROM catalogos.categorias
                WHERE estado = 1
                ORDER BY nombre
                """;

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Categoria categoria = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")
                );

                listaCategorias.add(categoria);
            }

            cmbCategoria.setItems(
                    FXCollections.observableArrayList(listaCategorias)
            );

            cmbFiltroCategoria.setItems(
                    FXCollections.observableArrayList(listaCategorias)
            );

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las categorías.",
                    e
            );
        }
    }


    // Cargar las marcas activas desde PostgreSQL

    private void cargarMarcas() {

        listaMarcas.clear();

        String sql = """
                SELECT
                    id_marca,
                    nombre
                FROM catalogos.marcas
                WHERE estado = 1
                ORDER BY nombre
                """;

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Marca marca = new Marca(
                        rs.getInt("id_marca"),
                        rs.getString("nombre")
                );

                listaMarcas.add(marca);
            }

            cmbMarca.setItems(
                    FXCollections.observableArrayList(listaMarcas)
            );

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las marcas.",
                    e
            );
        }
    }


    // Cargar las opciones de ordenamiento

    private void cargarOpcionesOrden() {

        cmbOrdenarPor.setItems(
                FXCollections.observableArrayList(
                        "Nombre",
                        "Precio menor a mayor",
                        "Precio mayor a menor",
                        "Fecha más reciente",
                        "Fecha más antigua",
                        "Stock mayor a menor",
                        "Stock menor a mayor"
                )
        );

        cmbOrdenarPor.setValue("Nombre");
    }


    // Convertir los resultados SQL en objetos Producto

    private Producto crearProductoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        Date fecha = rs.getDate("fecha_creacion");

        return new Producto(

                rs.getInt("id_producto"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("stock"),

                fecha == null ? null : fecha.toLocalDate(),

                rs.getInt("id_categoria"),
                rs.getString("categoria"),

                rs.getInt("id_marca"),
                rs.getString("marca"),

                rs.getInt("estado")
        );
    }


    // Cargar todos los productos activos en el TableView

    private void cargarProductos() {

        String sql = """
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.descripcion,
                    p.precio,
                    p.stock,
                    p.fecha_creacion,
                    c.id_categoria,
                    c.nombre AS categoria,
                    m.id_marca,
                    m.nombre AS marca,
                    p.estado
                FROM catalogos.productos p
                INNER JOIN catalogos.categorias c
                    ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas m
                    ON p.id_marca = m.id_marca
                WHERE p.estado = 1
                ORDER BY p.nombre ASC
                """;

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            listaProductos.clear();

            while (rs.next()) {

                listaProductos.add(
                        crearProductoDesdeResultSet(rs)
                );
            }

            tblProductos.setItems(listaProductos);

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar los productos.",
                    e
            );
        }
    }


    // Verificar que un código no esté registrado en otro producto

    private boolean existeCodigo(
            String codigo,
            Integer idProductoExcluir
    ) throws SQLException {

        String sql = """
                SELECT 1
                FROM catalogos.productos
                WHERE codigo = ?
                """;

        if (idProductoExcluir != null) {
            sql += " AND id_producto <> ?";
        }

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, codigo);

            if (idProductoExcluir != null) {
                ps.setInt(2, idProductoExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    // Validar los datos ingresados antes de guardar o actualizar

    private boolean validarFormulario() {

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (codigo.isEmpty() || codigo.length() > 20) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código incorrecto",
                    "El código es obligatorio y debe tener máximo 20 caracteres."
            );

            txtCodigo.requestFocus();
            return false;
        }

        if (nombre.isEmpty() || nombre.length() > 120) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Nombre incorrecto",
                    "El nombre es obligatorio y debe tener máximo 120 caracteres."
            );

            txtNombre.requestFocus();
            return false;
        }

        if (descripcion.isEmpty() || descripcion.length() > 200) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Descripción incorrecta",
                    "La descripción es obligatoria y debe tener máximo 200 caracteres."
            );

            txtDescripcion.requestFocus();
            return false;
        }

        try {

            BigDecimal precio = new BigDecimal(
                    txtPrecio.getText().trim().replace(",", ".")
            );

            if (precio.compareTo(BigDecimal.ZERO) <= 0
                    || precio.compareTo(
                    new BigDecimal("99999999.99")
            ) > 0
                    || precio.stripTrailingZeros().scale() > 2) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio debe ser mayor que cero, no superar 99999999.99 y tener máximo 2 decimales."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio incorrecto",
                    "Ingrese un precio numérico válido."
            );

            txtPrecio.requestFocus();
            return false;
        }

        try {

            int stock = Integer.parseInt(
                    txtStock.getText().trim()
            );

            if (stock < 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock incorrecto",
                        "El stock no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Stock incorrecto",
                    "El stock debe ser un número entero."
            );

            txtStock.requestFocus();
            return false;
        }

        if (cmbCategoria.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Categoría requerida",
                    "Seleccione una categoría."
            );

            return false;
        }

        if (cmbMarca.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Marca requerida",
                    "Seleccione una marca."
            );

            return false;
        }

        if (dpFecha.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Fecha requerida",
                    "Seleccione una fecha válida."
            );

            return false;
        }

        return true;
    }


    // Registrar un nuevo producto en PostgreSQL

    @FXML
    private void guardarProducto() {

        if (!validarFormulario()) {
            return;
        }

        String codigo = txtCodigo.getText().trim();

        // Comprobar que el código sea único

        try {

            if (existeCodigo(codigo, null)) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código " + codigo +
                                " ya pertenece a otro producto."
                );

                txtCodigo.requestFocus();
                return;
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo verificar el código del producto.",
                    e
            );

            return;
        }

        String sql = """
                INSERT INTO catalogos.productos
                (
                    codigo,
                    nombre,
                    descripcion,
                    precio,
                    stock,
                    fecha_creacion,
                    id_categoria,
                    id_marca,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """;

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    codigo
            );

            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );

            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );

            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                                    .replace(",", ".")
                    )
            );

            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );

            ps.setDate(
                    6,
                    Date.valueOf(
                            dpFecha.getValue()
                    )
            );

            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );

            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro guardado",
                        "El producto fue registrado correctamente."
                );

                limpiarFormulario();
                cargarProductos();
            }

        } catch (SQLException e) {

            // Controlar códigos duplicados detectados por PostgreSQL

            if ("23505".equals(e.getSQLState())) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Registro duplicado",
                        "El código ingresado ya existe."
                );

            } else {

                mostrarErrorBaseDatos(
                        "No se pudo guardar el producto.",
                        e
                );
            }
        }
    }


    // Actualizar los datos del producto seleccionado

    @FXML
    private void actualizarProducto() {

        Producto productoSeleccionado =
                tblProductos.getSelectionModel()
                        .getSelectedItem();

        if (productoSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto del TableView antes de actualizar."
            );

            return;
        }

        if (!validarFormulario()) {
            return;
        }

        String codigo = txtCodigo.getText().trim();

        // Verificar que otro producto no utilice el mismo código

        try {

            if (existeCodigo(
                    codigo,
                    productoSeleccionado.getIdProducto()
            )) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado pertenece a otro producto."
                );

                txtCodigo.requestFocus();
                return;
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo verificar el código del producto.",
                    e
            );

            return;
        }

        String sql = """
                UPDATE catalogos.productos
                SET
                    codigo = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio = ?,
                    stock = ?,
                    fecha_creacion = ?,
                    id_categoria = ?,
                    id_marca = ?
                WHERE id_producto = ?
                  AND estado = 1
                """;

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    codigo
            );

            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );

            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );

            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                                    .replace(",", ".")
                    )
            );

            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );

            ps.setDate(
                    6,
                    Date.valueOf(
                            dpFecha.getValue()
                    )
            );

            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );

            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );

            ps.setInt(
                    9,
                    productoSeleccionado.getIdProducto()
            );

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "El producto fue actualizado correctamente."
                );

                limpiarFormulario();
                cargarProductos();

            } else {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Producto no disponible",
                        "No se encontró un producto activo para actualizar."
                );

                cargarProductos();
            }

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado ya está siendo utilizado."
                );

            } else {

                mostrarErrorBaseDatos(
                        "No se pudo actualizar el producto.",
                        e
                );
            }
        }
    }


    // Desactivar un producto mediante eliminación lógica

    @FXML
    private void eliminarProducto() {

        Producto productoSeleccionado =
                tblProductos.getSelectionModel()
                        .getSelectedItem();

        if (productoSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto antes de eliminarlo."
            );

            return;
        }

        // Solicitar confirmación antes de desactivar el producto

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle(
                "Confirmar eliminación"
        );

        confirmacion.setHeaderText(
                "Eliminar producto"
        );

        confirmacion.setContentText(
                "¿Desea desactivar el producto "
                        + productoSeleccionado.getNombre()
                        + "?"
        );

        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();

        if (respuesta.isEmpty()
                || respuesta.get() != ButtonType.OK) {

            return;
        }

        String sql = """
                UPDATE catalogos.productos
                SET estado = 0
                WHERE id_producto = ?
                  AND estado = 1
                """;

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    productoSeleccionado.getIdProducto()
            );

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue desactivado correctamente."
                );

                limpiarFormulario();
                cargarProductos();

            } else {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Producto no disponible",
                        "El producto ya no se encuentra activo."
                );

                cargarProductos();
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo eliminar el producto.",
                    e
            );
        }
    }


    // Buscar productos mediante filtros y parámetros SQL

    @FXML
    private void buscarProductos() {

        // Consulta principal de productos activos

        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.descripcion,
                    p.precio,
                    p.stock,
                    p.fecha_creacion,
                    c.id_categoria,
                    c.nombre AS categoria,
                    m.id_marca,
                    m.nombre AS marca,
                    p.estado
                FROM catalogos.productos p
                INNER JOIN catalogos.categorias c
                    ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas m
                    ON p.id_marca = m.id_marca
                WHERE p.estado = 1
                """);

        // Almacenar los parámetros de los filtros

        List<Object> parametros = new ArrayList<>();

        String texto = txtBuscar.getText().trim();

        // Buscar por código o nombre

        if (!texto.isBlank()) {

            sql.append("""
                    AND (
                        p.codigo ILIKE ?
                        OR p.nombre ILIKE ?
                    )
                    """);

            parametros.add("%" + texto + "%");
            parametros.add("%" + texto + "%");
        }

        // Filtrar por categoría

        Categoria categoria =
                cmbFiltroCategoria.getValue();

        if (categoria != null) {

            sql.append("""
                    AND p.id_categoria = ?
                    """);

            parametros.add(
                    categoria.getIdCategoria()
            );
        }

        // Filtrar por fecha inicial

        LocalDate fechaDesde =
                dpDesde.getValue();

        if (fechaDesde != null) {

            sql.append("""
                    AND p.fecha_creacion >= ?
                    """);

            parametros.add(fechaDesde);
        }

        // Filtrar por fecha final

        LocalDate fechaHasta =
                dpHasta.getValue();

        if (fechaHasta != null) {

            sql.append("""
                    AND p.fecha_creacion <= ?
                    """);

            parametros.add(fechaHasta);
        }

        // Validar el rango de fechas

        if (fechaDesde != null
                && fechaHasta != null
                && fechaDesde.isAfter(fechaHasta)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de fechas incorrecto",
                    "La fecha inicial no puede ser posterior a la fecha final."
            );

            return;
        }

        // Filtrar por precio mínimo

        BigDecimal precioMinimo = null;

        if (!txtPrecioMinimo.getText().isBlank()) {

            try {

                precioMinimo = new BigDecimal(
                        txtPrecioMinimo.getText()
                                .trim().replace(",", ".")
                );

                if (precioMinimo.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "El precio mínimo no puede ser negativo."
                    );

                    return;
                }

                sql.append("""
                        AND p.precio >= ?
                        """);

                parametros.add(precioMinimo);

            } catch (NumberFormatException e) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio mínimo debe ser numérico."
                );

                txtPrecioMinimo.requestFocus();
                return;
            }
        }

        // Filtrar por precio máximo

        BigDecimal precioMaximo = null;

        if (!txtPrecioMaximo.getText().isBlank()) {

            try {

                precioMaximo = new BigDecimal(
                        txtPrecioMaximo.getText()
                                .trim().replace(",", ".")
                );

                if (precioMaximo.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "El precio máximo no puede ser negativo."
                    );

                    return;
                }

                sql.append("""
                        AND p.precio <= ?
                        """);

                parametros.add(precioMaximo);

            } catch (NumberFormatException e) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio máximo debe ser numérico."
                );

                txtPrecioMaximo.requestFocus();
                return;
            }
        }

        // Validar el rango de precios

        if (precioMinimo != null
                && precioMaximo != null
                && precioMinimo.compareTo(precioMaximo) > 0) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de precios incorrecto",
                    "El precio mínimo no puede ser mayor que el precio máximo."
            );

            return;
        }

        // Mostrar únicamente productos con stock disponible

        if (chkConExistencias.isSelected()) {

            sql.append("""
                    AND p.stock > 0
                    """);
        }

        // Aplicar el ordenamiento seleccionado

        String orden = cmbOrdenarPor.getValue();

        if (orden == null) {
            orden = "Nombre";
        }

        switch (orden) {

            case "Precio menor a mayor" ->
                    sql.append("""
                            ORDER BY p.precio ASC
                            """);

            case "Precio mayor a menor" ->
                    sql.append("""
                            ORDER BY p.precio DESC
                            """);

            case "Fecha más reciente" ->
                    sql.append("""
                            ORDER BY p.fecha_creacion DESC
                            """);

            case "Fecha más antigua" ->
                    sql.append("""
                            ORDER BY p.fecha_creacion ASC
                            """);

            case "Stock mayor a menor" ->
                    sql.append("""
                            ORDER BY p.stock DESC
                            """);

            case "Stock menor a mayor" ->
                    sql.append("""
                            ORDER BY p.stock ASC
                            """);

            default ->
                    sql.append("""
                            ORDER BY p.nombre ASC
                            """);
        }

        // Ejecutar la búsqueda con PreparedStatement

        try (
                Connection conexion =
                        DataBaseConnection.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql.toString()
                        )
        ) {

            // Asignar los parámetros a la consulta

            for (int i = 0; i < parametros.size(); i++) {

                Object valor = parametros.get(i);

                if (valor instanceof LocalDate fecha) {

                    ps.setDate(
                            i + 1,
                            Date.valueOf(fecha)
                    );

                } else if (valor instanceof BigDecimal numero) {

                    ps.setBigDecimal(
                            i + 1,
                            numero
                    );

                } else {

                    ps.setObject(
                            i + 1,
                            valor
                    );
                }
            }

            // Convertir los resultados en productos

            try (ResultSet rs = ps.executeQuery()) {

                listaProductos.clear();

                while (rs.next()) {

                    listaProductos.add(
                            crearProductoDesdeResultSet(rs)
                    );
                }
            }

            tblProductos.setItems(listaProductos);

            // Informar si no se encontraron coincidencias

            if (listaProductos.isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo realizar la búsqueda.",
                    e
            );
        }
    }


    // Limpiar los filtros y mostrar todos los productos activos

    @FXML
    private void limpiarFiltros() {

        txtBuscar.clear();

        cmbFiltroCategoria.setValue(null);

        dpDesde.setValue(null);
        dpHasta.setValue(null);

        txtPrecioMinimo.clear();
        txtPrecioMaximo.clear();

        chkConExistencias.setSelected(false);

        cmbOrdenarPor.setValue("Nombre");

        cargarProductos();
    }


    // Preparar el formulario para registrar un producto nuevo

    @FXML
    private void nuevoProducto() {

        limpiarFormulario();
        txtCodigo.requestFocus();
    }


    // Limpiar los campos del formulario

    private void limpiarFormulario() {

        tblProductos.getSelectionModel().clearSelection();

        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();

        cmbCategoria.setValue(null);
        cmbMarca.setValue(null);

        dpFecha.setValue(LocalDate.now());
    }


    // Mostrar mensajes de advertencia e información

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }


    // Mostrar errores relacionados con PostgreSQL

    private void mostrarErrorBaseDatos(
            String mensaje,
            SQLException e
    ) {

        e.printStackTrace();

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de base de datos",
                mensaje + "\n\nDetalle: " + e.getMessage()
        );
    }
}