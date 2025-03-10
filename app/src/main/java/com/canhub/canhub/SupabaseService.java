package com.canhub.canhub;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseService {
    private static Retrofit retrofit;

    public static SupabaseAPI getAPI() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Supabase.getSupabaseUrl()) // Usa la URL de Supabase desde tu clase Supabase
                    .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Java
                    .build();
        }
        return retrofit.create(SupabaseAPI.class);
    }
}
