package com.canhub.canhub;

public class Escuela {
    private String nombre;
    private String descripcion;
    private int imagenResId;

    public Escuela(String nombre, String descripcion, int imagenResId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenResId = imagenResId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}
