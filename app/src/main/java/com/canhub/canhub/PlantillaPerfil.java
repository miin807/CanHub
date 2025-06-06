package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.canhub.canhub.formulario.Formulariopt1;
import com.canhub.canhub.lanzamientos.Altitud;
import com.canhub.canhub.lanzamientos.Latitud;
import com.canhub.canhub.lanzamientos.Presion;
import com.canhub.canhub.lanzamientos.Temperatura;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class PlantillaPerfil extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private String nombre;
    private  String fecha;
    private boolean inicioSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plantilla_perfil);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion2);
        bottomNavigationView.setSelectedItemId(R.id.inicio);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId()==R.id.biblioteca) {
                Intent int1 = new Intent(this, Busqueda.class);
                int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int1);
            }else if (item.getItemId()==R.id.inicio){
                Intent int2 = new Intent(this, Inicio.class);
                int2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(int2);
            }else if(item.getItemId() == R.id.anadir){
                inicioSesion = Login.getinicioSesion();

                if(inicioSesion){
                    Intent int3 = new Intent(PlantillaPerfil.this, Formulariopt1.class);
                    int3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int3);
                }
                else {
                    Toast.makeText(PlantillaPerfil.this,"Tienen que inicar sesion", Toast.LENGTH_SHORT).show();
                    Intent int4 = new Intent(PlantillaPerfil.this, SignUp.class);
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
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        aplicarPerfilExterno();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString("nombrecentro", getIntent().getStringExtra("nombrecentro"));
        bundle.putString("fecha", getIntent().getStringExtra("fecha"));

        adapter.addFragment(Presion.newInstance(bundle), "Presion");
        adapter.addFragment(Altitud.newInstance(bundle), "Altitud");
        adapter.addFragment(Temperatura.newInstance(bundle), "Temperatura");
        adapter.addFragment(Latitud.newInstance(bundle), "Latitud");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

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
        String fecha;

        nombre=getIntent().getStringExtra("nombrecentro");
        imagen=getIntent().getStringExtra("img_cansat");
        descripcion=getIntent().getStringExtra("descripcion_centro");
        fecha= getIntent().getStringExtra("fecha");

        ImageView img = findViewById(R.id.logo);
        TextView nombre2 = findViewById(R.id.nombreSignUp);
        TextView descrip = findViewById(R.id.descripcionEscuela);
        TextView fech = findViewById(R.id.fechaEscuela);

        nombre2.setText(nombre);
        descrip.setText(descripcion);
        fech.setText(fecha);

        Glide.with(this)
                .load(imagen)
                .placeholder(R.drawable.correcto) // Imagen por defecto mientras carga
                .error(R.drawable.error)// Imagen si falla la carga
                .into(img);


    }


    private void goInicio(){
        Intent intent = new Intent(this, Inicio.class);
        startActivity(intent);
    }
}