package com.canhub.canhub;

public class Escuela {
    private String nombre;
    private String descripcion;
    private int imagen; // Si la imagen se almacena como URL en Supabase

    public Escuela(String nombre, String descripcion, int imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagen() {
        return imagen;
    }
}
