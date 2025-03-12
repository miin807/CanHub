package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

public class PlantillaPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plantilla_perfil);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion2);
        bottomNavigationView.setSelectedItemId(R.id.menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.inicio) {
                startActivity(new Intent(this, Inicio.class));
            } else if (item.getItemId() == R.id.biblioteca) {
                startActivity(new Intent(this, Busqueda.class));
            } else if (item.getItemId() == R.id.anadir) {
                startActivity(new Intent(this, Formulariopt1.class));
            }
            return false;
        });

        ImageButton btnAtras = findViewById(R.id.atras);
        btnAtras.setOnClickListener(view -> goInicio());

        aplicarPerfilExterno();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void aplicarPerfilExterno(){
        String nombre;
        String imagen;
        String descripcion;
        Date fecha;

        nombre=getIntent().getStringExtra("nombrecentro");
        imagen=getIntent().getStringExtra("img_centro");
        descripcion=getIntent().getStringExtra("descripcion_centro");
        fecha= (Date) getIntent().getSerializableExtra("fecha");

        ImageView img = findViewById(R.id.logo);
        TextView nombre2 = findViewById(R.id.nombreSignUp);
        TextView descrip = findViewById(R.id.descripcionEscuela);
        TextView fech = findViewById(R.id.fechaEscuela);

        nombre2.setText(nombre);
        descrip.setText(descripcion);
        fech.setText( fecha.toString());

        Glide.with(this)
                .load(imagen)
                .placeholder(R.drawable.placeholder) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(img);
    }

    private void goInicio(){
        Intent intent = new Intent(this, Inicio.class);
        startActivity(intent);
    }
}