
package org.example.proyectocrud.model;

public class Marca {

    private int idMarca;
    private String nombre;
    private String paisOrigen;
    private int estado;

    public Marca(int idMarca, String nombre) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.paisOrigen = null;
        this.estado = 1;
    }

    public Marca(
            int idMarca,
            String nombre,
            String paisOrigen,
            int estado) {

        this.idMarca = idMarca;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.estado = estado;
    }


    public int getIdMarca() {
        return idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public int getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
