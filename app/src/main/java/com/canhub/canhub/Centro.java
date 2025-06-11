package com.canhub.canhub;

import com.google.gson.annotations.SerializedName;

public class Centro {
    @SerializedName("nombrecentro")
    public String nombre;

    @SerializedName("descripcion_centro")
    public String descripcion;

    @SerializedName("fecha")
    public String fecha;

    @SerializedName("img_centro")
    public String img_centro; // <- este campo es el importante
}

