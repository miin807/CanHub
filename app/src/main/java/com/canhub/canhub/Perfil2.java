package com.canhub.canhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Perfil2 extends AppCompatActivity {

    private static final int REQUEST_EDITAR_PERFIL = 1;

    private TextView nombreSignUp;
    private ImageView logo;
    private ImageButton botonEditar, btnAtras,btnCerrarSesion;
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";

    private final OkHttpClient client = new OkHttpClient();
    private boolean esUsuarioReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil2);

        nombreSignUp = findViewById(R.id.nombreSignUp);
        logo = findViewById(R.id.logo);
        botonEditar = findViewById(R.id.editar);
        btnAtras = findViewById(R.id.atras);
        btnCerrarSesion = findViewById(R.id.cerrar);
        esUsuarioReal = Login.esUsuarioAutenticado(this);
        cargarDatosPerfil();

        btnAtras.setOnClickListener(view -> goInicio());


        botonEditar.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil2.this, EditarPerfil.class);
            startActivityForResult(intent, REQUEST_EDITAR_PERFIL);
        });


        btnCerrarSesion.setOnClickListener(view -> cerrarSesionSupabase());
        if (!esUsuarioReal) {
            botonEditar.setEnabled(false);
            botonEditar.setAlpha(0.5f); // Visualmente desactivado
        }
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
                runOnUiThread(() -> Toast.makeText(Perfil2.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear(); // Borra todo: sesión e invitado
                    editor.apply();

                    Toast.makeText(Perfil2.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    irALogin();
                });
            }
        });
    }
//    private void cargarDatosPerfil() {
//        if (esUsuarioReal) {
//        SharedPreferences prefs = getSharedPreferences("perfil", MODE_PRIVATE);
//        String nombre = prefs.getString("nombre", "IES Juan de la Cierva");
//        nombreSignUp.setText(nombre);
//    } else {
//        nombreSignUp.setText("Invitado");
//        logo.setImageResource(R.drawable.perfil_defecto);
//        }
//    }
private void cargarDatosPerfil() {
    SharedPreferences prefsSesion = getSharedPreferences("Sesion", MODE_PRIVATE);
    String userId = prefsSesion.getString("userId", "");
    String accessToken = prefsSesion.getString("accessToken", "");

    if (!esUsuarioReal || userId.isEmpty()) {
        nombreSignUp.setText("Invitado");
        logo.setImageResource(R.drawable.perfil_defecto);
        return;
    }

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
            .url(SUPABASE_URL + "/rest/v1/perfiles?user_id=eq." + userId + "&select=nombre,img_user")
            .addHeader("apikey", API_KEY)
            .addHeader("Authorization", "Bearer " + accessToken)
            .build();

    client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> Toast.makeText(Perfil2.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String json = response.body().string();
                try {
                    // Respuesta es un array con un solo objeto
                    org.json.JSONArray jsonArray = new org.json.JSONArray(json);
                    if (jsonArray.length() > 0) {
                        org.json.JSONObject usuario = jsonArray.getJSONObject(0);
                        final String nombre = usuario.optString("nombre", "Usuario");
                        final String fotoUrl = usuario.optString("img_user", "");

                        runOnUiThread(() -> {
                            nombreSignUp.setText(nombre);
                            if (!fotoUrl.isEmpty()) {
                                Glide.with(Perfil2.this)
                                        .load(fotoUrl)
                                        .placeholder(R.drawable.perfil_defecto)
                                        .into(logo);
                            } else {
                                logo.setImageResource(R.drawable.perfil_defecto);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(() -> Toast.makeText(Perfil2.this, "Error al obtener datos del perfil", Toast.LENGTH_SHORT).show());
            }
        }
    });
}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDITAR_PERFIL && resultCode == RESULT_OK) {
            cargarDatosPerfil(); // Recarga los datos al volver
    }
    }

    private void goInicio(){
        Intent intent = new Intent(this, Inicio.class);
        startActivity(intent);
    }
    private void irALogin() {
        Intent intent = new Intent(Perfil2.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

