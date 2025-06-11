package com.canhub.canhub;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SupabaseAPI {
    @GET("/rest/v1/datoscentro?select=nombrecentro,descripcion_centro,img_cansat,id_usuario,fecha&order=fecha.desc")
    Call<List<Escuela>> obtenerEscuelas(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );


    @GET("/rest/v1/datoscentro?select=nombrecentro, descripcion_centro, fecha") // Asumiendo que tu tabla de perfiles se llama "perfiles"
    Call<List<PerfilUsuario>> obtenerPerfilUsuario(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Query("select") String select
    );

    @GET("/rest/v1/datoscentro")  // ← asegúrate de usar la ruta completa correcta
    Call<List<Centro>> obtenerCentroPorNombre(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Query("nombrecentro") String nombre,
            @Query("select") String select
    );

}