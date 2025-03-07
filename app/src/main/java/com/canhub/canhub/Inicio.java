package com.canhub.canhub;

import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canhub.canhub.databinding.ActivityInicioBinding;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Inicio extends AppCompatActivity {

    private ActivityInicioBinding binding;
    private boolean inicioSesion;

    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTczOTQ0MzY4NywiZXhwIjoyMDU1MDE5Njg3fQ.KeG_MWBttLE2fJIqLySPr0eUg_NSypLypwUkJ7_Sd0c";

    private LinearLayout contentedCarts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);


        // Verificar si el usuario está logueado o como invitado
        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        boolean isGuest = preferences.getBoolean("isGuest", false);

        inicioSesion = Login.getinicioSesion();
        if (!isLoggedIn && !isGuest) {
            Intent intent = new Intent(Inicio.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }


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


                if(inicioSesion){
                    startActivity(new Intent(this, Formulariopt1.class));
                }
                else {
                    Toast.makeText(Inicio.this, inicia_sesion_primero, Toast.LENGTH_SHORT).show();
                    Intent int4 = new Intent(Inicio.this, SignUp.class);
                    int4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int4);
                }
            }
            return false;
        });
    }

    private void obtenerDatosEscuelas() {
        SupabaseAPI api = SupabaseService.getAPI();
        Call<List<Escuela>> call = api.obtenerEscuelas(Supabase.getSupabaseKey(), "Bearer " + Supabase.getSupabaseKey());

        call.enqueue(new Callback<List<Escuela>>() {
            @Override
            public void onResponse(Call<List<Escuela>> call, Response<List<Escuela>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Escuela> listaEscuelas = response.body();
                    for (Escuela escuela : listaEscuelas) {
                        agregarEscuela(contentedCarts, escuela);
                    }
                } else {
                    Log.e("Supabase", "Error en la respuesta: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Escuela>> call, Throwable t) {
                Log.e("Supabase", "Error en la conexión: " + t.getMessage());
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
                .placeholder(R.drawable.placeholder) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(image);

        contender.addView(cartaView);
    }
}