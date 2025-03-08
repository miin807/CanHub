package com.canhub.canhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Perfil extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ImageButton btnCerrarSesion = findViewById(R.id.cierre);
        btnCerrarSesion.setOnClickListener(view -> cerrarSesionSupabase());
    }

    private void cerrarSesionSupabase() {
        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String token = preferences.getString("sessionToken", "");

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/logout")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("", MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Perfil.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear(); // Borra todo: sesión e invitado
                    editor.apply();

                    Toast.makeText(Perfil.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    irALogin();
                });
            }
        });
    }


    private void irALogin() {
        Intent intent = new Intent(Perfil.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}