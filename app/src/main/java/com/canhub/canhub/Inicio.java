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

import com.canhub.canhub.databinding.ActivityInicioBinding;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Inicio extends AppCompatActivity {

    private ActivityInicioBinding binding;
    private boolean inicioSesion;

    private static final String SUPABASE_URL = "https://TU_PROYECTO.supabase.co/rest/v1/";
    private static final String SUPABASE_API_KEY = "TU_API_KEY";

    private LinearLayout contentedCarts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setContentView(R.layout.activity_inicio);

        contentedCarts = findViewById(R.id.contenedorCartas);

        List<Escuela> listaEscuelas = new ArrayList<>();
        listaEscuelas.add(new Escuela("IES Juan de la Cierva", "Lorem ipsum dolor sit amet.", R.drawable.logo1));
        listaEscuelas.add(new Escuela("Institut de Terrassa", "Lorem ipsum dolor sit amet.", R.drawable.logo2));
        listaEscuelas.add(new Escuela("IES Príncipe Felipe", "Descripción adicional", R.drawable.logo3));

        for (Escuela escuela : listaEscuelas) {
            agregarEscuela(contentedCarts, escuela);
        }

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

    private void agregarEscuela(LinearLayout contender, Escuela escuela) {
        View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, contender, false);

        TextView title = cartaView.findViewById(R.id.nombreEscuela);
        TextView description = cartaView.findViewById(R.id.descripcionEscuela);
        ImageView image = cartaView.findViewById(R.id.imagenEscuela);

        title.setText(escuela.getNombre());
        description.setText(escuela.getDescripcion());
        image.setImageResource(escuela.getImagenResId());

        contender.addView(cartaView);
    }
}