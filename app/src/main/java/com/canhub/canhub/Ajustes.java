package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Ajustes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajustes);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()== R.id.menu){
                item.setChecked(true);
                startActivity(new Intent(this, Ajustes.class));
            }else if (item.getItemId()== R.id.biblioteca) {
                item.setChecked(true);
                startActivity(new Intent(this, Busqueda.class));
            }else if (item.getItemId()== R.id.inicio){
                item.setChecked(true);
                startActivity(new Intent(this, Inicio.class));
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goPerfil(View view) {
        Intent intent = new Intent(Ajustes.this,Perfil.class);
        startActivity(intent);
    }
}