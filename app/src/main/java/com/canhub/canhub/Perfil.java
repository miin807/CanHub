package com.canhub.canhub;

import static com.canhub.canhub.R.string.inicia_sesion_primero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Perfil extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String datoscentro = "https://pzlqlnjkzkxaitkphclx.supabase.co/rest/v1/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";
    private boolean inicioSesion;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);



        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion2);
        bottomNavigationView.setSelectedItemId(R.id.menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.inicio) {
                startActivity(new Intent(this, Inicio.class));
            } else if (item.getItemId() == R.id.biblioteca) {
                startActivity(new Intent(this, Busqueda.class));
            } else if (item.getItemId() == R.id.anadir) {
                inicioSesion = Login.getinicioSesion();

                if(inicioSesion){
                    Intent int3 = new Intent(Perfil.this, Formulariopt1.class);
                    int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int3);
                }
                else {
                    Toast.makeText(Perfil.this,  inicia_sesion_primero, Toast.LENGTH_SHORT).show();
                    Intent int4 = new Intent(Perfil.this, SignUp.class);
                    int4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int4);
                }

            }else if (item.getItemId()==R.id.menu){
            Bottomsheet bottomSheet = new Bottomsheet();
            bottomSheet.show(getSupportFragmentManager(), "Opciones");
            }

            return false;
        });

        ImageButton btnAtras = findViewById(R.id.atras);
        btnAtras.setOnClickListener(view -> goInicio());


        obtenerDatosCentro();


    }


    private void obtenerDatosCentro() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(datoscentro)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseAPI api = retrofit.create(SupabaseAPI.class);
        retrofit2.Call<List<PerfilUsuario>> call = api.obtenerPerfilUsuario(Supabase.getSupabaseKey(), "Bearer " + Supabase.getSupabaseKey(), "nombre,img_perfil");

        call.enqueue(new retrofit2.Callback<List<PerfilUsuario>>() {
            @Override
            public void onResponse(retrofit2.Call<List<PerfilUsuario>> call, retrofit2.Response<List<PerfilUsuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PerfilUsuario usuario : response.body()) {

                        Log.d("Supabase", "Nombre: " + usuario.getNombre());
                        Log.d("Supabase", "Descripcion: " + usuario.getDescripcion());
                        Log.d("Supabase", "Fecha: " + usuario.getFecha());

                        setPerfil(usuario);
                    }
                } else {
                    Log.e("Supabase", "Error en la respuesta: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<PerfilUsuario>> call, Throwable t) {
                Log.e("Supabase", "Error al obtener datos", t);
            }
        });
    }

    private void goInicio(){
        Intent intent = new Intent(this, Inicio.class);
        startActivity(intent);
    }

    private void setPerfil(PerfilUsuario usuario){
        ImageView img = findViewById(R.id.logo);
        TextView nombre = findViewById(R.id.nombreSignUp);
        TextView descripcion = findViewById(R.id.descripcionEscuela);
        TextView fecha = findViewById(R.id.fechaEscuela);

        nombre.setText(usuario.getNombre());
        descripcion.setText(usuario.getDescripcion());
        fecha.setText(usuario.getFecha().toString());

        Glide.with(this)
                .load(R.drawable.perfil)
                .placeholder(R.drawable.correcto) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(img);
    }


}