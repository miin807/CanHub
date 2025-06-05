package com.canhub.canhub;


import com.google.gson.annotations.SerializedName;

public class Escuela {
    @SerializedName("nombrecentro")
    private String nombrecentro;

    @SerializedName("descripcion_centro")
    private String descripcion_centro;

    @SerializedName("img_centro")
    private String img_centro;

    @SerializedName("fecha")
    private String fecha;


    public Escuela(String nombrecentro, String descripcion_centro, String img_centro, String fecha) {
        this.nombrecentro = nombrecentro;
        this.descripcion_centro = descripcion_centro;
        this.img_centro = img_centro;
        this.fecha = fecha;

    }

    public String getNombre() {
        return nombrecentro;
    }

    public String getDescripcion() {
        return descripcion_centro;
    }

    public String getImagen() { return img_centro; }

    public String getFecha() {
        return fecha;
    }

}
