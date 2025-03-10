package com.canhub.canhub;

public class PerfilUsuario {
    private String nombrecentro;

    public PerfilUsuario(String nombrecentro, String img_perfil) {
        this.nombrecentro = nombrecentro;
    }

    public String getNombre() {
        return nombrecentro;
    }

}
