package com.canhub.canhub;

import static com.canhub.canhub.R.string.agregar_imagen;
import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Lanzamiento extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";
    private boolean inicioSesion;
    private LinearLayout contenedorCartas;
    private final     OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lanzamiento);


        contenedorCartas = findViewById(R.id.contenedorCartas);
      //  obtenerDatosEscuelas();
        obtenerLanzamiento();

        // Configuración de la barra de navegación
        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion);
        bottomNavigationView.setSelectedItemId(R.id.menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.biblioteca) {
                    Intent int1 = new Intent(Lanzamiento.this, Busqueda.class);
                    int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int1);
                }else if (item.getItemId() == R.id.inicio) {
                    Intent int1 = new Intent(Lanzamiento.this, Inicio.class);
                    int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int1);
                } else if (item.getItemId() == R.id.anadir) {
                    // Usamos el método para verificar si es un usuario autenticado (no invitado)
                    if (Login.esUsuarioAutenticado(Lanzamiento.this)) {
                        Intent int3 = new Intent(Lanzamiento.this, Formulariopt1.class);
                        int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(int3);
                    } else {
                        // El usuario es invitado, mostramos un AlertDialog
                        new AlertDialog.Builder(Lanzamiento.this)
                                .setTitle("Acceso restringido")
                                .setMessage("Para acceder a esta función debes iniciar sesión. ¿Deseas continuar hacia el Login?")
                                .setPositiveButton("Continuar", (dialog, which) -> {
                                    Intent intent = new Intent(Lanzamiento.this, Login.class);
                                    startActivity(intent);
                                })
                                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                }
                return false;
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void obtenerLanzamiento() {
        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String accessToken = preferences.getString("accessToken", "");

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/datoscentro?id_perfil=eq." + userId)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Lanzamiento.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(Lanzamiento.this, "Error en la respuesta", Toast.LENGTH_SHORT).show());
                    Log.e("Supabase", "Código: " + response.code());
                    Log.e("Supabase", "Mensaje: " + response.message());
                    Log.e("Supabase", "Body: " + response.body().string());
                    Log.d("Supabase", "userId: " + userId);
                    Log.d("Supabase", "URL consulta: " + SUPABASE_URL + "/rest/v1/datoscentro?id_perfil=eq." + userId);
                    return;
                }

                String json = response.body().string();
                Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> lanzamientos = new Gson().fromJson(json, listType);

                // Invertimos la lista
                Collections.reverse(lanzamientos);

                runOnUiThread(() -> {
                    for (Map<String, Object> item : lanzamientos) {
                        String nombre = (String) item.get("nombrecentro");
                        String descripcion = (String) item.get("descripcion_centro");
                        String imagenUrl = (String) item.get("img_centro");
                        String fecha = (String) item.get("fecha");
                        Log.d("Supabase", "Item recibido: " + item.toString());

                        agregarEscuela(contenedorCartas, nombre, descripcion, imagenUrl, fecha);
                    }
                });
            }
        });
    }
  /*  public void obtenerDatosEscuelas(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseAPI api = retrofit.create(SupabaseAPI.class);
        Call<List<Escuela>> call = api.obtenerEscuelas(Supabase.getSupabaseKey(), "Bearer " + Supabase.getSupabaseKey());

        call.enqueue(new Callback<List<Escuela>>() {
            @Override
            public void onResponse(Call<List<Escuela>> call, Response<List<Escuela>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Escuela escuela : response.body()) {

                        Log.d("Supabase", "Nombre: " + escuela.getNombre());
                        Log.d("Supabase", "Descripcion: " + escuela.getDescripcion());
                        Log.d("Supabase", "Imagen: " + escuela.getImagen());

                        agregarEscuela(contenedorCartas, escuela);
                    }
                } else {
                    Log.e("Supabase", "Error en la respuesta: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Escuela>> call, Throwable t) {
                Log.e("Supabase", "Error al obtener datos", t);
            }
        });
    }*/
    private void agregarEscuela(LinearLayout contender, String nombre,String descripcion, String imagen,String fecha) {
        View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, contender, false);

        TextView title = cartaView.findViewById(R.id.nombreEscuela);
        TextView description = cartaView.findViewById(R.id.descripcionEscuela);
        ImageView image = cartaView.findViewById(R.id.imagenEscuela);

        title.setText(nombre);
        description.setText(descripcion);

        // Cargar imagen con Glide desde URL
        Glide.with(this)
                .load(imagen)
                .placeholder(R.drawable.canhub) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(image);

        contender.addView(cartaView);

        cartaView.setOnClickListener(v -> {
            abrirLanzamiento(nombre, imagen,fecha);
        });
    }

    private void abrirLanzamiento(String nombre, String imagen,String fecha) {
        Intent intent = new Intent(Lanzamiento.this, GraficaJson.class);
            intent.putExtra("nombrecentro", nombre);
            intent.putExtra("img_centro", imagen);
            intent.putExtra("fecha",fecha);

           startActivity(intent);
    }
}