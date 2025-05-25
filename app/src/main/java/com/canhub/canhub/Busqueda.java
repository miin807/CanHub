package com.canhub.canhub;

import static com.canhub.canhub.Inicio.abrirPerfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;

public class Busqueda extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private LinearLayout layoutContenedor;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        searchView = findViewById(R.id.busqueda);
        layoutContenedor = findViewById(R.id.contenedorCartas);
        client = new OkHttpClient();

        searchView.setOnQueryTextListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion);
        bottomNavigationView.setSelectedItemId(R.id.biblioteca);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.inicio) {
                startActivity(new Intent(this, Inicio.class));
            } else if (item.getItemId() == R.id.menu) {
                new Bottomsheet().show(getSupportFragmentManager(), "Opciones");
            } else if (item.getItemId() == R.id.anadir) {
                startActivity(new Intent(this, Formulariopt1.class));
            }
            return false;
        });
    }

    private void buscarEscuelasConOkHttp(String texto) {
        String baseUrl = Supabase.getSupabaseUrl() + "/rest/v1/datoscentro";

        // Codificar el texto de búsqueda individualmente
        String textoCodificado = Uri.encode(texto);
        String likePattern = Uri.encode("%" + texto + "%"); // Codificar el patrón completo

        // Construir la consulta sin codificar los operadores de Supabase
        String queryParams = "?select=nombrecentro,descripcion_centro,img_centro,fecha" +
                "&or=(nombrecentro.ilike." + likePattern +
                ",descripcion_centro.ilike." + likePattern +
                ",fecha.ilike." + likePattern + ")";

        String fullUrl = baseUrl + queryParams;

        Log.d("Busqueda", "URL completa: " + fullUrl); // Para depuración

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Authorization", "Bearer " + Supabase.getSupabaseKey())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation") // Header importante para Supabase
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(Busqueda.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Busqueda", "Error en la solicitud", e);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body() != null ? response.body().string() : "{}";
                Log.d("Busqueda", "Código de respuesta: " + response.code());
                Log.d("Busqueda", "Respuesta JSON: " + json);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            Escuela[] lista = new Gson().fromJson(json, Escuela[].class);
                            if (lista != null && lista.length > 0) {
                                agregarEscuela(Arrays.asList(lista));
                            } else {
                                Toast.makeText(Busqueda.this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Busqueda.this, "Error al procesar resultados", Toast.LENGTH_SHORT).show();
                            Log.e("Busqueda", "Error parsing JSON", e);
                        }
                    } else {
                        String errorMsg = "Error del servidor: " + response.code();
                        if (response.code() == 500) {
                            errorMsg += " - Revise la sintaxis de la consulta";
                        }
                        Toast.makeText(Busqueda.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("Busqueda", "Error response: " + response.code() + " - " + json);
                    }
                });
            }
        });
    }

    private void agregarEscuela(List<Escuela> escuelas) {
        for (Escuela escuela : escuelas) {
            View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, layoutContenedor, false);

            TextView title = cartaView.findViewById(R.id.nombreEscuela);
            TextView description = cartaView.findViewById(R.id.descripcionEscuela);
            ImageView image = cartaView.findViewById(R.id.imagenEscuela);

            title.setText(escuela.getNombre());
            description.setText(escuela.getDescripcion());

            Glide.with(this)
                    .load(escuela.getImagen())
                    .placeholder(R.drawable.correcto)
                    .error(R.drawable.error)
                    .into(image);

            cartaView.setOnClickListener(v -> abrirPerfil(
                    v,
                    escuela.getNombre(),
                    escuela.getImagen(),
                    escuela.getDescripcion(),
                    escuela.getFecha()
            ));

            layoutContenedor.addView(cartaView);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null && !query.isEmpty()) {
            buscarEscuelasConOkHttp(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }
}
