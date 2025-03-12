package com.canhub.canhub;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SupabaseAPI {
    @GET("/rest/v1/datoscentro?select=nombrecentro,descripcion_centro,img_centro,id_usuario") // Asegúrate de que "institutos" es el nombre correcto de la tabla en Supabase
    Call<List<Escuela>> obtenerEscuelas(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );

    @GET("/rest/v1/datoscentro?select=nombrecentro, descripcion_centro, fecha") // Asumiendo que tu tabla de perfiles se llama "perfiles"
    Call<List<PerfilUsuario>> obtenerPerfilUsuario(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Query("select") String select
    );
}