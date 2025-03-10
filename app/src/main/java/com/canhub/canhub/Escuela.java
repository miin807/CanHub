package com.canhub.canhub;

public class Escuela {
    private String nombrecentro;
    private String descripcion_centro;
    private String img_centro; // Si la imagen se almacena como URL en Supabase

    public Escuela(String nombrecentro, String descripcion_centro, String img_centro) {
        this.nombrecentro = nombrecentro;
        this.descripcion_centro = descripcion_centro;
        this.img_centro = img_centro;
    }

    public String getNombre() {
        return nombrecentro;
    }

    public String getDescripcion() {
        return descripcion_centro;
    }

    public String getImagen() {
        return img_centro;
    }
}
