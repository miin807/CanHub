package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Perfil extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ImageButton btnCerrarSesion = findViewById(R.id.cierre);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Perfil.this, getString(R.string.cierreSesion), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Perfil.this, Login.class));
                finish();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion2);
        bottomNavigationView.setSelectedItemId(R.id.menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId()==R.id.biblioteca) {
                Intent int1 = new Intent(this, Busqueda.class);
                int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int1);
            }else if (item.getItemId()==R.id.inicio){
                Intent int2 = new Intent(this, Inicio.class);
                int2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int2);

            }else if (item.getItemId()==R.id.menu){
            Bottomsheet bottomSheet = new Bottomsheet();
            bottomSheet.show(getSupportFragmentManager(), "Opciones");}

            else if(item.getItemId() == R.id.anadir){
                Intent int3 = new Intent(Perfil.this, Formulariopt1.class);
                int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int3);
            }
            return false;
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goInicio(View view) {
        Intent intent = new Intent(Perfil.this,Inicio.class);
        startActivity(intent);
    }
}