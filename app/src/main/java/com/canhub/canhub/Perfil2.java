package com.canhub.canhub;

import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Perfil2 extends AppCompatActivity {

    private static final int REQUEST_EDITAR_PERFIL = 1;
    private TextView nombreSignUp,descripcionSignUp;
    private ImageView logo;
    private ImageButton botonEditar, btnAtras;
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";
    private final OkHttpClient client = new OkHttpClient();
    private boolean esUsuarioReal;
    private boolean inicioSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil2);

        nombreSignUp = findViewById(R.id.nombreSignUp);
        descripcionSignUp = findViewById(R.id.descripcion);
        logo = findViewById(R.id.logo);
        botonEditar = findViewById(R.id.editar);
        btnAtras = findViewById(R.id.atras);


        esUsuarioReal = Login.esUsuarioAutenticado(this);
        cargarDatosPerfil();

        btnAtras.setOnClickListener(view -> goInicio());
        botonEditar.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil2.this, EditarPerfil.class);
            startActivityForResult(intent, REQUEST_EDITAR_PERFIL);
        });

        if (!esUsuarioReal) {
            botonEditar.setEnabled(false);
            botonEditar.setAlpha(0.5f);


        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion2);
        bottomNavigationView.setSelectedItemId(R.id.menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.biblioteca) {
                Intent int1 = new Intent(this, Busqueda.class);
                int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int1);
            } else if (item.getItemId() == R.id.inicio) {
                Intent int2 = new Intent(this, Inicio.class);
                int2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int2);
            } else if (item.getItemId() == R.id.anadir) {
                inicioSesion = Login.getinicioSesion();

                if (inicioSesion) {
                    Intent int3 = new Intent(Perfil2.this, Formulariopt1.class);
                    int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int3);
                } else {
                    Toast.makeText(Perfil2.this, "Tienen que inicar sesion", Toast.LENGTH_SHORT).show();
                    Intent int4 = new Intent(Perfil2.this, SignUp.class);
                    int4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int4);
                }

            } else if (item.getItemId() == R.id.menu) {
                Bottomsheet bottomSheet = new Bottomsheet();
                bottomSheet.show(getSupportFragmentManager(), "Opciones");
            }
            return false;
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosPerfil();
    }



    private void cargarDatosPerfil() {
        SharedPreferences prefsSesion = getSharedPreferences("Sesion", MODE_PRIVATE);
        String userId = prefsSesion.getString("userId", "");
        String accessToken = prefsSesion.getString("accessToken", "");

        if (!esUsuarioReal || userId.isEmpty()) {
            nombreSignUp.setText("Invitado");
            descripcionSignUp.setText("descripción invitado");
            logo.setImageResource(R.drawable.perfil_defecto);
            return;
        }

        String requestUrl = SUPABASE_URL + "/rest/v1/perfiles?auth_id=eq." + userId + "&select=nombre,descripcion,img_user";
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Perfil2.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            JSONObject usuario = jsonArray.getJSONObject(0);
                            final String nombre = usuario.optString("nombre", "Usuario");
                            final String descripcion = usuario.optString("descripcion", "Descripcion");
                            final String fotoUrl = usuario.optString("img_user", "");
                            runOnUiThread(() -> {
                                // Se establece el valor reemplazando el anterior
                                nombreSignUp.setText(nombre);
                                descripcionSignUp.setText(descripcion);
                                if (!fotoUrl.isEmpty()) {
                                    String imageURLConRefresh = fotoUrl + "?t=" + System.currentTimeMillis();

                                    Glide.with(Perfil2.this)
                                            .load(imageURLConRefresh)
                                            .circleCrop()
                                            .signature(new com.bumptech.glide.signature.ObjectKey(System.currentTimeMillis()))
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                    runOnUiThread(() ->
                            Toast.makeText(Perfil2.this, "Error al obtener datos del perfil", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDITAR_PERFIL && resultCode == RESULT_OK) {
            // Si se recibe el extra "nombre_actualizado", se reemplaza el valor
            if (data != null && data.hasExtra("nombre_actualizado")) {
                String nuevoNombre = data.getStringExtra("nombre_actualizado");
                nombreSignUp.setText(nuevoNombre);
            }
            // Si se recibe el extra "descripcion_actualizada", se reemplaza el valor
            if (data != null && data.hasExtra("descripcion_actualizada")) {
                String nuevaDescripcion = data.getStringExtra("descripcion_actualizada");
                descripcionSignUp.setText(nuevaDescripcion);
            }

            cargarDatosPerfil();
        }
    }

    private void goInicio() {
        startActivity(new Intent(this, Inicio.class));
    }

}
