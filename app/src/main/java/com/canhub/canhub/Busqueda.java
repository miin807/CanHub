package com.canhub.canhub;

import static com.canhub.canhub.Inicio.abrirPerfil;
import static com.canhub.canhub.SupabaseService.retrofit;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Busqueda extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private LinearLayout layoutContenedor;

    private SupabaseAPI supabaseAPI;

    private OkHttpClient client;

    private List<Escuela> escuelas = new ArrayList<>();

    private static final String URL_ESCUELAS = Supabase.getSupabaseUrl() + "/rest/v1/datoscentro?select=*,nombrecentro,descripcion_centro,img_centro,fecha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        searchView = findViewById(R.id.busqueda);
        layoutContenedor = findViewById(R.id.contenedorCartas); // debe estar en tu XML



        setupSearchView();


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

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEscuelas(newText);
                return true;
            }
        });
    }

    private Request buildRequest(){
        Request.Builder builder = new Request.Builder()
                .url(URL_ESCUELAS)
                .addHeader("apikey", Supabase.getSupabaseKey());
                builder.addHeader("Cache-Control", "no-cache");


        return builder.build();
    }
    private void cargarEscuelas() {
        Request request = buildRequest();

        supabaseAPI = retrofit.create(SupabaseAPI.class);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Busqueda.this, "Error al cargar los proyectos ", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    buscarEscuelas(response);

                } else {
                    Toast.makeText(Busqueda.this, "Sin resultados", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    private void buscarEscuelas(Response response) {
        String json = response.body().toString();
        Gson gson = new Gson();
        Escuela[] listaescuelas = gson.fromJson(json, Escuela[].class);
        escuelas=new ArrayList<>(Arrays.asList(listaescuelas));

        filtrarEscuelas(json);
    }

    private void filtrarEscuelas(String texto) {
        List<Escuela> filtrados = new ArrayList<>();

        for(Escuela escuela : escuelas) {
            if(escuela.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                    escuela.getDescripcion().toLowerCase().contains(texto.toLowerCase()) || escuela.getFecha().contains(texto.toLowerCase())) {
                filtrados.add(escuela);
            }
        }

        agregarEscuela(filtrados);

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



    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        return false;
    }
}

