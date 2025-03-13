package com.canhub.canhub;

import java.util.Date;

public class Escuela {
    private String nombrecentro;
    private String descripcion_centro;
    private String img_centro;
    private Date fecha;


    public Escuela(String nombrecentro, String descripcion_centro, String img_centro, Date fecha) {
        this.nombrecentro = nombrecentro;
        this.descripcion_centro = descripcion_centro;
        this.img_centro = img_centro;
        this.fecha=fecha;
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

    public Date getFecha() {
        return fecha;
    }

}
