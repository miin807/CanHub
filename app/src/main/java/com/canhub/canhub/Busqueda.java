package com.canhub.canhub;


import static com.canhub.canhub.Inicio.abrirPerfil;
import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;

public class Busqueda extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private boolean inicioSesion;

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
            if (item.getItemId()==R.id.biblioteca) {
                Intent int1 = new Intent(this, Busqueda.class);
                int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int1);
            }else if (item.getItemId()==R.id.inicio){
                Intent int2 = new Intent(this, Inicio.class);
                int2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int2);
            }else if(item.getItemId() == R.id.anadir){
                inicioSesion = Login.getinicioSesion();

                if(inicioSesion){
                    Intent int3 = new Intent(Busqueda.this, Formulariopt1.class);
                    int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int3);
                }
                else {
                    Toast.makeText(Busqueda.this,  inicia_sesion_primero, Toast.LENGTH_SHORT).show();
                    Intent int4 = new Intent(Busqueda.this, SignUp.class);
                    int4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int4);
                }

            }else if (item.getItemId()==R.id.menu){
                Bottomsheet bottomSheet = new Bottomsheet();
                bottomSheet.show(getSupportFragmentManager(), "Opciones");
            }

            return false;
        });
    }

    private void buscarEscuelasConOkHttp(String texto) {
        String baseUrl = Supabase.getSupabaseUrl() + "/rest/v1/datoscentro";

        try {
            String textoCodificado = URLEncoder.encode("%" + texto + "%", "UTF-8");

            String queryParams = String.format(
                    "?select=nombrecentro,descripcion_centro,img_centro,fecha" +
                            "&or=(nombrecentro.ilike.%s,descripcion_centro.ilike.%s,fecha.ilike.%s)" +
                            "&order=fecha.desc&limit=1000",
                    textoCodificado, textoCodificado, textoCodificado
            );

            String fullUrl = baseUrl + queryParams;
            Log.d("BusquedaDebug", "URL de consulta: " + fullUrl);

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .addHeader("apikey", Supabase.getSupabaseKey())
                    .addHeader("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .addHeader("Accept", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(Busqueda.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        Log.e("BusquedaDebug", "Error en la solicitud", e);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                Escuela[] escuelas = new Gson().fromJson(responseBody, Escuela[].class);
                                if (escuelas != null && escuelas.length > 0) {
                                    agregarEscuela(Arrays.asList(escuelas));
                                } else {
                                    mostrarMensaje("No se encontraron resultados");
                                }
                            } catch (Exception e) {
                                mostrarMensaje("Error procesando resultados");
                                Log.e("BusquedaDebug", "Error parsing JSON", e);
                            }
                        } else {
                            mostrarMensaje("Error del servidor: " + response.code());
                            Log.e("BusquedaDebug", "Error response: " + responseBody);
                        }
                    });
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e("BusquedaDebug", "Error codificando texto", e);
            mostrarMensaje("Error en la búsqueda");
        }
    }


    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        Log.d("BusquedaDebug", mensaje);
    }

    private void agregarEscuela(List<Escuela> escuelas) {
        runOnUiThread(() -> {
            Log.d("BusquedaDebug", "Número de escuelas recibidas: " + escuelas.size());

            for (Escuela escuela : escuelas) {
                Log.d("BusquedaDebug", "Escuela: " + escuela.getNombre() +
                        ", Imagen: " + escuela.getImagen() +
                        ", Desc: " + escuela.getDescripcion());
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
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null && !query.trim().isEmpty()) {
            // Limpiar resultados anteriores
            layoutContenedor.removeAllViews();
            // Mostrar indicador de carga
            // Ejecutar búsqueda
            buscarEscuelasConOkHttp(query.trim());
        } else {
            Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show();
        }
        return true; // Indicar que hemos manejado el evento
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Opcional: Búsqueda en tiempo real con debounce
        if (newText != null && newText.length() >= 3) { // Solo buscar después de 3 caracteres
            handler.removeCallbacks(searchRunnable); // Cancelar búsquedas previas pendientes
            handler.postDelayed(searchRunnable, 500); // Esperar 500ms sin cambios
        } else if (newText != null && newText.isEmpty()) {
            // Limpiar resultados si el campo queda vacío
            layoutContenedor.removeAllViews();
        }
        return true;
    }

    // Para implementar el debounce en onQueryTextChange
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                layoutContenedor.removeAllViews();
                buscarEscuelasConOkHttp(query.trim());
            }
        }
    };
}
