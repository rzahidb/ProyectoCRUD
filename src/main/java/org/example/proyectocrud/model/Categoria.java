
package org.example.proyectocrud.model;

public class Categoria {

    private int idCategoria;
    private String nombre;
    private String descripcion;
    private int estado;

    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = null;
        this.estado = 1;
    }

    public Categoria(
            int idCategoria,
            String nombre,
            String descripcion,
            int estado) {

        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }


    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
