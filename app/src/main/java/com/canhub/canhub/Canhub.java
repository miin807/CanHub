package com.canhub.canhub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Canhub extends AppCompatActivity {
private TextView enlace1,enlace2,enlace3;
private TextView git1,git2,git3;
private boolean inicioSesion;
private ImageView foto1,foto2,foto3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_canhub);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        foto1 = findViewById(R.id.perfil1);
        Glide.with(this)
                .load(R.drawable.foto_anahi)
                .error(R.drawable.error)
                .circleCrop()
                .into(foto1);

        foto2 = findViewById(R.id.perfil2);
        Glide.with(this)
                .load(R.drawable.foto_marina)
                .circleCrop()
                .into(foto2);

        foto3 = findViewById(R.id.perfil3);
        Glide.with(this)
                .load(R.drawable.foto_min)
                .circleCrop()
                .into(foto3);

        enlace1 = findViewById(R.id.rol1);
         enlace2 = findViewById(R.id.rol2);
         enlace3 = findViewById(R.id.rol3);
         git1 = findViewById(R.id.github1);
         git2 = findViewById(R.id.github2);
         git3 = findViewById(R.id.github3);

        enlace1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/AnahiHinojosa";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        git1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/AnahiHinojosa";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        enlace2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/MarinaSierra";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        git2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/AnahiHinojosa";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        enlace3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/miin807";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        git3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/AnahiHinojosa";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion2);
        bottomNavigationView.setSelectedItemId(R.id.menu);

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
                inicioSesion = Login.getinicioSesion();

                if (inicioSesion) {
                    Intent int3 = new Intent(Canhub.this, Formulariopt1.class);
                    int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int3);
                } else {
                    Toast.makeText(Canhub.this, "Tienen que inicar sesion", Toast.LENGTH_SHORT).show();
                    Intent int4 = new Intent(Canhub.this, SignUp.class);
                    int4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int4);
                }

            } else if (item.getItemId() == R.id.menu) {
                Bottomsheet bottomSheet = new Bottomsheet();
                bottomSheet.show(getSupportFragmentManager(), "Opciones");
            }
            return false;
        });


    }
}