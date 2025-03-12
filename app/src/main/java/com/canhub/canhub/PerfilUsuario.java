package com.canhub.canhub;

import java.util.Date;

public class PerfilUsuario {
    private String nombrecentro;
    private String descripcion_centro;
    private Date fecha;

    public PerfilUsuario(String nombrecentro, String descripcion_centro, Date fecha) {
        this.nombrecentro = nombrecentro;
    }

    public String getNombre() {
        return nombrecentro;
    }

    public String getDescripcion() { return descripcion_centro;}

    public Date getFecha() { return fecha; }

}
