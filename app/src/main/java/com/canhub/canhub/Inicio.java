package com.canhub.canhub;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co/rest/v1/";

    private LinearLayout contentedCarts;
    private boolean inicioSesion;
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
                Intent int1 = new Intent(this, Busqueda.class);
                int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int1);
            } else if (item.getItemId() == R.id.inicio) {
                Intent int2 = new Intent(this, Inicio.class);
                int2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int2);
            } else if (item.getItemId() == R.id.anadir) {
                // Usamos el método para verificar si es un usuario autenticado (no invitado)
                if (Login.esUsuarioAutenticado(Inicio.this)) {
                    Intent int3 = new Intent(Inicio.this, Formulariopt1.class);
                    int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int3);
                } else {
                    // El usuario es invitado, mostramos un AlertDialog
                    new AlertDialog.Builder(Inicio.this)
                            .setTitle("Acceso restringido")
                            .setMessage("Para acceder a esta función debes iniciar sesión. ¿Deseas continuar hacia el Login?")
                            .setPositiveButton("Continuar", (dialog, which) -> {
                                Intent intent = new Intent(Inicio.this, Login.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            } else if (item.getItemId() == R.id.menu) {
                Bottomsheet bottomSheet = new Bottomsheet();
                bottomSheet.show(getSupportFragmentManager(), "Opciones");
            }
            return true;
        });
    }

        private void obtenerDatosEscuelas() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseAPI api = retrofit.create(SupabaseAPI.class);
        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String accessToken = preferences.getString("accessToken", "");

        Call<List<Escuela>> call = api.obtenerEscuelas(
                Supabase.getSupabaseKey(),
                "Bearer " + accessToken
        );


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

    private static final String SUPABASE_STORAGE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co/storage/v1/object/public/imagencansat/";

    private void agregarEscuela(LinearLayout contender, Escuela escuela) {
        View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, contender, false);

        TextView title = cartaView.findViewById(R.id.nombreEscuela);
        TextView description = cartaView.findViewById(R.id.descripcionEscuela);
        ImageView image = cartaView.findViewById(R.id.imagenEscuela);

        title.setText(escuela.getNombre());
        description.setText(escuela.getDescripcion());

        // Construir la URL correcta de la imagen
        String imagenUrl = construirUrlImagen(escuela.getImagen());

        // Cargar imagen con Glide desde URL
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.correcto)
                    .error(R.drawable.error)
                    .into(image);
        } else {
            image.setImageResource(R.drawable.error);
        }

        contender.addView(cartaView);
        cartaView.setOnClickListener(v -> abrirPerfil(v, escuela.getNombre(), escuela.getDescripcion(), escuela.getFecha()));
    }

    private String construirUrlImagen(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return null;
        }

        // Si ya es una URL completa, devolverla tal cual
        if (nombreArchivo.startsWith("http")) {
            return nombreArchivo;
        }

        // Construir la URL completa con la ruta base
        return SUPABASE_STORAGE_URL + nombreArchivo;
    }


    static void abrirPerfil(View view, String nombre, String descripcion, String fecha) {
        Intent intent = new Intent(view.getContext(), PlantillaPerfil.class);
        intent.putExtra("nombrecentro", nombre);
        intent.putExtra("descripcion_centro", descripcion);
        intent.putExtra("fecha", fecha);

        view.getContext().startActivity(intent);
    }
}
