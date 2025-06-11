package com.canhub.canhub;


import com.google.gson.annotations.SerializedName;

public class Escuela {
    @SerializedName("nombrecentro")
    private String nombrecentro;

    @SerializedName("descripcion_centro")
    private String descripcion_centro;

    @SerializedName("img_cansat")
    private String img_cansat;

    @SerializedName("fecha")
    private String fecha;


    public Escuela(String nombrecentro, String descripcion_centro, String img_cansat, String fecha) {
        this.nombrecentro = nombrecentro;
        this.descripcion_centro = descripcion_centro;
        this.img_cansat = img_cansat;
        this.fecha = fecha;

    }

    public String getNombre() {
        return nombrecentro;
    }

    public String getDescripcion() {
        return descripcion_centro;
    }

    public String getImagen() { return img_cansat; }

    public String getFecha() {
        return fecha;
    }

}
