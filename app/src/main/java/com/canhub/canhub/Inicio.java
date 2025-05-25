package com.canhub.canhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co/rest/v1/";

    private LinearLayout contentedCarts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        contentedCarts = findViewById(R.id.contenedorCartas);
        obtenerDatosEscuelas();

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion3);
        bottomNavigationView.setSelectedItemId(R.id.inicio);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.biblioteca) {
                startActivity(new Intent(this, Busqueda.class));
            } else if (item.getItemId() == R.id.menu) {
                new Bottomsheet().show(getSupportFragmentManager(), "Opciones");
            } else if (item.getItemId() == R.id.anadir) {
                startActivity(new Intent(this, Formulariopt1.class));
            }
            return false;
        });
    }

    private void obtenerDatosEscuelas() {
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

                        agregarEscuela(contentedCarts, escuela);
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
    }

    private void agregarEscuela(LinearLayout contender, Escuela escuela) {
        View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, contender, false);

        TextView title = cartaView.findViewById(R.id.nombreEscuela);
        TextView description = cartaView.findViewById(R.id.descripcionEscuela);
        ImageView image = cartaView.findViewById(R.id.imagenEscuela);

        title.setText(escuela.getNombre());
        description.setText(escuela.getDescripcion());

        // Cargar imagen con Glide desde URL
        Glide.with(this)
                .load(escuela.getImagen())
                .placeholder(R.drawable.correcto) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(image);

        contender.addView(cartaView);

//        cartaView.setOnClickListener(v -> abrirPerfil(v, escuela.getNombre(), escuela.getImagen(), escuela.getDescripcion(), escuela.getFecha()));
    }


//    private static void abrirPerfil(View view, String nombre, String imagen, String descripcion, Date fecha) {
//        Intent intent = new Intent(view.getContext(), PlantillaPerfil.class);
//        intent.putExtra("nombrecentro", nombre);
//        intent.putExtra("img_centro", imagen);
//        intent.putExtra("descripcion_centro", descripcion);
//        intent.putExtra("fecha", fecha);
//
//        view.getContext().startActivity(intent);
//    }
}
