package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.canhub.canhub.lanzamientos.Presion;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;


public class GraficaJson extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView nombre, descripcion;
    private ImageView imagen;
    private LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grafica_json);

        contenedor = findViewById(R.id.cont);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        nombre = findViewById(R.id.nombrelanzamiento);
        descripcion = findViewById(R.id.descripcionlanzamiento);
        imagen = findViewById(R.id.imagen);
        bajarDatos();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Presion(), "Presion");
        adapter.addFragment(new Presion(), "Altitud");
        adapter.addFragment(new Presion(), "Temperatura");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void bajarDatos() {
        String nombre1;
        String imagen1;
        String descripcion1;

        nombre1=getIntent().getStringExtra("nombrecentro");
        imagen1=getIntent().getStringExtra("img_centro");
        descripcion1=getIntent().getStringExtra("descripcion_centro");

        nombre.setText(nombre1);
        descripcion.setText(descripcion1);
        // Cargar imagen con Glide desde URL
        Glide.with(this)
                .load(imagen1)
                .placeholder(R.drawable.placeholder) // Imagen por defecto mientras carga
                .error(R.drawable.error) // Imagen si falla la carga
                .into(imagen);
    }

    public void goLanzamiento(View view) {
        Intent intent = new Intent(GraficaJson.this,Lanzamiento.class);
        startActivity(intent);
    }
}