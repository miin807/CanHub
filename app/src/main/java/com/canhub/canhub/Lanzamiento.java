package com.canhub.canhub;

import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Lanzamiento extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co/rest/v1/";
    private boolean inicioSesion;
    private LinearLayout contenedorCartas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lanzamiento);


        contenedorCartas = findViewById(R.id.contenedorCartas);
        obtenerDatosEscuelas();


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

                }else if (item.getItemId()==R.id.menu){
                    Bottomsheet bottomSheet = new Bottomsheet();
                    bottomSheet.show(getSupportFragmentManager(), "Opciones");
                } else if (item.getItemId() == R.id.anadir) {

                    inicioSesion = Login.getinicioSesion();

                    if(inicioSesion){
                        Intent int3 = new Intent(Lanzamiento.this, Formulariopt1.class);
                        int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(int3);
                    }
                    else {
                        Toast.makeText(Lanzamiento.this,  inicia_sesion_primero, Toast.LENGTH_SHORT).show();
                        Intent int4 = new Intent(Lanzamiento.this, SignUp.class);
                        int4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(int4);
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

    public void obtenerDatosEscuelas(){
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
                .placeholder(R.drawable.canhub) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(image);

        contender.addView(cartaView);

        cartaView.setOnClickListener(v -> abrirLanzamiento( escuela.getNombre(), escuela.getImagen(), escuela.getDescripcion()));
    }

    private void abrirLanzamiento(String nombre, String imagen, String descripcion) {
        Intent intent = new Intent(Lanzamiento.this, GraficaJson.class);
            intent.putExtra("nombrecentro", nombre);
            intent.putExtra("img_centro", imagen);
            intent.putExtra("descripcion_centro", descripcion);
           startActivity(intent);
    }
}