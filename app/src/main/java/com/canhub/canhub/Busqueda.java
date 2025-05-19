package com.canhub.canhub;

import static com.canhub.canhub.Inicio.abrirPerfil;
import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Busqueda extends AppCompatActivity {

    private EditText editText;
    private Button buttonBuscar;
    private LinearLayout layoutContenedor;

    private SupabaseAPI supabaseAPI;
    private final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        editText = findViewById(R.id.editTextAnio);
        buttonBuscar = findViewById(R.id.buttonBuscar);
        layoutContenedor = findViewById(R.id.layoutContenedor); // debe estar en tu XML

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pzlqlnjkzkxaitkphclx.supabase.co") // reemplaza con tu URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        supabaseAPI = retrofit.create(SupabaseAPI.class);

        buttonBuscar.setOnClickListener(v -> {
            String texto = editText.getText().toString().toLowerCase().trim();
            if (!texto.isEmpty()) {
                buscarEscuelas(texto);
            }
        });


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

    private void buscarEscuelas(String texto) {
        layoutContenedor.removeAllViews(); // limpiar resultados anteriores

        String filtro = "nombrecentro.ilike.*" + texto + ",*fecha.ilike.*" + texto + ",*descripcion_centro.ilike.*" + texto + "*";



        supabaseAPI.buscarPorTexto(apiKey, "Bearer " + apiKey, filtro).enqueue(new Callback<List<Escuela>>() {

            @Override
            public void onResponse(Call<List<Escuela>> call, Response<List<Escuela>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Escuela escuela : response.body()) {
                        Log.d("Busqueda", "Escuela: " + escuela.getNombre());
                        agregarEscuela(layoutContenedor, escuela);
                    }
                } else {
                    Toast.makeText(Busqueda.this, "Sin resultados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Escuela>> call, Throwable t) {
                Toast.makeText(Busqueda.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        Glide.with(this)
                .load(escuela.getImagen())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(image);

        contender.addView(cartaView);

        cartaView.setOnClickListener(v -> abrirPerfil(
                v,
                escuela.getNombre(),
                escuela.getImagen(),
                escuela.getDescripcion(),
                escuela.getFecha()));
    }
}

